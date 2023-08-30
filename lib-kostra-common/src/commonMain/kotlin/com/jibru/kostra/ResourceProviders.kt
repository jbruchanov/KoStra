package com.jibru.kostra

import com.jibru.kostra.icu.FixedDecimal
import com.jibru.kostra.icu.IFixedDecimal

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

    enum class Type { Plurals, Ordinals }

    fun get(key: PluralResourceKey, qualifiers: Qualifiers, quantity: IFixedDecimal, type: Type): String
    fun get(key: PluralResourceKey, qualifiers: Qualifiers, quantity: IFixedDecimal, type: Type, vararg formatArgs: Any): String =
        get(key, qualifiers, quantity, type).format(*formatArgs)

    fun get(key: PluralResourceKey, qualifiers: Qualifiers, quantity: Int, type: Type): String =
        get(key, qualifiers, FixedDecimal(quantity.toLong()), type)

    fun get(key: PluralResourceKey, qualifiers: Qualifiers, quantity: Int, type: Type, vararg formatArgs: Any): String =
        get(key, qualifiers, FixedDecimal(quantity.toLong()), type).format(*formatArgs)

    companion object : Plurals {
        override fun get(key: PluralResourceKey, qualifiers: Qualifiers, quantity: IFixedDecimal, type: Type): String =
            throw MissingResourceException(key, qualifiers, "plural:$type")
    }
}

interface FileReferences {
    fun get(key: AssetResourceKey, qualifiers: Qualifiers): String

    companion object : FileReferences {
        override fun get(key: AssetResourceKey, qualifiers: Qualifiers): String =
            throw MissingResourceException(key, qualifiers, "fileRef")
    }
}
