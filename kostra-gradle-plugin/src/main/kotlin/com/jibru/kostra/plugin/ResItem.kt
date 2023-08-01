package com.jibru.kostra.plugin

import com.jibru.kostra.internal.Qualifiers
import java.io.File

sealed class ResItem {
    abstract val key: String
    abstract val qualifiers: Qualifiers
    abstract val category: String

    data class StringRes(
        override val key: String,
        val value: String,
        override val qualifiers: Qualifiers,
    ) : ResItem() {
        override val category: String = "string"
    }

    data class StringArray(
        override val key: String,
        val items: List<String>,
        override val qualifiers: Qualifiers,
    ) : ResItem() {
        override val category: String = "stringArray"
    }

    data class Plurals(
        override val key: String,
        val items: Map<String, String>,
        override val qualifiers: Qualifiers,
    ) : ResItem() {
        override val category: String = "plural"
    }

    data class FileRes(
        override val key: String,
        val file: File,
        override val qualifiers: Qualifiers,
        override val category: String,
    ) : ResItem() {
        val drawable = drawableCategories.contains(category)
    }

    companion object {
        private val drawableCategories = setOf("drawable")
    }
}
