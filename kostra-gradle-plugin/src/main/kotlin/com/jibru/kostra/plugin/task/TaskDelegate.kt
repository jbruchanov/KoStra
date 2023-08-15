package com.jibru.kostra.plugin.task

import com.jibru.kostra.plugin.FileResolver
import com.jibru.kostra.plugin.FileResolverConfig
import com.jibru.kostra.plugin.ResItem
import com.jibru.kostra.plugin.ResourcesKtGenerator
import com.jibru.kostra.plugin.ext.minify
import java.io.File

object TaskDelegate {

    class Config(
        val resourceDirs: List<File>,
        val fileResolverConfig: FileResolverConfig,
        val kClassName: String,
        val output: File,
    )

    fun analyseCode(
        resourceDirs: List<File>,
        fileResolverConfig: FileResolverConfig,
    ): List<ResItem> = FileResolver(fileResolverConfig).resolve(resourceDirs)

    fun generateCode(items: List<ResItem>, kClassName: String, output: File, minify: Boolean = true) {
        val result = ResourcesKtGenerator(
            className = kClassName,
            items = items,
            useAliasImports = false,
        ).let {
            listOf(it.generateKClass(), it.generateResources())
        }
        output.deleteRecursively()
        result.onEach {
            val file = File(output, "${it.name}.kt")
            file.parentFile.mkdirs()
            if (minify) {
                file.writeText(it.minify())
            } else {
                file.writeText(it.toString())
            }
        }
    }
}
