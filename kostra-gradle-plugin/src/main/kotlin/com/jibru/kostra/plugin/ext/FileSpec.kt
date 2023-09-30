package com.jibru.kostra.plugin.ext

import com.jibru.kostra.plugin.KostraPluginConfig
import com.jibru.kostra.plugin.ResourcesKtGenerator
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FileSpec

internal fun FileSpec.minify(useAliasedImports: Boolean = KostraPluginConfig.AliasedImports) = toString()
    .trim()
    .replace("public ", "")
    .replace("\n\n", "\n")
    .fixAliasImports(useAliasedImports)

//https://github.com/square/kotlinpoet/issues/1696
internal fun String.fixAliasImports(useAliasImports: Boolean = KostraPluginConfig.AliasedImports): String {
    if (!useAliasImports) return this
    var result = this
    ResourcesKtGenerator.AliasedImports.forEach { (klass, alias) ->
        //":= " to avoid replacing no package imports
        result = result
            .replace(": " + requireNotNull(klass.simpleName), ": $alias")
            .replace("= " + requireNotNull(klass.simpleName), "= $alias")
    }
    return result
}

fun FileSpec.Builder.addDefaultSuppressAnnotation(vararg extras: String): FileSpec.Builder {
    val items = (extras.toList() + "ktlint").toTypedArray()
    return addAnnotation(
        AnnotationSpec.builder(Suppress::class)
            .addMember(items.joinToString { "%S" }, *items)
            .build(),
    )
}
