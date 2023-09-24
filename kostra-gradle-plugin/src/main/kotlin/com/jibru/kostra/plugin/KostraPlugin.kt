@file:Suppress("unused")
@file:OptIn(FlowPreview::class, DelicateCoroutinesApi::class)

package com.jibru.kostra.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.jibru.kostra.plugin.KostraPluginConfig.fileWatcherLog
import com.jibru.kostra.plugin.KostraPluginConfig.outputSourceDir
import com.jibru.kostra.plugin.task.AnalyseResourcesTask
import com.jibru.kostra.plugin.task.ComposeDefaults
import com.jibru.kostra.plugin.task.GenerateCodeTask
import com.jibru.kostra.plugin.task.GenerateComposeDefaultsTask
import com.jibru.kostra.plugin.task.GenerateDatabasesTask
import com.jibru.kostra.plugin.task.TaskDelegate
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.TaskProvider
import org.gradle.configurationcache.extensions.capitalized
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.AbstractExecutable
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.slf4j.LoggerFactory
import java.io.File
import java.time.LocalDateTime

class KostraPlugin : Plugin<Project> {

    private val logger = LoggerFactory.getLogger(KostraPlugin::class.java)

    override fun apply(target: Project) = with(KostraPluginConfig) {
        val extension = target.extensions.create(DslObjectName, KostraPluginExtension::class.java)

        val analyseResourcesTaskProvider = target.tasks
            .register(KostraPluginConfig.Tasks.AnalyseResources, AnalyseResourcesTask::class.java) {
                it.outputFile.set(extension.analysisFile())
                it.resourceDirs.addAll(extension.resourceDirs)
                it.resourceDirs.addAll(extension.androidResources.resourceDirs)
            }

        val generateResourcesTaskProvider = target.tasks
            .register(KostraPluginConfig.Tasks.GenerateResources, GenerateCodeTask::class.java) { task ->
                task.kClassName.set(extension.className)
                task.resourcesAnalysisFile.set(analyseResourcesTaskProvider.get().outputFile)
                task.composeDefaults.set(extension.composeDefaults)
                task.resDbsFolderName.set(extension.outputDatabaseDirName)
                task.outputDir.set(extension.outputSourceDir())
                task.dependsOn(analyseResourcesTaskProvider)
            }

        val generateDatabasesTaskTaskProvider = target.tasks
            .register(KostraPluginConfig.Tasks.GenerateDatabases, GenerateDatabasesTask::class.java) {
                it.resourcesAnalysisFile.set(analyseResourcesTaskProvider.flatMap { v -> v.outputFile })
                it.databaseDir.set(extension.outputDatabaseDirName.get())
                it.outputDir.set(extension.outputResourcesDir())
                it.dependsOn(analyseResourcesTaskProvider)
            }

        target.tasks.findByName("clean")?.apply {
            finalizedBy(generateResourcesTaskProvider)
        }

        target.defaultTasks(generateResourcesTaskProvider.name)

        extension.apply {
            autoConfig.set(true)
            useFileWatcher.set(false)
            strictLocale.set(true)
            className.set(KClassName)
            outputDir.set(target.defaultOutputDir())
            outputDatabaseDirName.set(ResourceDbFolderName)
            composeDefaults.set(target.plugins.any { it.javaClass.packageName.startsWith(ComposePluginPackage) })
        }
        extension.androidResources.apply {
            stringFiles.set(FileResolverConfig.Defaults.stringFiles.toMutableSet())
            painterGroups.set(FileResolverConfig.Defaults.painterGroups.toMutableSet())
        }

        target.afterEvaluate { project ->
            if (extension.autoConfig.get()) {
                tryUpdateSourceSets(project, extension, generateResourcesTaskProvider, generateDatabasesTaskTaskProvider)
                tryAddNativeCopyTasks(project, extension, generateDatabasesTaskTaskProvider)
            }
            updateFileWatcher(target, extension)
        }
    }

    private fun tryAddNativeCopyTasks(
        project: Project,
        extension: KostraPluginExtension,
        generateDbTaskProvider: TaskProvider<GenerateDatabasesTask>,
    ) {
        val otherTaskDeps = { task: Task, linkNativeVariant: String ->
            project.tasks.getByName("link${linkNativeVariant}Native").dependsOn(task)
            project.tasks.getByName("nativeProcessResources").mustRunAfter(task)
        }

        project.extensions.findByType(KotlinMultiplatformExtension::class.java)
            ?.targets
            ?.findByName("native")
            ?.let { it as? KotlinNativeTarget }
            ?.binaries
            ?.filterIsInstance<AbstractExecutable>()
            ?.map { it.name.capitalized() to it.outputDirectory }
            ?.onEach { (name, outputDir) ->
                //copy predefined resources
                project.tasks.register(KostraPluginConfig.Tasks.CopyResourcesForNativeTemplate_xy.format("Resources", name), Copy::class.java) {
                    it.group = KostraPluginConfig.Tasks.Group
                    it.from(extension.resourceDirs)
                    it.into(outputDir)
                    otherTaskDeps(it, name)
                }

                //copy generated string dbs
                project.tasks.register(KostraPluginConfig.Tasks.CopyResourcesForNativeTemplate_xy.format("DBs", name), Copy::class.java) {
                    it.group = KostraPluginConfig.Tasks.Group
                    it.from(generateDbTaskProvider)
                    it.into(outputDir)
                    otherTaskDeps(it, name)
                }
            }
    }

