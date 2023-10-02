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

public inline fun stringResource(key: StringResourceKey, vararg formatArgs: Any): String =
    Resources.string.get(key, DefaultQualifiersProvider.current, *formatArgs)

public inline fun painterResource(key: PainterResourceKey): ByteArray =
    Resources.binaryByteArray(key, DefaultQualifiersProvider.current)

public inline fun assetPath(key: AssetResourceKey): String = Resources.binary.get(key,
    DefaultQualifiersProvider.current)

public inline fun binaryResource(key: AssetResourceKey): ByteArray = Resources.binaryByteArray(key,
    DefaultQualifiersProvider.current)

public inline fun pluralResource(
  key: PluralResourceKey,
  quantity: Int,
  vararg formatArgs: Any,
): String = Resources.plural.get(key, DefaultQualifiersProvider.current, quantity, Plurals,
    *formatArgs)

public inline fun ordinalResource(
  key: PluralResourceKey,
  quantity: Int,
  vararg formatArgs: Any,
): String = Resources.plural.get(key, DefaultQualifiersProvider.current, quantity, Ordinals,
    *formatArgs)

public inline fun pluralResource(
  key: PluralResourceKey,
  quantity: IFixedDecimal,
  vararg formatArgs: Any,
): String = Resources.plural.get(key, DefaultQualifiersProvider.current, quantity, Plurals,
    *formatArgs)

public inline fun ordinalResource(
  key: PluralResourceKey,
  quantity: IFixedDecimal,
  vararg formatArgs: Any,
): String = Resources.plural.get(key, DefaultQualifiersProvider.current, quantity, Ordinals,
    *formatArgs)
