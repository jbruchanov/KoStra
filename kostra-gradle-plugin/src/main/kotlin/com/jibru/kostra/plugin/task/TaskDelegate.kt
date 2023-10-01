package com.jibru.kostra.plugin.task

import com.jibru.kostra.plugin.DefaultsKtGenerator
import com.jibru.kostra.plugin.FileResolver
import com.jibru.kostra.plugin.FileResolverConfig
import com.jibru.kostra.plugin.KostraPluginConfig
import com.jibru.kostra.plugin.ResItem
import com.jibru.kostra.plugin.ResourcesDefaults
import com.jibru.kostra.plugin.ResourcesKtGenerator
import com.jibru.kostra.plugin.ext.fixAliasImports
import com.jibru.kostra.plugin.ext.lowerCasedWith
import com.jibru.kostra.plugin.ext.minify
import com.squareup.kotlinpoet.FileSpec
import java.io.File

object TaskDelegate {

    class Config(
        val resourceDirs: List<File>,
        val fileResolverConfig: FileResolverConfig,
        val kClassName: String,
        val outputDir: File,
        val resDbsFolderName: String,
        val modulePrefix: String,
        val interfaces: Boolean,
        val addJvmInline: Boolean,
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
        modulePrefix: String = "",
        internalVisibility: Boolean = false,
        interfaces: Boolean = true,
        minify: Boolean = true,
        addJvmInline: Boolean = true,
    ) {
        val result = ResourcesKtGenerator(
            items = items,
            className = kClassName,
            resDbsFolderName = modulePrefix.lowerCasedWith(resDbsFolderName),
            resourcePropertyName = modulePrefix + KostraPluginConfig.ResourcePropertyName,
            internalVisibility = internalVisibility,
            useAliasImports = KostraPluginConfig.AliasedImports,
        ).let {
            buildList {
                add(CodeWrapper.from(it.generateKClass(interfaces = interfaces), minify, KostraPluginConfig.AliasedImports))
                if (interfaces) {
                    add(CodeWrapper.from(it.generateIfaces(), minify, KostraPluginConfig.AliasedImports))
                }
                add(CodeWrapper.from(it.generateResources(), minify, false))
                add(CodeWrapper(KostraPluginConfig.ModuleResourceKeyName + ".kt", it.generateResourceProviders(addJvmInline)))
            }
        }
        outputDir.deleteRecursively()
        result.onEach { codeWrapper ->
            val file = File(outputDir, codeWrapper.fileName)
            file.parentFile.mkdirs()
            try {
                file.writeText(codeWrapper.code)
            } catch (t: Throwable) {
                throw IllegalStateException("Unable to generate source code of '$file'", t)
            }
        }
    }

    fun generateComposeDefaults(
        kClassName: String,
        resourcesDefaults: List<ResourcesDefaults>,
        outputDir: File,
        modulePrefix: String,
        internalVisibility: Boolean,
        minify: Boolean = true,
    ) {
        val fileSpecs = DefaultsKtGenerator(kClassName = kClassName, modulePrefix = modulePrefix, internalVisibility = internalVisibility)
            .generateComposeDefaults(*resourcesDefaults.toTypedArray())
        outputDir.deleteRecursively()

        fileSpecs.onEach { fileSpec ->
            val file = File(outputDir, "${fileSpec.name}.kt")
            file.parentFile.mkdirs()
            try {
                if (minify) {
                    //aliasedImports unwanted here
                    file.writeText(fileSpec.minify(useAliasedImports = false))
                } else {
                    file.writeText(fileSpec.toString())
                }
            } catch (t: Throwable) {
                throw IllegalStateException("Unable to generate source code of '$file'", t)
            }
        }
    }
}

private class CodeWrapper(
    val fileName: String,
    val code: String,
) {
    companion object {
        fun from(fileSpec: FileSpec, minify: Boolean, fixAliasedImports: Boolean): CodeWrapper {
            val code = if (minify) {
                fileSpec.minify(fixAliasedImports)
            } else {
                fileSpec.toString().fixAliasImports(fixAliasedImports)
            }
            return CodeWrapper("${fileSpec.name}.kt", code)
        }
    }
}
