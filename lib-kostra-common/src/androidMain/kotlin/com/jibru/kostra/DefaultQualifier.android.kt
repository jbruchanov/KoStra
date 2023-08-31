package com.jibru.kostra

import android.content.res.Resources
import java.util.Locale as JvmLocale

actual fun defaultQualifiers(): KQualifiers = KQualifiers(
    locale = JvmLocale.getDefault().let { KLocale(it.language, it.country.takeIf { code -> code.isEmpty() || code.length == 2 }) },
    //taken from JVM LocalDensity
    dpi = KDpi.getClosest(Resources.getSystem().displayMetrics.density),
)
