package com.jibru.kostra.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.loadSvgPainter
import com.jibru.kostra.KQualifiers
import com.jibru.kostra.KResources
import com.jibru.kostra.PainterResourceKey
import com.jibru.kostra.binaryInputStream

@Composable
fun KResources.svgPainter(key: PainterResourceKey, qualifiers: KQualifiers = LocalQualifiers.current): Painter =
    loadSvgPainter(inputStream = binaryInputStream(key, qualifiers), density = LocalDensity.current)
