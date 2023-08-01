package com.jibru.kostra.plugin

import java.io.File

object FileResolver {
    fun find(rootFolder: File) {
        val allFiles = rootFolder.walkTopDown().toList()
        val listFiles = rootFolder.listFiles() ?: return
        val cats = listFiles.filter { it.isDirectory }
            .groupBy { it.name.substringBefore("-") }
            .mapValues { it.value.map { it.name.substringAfter("-", "") to it } }
    }
}
