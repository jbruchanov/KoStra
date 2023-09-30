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
public inline fun stringResource(key: StringResourceKey): String = Resources.string.get(key,
    LocalQualifiers.current)

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
public inline fun pluralStringResource(key: PluralResourceKey, quantity: IFixedDecimal): String =
    Resources.plural.get(key, LocalQualifiers.current, quantity, Plurals)

@Composable
public inline fun pluralStringResource(key: PluralResourceKey, quantity: Int): String =
    Resources.plural.get(key, LocalQualifiers.current, quantity, Plurals)

@Composable
public inline fun pluralStringResource(
  key: PluralResourceKey,
  quantity: IFixedDecimal,
  vararg formatArgs: Any,
): String = Resources.plural.get(key, LocalQualifiers.current, quantity, Plurals, *formatArgs)

@Composable
public inline fun pluralStringResource(
  key: PluralResourceKey,
  quantity: Int,
  vararg formatArgs: Any,
): String = Resources.plural.get(key, LocalQualifiers.current, quantity, Plurals, *formatArgs)

@Composable
public inline fun ordinalStringResource(key: PluralResourceKey, quantity: IFixedDecimal): String =
    Resources.plural.get(key, LocalQualifiers.current, quantity, Ordinals)

@Composable
public inline fun ordinalStringResource(key: PluralResourceKey, quantity: Int): String =
    Resources.plural.get(key, LocalQualifiers.current, quantity, Ordinals)

@Composable
public inline fun ordinalStringResource(
  key: PluralResourceKey,
  quantity: IFixedDecimal,
  vararg formatArgs: Any,
): String = Resources.plural.get(key, LocalQualifiers.current, quantity, Ordinals, *formatArgs)

@Composable
public inline fun ordinalStringResource(
  key: PluralResourceKey,
  quantity: Int,
  vararg formatArgs: Any,
): String = Resources.plural.get(key, LocalQualifiers.current, quantity, Ordinals, *formatArgs)
