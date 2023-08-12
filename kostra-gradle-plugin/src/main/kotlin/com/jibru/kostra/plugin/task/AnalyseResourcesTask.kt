package com.jibru.kostra.plugin.task

import com.jibru.kostra.plugin.FileResolver
import com.jibru.kostra.plugin.FileResolverConfig
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
        val config = FileResolverConfig(
            /*keyMapper = ext.keyTransform.get()*/
        )
        val result = FileResolver(config).resolve(resourceDirs.get())
        val outputFile = outputFile.get().asFile
        outputFile.parentFile.mkdirs()
        val writer = ObjectOutputStream(FileOutputStream(outputFile))
        writer.writeObject(result)
    }
}
