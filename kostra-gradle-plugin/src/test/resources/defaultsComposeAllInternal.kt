@file:Suppress("NOTHING_TO_INLINE", "ktlint")

package com.sample.app

import com.jibru.kostra.DefaultQualifiersProvider
import com.jibru.kostra.Plurals.Type.Ordinals
import com.jibru.kostra.Plurals.Type.Plurals
import com.jibru.kostra.binaryByteArray
import com.jibru.kostra.icu.IFixedDecimal
import kotlin.Any
import kotlin.ByteArray
import kotlin.Int
import kotlin.String
import kotlin.Suppress

internal inline fun stringResource(key: StringResourceKey): String = Resources.string.get(key,
    DefaultQualifiersProvider.current)

internal inline fun stringResource(key: StringResourceKey, vararg formatArgs: Any): String =
    Resources.string.get(key, DefaultQualifiersProvider.current, *formatArgs)

internal inline fun byteArray(key: PainterResourceKey): ByteArray = Resources.binaryByteArray(key,
    DefaultQualifiersProvider.current)

internal inline fun assetPath(key: AssetResourceKey): String = Resources.binary.get(key,
    DefaultQualifiersProvider.current)

internal inline fun binaryResource(key: AssetResourceKey): ByteArray =
    Resources.binaryByteArray(key, DefaultQualifiersProvider.current)

internal inline fun pluralStringResource(
  key: PluralResourceKey,
  quantity: Int,
  vararg formatArgs: Any,
): String = Resources.plural.get(key, DefaultQualifiersProvider.current, quantity, Plurals,
    *formatArgs)

internal inline fun pluralStringResource(key: PluralResourceKey, quantity: Int): String =
    Resources.plural.get(key, DefaultQualifiersProvider.current, quantity, Plurals)

internal inline fun ordinalStringResource(
  key: PluralResourceKey,
  quantity: Int,
  vararg formatArgs: Any,
): String = Resources.plural.get(key, DefaultQualifiersProvider.current, quantity, Ordinals,
    *formatArgs)

internal inline fun ordinalStringResource(key: PluralResourceKey, quantity: Int): String =
    Resources.plural.get(key, DefaultQualifiersProvider.current, quantity, Ordinals)

internal inline fun pluralStringResource(
  key: PluralResourceKey,
  quantity: IFixedDecimal,
  vararg formatArgs: Any,
): String = Resources.plural.get(key, DefaultQualifiersProvider.current, quantity, Plurals,
    *formatArgs)

internal inline fun pluralStringResource(key: PluralResourceKey, quantity: IFixedDecimal): String =
    Resources.plural.get(key, DefaultQualifiersProvider.current, quantity, Plurals)

internal inline fun ordinalStringResource(
  key: PluralResourceKey,
  quantity: IFixedDecimal,
  vararg formatArgs: Any,
): String = Resources.plural.get(key, DefaultQualifiersProvider.current, quantity, Ordinals,
    *formatArgs)

internal inline fun ordinalStringResource(key: PluralResourceKey, quantity: IFixedDecimal): String =
    Resources.plural.get(key, DefaultQualifiersProvider.current, quantity, Ordinals)

internal inline fun StringResourceKey.`get`(): String = Resources.string.get(this,
    DefaultQualifiersProvider.current)

internal inline fun StringResourceKey.`get`(vararg formatArgs: Any): String =
    Resources.string.get(this, DefaultQualifiersProvider.current, *formatArgs)

internal inline fun PainterResourceKey.getByteArray(): ByteArray = Resources.binaryByteArray(this,
    DefaultQualifiersProvider.current)

internal inline fun AssetResourceKey.getAssetPath(): String = Resources.binary.get(this,
    DefaultQualifiersProvider.current)

internal inline fun BinaryResourceKey.`get`(): ByteArray = Resources.binaryByteArray(this,
    DefaultQualifiersProvider.current)

internal inline fun PluralResourceKey.`get`(quantity: Int, vararg formatArgs: Any): String =
    Resources.plural.get(this, DefaultQualifiersProvider.current, quantity, Plurals, *formatArgs)

internal inline fun PluralResourceKey.`get`(quantity: Int): String = Resources.plural.get(this,
    DefaultQualifiersProvider.current, quantity, Plurals)

internal inline fun PluralResourceKey.getOrdinal(quantity: Int, vararg formatArgs: Any): String =
    Resources.plural.get(this, DefaultQualifiersProvider.current, quantity, Ordinals, *formatArgs)

internal inline fun PluralResourceKey.getOrdinal(quantity: Int): String = Resources.plural.get(this,
    DefaultQualifiersProvider.current, quantity, Ordinals)

internal inline fun PluralResourceKey.`get`(quantity: IFixedDecimal, vararg formatArgs: Any): String
    = Resources.plural.get(this, DefaultQualifiersProvider.current, quantity, Plurals, *formatArgs)

internal inline fun PluralResourceKey.`get`(quantity: IFixedDecimal): String =
    Resources.plural.get(this, DefaultQualifiersProvider.current, quantity, Plurals)

internal inline fun PluralResourceKey.getOrdinal(quantity: IFixedDecimal, vararg formatArgs: Any):
    String = Resources.plural.get(this, DefaultQualifiersProvider.current, quantity, Ordinals,
    *formatArgs)

internal inline fun PluralResourceKey.getOrdinal(quantity: IFixedDecimal): String =
    Resources.plural.get(this, DefaultQualifiersProvider.current, quantity, Ordinals)
