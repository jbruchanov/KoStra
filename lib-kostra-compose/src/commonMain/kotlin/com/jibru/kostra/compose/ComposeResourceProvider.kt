@file:OptIn(InternalResourceApi::class)
@file:Suppress("unused")

package com.jibru.kostra.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.jibru.kostra.KQualifiers
import com.jibru.kostra.KResources
import com.jibru.kostra.PainterResourceKey
import com.jibru.kostra.assetPath
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.ResourceItem
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.vectorResource

@Composable
internal fun KResources.composePainter(key: PainterResourceKey, qualifiers: KQualifiers): Painter {
    val path = assetPath(key, qualifiers)
    require(!path.endsWith(".svg", ignoreCase = true)) { "Unsupported SVG on current platform, key:$key, asset:'$path'" }
    val isXml = path.endsWith(".xml") || path.endsWith(".vxml")
    //imageResources is caching stuff internally
    //it's a copy of org.jetbrains.compose.resources.painterResource, just using also vxml for the vector drawables
    return if (isXml) {
        rememberVectorPainter(vectorResource(path.toDrawableResource()))
    } else {
        val imageResource = imageResource(path.toDrawableResource())
        remember(imageResource) { BitmapPainter(imageResource) }
    }
}

private fun String.toDrawableResource() = DrawableResource(this, setOf(ResourceItem(path = this, qualifiers = emptySet(), offset = 0L, size = -1)))

@Composable
expect fun KResources.painter(key: PainterResourceKey, qualifiers: KQualifiers): Painter
