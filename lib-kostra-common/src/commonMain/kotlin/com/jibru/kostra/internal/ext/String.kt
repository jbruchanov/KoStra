package com.jibru.kostra.internal.ext

fun String.takeIfNotEmpty() = if (isNotEmpty()) this else null
