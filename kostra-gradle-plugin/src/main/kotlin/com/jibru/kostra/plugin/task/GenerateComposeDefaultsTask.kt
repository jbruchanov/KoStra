package com.jibru.kostra.plugin.task

import com.jibru.kostra.plugin.KostraPluginConfig
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

enum class ComposeDefaults { Common, Svg }

abstract class GenerateComposeDefaultsTask : DefaultTask() {

    @get:Input
    abstract val composeDefaults: Property<ComposeDefaults>

    @get:Input
    abstract val kClassName: Property<String>

    @get:OutputDirectory
    abstract val outputDir: Property<File>

    init {
        group = KostraPluginConfig.Tasks.Group
    }

    @TaskAction
    fun run() = with(TaskDelegate) {
        val outputDir = outputDir.get()
        outputDir.deleteRecursively()

        generateComposeDefaults(
            kClassName = kClassName.get(),
            composeDefaults = composeDefaults.get(),
            outputDir = outputDir,
        )
    }
}
