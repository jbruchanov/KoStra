package com.jibru.kostra.plugin.task

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
        val composeDefaults: Boolean,
        val outputDir: File,
        val resDbsFolderName: String,
    )

    fun analyseCode(
        resourceDirs: List<File>,
        fileResolverConfig: FileResolverConfig,
    ): List<ResItem> = FileResolver(fileResolverConfig).resolve(resourceDirs)

    fun generateCode(
        items: List<ResItem>,
        kClassName: String,
        composeDefaults: Boolean,
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
                add(it.generateKClass())
                add(it.generateResources())
                if (composeDefaults) {
                    add(it.generateComposeDefaults())
                }
            }
        }
        outputDir.deleteRecursively()
        result.onEach {
            val file = File(outputDir, "${it.name}.kt")
            file.parentFile.mkdirs()
            try {
                if (minify) {
                    file.writeText(it.minify().fixAliasImports())
                } else {
                    file.writeText(it.toString().fixAliasImports())
                }
            } catch (t: Throwable) {
                throw IllegalStateException("Unable to generate source code of '$file'", t)
            }
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
