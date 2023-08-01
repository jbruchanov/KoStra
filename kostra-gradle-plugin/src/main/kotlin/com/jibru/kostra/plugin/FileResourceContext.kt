package com.jibru.kostra.plugin

import java.io.File

data class FileResourceContext(
    val file: File,
    val category: String,
    val locale: String,
    val qualifiers: Set<String>,
) {
    val isDefault = locale.isEmpty()
}
