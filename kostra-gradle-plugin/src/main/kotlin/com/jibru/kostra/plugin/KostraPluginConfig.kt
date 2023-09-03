package com.jibru.kostra.plugin

import java.io.File
import org.gradle.api.Project

object KostraPluginConfig {
    const val DslObjectName = "kostra"
    const val PackageName = "com.jibru.kostra"
    const val PackageNameCompose = "$PackageName.compose"
    const val PackageNameIcu = "$PackageName.icu"
    const val KClassName = "$PackageName.K"
    const val ResourcePropertyName = "Resources"

    //looks like _kostra is ignored
    const val ResourceDbFolderName = "kresources"
    const val ComposeDefaultResourceProvider = "ComposeDefaultResourceProvider"

    fun KostraPluginExtension.analysisFile() = File(outputDir.get(), "resources.obj")
    fun KostraPluginExtension.outputSourceDir() = outputDir.map { File(it, "src") }
    fun KostraPluginExtension.outputResourcesDir() = outputDir.map { File(it, "resources") }
    fun KostraPluginExtension.outputDatabasesDir() = File(outputResourcesDir().get(), outputDatabaseDirName.get())

    fun Project.defaultOutputDir() = File(buildDir, "kostra")
    fun Project.fileWatcherLog() = File(defaultOutputDir(), "filewatcher.log")

    object Tasks {
        const val Group = "kostra"
        const val AnalyseResources = "analyseResources"
        const val GenerateCode = "generateCode"
        const val GenerateDatabases = "generateDatabases"
        const val CopyResourcesForNativeTemplate_xy = "copy%sToNative%sOutput"
    }
}
