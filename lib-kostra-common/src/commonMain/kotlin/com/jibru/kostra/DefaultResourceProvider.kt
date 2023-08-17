package com.jibru.kostra

import com.jibru.kostra.internal.KostraResources
import com.jibru.kostra.internal.Qualifiers
import com.jibru.kostra.internal.loadResource

fun KostraResources.string(key: StringResourceKey): String =
    string.get(key, defaultQualifiers())

fun KostraResources.string(key: StringResourceKey, vararg formatArgs: Any): String =
    string.get(key, defaultQualifiers(), *formatArgs)

fun KostraResources.plural(key: PluralResourceKey, quantity: Int): String =
    this.plural.get(key, defaultQualifiers(), quantity.toFloat())

fun KostraResources.plural(key: PluralResourceKey, quantity: Float): String =
    this.plural.get(key, defaultQualifiers(), quantity)

fun KostraResources.plural(key: PluralResourceKey, quantity: Int, vararg formatArgs: Any): String =
    this.plural.get(key, defaultQualifiers(), quantity.toFloat(), *formatArgs)

fun KostraResources.plural(key: PluralResourceKey, quantity: Float, vararg formatArgs: Any): String =
    this.plural.get(key, defaultQualifiers(), quantity, *formatArgs)

fun KostraResources.assetPath(key: AssetResourceKey, qualifiers: Qualifiers = defaultQualifiers()): String =
    this.binary.get(key, qualifiers)

fun KostraResources.binaryByteArray(key: AssetResourceKey, qualifiers: Qualifiers = defaultQualifiers()): ByteArray =
    loadResource(binary.get(key, qualifiers))
