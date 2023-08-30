@file:OptIn(ExperimentalResourceApi::class)

package com.jibru.kostra.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.jibru.kostra.AssetResourceKey
import com.jibru.kostra.KostraResources
import com.jibru.kostra.PainterResourceKey
import com.jibru.kostra.PluralResourceKey
import com.jibru.kostra.Plurals
import com.jibru.kostra.Qualifiers
import com.jibru.kostra.StringResourceKey
import com.jibru.kostra.assetPath
import com.jibru.kostra.icu.IFixedDecimal
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.resource

@Composable
fun KostraResources.string(key: StringResourceKey): String =
    string.get(key, LocalQualifiers.current)

@Composable
fun KostraResources.string(key: StringResourceKey, vararg formatArgs: Any): String =
    string.get(key, LocalQualifiers.current, *formatArgs)

@Composable
fun KostraResources.plural(key: PluralResourceKey, quantity: Int): String =
    plural.get(key, LocalQualifiers.current, quantity, Plurals.Type.Plurals)

@Composable
fun KostraResources.plural(key: PluralResourceKey, quantity: IFixedDecimal): String =
    plural.get(key, LocalQualifiers.current, quantity, Plurals.Type.Plurals)

@Composable
fun KostraResources.plural(key: PluralResourceKey, quantity: Int, vararg formatArgs: Any): String =
    plural.get(key, LocalQualifiers.current, quantity, Plurals.Type.Plurals, *formatArgs)

@Composable
fun KostraResources.plural(key: PluralResourceKey, quantity: IFixedDecimal, vararg formatArgs: Any): String =
    plural.get(key, LocalQualifiers.current, quantity, Plurals.Type.Plurals, *formatArgs)

@Composable
fun KostraResources.ordinal(key: PluralResourceKey, quantity: Int): String =
    plural.get(key, LocalQualifiers.current, quantity, Plurals.Type.Ordinals)

@Composable
fun KostraResources.ordinal(key: PluralResourceKey, quantity: IFixedDecimal): String =
    plural.get(key, LocalQualifiers.current, quantity, Plurals.Type.Ordinals)

@Composable
fun KostraResources.ordinal(key: PluralResourceKey, quantity: Int, vararg formatArgs: Any): String =
    plural.get(key, LocalQualifiers.current, quantity, Plurals.Type.Ordinals, *formatArgs)

@Composable
fun KostraResources.ordinal(key: PluralResourceKey, quantity: IFixedDecimal, vararg formatArgs: Any): String =
    plural.get(key, LocalQualifiers.current, quantity, Plurals.Type.Ordinals, *formatArgs)

@Composable
fun KostraResources.painter(key: PainterResourceKey): Painter =
    androidx.compose.ui.res.painterResource(assetPath(key, LocalQualifiers.current))

@Composable
fun KostraResources.assetPath(key: AssetResourceKey, qualifiers: Qualifiers = LocalQualifiers.current): String =
    binary.get(key, qualifiers)

suspend fun KostraResources.binaryByteArray(key: AssetResourceKey, qualifiers: Qualifiers): ByteArray =
    resource(binary.get(key, qualifiers)).readBytes()
