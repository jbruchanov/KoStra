package test

import com.squareup.kotlinpoet.FileSpec

fun FileSpec.minify() = toString()
    .trim()
    .replace("public ", "")
    .replace("\n\n", "\n")
