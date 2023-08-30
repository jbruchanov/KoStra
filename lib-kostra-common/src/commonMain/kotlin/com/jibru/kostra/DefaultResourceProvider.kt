package com.jibru.kostra

import com.jibru.kostra.icu.FixedDecimal
import com.jibru.kostra.internal.loadResource

fun KostraResources.string(key: StringResourceKey): String =
    string.get(key, defaultQualifiers())

fun KostraResources.string(key: StringResourceKey, vararg formatArgs: Any): String =
    string.get(key, defaultQualifiers(), *formatArgs)

fun KostraResources.plural(key: PluralResourceKey, quantity: Int): String =
    this.plural.get(key, defaultQualifiers(), FixedDecimal(quantity.toLong()), Plurals.Type.Plurals)

fun KostraResources.plural(key: PluralResourceKey, quantity: FixedDecimal): String =
    this.plural.get(key, defaultQualifiers(), quantity, Plurals.Type.Plurals)

fun KostraResources.plural(key: PluralResourceKey, quantity: Int, vararg formatArgs: Any): String =
    this.plural.get(key, defaultQualifiers(), FixedDecimal(quantity.toLong()), Plurals.Type.Plurals, *formatArgs)

fun KostraResources.plural(key: PluralResourceKey, quantity: FixedDecimal, vararg formatArgs: Any): String =
    this.plural.get(key, defaultQualifiers(), quantity, Plurals.Type.Plurals, *formatArgs)

fun KostraResources.ordinal(key: PluralResourceKey, quantity: Int): String =
    this.plural.get(key, defaultQualifiers(), FixedDecimal(quantity.toLong()), Plurals.Type.Ordinals)

fun KostraResources.ordinal(key: PluralResourceKey, quantity: FixedDecimal): String =
    this.plural.get(key, defaultQualifiers(), quantity, Plurals.Type.Ordinals)

fun KostraResources.ordinal(key: PluralResourceKey, quantity: Int, vararg formatArgs: Any): String =
    this.plural.get(key, defaultQualifiers(), FixedDecimal(quantity.toLong()), Plurals.Type.Ordinals, *formatArgs)

fun KostraResources.ordinal(key: PluralResourceKey, quantity: FixedDecimal, vararg formatArgs: Any): String =
    this.plural.get(key, defaultQualifiers(), quantity, Plurals.Type.Ordinals, *formatArgs)

fun KostraResources.assetPath(key: AssetResourceKey, qualifiers: Qualifiers = defaultQualifiers()): String =
    this.binary.get(key, qualifiers)

fun KostraResources.binaryByteArray(key: AssetResourceKey, qualifiers: Qualifiers = defaultQualifiers()): ByteArray =
    loadResource(binary.get(key, qualifiers))
