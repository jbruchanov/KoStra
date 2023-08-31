package com.jibru.kostra

import java.awt.GraphicsEnvironment
import java.util.Locale as JvmLocale

actual fun defaultQualifiers(): KQualifiers = KQualifiers(
    locale = JvmLocale.getDefault().let { KLocale(it.language, it.country.takeIf { code -> code.isEmpty() || code.length == 2 }) },
    //taken from JVM LocalDensity
    dpi = GraphicsEnvironment.getLocalGraphicsEnvironment()
        ?.takeIf { !it.isHeadlessInstance }
        ?.defaultScreenDevice
        ?.defaultConfiguration
        ?.defaultTransform
        ?.scaleX?.toFloat()
        ?.let { KDpi.getClosest(it) }
        ?: KDpi.Undefined,
)
