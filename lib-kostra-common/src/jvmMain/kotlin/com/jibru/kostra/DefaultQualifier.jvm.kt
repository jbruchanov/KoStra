package com.jibru.kostra

import java.awt.GraphicsEnvironment
import java.util.Locale as JvmLocale

actual fun defaultQualifiers(): KQualifiers {
    val kQualifiers = KQualifiers(
        locale = KLocale(JvmLocale.getDefault().toLanguageTag()),
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
    return kQualifiers
}
