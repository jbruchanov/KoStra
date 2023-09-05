package com.jibru.kostra.plugin.task

import com.jibru.kostra.plugin.KostraPluginConfig
import com.jibru.kostra.plugin.KostraPluginExtension
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class AnalyseResourcesTask : DefaultTask() {
    @get:InputFiles
    abstract val resourceDirs: ListProperty<File>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    init {
        group = KostraPluginConfig.Tasks.Group
    }

    @TaskAction
    fun run() = with(TaskDelegate) {
        val extension = project.extensions.getByType(KostraPluginExtension::class.java)
        val items = analyseCode(
            resourceDirs = extension.allResourceDirs(),
            fileResolverConfig = extension.toFileResolverConfig(),
        )
        val outputFile = outputFile.get().asFile
        outputFile.parentFile.mkdirs()
        val writer = ObjectOutputStream(FileOutputStream(outputFile))
        writer.writeObject(items)
    }
}
