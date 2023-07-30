package com.jibru.kostra

import java.io.File

sealed class ResourceItem {
    abstract val key: String
    abstract val locale: String

    data class StringRes(
        override val key: String,
        val value: String,
        override val locale: String,
    ) : ResourceItem()

    data class StringArray(
        override val key: String,
        val items: List<String>,
        override val locale: String,
    ) : ResourceItem()

    data class Plurals(
        override val key: String,
        val items: Map<String, String>,
        override val locale: String,
    ) : ResourceItem()

    data class FileRes(
        override val key: String,
        val file: File,
        override val locale: String,
    ) : ResourceItem()
}
