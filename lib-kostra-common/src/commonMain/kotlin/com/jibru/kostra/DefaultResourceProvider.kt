package com.jibru.kostra

import com.jibru.kostra.icu.FixedDecimal
import com.jibru.kostra.internal.loadResource

fun KResources.string(key: StringResourceKey): String =
    string.get(key, DefaultQualifiersProvider.get())

fun KResources.string(key: StringResourceKey, vararg formatArgs: Any): String =
    string.get(key, DefaultQualifiersProvider.get(), *formatArgs)

fun KResources.plural(key: PluralResourceKey, quantity: Int): String =
    this.plural.get(key, DefaultQualifiersProvider.get(), FixedDecimal(quantity.toLong()), Plurals.Type.Plurals)

fun KResources.plural(key: PluralResourceKey, quantity: FixedDecimal): String =
    this.plural.get(key, DefaultQualifiersProvider.get(), quantity, Plurals.Type.Plurals)

fun KResources.plural(key: PluralResourceKey, quantity: Int, vararg formatArgs: Any): String =
    this.plural.get(key, DefaultQualifiersProvider.get(), FixedDecimal(quantity.toLong()), Plurals.Type.Plurals, *formatArgs)

fun KResources.plural(key: PluralResourceKey, quantity: FixedDecimal, vararg formatArgs: Any): String =
    this.plural.get(key, DefaultQualifiersProvider.get(), quantity, Plurals.Type.Plurals, *formatArgs)

fun KResources.ordinal(key: PluralResourceKey, quantity: Int): String =
    this.plural.get(key, DefaultQualifiersProvider.get(), FixedDecimal(quantity.toLong()), Plurals.Type.Ordinals)

fun KResources.ordinal(key: PluralResourceKey, quantity: FixedDecimal): String =
    this.plural.get(key, DefaultQualifiersProvider.get(), quantity, Plurals.Type.Ordinals)

fun KResources.ordinal(key: PluralResourceKey, quantity: Int, vararg formatArgs: Any): String =
    this.plural.get(key, DefaultQualifiersProvider.get(), FixedDecimal(quantity.toLong()), Plurals.Type.Ordinals, *formatArgs)

fun KResources.ordinal(key: PluralResourceKey, quantity: FixedDecimal, vararg formatArgs: Any): String =
    this.plural.get(key, DefaultQualifiersProvider.get(), quantity, Plurals.Type.Ordinals, *formatArgs)

fun KResources.assetPath(key: AssetResourceKey, qualifiers: KQualifiers = DefaultQualifiersProvider.get()): String =
    this.binary.get(key, qualifiers)

fun KResources.binaryByteArray(key: AssetResourceKey, qualifiers: KQualifiers = DefaultQualifiersProvider.get()): ByteArray =
    loadResource(binary.get(key, qualifiers))
