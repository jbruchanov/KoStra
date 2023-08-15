package com.jibru.kostra.plugin

import org.gradle.api.Project
import java.io.File

object KostraPluginConfig {
    val DslObjectName = "kostra"
    val KClassName = "com.jibru.kostra.K"
    val ResourcePropertyName = "Resources"
    val ResourceDbFolderName = "__kostra"

    fun KostraPluginExtension.analysisFile() = File(outputDir.get(), "resources.obj")
    fun KostraPluginExtension.outputSourceDir() = outputDir.map { File(it, "src") }
    fun KostraPluginExtension.outputResourcesDir() = outputDir.map { File(it, "resources") }
    fun KostraPluginExtension.outputDatabasesDir() = File(outputResourcesDir().get(), ResourceDbFolderName)

    fun Project.defaultOutputDir() = File(buildDir, "kostra")
    fun Project.fileWatcherLog() = File(defaultOutputDir(), "filewatcher.log")

    object Tasks {
        val Group = "kostra"
        val AnalyseResources = "analyseResources"
        val GenerateCode = "generateCode"
        val GenerateDatabases = "generateDatabases"
    }
}
