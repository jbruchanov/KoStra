package com.jibru.kostra

import android.content.res.Resources
import java.util.Locale as JvmLocale

actual fun defaultQualifiers(): Qualifiers = Qualifiers(
    locale = JvmLocale.getDefault().let { KLocale(it.language, it.country.takeIf { code -> code.isEmpty() || code.length == 2 }) },
    //taken from JVM LocalDensity
    dpi = Dpi.getClosest(Resources.getSystem().displayMetrics.density),
)
