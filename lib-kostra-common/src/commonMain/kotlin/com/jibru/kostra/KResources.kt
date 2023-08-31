package com.jibru.kostra

interface KResources {
    val string: Strings
    val plural: Plurals
    val binary: FileReferences
}

class KAppResources(
    override val string: Strings = Strings,
    override val plural: Plurals = Plurals,
    override val binary: FileReferences = FileReferences,
) : KResources
