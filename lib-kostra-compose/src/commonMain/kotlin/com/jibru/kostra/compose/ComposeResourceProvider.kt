@file:OptIn(ExperimentalResourceApi::class)
@file:Suppress("unused")

package com.jibru.kostra.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.jibru.kostra.KQualifiers
import com.jibru.kostra.KResources
import com.jibru.kostra.PainterResourceKey
import com.jibru.kostra.assetPath
import org.jetbrains.compose.resources.ExperimentalResourceApi

@Composable
internal fun KResources.composePainter(key: PainterResourceKey, qualifiers: KQualifiers): Painter {
    val path = assetPath(key, qualifiers)
    require(!path.endsWith(".svg", ignoreCase = true)) { "Unsupported SVG on current platform, key:$key, asset:'$path'" }
    return org.jetbrains.compose.resources.painterResource(path)
}

@Composable
expect fun KResources.painter(key: PainterResourceKey, qualifiers: KQualifiers): Painter
