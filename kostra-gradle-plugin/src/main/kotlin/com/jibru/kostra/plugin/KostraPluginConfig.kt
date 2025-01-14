package com.jibru.kostra.plugin

import com.jibru.kostra.plugin.ext.capitalize
import org.gradle.api.Project
import java.io.File

object KostraPluginConfig {
    const val DslObjectName = "kostra"
    const val PackageName = "com.jibru.kostra"
    const val PackageNameCompose = "$PackageName.compose"
    const val PackageNameIcu = "$PackageName.icu"
    const val KClassName = "app.K"
    const val ResourcePropertyName = "Resources"
    const val AliasedImports = true
    const val ModuleResourceKeyName = "ModuleResourceKey"
    const val ComposePluginPackage = "org.jetbrains.compose"
    val ImageExts = setOf("bmp", "jpg", "jpeg", "png", "svg", "webp", "vxml")

    //looks like _kostra is ignored
    const val ResourceDbFolderName = "kresources"
    const val ComposeDefaultResourceProvider_x = "%sDefaultResourceProvider"

    fun Project.analysisFile() = File(defaultOutputDir(), "resources.obj")

    fun Project.outputSourceDir(variant: String = "") = File(defaultOutputDir(), "src${variant.capitalize()}")

    fun Project.outputResourcesDir() = File(defaultOutputDir(), "resources")

    fun Project.defaultOutputDir() = File(layout.buildDirectory.asFile.get(), "generated/kostra")

    fun Project.fileWatcherLog() = File(defaultOutputDir(), "filewatcher.log")

    object Tasks {
        const val Group = "kostra"
        const val AnalyseResources = "analyseResources"
        const val GenerateResources = "generateResources"
        const val GenerateDefaults = "generateDefaults"
        const val GenerateDatabases = "generateDatabases"
        const val CopyResourcesForNativeTemplate_xy = "copy%sToNative%sOutput"
    }
}
