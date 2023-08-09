package com.jibru.kostra

import com.jibru.kostra.internal.Qualifiers

interface Strings {
    fun get(key: StringResourceKey, qualifiers: Qualifiers): String
    fun get(key: StringResourceKey, qualifiers: Qualifiers, vararg formatArgs: Any): String =
        get(key, qualifiers).format(*formatArgs)

    companion object : Strings {
        override fun get(key: StringResourceKey, qualifiers: Qualifiers): String =
            throw MissingResourceException(key, qualifiers, "string")
    }
}

interface Plurals {
    fun get(key: PluralResourceKey, qualifiers: Qualifiers, quantity: Float): String
    fun get(key: PluralResourceKey, qualifiers: Qualifiers, quantity: Float, vararg formatArgs: Any): String =
        get(key, qualifiers, quantity).format(*formatArgs)

    companion object : Plurals {
        override fun get(key: PluralResourceKey, qualifiers: Qualifiers, quantity: Float): String =
            throw MissingResourceException(key, qualifiers, "plural")
    }
}

interface FileReferences {
    fun get(key: AssetResourceKey, qualifiers: Qualifiers): String

    companion object : FileReferences {
        override fun get(key: AssetResourceKey, qualifiers: Qualifiers): String =
            throw MissingResourceException(key, qualifiers, "fileRef")
    }
}
