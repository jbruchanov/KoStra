package com.jibru.kostra.plugin.ext

import com.squareup.kotlinpoet.FileSpec

fun FileSpec.minify() = toString()
    .trim()
    .replace("public ", "")
    .replace("\n\n", "\n")
