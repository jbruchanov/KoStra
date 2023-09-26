package com.jibru.kostra.plugin.ext

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FileSpec

fun FileSpec.minify() = toString()
    .trim()
    .replace("public ", "")
    .replace("\n\n", "\n")

fun FileSpec.Builder.addDefaultSuppressAnnotation(vararg extras: String): FileSpec.Builder {
    val items = (extras.toList() + "ktlint").toTypedArray()
    return addAnnotation(
        AnnotationSpec.builder(Suppress::class)
            .addMember(items.joinToString { "%S" }, *items)
            .build(),
    )
}
