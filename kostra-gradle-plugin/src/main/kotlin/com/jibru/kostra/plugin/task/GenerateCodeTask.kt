package com.jibru.kostra.plugin.task

import com.jibru.kostra.plugin.KostraPluginConfig
import com.jibru.kostra.plugin.ResItem
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.FileInputStream
import java.io.ObjectInputStream

abstract class GenerateCodeTask : DefaultTask() {

    @get:InputFile
    abstract val resources: RegularFileProperty

    @get:Input
    abstract val kClassName: Property<String>

    @get:OutputDirectory
    abstract val output: Property<File>

    init {
        group = KostraPluginConfig.Tasks.Group
    }

    @Suppress("UNCHECKED_CAST")
    @TaskAction
    fun run() = with(TaskDelegate) {
        val items = ObjectInputStream(FileInputStream(resources.get().asFile)).readObject() as List<ResItem>
        val output = output.get()
        output.deleteRecursively()

        generateCode(
            kClassName = kClassName.get(),
            items = items,
            output = output,
        )
    }
}
