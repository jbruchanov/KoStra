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
public inline fun StringResourceKey.`get`(): String = Resources.string.get(this,
    LocalQualifiers.current)

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
public inline fun PluralResourceKey.`get`(quantity: Int): String = Resources.plural.get(this,
    LocalQualifiers.current, quantity, Plurals)

@Composable
public inline fun PluralResourceKey.getOrdinal(quantity: Int, vararg formatArgs: Any): String =
    Resources.plural.get(this, LocalQualifiers.current, quantity, Ordinals, *formatArgs)

@Composable
public inline fun PluralResourceKey.getOrdinal(quantity: Int): String = Resources.plural.get(this,
    LocalQualifiers.current, quantity, Ordinals)

@Composable
public inline fun PluralResourceKey.`get`(quantity: IFixedDecimal, vararg formatArgs: Any): String =
    Resources.plural.get(this, LocalQualifiers.current, quantity, Plurals, *formatArgs)

@Composable
public inline fun PluralResourceKey.`get`(quantity: IFixedDecimal): String =
    Resources.plural.get(this, LocalQualifiers.current, quantity, Plurals)

@Composable
public inline fun PluralResourceKey.getOrdinal(quantity: IFixedDecimal, vararg formatArgs: Any):
    String = Resources.plural.get(this, LocalQualifiers.current, quantity, Ordinals, *formatArgs)

@Composable
public inline fun PluralResourceKey.getOrdinal(quantity: IFixedDecimal): String =
    Resources.plural.get(this, LocalQualifiers.current, quantity, Ordinals)
