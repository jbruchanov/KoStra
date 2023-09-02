package com.jibru.kostra.compose

import androidx.compose.runtime.compositionLocalOf
import com.jibru.kostra.DefaultQualifiersProvider

val LocalQualifiers = compositionLocalOf { DefaultQualifiersProvider.get() }
