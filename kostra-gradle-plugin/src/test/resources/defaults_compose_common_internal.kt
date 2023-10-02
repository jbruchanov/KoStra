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
internal inline fun stringResource(key: StringResourceKey, vararg formatArgs: Any): String =
    Resources.string.get(key, LocalQualifiers.current, *formatArgs)

@Composable
internal inline fun painterResource(key: PainterResourceKey): Painter = Resources.painter(key,
    LocalQualifiers.current)

@Composable
internal inline fun assetPath(key: AssetResourceKey): String = Resources.binary.get(key,
    LocalQualifiers.current)

@Composable
internal inline fun binaryResource(key: AssetResourceKey): ByteArray =
    Resources.binaryByteArray(key, LocalQualifiers.current)

@Composable
internal inline fun pluralResource(
  key: PluralResourceKey,
  quantity: Int,
  vararg formatArgs: Any,
): String = Resources.plural.get(key, LocalQualifiers.current, quantity, Plurals, *formatArgs)

@Composable
internal inline fun ordinalResource(
  key: PluralResourceKey,
  quantity: Int,
  vararg formatArgs: Any,
): String = Resources.plural.get(key, LocalQualifiers.current, quantity, Ordinals, *formatArgs)

@Composable
internal inline fun pluralResource(
  key: PluralResourceKey,
  quantity: IFixedDecimal,
  vararg formatArgs: Any,
): String = Resources.plural.get(key, LocalQualifiers.current, quantity, Plurals, *formatArgs)

@Composable
internal inline fun ordinalResource(
  key: PluralResourceKey,
  quantity: IFixedDecimal,
  vararg formatArgs: Any,
): String = Resources.plural.get(key, LocalQualifiers.current, quantity, Ordinals, *formatArgs)
