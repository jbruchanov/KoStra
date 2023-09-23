package com.jibru.kostra.plugin

import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized
import java.io.File

object KostraPluginConfig {
    const val DslObjectName = "kostra"
    const val PackageName = "com.jibru.kostra"
    const val PackageNameCompose = "$PackageName.compose"
    const val PackageNameIcu = "$PackageName.icu"
    const val KClassName = "$PackageName.K"
    const val ResourcePropertyName = "Resources"
    const val AliasedImports = true
    const val ComposePluginPackage = "org.jetbrains.compose"
    val ImageExts = setOf("bmp", "jpg", "jpeg", "png", "svg", "webp")

    //looks like _kostra is ignored
    const val ResourceDbFolderName = "kresources"
    const val ComposeDefaultResourceProvider_x = "Compose%sDefaultResourceProvider"

    fun KostraPluginExtension.analysisFile() = File(outputDir.get(), "resources.obj")

    fun KostraPluginExtension.outputSourceDir(variant: String = "") = outputDir.map { File(it, "src${variant.capitalized()}") }

    fun KostraPluginExtension.outputResourcesDir() = outputDir.map { File(it, "resources") }

    fun Project.defaultOutputDir() = File(buildDir, "kostra")

    fun Project.fileWatcherLog() = File(defaultOutputDir(), "filewatcher.log")

    object Tasks {
        const val Group = "kostra"
        const val AnalyseResources = "analyseResources"
        const val GenerateResources = "generateResources"
        const val GenerateComposeDefaults_x = "generate%sComposeDefaults"
        const val GenerateDatabases = "generateDatabases"
        const val CopyResourcesForNativeTemplate_xy = "copy%sToNative%sOutput"
    }
}
