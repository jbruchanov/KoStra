package com.jibru.kostra.plugin.ext

import com.jibru.kostra.KLocale

internal fun KLocale.formattedDbKey() =
    key.toString().padStart(8, '0').windowed(2, 2).joinToString("_").trimStart('0')
