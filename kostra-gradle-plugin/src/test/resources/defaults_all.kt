@file:Suppress("NOTHING_TO_INLINE", "ktlint")

package com.sample.app.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.jibru.kostra.Plurals.Type.Ordinals
import com.jibru.kostra.Plurals.Type.Plurals
import com.jibru.kostra.binaryByteArray
import com.jibru.kostra.compose.LocalQualifiers
import com.jibru.kostra.compose.painter
import com.jibru.kostra.icu.IFixedDecimal
import com.sample.app.AssetResourceKey
import com.sample.app.BinaryResourceKey
import com.sample.app.PainterResourceKey
import com.sample.app.PluralResourceKey
import com.sample.app.Resources
import com.sample.app.StringResourceKey
import kotlin.Any
import kotlin.ByteArray
import kotlin.Int
import kotlin.String
import kotlin.Suppress

@Composable
public inline fun stringResource(key: StringResourceKey, vararg formatArgs: Any): String =
    Resources.string.get(key, LocalQualifiers.current, *formatArgs)

@Composable
public inline fun painterResource(key: PainterResourceKey): Painter = Resources.painter(key,
    LocalQualifiers.current)

@Composable
public inline fun assetPath(key: AssetResourceKey): String = Resources.binary.get(key,
    LocalQualifiers.current)

@Composable
public inline fun binaryResource(key: AssetResourceKey): ByteArray = Resources.binaryByteArray(key,
    LocalQualifiers.current)

@Composable
public inline fun pluralResource(
  key: PluralResourceKey,
  quantity: Int,
  vararg formatArgs: Any,
): String = Resources.plural.get(key, LocalQualifiers.current, quantity, Plurals, *formatArgs)

@Composable
public inline fun ordinalResource(
  key: PluralResourceKey,
  quantity: Int,
  vararg formatArgs: Any,
): String = Resources.plural.get(key, LocalQualifiers.current, quantity, Ordinals, *formatArgs)

@Composable
public inline fun pluralResource(
  key: PluralResourceKey,
  quantity: IFixedDecimal,
  vararg formatArgs: Any,
): String = Resources.plural.get(key, LocalQualifiers.current, quantity, Plurals, *formatArgs)

@Composable
public inline fun ordinalResource(
  key: PluralResourceKey,
  quantity: IFixedDecimal,
  vararg formatArgs: Any,
): String = Resources.plural.get(key, LocalQualifiers.current, quantity, Ordinals, *formatArgs)

@Composable
public inline fun StringResourceKey.`get`(vararg formatArgs: Any): String =
    Resources.string.get(this, LocalQualifiers.current, *formatArgs)

@Composable
public inline fun PainterResourceKey.`get`(): Painter = Resources.painter(this,
    LocalQualifiers.current)

@Composable
public inline fun AssetResourceKey.getAssetPath(): String = Resources.binary.get(this,
    LocalQualifiers.current)

@Composable
public inline fun BinaryResourceKey.`get`(): ByteArray = Resources.binaryByteArray(this,
    LocalQualifiers.current)

@Composable
public inline fun PluralResourceKey.`get`(quantity: Int, vararg formatArgs: Any): String =
    Resources.plural.get(this, LocalQualifiers.current, quantity, Plurals, *formatArgs)

@Composable
public inline fun PluralResourceKey.getOrdinal(quantity: Int, vararg formatArgs: Any): String =
    Resources.plural.get(this, LocalQualifiers.current, quantity, Ordinals, *formatArgs)

@Composable
public inline fun PluralResourceKey.`get`(quantity: IFixedDecimal, vararg formatArgs: Any): String =
    Resources.plural.get(this, LocalQualifiers.current, quantity, Plurals, *formatArgs)

@Composable
public inline fun PluralResourceKey.getOrdinal(quantity: IFixedDecimal, vararg formatArgs: Any):
    String = Resources.plural.get(this, LocalQualifiers.current, quantity, Ordinals, *formatArgs)

----
@file:Suppress("NOTHING_TO_INLINE", "ktlint")

package com.sample.app

import com.jibru.kostra.DefaultQualifiersProvider
import com.jibru.kostra.KQualifiers
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

public inline fun StringResourceKey.`get`(vararg formatArgs: Any): String =
    Resources.string.get(this, DefaultQualifiersProvider.current, *formatArgs)

public inline fun PainterResourceKey.getByteArray(): ByteArray = Resources.binaryByteArray(this,
    DefaultQualifiersProvider.current)

public inline fun AssetResourceKey.getAssetPath(): String = Resources.binary.get(this,
    DefaultQualifiersProvider.current)

public inline fun BinaryResourceKey.`get`(): ByteArray = Resources.binaryByteArray(this,
    DefaultQualifiersProvider.current)

public inline fun PluralResourceKey.`get`(quantity: Int, vararg formatArgs: Any): String =
    Resources.plural.get(this, DefaultQualifiersProvider.current, quantity, Plurals, *formatArgs)

public inline fun PluralResourceKey.getOrdinal(quantity: Int, vararg formatArgs: Any): String =
    Resources.plural.get(this, DefaultQualifiersProvider.current, quantity, Ordinals, *formatArgs)

public inline fun PluralResourceKey.`get`(quantity: IFixedDecimal, vararg formatArgs: Any): String =
    Resources.plural.get(this, DefaultQualifiersProvider.current, quantity, Plurals, *formatArgs)

public inline fun PluralResourceKey.getOrdinal(quantity: IFixedDecimal, vararg formatArgs: Any):
    String = Resources.plural.get(this, DefaultQualifiersProvider.current, quantity, Ordinals,
    *formatArgs)

public inline fun StringResourceKey.`get`(qualifiers: KQualifiers, vararg formatArgs: Any): String =
    Resources.string.get(this, qualifiers, *formatArgs)

public inline fun PainterResourceKey.getByteArray(qualifiers: KQualifiers): ByteArray =
    Resources.binaryByteArray(this, qualifiers)

public inline fun AssetResourceKey.getAssetPath(qualifiers: KQualifiers): String =
    Resources.binary.get(this, qualifiers)

public inline fun BinaryResourceKey.`get`(qualifiers: KQualifiers): ByteArray =
    Resources.binaryByteArray(this, qualifiers)

public inline fun PluralResourceKey.`get`(
  qualifiers: KQualifiers,
  quantity: Int,
  vararg formatArgs: Any,
): String = Resources.plural.get(this, qualifiers, quantity, Plurals, *formatArgs)

public inline fun PluralResourceKey.getOrdinal(
  qualifiers: KQualifiers,
  quantity: Int,
  vararg formatArgs: Any,
): String = Resources.plural.get(this, qualifiers, quantity, Ordinals, *formatArgs)

public inline fun PluralResourceKey.`get`(
  qualifiers: KQualifiers,
  quantity: IFixedDecimal,
  vararg formatArgs: Any,
): String = Resources.plural.get(this, qualifiers, quantity, Plurals, *formatArgs)

public inline fun PluralResourceKey.getOrdinal(
  qualifiers: KQualifiers,
  quantity: IFixedDecimal,
  vararg formatArgs: Any,
): String = Resources.plural.get(this, qualifiers, quantity, Ordinals, *formatArgs)
