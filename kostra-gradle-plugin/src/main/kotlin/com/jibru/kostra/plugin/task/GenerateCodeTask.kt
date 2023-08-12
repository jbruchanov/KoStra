package com.jibru.kostra.plugin.task

import com.jibru.kostra.plugin.ResItem
import com.jibru.kostra.plugin.ResourcesKtGenerator
import com.jibru.kostra.plugin.ext.minify
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
    abstract val packageName: Property<String>

    @get:Input
    abstract val kClassName: Property<String>

    @get:OutputDirectory
    abstract val output: Property<File>

    init {
        group = "kostra"
    }

    @Suppress("UNCHECKED_CAST")
    @TaskAction
    fun run() {
        val items = ObjectInputStream(FileInputStream(resources.get().asFile)).readObject() as List<ResItem>
        val result = ResourcesKtGenerator(
            packageName = packageName.get(),
            className = kClassName.get(),
            items = items,
        ).let {
            listOf(it.generateKClass(), it.generateResources())
        }
        val output = output.get()
        output.deleteRecursively()
        result.onEach {
            val file = File(output, "${it.name}.kt")
            file.parentFile.mkdirs()
            file.writeText(it.minify())
        }
    }
}