    private fun tryUpdateSourceSets(
        project: Project,
        extension: KostraPluginExtension,
        generateCodeTaskProvider: TaskProvider<GenerateCodeTask>,
        generateDbTaskProvider: TaskProvider<GenerateDatabasesTask>,
    ) {
        run JavaPlugin@{
            (project.extensions.findByType(JavaPluginExtension::class.java) ?: return@JavaPlugin)
                .sourceSets
                .findByName("main")
                .let { mainSourceSet ->
                    if (mainSourceSet == null) {
                        logger.warn("Kostra: ${project.name}:main source set not found, unable to finish auto setup!")
                        return@let
                    }
                    //let java know about kostra sourceDir
                    mainSourceSet.java.srcDir(generateCodeTaskProvider)

                    //put into kostra extension the resource folders, to let KGP know what we currently have
                    extension.resourceDirs.set(extension.resourceDirs.get() + mainSourceSet.resources.srcDirs)

                    //let java know about kostra resource dir
                    mainSourceSet.resources.srcDir(generateDbTaskProvider)
                    logger.info("Java updated resourceDirs:${mainSourceSet.resources.srcDirs.joinToString()}")
                }
        }

        run KotlinMultiplatform@{
            val kotlinMultiplatformExtension = project.extensions.findByType(KotlinMultiplatformExtension::class.java) ?: return@KotlinMultiplatform
            kotlinMultiplatformExtension
                .sourceSets
                .let {
                    val commonMainSourceSet = it.findByName("commonMain")
                    if (commonMainSourceSet == null) {
                        logger.warn("Kostra: ${project.name}:commonMain source set not found, unable to finish auto setup!")
                        return@let
                    }

                    //let java know about kostra sourceDir
                    commonMainSourceSet.kotlin.srcDir(generateCodeTaskProvider)
                    //put into kostra extension the resource folders
                    extension.resourceDirs.set(extension.resourceDirs.get() + commonMainSourceSet.resources.srcDirs)
                    //let KMP know about kostra resource dir
                    commonMainSourceSet.resources.srcDir(generateDbTaskProvider)
                }

            val hasComposePlugin = project.plugins.any { it.javaClass.packageName.startsWith(KostraPluginConfig.ComposePluginPackage) }
            if (hasComposePlugin) {
                kotlinMultiplatformExtension
                    .targets
                    .filter { it.name == "metadata" }
                    .also { check(it.isNotEmpty()) { "Undefined metadata/common target in '${project.name}'" } }
                    .let { ktTargets ->
                        //currently SVG hidden by expect/actual, kept for potential future usage when we need to generate extra code for particular target
                        val composeDefaults = ComposeDefaults.Common
                        val taskProvider = createGenerateComposeDefaultsTask(
                            project = project,
                            variantName = composeDefaults.name,
                            composeDefaults = composeDefaults,
                            extension = extension,
                        )
                        ktTargets.onEach { ktTarget ->
                            ktTarget.compilations.onEach { compilation ->
                                compilation.defaultSourceSet.kotlin.srcDir(taskProvider)
                            }
                        }
                    }
            }
        }

        //android plugin doesn't seem to be taking stuff from KMP common, mostlikely because "jvm resources" are not same as "android res" resources
        run Android@{
            val sourceSets = project.extensions.findByType(LibraryExtension::class.java)?.sourceSets
                ?: project.extensions.findByType(AppExtension::class.java)?.sourceSets

            (sourceSets ?: return@Android)
                .findByName("main")
                ?.resources
                .let { resources ->
                    if (resources == null) {
                        logger.warn("Kostra: ${project.name}:main resources found, unable to finish auto setup!")
                        return@let
                    }
                    //add kostra resources part of android resources (not res <- android resources, just "jar" resources)
                    //we don't want androidResources.resourceDirs here, those are parsed and converted into own db
                    resources.srcDir(extension.resourceDirs.get())
                    //add kostra db as part of android resources
                    resources.srcDir(generateDbTaskProvider)
                }
        }
    }

    private fun createGenerateComposeDefaultsTask(
        project: Project,
        variantName: String,
        composeDefaults: ComposeDefaults,
        extension: KostraPluginExtension,
    ): TaskProvider<GenerateComposeDefaultsTask> {
        val taskProvider = project.tasks.register(
            KostraPluginConfig.Tasks.GenerateComposeDefaults_x.format(variantName),
            GenerateComposeDefaultsTask::class.java,
        ) {
            it.group = KostraPluginConfig.Tasks.Group
            it.composeDefaults.set(composeDefaults)
            it.kClassName.set(extension.className)
            it.outputDir.set(extension.outputSourceDir(variantName))
        }
        return taskProvider
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
                fileResolverConfig = extension.toFileResolverConfig(),
                kClassName = extension.className.get(),
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
            generateResources(
                items = items,
                kClassName = taskDelegateConfig.kClassName,
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
