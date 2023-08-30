package com.jibru.kostra.plugin.ext

import com.jibru.kostra.Locale

internal fun Locale.formattedDbKey() =
    key.toString().padStart(8, '0').windowed(2, 2).joinToString("_").trimStart('0')
