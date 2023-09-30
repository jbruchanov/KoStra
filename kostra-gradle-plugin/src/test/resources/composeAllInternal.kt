@file:Suppress("NOTHING_TO_INLINE", "ktlint")

package com.sample.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.jibru.kostra.Plurals.Type.Ordinals
import com.jibru.kostra.Plurals.Type.Plurals
import com.jibru.kostra.compose.LocalQualifiers
import com.jibru.kostra.compose.painter
import com.jibru.kostra.icu.IFixedDecimal
import kotlin.Any
import kotlin.Int
import kotlin.String
import kotlin.Suppress

@Composable
internal inline fun stringResource(key: StringResourceKey): String = Resources.string.get(key,
    LocalQualifiers.current)

@Composable
internal inline fun stringResource(key: StringResourceKey, vararg formatArgs: Any): String =
    Resources.string.get(key, LocalQualifiers.current, *formatArgs)

@Composable
internal inline fun painterResource(key: PainterResourceKey): Painter = Resources.painter(key,
    LocalQualifiers.current)

@Composable
internal inline fun assetPath(key: AssetResourceKey): String = Resources.binary.get(key,
    LocalQualifiers.current)

@Composable
internal inline fun StringResourceKey.`get`(): String = Resources.string.get(this,
    LocalQualifiers.current)

@Composable
internal inline fun StringResourceKey.`get`(vararg formatArgs: Any): String =
    Resources.string.get(this, LocalQualifiers.current, *formatArgs)

@Composable
internal inline fun PainterResourceKey.`get`(): Painter = Resources.painter(this,
    LocalQualifiers.current)

@Composable
internal inline fun AssetResourceKey.`get`(): String = Resources.binary.get(this,
    LocalQualifiers.current)

@Composable
internal inline fun pluralStringResource(key: PluralResourceKey, quantity: IFixedDecimal): String =
    Resources.plural.get(key, LocalQualifiers.current, quantity, Plurals)

@Composable
internal inline fun pluralStringResource(key: PluralResourceKey, quantity: Int): String =
    Resources.plural.get(key, LocalQualifiers.current, quantity, Plurals)

@Composable
internal inline fun pluralStringResource(
  key: PluralResourceKey,
  quantity: IFixedDecimal,
  vararg formatArgs: Any,
): String = Resources.plural.get(key, LocalQualifiers.current, quantity, Plurals, *formatArgs)

@Composable
internal inline fun pluralStringResource(
  key: PluralResourceKey,
  quantity: Int,
  vararg formatArgs: Any,
): String = Resources.plural.get(key, LocalQualifiers.current, quantity, Plurals, *formatArgs)

@Composable
internal inline fun PluralResourceKey.`get`(quantity: IFixedDecimal): String =
    Resources.plural.get(this, LocalQualifiers.current, quantity, Plurals)

@Composable
internal inline fun PluralResourceKey.`get`(quantity: Int): String = Resources.plural.get(this,
    LocalQualifiers.current, quantity, Plurals)

@Composable
internal inline fun PluralResourceKey.`get`(quantity: IFixedDecimal, vararg formatArgs: Any): String
    = Resources.plural.get(this, LocalQualifiers.current, quantity, Plurals, *formatArgs)

@Composable
internal inline fun PluralResourceKey.`get`(quantity: Int, vararg formatArgs: Any): String =
    Resources.plural.get(this, LocalQualifiers.current, quantity, Plurals, *formatArgs)

@Composable
internal inline fun ordinalStringResource(key: PluralResourceKey, quantity: IFixedDecimal): String =
    Resources.plural.get(key, LocalQualifiers.current, quantity, Ordinals)

@Composable
internal inline fun ordinalStringResource(key: PluralResourceKey, quantity: Int): String =
    Resources.plural.get(key, LocalQualifiers.current, quantity, Ordinals)

@Composable
internal inline fun ordinalStringResource(
  key: PluralResourceKey,
  quantity: IFixedDecimal,
  vararg formatArgs: Any,
): String = Resources.plural.get(key, LocalQualifiers.current, quantity, Ordinals, *formatArgs)

@Composable
internal inline fun ordinalStringResource(
  key: PluralResourceKey,
  quantity: Int,
  vararg formatArgs: Any,
): String = Resources.plural.get(key, LocalQualifiers.current, quantity, Ordinals, *formatArgs)

@Composable
internal inline fun PluralResourceKey.getOrdinal(quantity: IFixedDecimal): String =
    Resources.plural.get(this, LocalQualifiers.current, quantity, Ordinals)

@Composable
internal inline fun PluralResourceKey.getOrdinal(quantity: Int): String = Resources.plural.get(this,
    LocalQualifiers.current, quantity, Ordinals)

@Composable
internal inline fun PluralResourceKey.getOrdinal(quantity: IFixedDecimal, vararg formatArgs: Any):
    String = Resources.plural.get(this, LocalQualifiers.current, quantity, Ordinals, *formatArgs)

@Composable
internal inline fun PluralResourceKey.getOrdinal(quantity: Int, vararg formatArgs: Any): String =
    Resources.plural.get(this, LocalQualifiers.current, quantity, Ordinals, *formatArgs)
