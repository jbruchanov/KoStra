package com.jibru.kostra.internal

import com.jibru.kostra.FileReferences
import com.jibru.kostra.Plurals
import com.jibru.kostra.Strings

interface KostraResources {
    val string: Strings
    val plural: Plurals
    val files: FileReferences
}

interface KostraResourceHider {
    companion object : KostraResourceHider
}

class AppResources(
    override val string: Strings = Strings,
    override val plural: Plurals = Plurals,
    override val files: FileReferences = FileReferences,
) : KostraResources
