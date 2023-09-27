package com.jibru.kostra.plugin.ext

import java.io.File

fun String.pathNormalise() = split(File.separator).joinToString(separator = "/")

//just to avoid leaking it from common
fun String.takeIfNotEmpty() = if (isNotEmpty()) this else null

fun String.lowerCasedWith(other: String) = (takeIfNotEmpty()?.let { "${it.lowercase()}_" } ?: "") + other
