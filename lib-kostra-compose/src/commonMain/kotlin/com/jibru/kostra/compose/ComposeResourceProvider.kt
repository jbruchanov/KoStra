@file:OptIn(ExperimentalResourceApi::class)
@file:Suppress("unused")

package com.jibru.kostra.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.jibru.kostra.KQualifiers
import com.jibru.kostra.KResources
import com.jibru.kostra.PainterResourceKey
import com.jibru.kostra.assetPath
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.vectorResource

@Composable
internal fun KResources.composePainter(key: PainterResourceKey, qualifiers: KQualifiers): Painter {
    val path = assetPath(key, qualifiers)
    require(!path.endsWith(".svg", ignoreCase = true)) { "Unsupported SVG on current platform, key:$key, asset:'$path'" }
    val isXml = path.endsWith(".xml") || path.endsWith(".vxml")
    //imageResources is caching stuff internally
    //it's a copy of org.jetbrains.compose.resources.painterResource, just using also vxml for the vector drawables
    if (isXml) {
        return rememberVectorPainter(vectorResource(DrawableResource(path)))
    } else {
        return BitmapPainter(imageResource(DrawableResource(path)))
    }
}

@Composable
expect fun KResources.painter(key: PainterResourceKey, qualifiers: KQualifiers): Painter
