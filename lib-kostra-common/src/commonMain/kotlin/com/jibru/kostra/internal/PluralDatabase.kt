package com.jibru.kostra.internal

import com.jibru.kostra.Locale
import com.jibru.kostra.MissingResourceException
import com.jibru.kostra.PluralResourceKey
import com.jibru.kostra.Plurals
import com.jibru.kostra.Qualifiers
import com.jibru.kostra.database.BinaryDatabase
import com.jibru.kostra.icu.IFixedDecimal
import com.jibru.kostra.icu.OrdinalRuleSpecs
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

    override fun get(key: PluralResourceKey, qualifiers: Qualifiers, quantity: IFixedDecimal, type: Plurals.Type): String {
        val pluralCategory = type.pluralCategory(quantity, qualifiers.locale)
        //try locale+region if exists
        return qualifiers.locale.takeIf { it.hasRegion() }?.let { getValue(key, qualifiers.locale, pluralCategory) }
            //try locale only
            ?: qualifiers.locale.takeIf { it != Locale.Undefined }?.let { getValue(key, qualifiers.locale.languageLocale(), pluralCategory) }
            //fallback
            ?: getValue(key, Locale.Undefined, pluralCategory)
            ?: throw MissingResourceException(key, qualifiers, "plural-${pluralCategory.keyword}")
    }

    private fun Plurals.Type.pluralCategory(quantity: IFixedDecimal, locale: Locale): PluralCategory {
        val specs = specs[locale]
            ?: specs[locale.languageLocale()]
            ?: throw IllegalStateException("Unable to find PluralCategory for $locale, quantity:$quantity")

        return specs.select(quantity)
    }

    private val Plurals.Type.specs
        get() = when (this) {
            Plurals.Type.Plurals -> PluralRuleSpecs
            Plurals.Type.Ordinals -> OrdinalRuleSpecs
        }
}
