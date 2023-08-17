package com.jibru.kostra.plugin

import org.gradle.api.Project
import java.io.File

object KostraPluginConfig {
    const val DslObjectName = "kostra"
    const val PackageName = "com.jibru.kostra"
    const val PackageNameCompose = "$PackageName.compose"
    const val KClassName = "$PackageName.K"
    const val ResourcePropertyName = "Resources"
    const val ResourceDbFolderName = "__kostra"
    const val ComposeDefaultResourceProvider = "ComposeDefaultResourceProvider"

    fun KostraPluginExtension.analysisFile() = File(outputDir.get(), "resources.obj")
    fun KostraPluginExtension.outputSourceDir() = outputDir.map { File(it, "src") }
    fun KostraPluginExtension.outputResourcesDir() = outputDir.map { File(it, "resources") }
    fun KostraPluginExtension.outputDatabasesDir() = File(outputResourcesDir().get(), ResourceDbFolderName)

    fun Project.defaultOutputDir() = File(buildDir, "kostra")
    fun Project.fileWatcherLog() = File(defaultOutputDir(), "filewatcher.log")

    object Tasks {
        const val Group = "kostra"
        const val AnalyseResources = "analyseResources"
        const val GenerateCode = "generateCode"
        const val GenerateDatabases = "generateDatabases"
    }
}
