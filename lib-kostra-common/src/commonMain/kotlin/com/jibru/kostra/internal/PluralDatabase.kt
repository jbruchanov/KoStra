package com.jibru.kostra.internal

import com.jibru.kostra.KLocale
import com.jibru.kostra.KQualifiers
import com.jibru.kostra.MissingResourceException
import com.jibru.kostra.PluralResourceKey
import com.jibru.kostra.Plurals
import com.jibru.kostra.database.BinaryDatabase
import com.jibru.kostra.icu.IFixedDecimal
import com.jibru.kostra.icu.OrdinalRuleSpecs
import com.jibru.kostra.icu.PluralCategory
import com.jibru.kostra.icu.PluralRuleSpecs

open class PluralDatabase(localeDatabases: Map<KLocale, String>) : Plurals {
    private val dbs = localeDatabases.mapValues { (_, file) ->
        lazy { BinaryDatabase(loadResource(file)) }
    }

    private val stride = PluralCategory.size

    protected open fun getValue(key: PluralResourceKey, locale: KLocale, plural: PluralCategory): String? {
        val dbKey = (key.key * stride) + plural.index
        return dbs[locale]?.value?.getListValue(dbKey)
    }

    override fun get(key: PluralResourceKey, qualifiers: KQualifiers, quantity: IFixedDecimal, type: Plurals.Type): String {
        //try locale+region if exists
        return qualifiers.locale.takeIf { it.hasRegion() }
            ?.let { locale -> getValue(key, qualifiers.locale, type.pluralCategory(quantity, locale)) }
            //try locale only
            ?: qualifiers.locale.takeIf { it != KLocale.Undefined }
                ?.let { qualifiers.locale.languageLocale() }
                ?.let { locale -> getValue(key, locale, type.pluralCategory(quantity, locale)) }
            //fallback
            ?: getValue(key, KLocale.Undefined, PluralCategory.Other)
            ?: throw MissingResourceException(key, qualifiers, "plural")
    }

    private fun Plurals.Type.pluralCategory(quantity: IFixedDecimal, locale: KLocale): PluralCategory {
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
