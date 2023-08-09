package com.jibru.kostra.internal

import com.jibru.kostra.MissingResourceException
import com.jibru.kostra.PluralResourceKey
import com.jibru.kostra.Plurals
import com.jibru.kostra.database.BinaryDatabase

open class PluralDatabase(localeDatabases: Map<Locale, String>) : Plurals {
    private val dbs = localeDatabases.mapValues { (locale, file) ->
        lazy { BinaryDatabase(loadResource(file)) }
    }

    private val stride = Plural.size

    protected open fun getValue(key: PluralResourceKey, locale: Locale, plural: Plural): String? {
        val dbKey = (key.key * stride) + plural.index
        return dbs[locale]?.value?.getListValue(dbKey)
    }

    override fun get(key: PluralResourceKey, qualifiers: Qualifiers, quantity: Float): String {
        val plural = plural(quantity, qualifiers.locale)
        //try locale+region if exists
        return qualifiers.locale.takeIf { it.hasRegion() }?.let { getValue(key, qualifiers.locale, plural) }
            //try locale only
            ?: qualifiers.locale.takeIf { it != Locale.Undefined }?.let { getValue(key, qualifiers.locale.languageLocale(), plural) }
            //fallback
            ?: getValue(key, Locale.Undefined, plural)
            ?: throw MissingResourceException(key, qualifiers, "plural-${plural.key}")
    }

    private fun plural(quantity: Float, locale: Locale): Plural {
        val languageLocale = locale.languageLocale()
        return when {
            locale == Locale.Undefined || languageLocale == Locale("en") -> when (quantity) {
                1f -> Plural.One
                else -> Plural.Other
            }

            //trivial for testing now
            languageLocale == Locale("cs") -> when {
                quantity == 1f -> Plural.One
                quantity == 2f || quantity == 3f || quantity == 4f -> Plural.Few
                quantity in 0f..1.5f -> Plural.Many
                else -> Plural.Other
            }

            else -> Plural.Other
        }
    }
}
