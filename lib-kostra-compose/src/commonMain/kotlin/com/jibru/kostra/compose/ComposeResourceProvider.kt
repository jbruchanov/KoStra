@file:OptIn(ExperimentalResourceApi::class)

package com.jibru.kostra.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.jibru.kostra.AssetResourceKey
import com.jibru.kostra.DrawableResourceKey
import com.jibru.kostra.PluralResourceKey
import com.jibru.kostra.StringResourceKey
import com.jibru.kostra.assetPath
import com.jibru.kostra.internal.KostraResources
import com.jibru.kostra.internal.Qualifiers
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
    plural.get(key, LocalQualifiers.current, quantity.toFloat())

@Composable
fun KostraResources.plural(key: PluralResourceKey, quantity: Float): String =
    plural.get(key, LocalQualifiers.current, quantity)

@Composable
fun KostraResources.plural(key: PluralResourceKey, quantity: Int, vararg formatArgs: Any): String =
    plural.get(key, LocalQualifiers.current, quantity.toFloat(), *formatArgs)

@Composable
fun KostraResources.plural(key: PluralResourceKey, quantity: Float, vararg formatArgs: Any): String =
    plural.get(key, LocalQualifiers.current, quantity, *formatArgs)

@Composable
fun KostraResources.painter(key: DrawableResourceKey): Painter =
    androidx.compose.ui.res.painterResource(assetPath(key, LocalQualifiers.current))

@Composable
fun KostraResources.assetPath(key: AssetResourceKey, qualifiers: Qualifiers = LocalQualifiers.current): String =
    binary.get(key, qualifiers)

suspend fun KostraResources.binaryByteArray(key: AssetResourceKey, qualifiers: Qualifiers): ByteArray =
    resource(binary.get(key, qualifiers)).readBytes()
