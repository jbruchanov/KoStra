package com.jibru.kostra.plugin.ext

import java.io.File

fun String.pathNormalise() = split(File.separator).joinToString(separator = "/")
