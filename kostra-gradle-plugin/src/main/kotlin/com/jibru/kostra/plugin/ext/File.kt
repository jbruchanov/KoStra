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

/*
return absolutePath.replace(to.absolutePath, "")
.let {
    if (path.length < absolutePath.length && path.startsWith(File.separator)) {
        path.substring(1)
    } else {
        path
    }
}.pathNormalise()
}
*/
