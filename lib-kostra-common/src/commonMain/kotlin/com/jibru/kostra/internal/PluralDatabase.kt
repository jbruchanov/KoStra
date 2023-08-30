package com.jibru.kostra.internal

import com.jibru.kostra.Locale
import com.jibru.kostra.MissingResourceException
import com.jibru.kostra.PluralResourceKey
import com.jibru.kostra.Plurals
import com.jibru.kostra.Qualifiers
import com.jibru.kostra.database.BinaryDatabase
import com.jibru.kostra.icu.IFixedDecimal
import com.jibru.kostra.icu.PluralCategory
import com.jibru.kostra.icu.PluralRuleSpecs

open class PluralDatabase(localeDatabases: Map<Locale, String>) : Plurals {
    private val dbs = localeDatabases.mapValues { (_, file) ->
        lazy { BinaryDatabase(loadResource(file)) }
    }

    private val stride = PluralCategory.size

    protected open fun getValue(key: PluralResourceKey, locale: Locale, plural: PluralCategory): String? {
        val dbKey = (key.key * stride) + plural.index
        return dbs[locale]?.value?.getListValue(dbKey)
    }

    override fun get(key: PluralResourceKey, qualifiers: Qualifiers, quantity: IFixedDecimal): String {
        val plural = pluralCategory(quantity, qualifiers.locale)
        //try locale+region if exists
        return qualifiers.locale.takeIf { it.hasRegion() }?.let { getValue(key, qualifiers.locale, plural) }
            //try locale only
            ?: qualifiers.locale.takeIf { it != Locale.Undefined }?.let { getValue(key, qualifiers.locale.languageLocale(), plural) }
            //fallback
            ?: getValue(key, Locale.Undefined, plural)
            ?: throw MissingResourceException(key, qualifiers, "plural-${plural.keyword}")
    }

    private fun pluralCategory(quantity: IFixedDecimal, locale: Locale): PluralCategory {
        val specs = PluralRuleSpecs[locale]
            ?: PluralRuleSpecs[locale.languageLocale()]
            ?: throw IllegalStateException("Unable to find PluralCategory for $locale, quantity:$quantity")

        return specs.select(quantity)
    }
}
