package com.jibru.kostra.compose

import androidx.compose.runtime.compositionLocalOf
import com.jibru.kostra.defaultQualifiers

val LocalQualifiers = compositionLocalOf { defaultQualifiers() }
