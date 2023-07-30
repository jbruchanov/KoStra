package com.jibru.kostra

import java.io.File

sealed class ResourceItem {
    abstract val key: String
    abstract val locale: String
    abstract val category: String

    data class StringRes(
        override val key: String,
        val value: String,
        override val locale: String,
    ) : ResourceItem() {
        override val category: String = "string"
    }

    data class StringArray(
        override val key: String,
        val items: List<String>,
        override val locale: String,
    ) : ResourceItem() {
        override val category: String = "stringArray"
    }

    data class Plurals(
        override val key: String,
        val items: Map<String, String>,
        override val locale: String,
    ) : ResourceItem() {
        override val category: String = "plural"
    }

    data class FileRes(
        override val key: String,
        val file: File,
        override val locale: String,
        override val category: String,
    ) : ResourceItem()
}
