package com.jibru.kostra.plugin.task

import com.jibru.kostra.plugin.ComposeDefaultsKtGenerator
import com.jibru.kostra.plugin.FileResolver
import com.jibru.kostra.plugin.FileResolverConfig
import com.jibru.kostra.plugin.KostraPluginConfig
import com.jibru.kostra.plugin.ResItem
import com.jibru.kostra.plugin.ResourcesKtGenerator
import com.jibru.kostra.plugin.ext.minify
import java.io.File

object TaskDelegate {

    class Config(
        val resourceDirs: List<File>,
        val fileResolverConfig: FileResolverConfig,
        val kClassName: String,
        val outputDir: File,
        val resDbsFolderName: String,
    )

    fun analyseCode(
        resourceDirs: List<File>,
        fileResolverConfig: FileResolverConfig,
    ): List<ResItem> = FileResolver(fileResolverConfig).resolve(resourceDirs)

    fun generateResources(
        items: List<ResItem>,
        kClassName: String,
        outputDir: File,
        resDbsFolderName: String,
        minify: Boolean = true,
    ) {
        val result = ResourcesKtGenerator(
            items = items,
            className = kClassName,
            resDbsFolderName = resDbsFolderName,
            useAliasImports = KostraPluginConfig.AliasedImports,
        ).let {
            buildList {
                add(it.generateKClass() to KostraPluginConfig.AliasedImports)
                add(it.generateResources() to false)
            }
        }
        outputDir.deleteRecursively()
        result.onEach { (fileSpec, fixAliasImports) ->
            val file = File(outputDir, "${fileSpec.name}.kt")
            file.parentFile.mkdirs()
            try {
                if (minify) {
                    file.writeText(fileSpec.minify().fixAliasImports(fixAliasImports))
                } else {
                    file.writeText(fileSpec.toString().fixAliasImports(fixAliasImports))
                }
            } catch (t: Throwable) {
                throw IllegalStateException("Unable to generate source code of '$file'", t)
            }
        }
    }

    fun generateComposeDefaults(
        kClassName: String,
        composeDefaults: ComposeDefaults,
        outputDir: File,
        minify: Boolean = true,
    ) {
        val fileSpec = ComposeDefaultsKtGenerator(kClassName = kClassName)
            .generateComposeDefaults(composeDefaults = composeDefaults)
        outputDir.deleteRecursively()

        val file = File(outputDir, "${fileSpec.name}.kt")
        file.parentFile.mkdirs()
        try {
            if (minify) {
                file.writeText(fileSpec.minify())
            } else {
                file.writeText(fileSpec.toString())
            }
        } catch (t: Throwable) {
            throw IllegalStateException("Unable to generate source code of '$file'", t)
        }
    }
}

//https://github.com/square/kotlinpoet/issues/1696
private fun String.fixAliasImports(useAliasImports: Boolean = KostraPluginConfig.AliasedImports): String {
    if (!useAliasImports) return this
    var result = this
    ResourcesKtGenerator.AliasedImports.forEach { (klass, alias) ->
        //" " to avoid replacing imports
        result = result.replace(" " + requireNotNull(klass.simpleName), " $alias")
    }
    return result
}
