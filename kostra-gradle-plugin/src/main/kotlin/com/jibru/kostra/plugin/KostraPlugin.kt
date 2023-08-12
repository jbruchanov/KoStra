@file:Suppress("unused")
@file:OptIn(ExperimentalTime::class)

package com.jibru.kostra.plugin

import com.jibru.kostra.plugin.task.AnalyseResourcesTask
import com.jibru.kostra.plugin.task.GenerateCodeTask
import com.jibru.kostra.plugin.task.GenerateDatabasesTask
import org.gradle.api.Plugin
import org.gradle.api.Project
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
            target.tasks.findByName("compileKotlinJvm")?.dependsOn(generateCodeTaskProvider)
            target.tasks.findByName("generateProjectStructureMetadata")?.dependsOn(generateDatabasesTaskTaskProvider)
            target.tasks.findByName("jvmProcessResources")?.dependsOn(generateDatabasesTaskTaskProvider)
        }

        extension.apply {
            packageName.set("com.jibru.kostra")
            kClassName.set("K")
            //TODO if KMP this else java maybe ?
            resourceDirs.set(listOf(target.file("src/commonMain/resources")))
            output.set(File(target.buildDir, "kostra"))
        }
    }
}
