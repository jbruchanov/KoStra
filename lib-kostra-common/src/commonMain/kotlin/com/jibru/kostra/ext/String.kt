package com.jibru.kostra.ext

fun String.takeIfNotEmpty() = if (isNotEmpty()) this else null
