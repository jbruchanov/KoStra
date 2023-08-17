@file:Suppress("unused")
@file:OptIn(FlowPreview::class)

package com.jibru.kostra.plugin

import com.jibru.kostra.plugin.KostraPluginConfig.fileWatcherLog
import com.jibru.kostra.plugin.KostraPluginConfig.outputResourcesDir
import com.jibru.kostra.plugin.KostraPluginConfig.outputSourceDir
import com.jibru.kostra.plugin.task.AnalyseResourcesTask
import com.jibru.kostra.plugin.task.GenerateCodeTask
import com.jibru.kostra.plugin.task.GenerateDatabasesTask
import com.jibru.kostra.plugin.task.TaskDelegate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.slf4j.LoggerFactory
import java.io.File
import java.time.LocalDateTime

class KostraPlugin : Plugin<Project> {

    private val logger = LoggerFactory.getLogger(KostraPlugin::class.java)

    override fun apply(target: Project) = with(KostraPluginConfig) {
        val extension = target.extensions.create(DslObjectName, KostraPluginExtension::class.java)

        val analyseResources = target.tasks.register(KostraPluginConfig.Tasks.AnalyseResources, AnalyseResourcesTask::class.java) {
            it.outputFile.set(extension.analysisFile())
            it.resourceDirs.set(extension.resourceDirs)
        }

        val generateCodeTaskProvider = target.tasks.register(KostraPluginConfig.Tasks.GenerateCode, GenerateCodeTask::class.java) { task ->
            task.kClassName.set(extension.className)
            task.resources.set(analyseResources.flatMap { it.outputFile })
            task.composeDefaults.set(extension.composeDefaults)
            task.outputDir.set(extension.outputSourceDir())
            task.dependsOn(analyseResources)
        }

        val generateDatabasesTaskTaskProvider = target.tasks.register(KostraPluginConfig.Tasks.GenerateDatabases, GenerateDatabasesTask::class.java) {
            it.resources.set(analyseResources.flatMap { it.outputFile })
            it.output.set(extension.outputDatabasesDir())
            it.dependsOn(analyseResources)
        }

        target.tasks.findByName("clean")?.apply {
            finalizedBy(generateCodeTaskProvider)
        }

        target.afterEvaluate {
            //TODO proper way of doing this
            //KMP
            target.tasks.findByName("compileKotlinJvm")?.dependsOn(generateCodeTaskProvider)
            target.tasks.findByName("compileDebugKotlinAndroid")?.dependsOn(generateCodeTaskProvider)
            target.tasks.findByName("compileReleaseKotlinAndroid")?.dependsOn(generateCodeTaskProvider)
            target.tasks.findByName("generateProjectStructureMetadata")?.dependsOn(generateDatabasesTaskTaskProvider)
            target.tasks.findByName("jvmProcessResources")?.dependsOn(generateDatabasesTaskTaskProvider)
            //JVM
            if (target.extensions.findByType(JavaPluginExtension::class.java) != null) {
                target.tasks.findByName("compileKotlin")?.dependsOn(generateCodeTaskProvider)
                target.tasks.findByName("processResources")?.dependsOn(generateDatabasesTaskTaskProvider)
            }
        }

        target.defaultTasks(generateCodeTaskProvider.name)

        extension.apply {
            autoConfig.set(true)
            useFileWatcher.set(false)
            className.set(KClassName)
            outputDir.set(target.defaultOutputDir())
            composeDefaults.set(target.plugins.any { it.javaClass.packageName.startsWith("org.jetbrains.compose") })
        }

        target.afterEvaluate { project ->
            if (extension.autoConfig.get()) {
                tryUpdateSourceSets(target, project, extension)
            }
            updateFileWatcher(target, extension)
        }
    }

    private fun tryUpdateSourceSets(target: Project, project: Project, extension: KostraPluginExtension) {
        val sourceDir = extension.outputSourceDir().get()
        val resourcesDir = extension.outputResourcesDir().get()

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

    private var fileWatcher = FileWatcher()
    private var fileWatcherJob: Job? = null
    private fun updateFileWatcher(target: Project, extension: KostraPluginExtension) {
        fileWatcherJob?.cancel()
        fileWatcherJob = null
        val folders = extension.resourceDirs.get().filter { it.isDirectory }
        if (folders.isNotEmpty() && extension.useFileWatcher.get()) {
            val taskDelegateConfig = TaskDelegate.Config(
                resourceDirs = extension.resourceDirs.get(),
                fileResolverConfig = extension.androidResources.toFileResolverConfig(),
                kClassName = extension.className.get(),
                composeDefaults = extension.composeDefaults.get(),
                output = File(extension.outputDir.get(), "src"),
            )
            val log = target.fileWatcherLog()
            fileWatcherJob = GlobalScope.launch(Dispatchers.IO) {
                fileWatcher.flowChanges(folders)
                    .debounce(1000L)
                    .collect {
                        log.appendText("${LocalDateTime.now()}\n")
                        onFileWatchedNotified(log = null, taskDelegateConfig)
                    }
            }
        }
    }

    private fun onFileWatchedNotified(
        log: File?,
        taskDelegateConfig: TaskDelegate.Config,
    ) = with(TaskDelegate) {
        runCatching {
            val items = analyseCode(
                resourceDirs = taskDelegateConfig.resourceDirs,
                fileResolverConfig = taskDelegateConfig.fileResolverConfig,
            )
            generateCode(
                kClassName = taskDelegateConfig.kClassName,
                items = items,
                composeDefaults = taskDelegateConfig.composeDefaults,
                outputDir = taskDelegateConfig.output,
            )
        }.exceptionOrNull()?.also {
            log?.let { log ->
                log.appendText((it.message ?: "null") + "\n")
                log.appendText(it.stackTraceToString())
                log.appendText("\n")
            }
        }
    }
}
