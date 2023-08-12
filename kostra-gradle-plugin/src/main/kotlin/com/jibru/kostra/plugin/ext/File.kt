package com.jibru.kostra.plugin.ext

import java.io.File

fun File.relativeTo(to: File, ignoreCase: Boolean = true): String = buildString {
    val rootPath = to.absolutePath
    val path = absolutePath
    if (path.startsWith(rootPath, ignoreCase = ignoreCase)) {
        append(path.drop(rootPath.length).pathNormalise())
        if (startsWith("/")) {
            deleteCharAt(0)
        }
    } else {
        append(path.pathNormalise())
    }
}
