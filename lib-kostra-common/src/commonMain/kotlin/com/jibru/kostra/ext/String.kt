package com.jibru.kostra.ext

internal fun String.takeIfNotEmpty() = ifEmpty { null }
