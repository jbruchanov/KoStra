@file:Suppress("unused")
@file:OptIn(FlowPreview::class)

package com.jibru.kostra.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.jibru.kostra.plugin.KostraPluginConfig.fileWatcherLog
import com.jibru.kostra.plugin.KostraPluginConfig.outputResourcesDir
import com.jibru.kostra.plugin.KostraPluginConfig.outputSourceDir
import com.jibru.kostra.plugin.task.AnalyseResourcesTask
import com.jibru.kostra.plugin.task.GenerateCodeTask
import com.jibru.kostra.plugin.task.GenerateDatabasesTask
import com.jibru.kostra.plugin.task.TaskDelegate
import java.io.File
import java.time.LocalDateTime
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

class KostraPlugin : Plugin<Project> {

    private val logger = LoggerFactory.getLogger(KostraPlugin::class.java)

    override fun apply(target: Project) = with(KostraPluginConfig) {
        val extension = target.extensions.create(DslObjectName, KostraPluginExtension::class.java)

        val analyseResources = target.tasks.register(KostraPluginConfig.Tasks.AnalyseResources, AnalyseResourcesTask::class.java) {
            it.outputFile.set(extension.analysisFile())
            it.resourceDirs.addAll(extension.resourceDirs)
            it.resourceDirs.addAll(extension.androidResources.resourceDirs)
        }

        val generateCodeTaskProvider = target.tasks.register(KostraPluginConfig.Tasks.GenerateCode, GenerateCodeTask::class.java) { task ->
            task.kClassName.set(extension.className)
            task.resources.set(analyseResources.flatMap { it.outputFile })
            task.composeDefaults.set(extension.composeDefaults)
            task.resDbsFolderName.set(extension.outputDatabaseDirName)
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
            //TODO is there better way ?
            val deps = mapOf(
                generateCodeTaskProvider to listOf("compileKotlin.*", "compile.*KotlinAndroid"),
                generateDatabasesTaskTaskProvider to listOf("jvmProcessResources", "generateProjectStructureMetadata", "processResources", "generate.*Resources"),
            )

            deps.forEach { (task, taskNames) ->
                val regexps = taskNames.map { it.toRegex() }
                val tasks = target.tasks.filter { t -> regexps.any { regex -> t.name.matches(regex) } }
                tasks.onEach { it.dependsOn(task) }
                if (tasks.isNotEmpty()) {
                    logger.info("KostraPlugin update tasks deps tasks:${tasks.joinToString { "'${it.name}'" }} dependsOn '${task.name}'")
                }
            }
        }

        target.defaultTasks(generateCodeTaskProvider.name)

        extension.apply {
            autoConfig.set(true)
            useFileWatcher.set(false)
            className.set(KClassName)
            outputDir.set(target.defaultOutputDir())
            outputDatabaseDirName.set(ResourceDbFolderName)
            composeDefaults.set(target.plugins.any { it.javaClass.packageName.startsWith("org.jetbrains.compose") })
        }

        target.afterEvaluate { project ->
            if (extension.autoConfig.get()) {
                tryUpdateSourceSets(project, extension)
            }
            updateFileWatcher(target, extension)
        }
    }

    private fun tryUpdateSourceSets(project: Project, extension: KostraPluginExtension) {
        val outputSourceDir = extension.outputSourceDir().get()
        val outputResourcesDir = extension.outputResourcesDir().get()

        run JavaPlugin@{
            project.extensions.findByType(JavaPluginExtension::class.java)
                ?.sourceSets
                ?.findByName("main")
                ?.let { mainSourceSet ->
                    logger.info("Java added sourceSet:${outputSourceDir.absolutePath}")
                    //let java know about kostra sourceDir
                    mainSourceSet.java.srcDir(outputSourceDir)

                    //put into kostra extension the resource folders
                    extension.resourceDirs.set(extension.resourceDirs.get() + mainSourceSet.resources.srcDirs)

                    //let java know about kostra resource dir
                    mainSourceSet.resources.srcDir(outputResourcesDir)
                    logger.info("Java updated resourceDirs:${mainSourceSet.resources.srcDirs.joinToString()}")
                }
        }

        run KotlinMultiplatform@{
            project.extensions.findByType(KotlinMultiplatformExtension::class.java)
                ?.sourceSets
                ?.findByName("commonMain")
                ?.let { commonMainSourceSet ->
                    logger.info("KMP added sourceSet:${outputSourceDir.absolutePath}")
                    //let java know about kostra sourceDir
                    commonMainSourceSet.kotlin.srcDir(outputSourceDir)

                    //put into kostra extension the resource folders
                    extension.resourceDirs.set(extension.resourceDirs.get() + commonMainSourceSet.resources.srcDirs)

                    //let java know about kostra resource dir
                    commonMainSourceSet.resources.srcDir(outputResourcesDir)
                    logger.info("KMP updated resourceDirs:${commonMainSourceSet.resources.srcDirs.joinToString()}")
                }
        }

        run Android@{
            val sourceSets = project.extensions.findByType(LibraryExtension::class.java)?.sourceSets
                ?: project.extensions.findByType(AppExtension::class.java)?.sourceSets

            sourceSets
                ?.findByName("main")
                ?.resources
                ?.let { resources ->
                    //add kostra resources part of android resources (not res <- android resources, just "jar" resources)
                    //we don't want androidResources.resourceDirs here, those are parsed and converted into own db
                    resources.srcDir(outputResourcesDir)
                    resources.srcDir(extension.resourceDirs.get())
                    logger.info("Android updated resourceDirs:\n${resources.srcDirs.joinToString()}")
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
                resourceDirs = extension.resourceDirs.get() + extension.androidResources.resourceDirs.get(),
                fileResolverConfig = extension.androidResources.toFileResolverConfig(),
                kClassName = extension.className.get(),
                composeDefaults = extension.composeDefaults.get(),
                outputDir = File(extension.outputDir.get(), "src"),
                resDbsFolderName = extension.outputDatabaseDirName.get(),
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
                outputDir = taskDelegateConfig.outputDir,
                resDbsFolderName = taskDelegateConfig.resDbsFolderName,
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
