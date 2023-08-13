package com.jibru.kostra.plugin.task

import com.jibru.kostra.plugin.FileResolver
import com.jibru.kostra.plugin.FileResolverConfig
import com.jibru.kostra.plugin.KostraPluginExtension
import com.jibru.kostra.plugin.ext.setOf
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream

abstract class AnalyseResourcesTask : DefaultTask() {
    @get:InputFiles
    abstract val resourceDirs: ListProperty<File>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    init {
        group = "kostra"
    }

    @TaskAction
    fun run() {
        val defaults = FileResolverConfig.Defaults
        val config = project.extensions.findByType(KostraPluginExtension::class.java)?.let {
            val android = it.androidResources
            FileResolverConfig(
                keyMapper = android.keyMapper.orNull?.let { closure -> { key, file -> closure.call(key, file) } }
                    ?: android.keyMapperKt.orNull
                    ?: defaults.keyMapper,
                stringFiles = android.stringFiles.orNull?.setOf { v -> v.toRegex() } ?: defaults.stringFiles,
                drawableGroups = android.drawableGroups.orNull?.setOf { v -> v.toRegex() } ?: defaults.drawableGroups,
                drawableExtensions = android.drawableExtensions.orNull?.toSet() ?: defaults.drawableExtensions,
            )
        } ?: FileResolverConfig()
        val result = FileResolver(config).resolve(resourceDirs.get())
        val outputFile = outputFile.get().asFile
        outputFile.parentFile.mkdirs()
        val writer = ObjectOutputStream(FileOutputStream(outputFile))
        writer.writeObject(result)
    }
}
