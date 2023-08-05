package com.jibru.kostra.plugin

import com.jibru.kostra.BinaryResourceKey
import com.jibru.kostra.DrawableResourceKey
import com.jibru.kostra.PluralResourceKey
import com.jibru.kostra.StringResourceKey
import com.jibru.kostra.internal.Qualifiers
import com.jibru.kostra.plugin.ext.relativeTo
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.typeNameOf
import java.io.File

interface StringValueResItem {
    val key: String
    val value: String
    val qualifiers: Qualifiers
    val group: String
}

sealed class ResItem {
    abstract val key: String
    abstract val qualifiers: Qualifiers
    abstract val group: String

    abstract val resourceKeyType: TypeName

    val distinctKey by lazy(LazyThreadSafetyMode.NONE) { Triple(key, qualifiers, group) }
    open val resourcesGroup get() = group

    data class StringRes(
        override val key: String,
        override val value: String,
        override val qualifiers: Qualifiers,
    ) : ResItem(), StringValueResItem {
        override val group: String = String
        override val resourceKeyType: TypeName = typeNameOf<StringResourceKey>()
    }

    data class StringArray(
        override val key: String,
        val items: List<String>,
        override val qualifiers: Qualifiers,
    ) : ResItem() {
        override val group: String = StringArray
        override val resourceKeyType: TypeName = throw UnsupportedOperationException("StringArray arrays not supported!")
    }

    data class Plurals(
        override val key: String,
        //indexes matching [Plural]
        val items: List<String?>,
        override val qualifiers: Qualifiers,
    ) : ResItem() {
        override val group: String = Plural
        override val resourceKeyType: TypeName = typeNameOf<PluralResourceKey>()
    }

    data class FileRes(
        override val key: String,
        val file: File,
        override val qualifiers: Qualifiers,
        override val group: String,
        val root: File, /* = file.parentFile.parentFile*/
    ) : ResItem(), StringValueResItem {
        val drawable = group == Drawable
        override val value by lazy { file.relativeTo(root, ignoreCase = true) }
        override val resourcesGroup: String = if (drawable) Drawable else Binary
        override val resourceKeyType: TypeName = if (drawable) typeNameOf<DrawableResourceKey>() else typeNameOf<BinaryResourceKey>()
    }

    companion object {
        const val Drawable = "drawable"
        const val String = "string"
        const val Binary = "binary"
        const val Plural = "plural"
        const val StringArray = "stringArray"
    }
}
