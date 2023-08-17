package com.jibru.kostra

interface KostraResources {
    val string: Strings
    val plural: Plurals
    val binary: FileReferences
}

class AppResources(
    override val string: Strings = Strings,
    override val plural: Plurals = Plurals,
    override val binary: FileReferences = FileReferences,
) : KostraResources
