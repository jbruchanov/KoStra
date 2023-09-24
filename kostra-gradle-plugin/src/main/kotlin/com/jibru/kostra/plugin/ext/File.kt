package com.jibru.kostra.plugin.ext

import com.jibru.kostra.plugin.KostraPluginConfig
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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

fun File.ext() = name.substringAfterLast(".", "").trim()

fun File.isImage() = ext().lowercase().let { KostraPluginConfig.ImageExts.contains(it) }

private val logFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

internal fun File.appendLog(msg: String) {
    appendText("${LocalDateTime.now().format(logFormatter)}: $msg\n")
}
