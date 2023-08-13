@file:Suppress("unused")
@file:OptIn(ExperimentalTime::class)

package com.jibru.kostra.plugin

import com.jibru.kostra.plugin.task.AnalyseResourcesTask
import com.jibru.kostra.plugin.task.GenerateCodeTask
import com.jibru.kostra.plugin.task.GenerateDatabasesTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.time.ExperimentalTime

class KostraPlugin : Plugin<Project> {

    private val logger = LoggerFactory.getLogger(KostraPlugin::class.java)

    override fun apply(target: Project) {
        val extension = target.extensions.create("kostra", KostraPluginExtension::class.java)

        val analyseResources = target.tasks.register("analyseResources", AnalyseResourcesTask::class.java) {
            it.outputFile.set(File(extension.output.get(), "resources.obj"))
            it.resourceDirs.set(extension.resourceDirs)
        }

        val generateCodeTaskProvider = target.tasks.register("generateCode", GenerateCodeTask::class.java) { task ->
            task.packageName.set(extension.packageName)
            task.kClassName.set(extension.kClassName)
            task.resources.set(analyseResources.flatMap { it.outputFile })
            task.output.set(extension.output.map { File(it, "src") })
            task.dependsOn(analyseResources)
        }

        val generateDatabasesTaskTaskProvider = target.tasks.register("generateDatabases", GenerateDatabasesTask::class.java) {
            it.resources.set(analyseResources.flatMap { it.outputFile })
            it.output.set(extension.output.map { File(it, "resources/__kostra") })
            it.dependsOn(analyseResources)
        }

        target.tasks.findByName("clean")?.apply {
            finalizedBy(generateCodeTaskProvider)
        }

        //KMP

        target.afterEvaluate {
            //TODO proper way of doing this
            target.tasks.findByName("compileKotlinJvm")?.dependsOn(generateCodeTaskProvider)
            target.tasks.findByName("compileDebugKotlinAndroid")?.dependsOn(generateCodeTaskProvider)
            target.tasks.findByName("compileReleaseKotlinAndroid")?.dependsOn(generateCodeTaskProvider)
            target.tasks.findByName("generateProjectStructureMetadata")?.dependsOn(generateDatabasesTaskTaskProvider)
            target.tasks.findByName("jvmProcessResources")?.dependsOn(generateDatabasesTaskTaskProvider)
        }

        target.defaultTasks(generateCodeTaskProvider.name)

        extension.apply {
            autoConfig.set(true)
            packageName.set("com.jibru.kostra")
            kClassName.set("K")
            output.set(File(target.buildDir, "kostra"))
        }

        target.afterEvaluate { project ->
            if (extension.autoConfig.get()) {
                tryUpdateSourceSets(target, project, extension)
            }
        }
    }

    private fun tryUpdateSourceSets(target: Project, project: Project, extension: KostraPluginExtension) {
        val sourceDir = File(target.buildDir, "kostra/src")
        val resourcesDir = File(target.buildDir, "kostra/resources")

        run JavaPlugin@{
            project.extensions.findByType(JavaPluginExtension::class.java)
                ?.sourceSets
                ?.findByName("main")
                ?.let { mainSourceSet ->
                    logger.info("Java added sourceSet:${sourceDir.absolutePath}")
                    //let java know about kostra sourceDir
                    mainSourceSet.java.srcDir(sourceDir)

                    //put into kostra extension the resource folders
                    extension.resourceDirs.set(extension.resourceDirs.get() + mainSourceSet.resources.srcDirs)

                    //let java know about kostra resource dir
                    mainSourceSet.resources.srcDir(resourcesDir)
                    logger.info("Java added resources:${mainSourceSet.resources.srcDirs.joinToString()}")
                }
        }

        run KotlinMultiplatform@{
            project.extensions.findByType(KotlinMultiplatformExtension::class.java)
                ?.sourceSets
                ?.findByName("commonMain")
                ?.let { commonMainSourceSet ->
                    logger.info("KMP added sourceSet:${sourceDir.absolutePath}")
                    //let java know about kostra sourceDir
                    commonMainSourceSet.kotlin.srcDir(sourceDir)

                    //put into kostra extension the resource folders
                    extension.resourceDirs.set(extension.resourceDirs.get() + commonMainSourceSet.resources.srcDirs)

                    //let java know about kostra resource dir
                    commonMainSourceSet.resources.srcDir(resourcesDir)
                    logger.info("KMP added resources:${commonMainSourceSet.resources.srcDirs.joinToString()}")
                }
        }
    }
}
