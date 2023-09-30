package com.jibru.kostra.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.jibru.kostra.KQualifiers
import com.jibru.kostra.KResources
import com.jibru.kostra.PainterResourceKey

@Composable
actual fun KResources.painter(key: PainterResourceKey, qualifiers: KQualifiers): Painter = composePainter(key, qualifiers)
