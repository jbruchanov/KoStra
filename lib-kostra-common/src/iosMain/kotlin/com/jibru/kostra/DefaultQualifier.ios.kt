package com.jibru.kostra

import platform.Foundation.NSLocale
import platform.Foundation.countryCode
import platform.Foundation.currentLocale
import platform.Foundation.languageCode
import platform.UIKit.UIScreen

actual fun defaultQualifiers(): KQualifiers = KQualifiers(
    locale = NSLocale.currentLocale.let { l -> KLocale(l.languageCode, l.countryCode) },
    dpi = KDpi.getClosest(UIScreen.mainScreen.scale.toFloat()),
)
