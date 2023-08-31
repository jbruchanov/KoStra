package com.jibru.kostra.internal

import com.jibru.kostra.KDpi
import com.jibru.kostra.Fixtures
import com.jibru.kostra.Fixtures.Resources.K
import com.jibru.kostra.MissingResourceException
import com.jibru.kostra.KQualifiers
import kotlin.test.assertEquals
import kotlin.test.Test
import kotlin.test.assertFailsWith

class FileDatabaseTest {

    private val db = Fixtures.Resources.drawableResources.binary

    @Test
    fun get() {
        assertEquals("undefinedDpiDefault", db.get(K.drawable.undefinedDpi, KQualifiers.Undefined))
        assertEquals("undefinedDpiDefault", db.get(K.drawable.undefinedDpi, KQualifiers("en-GB", KDpi.XXXHDPI)))

        assertEquals("xxHdpiOnly", db.get(K.drawable.xxHdpiOnly, KQualifiers("en-GB", KDpi.XXHDPI)))
        assertEquals("xxHdpiOnly", db.get(K.drawable.xxHdpiOnly, KQualifiers("en-US", KDpi.XXHDPI)))
        assertEquals("xxHdpiOnly", db.get(K.drawable.xxHdpiOnly, KQualifiers("en", KDpi.XXHDPI)))
        assertEquals("xxHdpiOnly", db.get(K.drawable.xxHdpiOnly, KQualifiers("cs-CZ", KDpi.XXHDPI)))
        assertEquals("xxHdpiOnly", db.get(K.drawable.xxHdpiOnly, KQualifiers("cs", KDpi.XXHDPI)))
        assertEquals("xxHdpiOnly", db.get(K.drawable.xxHdpiOnly, KQualifiers("", KDpi.XXHDPI)))

        assertFailsWith<MissingResourceException> { db.get(K.drawable.xxHdpiOnly, KQualifiers("en-GB", KDpi.MDPI)) }
        assertFailsWith<MissingResourceException> { db.get(K.drawable.xxHdpiOnly, KQualifiers("en", KDpi.MDPI)) }
        assertFailsWith<MissingResourceException> { db.get(K.drawable.xxHdpiOnly, KQualifiers("", KDpi.MDPI)) }

        assertEquals("multipleDpiDefault", db.get(K.drawable.multipleDpi, KQualifiers.Undefined))
        assertEquals("multipleDpiDefault", db.get(K.drawable.multipleDpi, KQualifiers("", KDpi.MDPI)))
        assertEquals("multipleDpiXXHDPI", db.get(K.drawable.multipleDpi, KQualifiers("en", KDpi.XXHDPI)))
        assertEquals("multipleDpiXXXHDPI", db.get(K.drawable.multipleDpi, KQualifiers("en-GB", KDpi.XXXHDPI)))

        assertEquals("multipleDpiKLocaleDefault", db.get(K.drawable.multipleDpiLocale, KQualifiers.Undefined))
        assertEquals("multipleDpiKLocaleEnXXHDPI", db.get(K.drawable.multipleDpiLocale, KQualifiers("en-US", dpi = KDpi.XXHDPI)))
        assertEquals("multipleDpiKLocaleEnGBUndefined", db.get(K.drawable.multipleDpiLocale, KQualifiers("en-GB", dpi = KDpi.XXHDPI)))
        assertEquals("multipleDpiKLocaleEnXXHDPI", db.get(K.drawable.multipleDpiLocale, KQualifiers("en", dpi = KDpi.XXHDPI)))
        assertEquals("multipleDpiKLocaleXXXHDPI", db.get(K.drawable.multipleDpiLocale, KQualifiers("en-US", dpi = KDpi.XXXHDPI)))
        assertEquals("multipleDpiKLocaleXXXHDPI", db.get(K.drawable.multipleDpiLocale, KQualifiers("en", dpi = KDpi.XXXHDPI)))

        assertEquals("multipleDpiKLocaleDefault", db.get(K.drawable.multipleDpiLocale, KQualifiers("cs-CZ", dpi = KDpi.XXHDPI)))
        assertEquals("multipleDpiKLocaleXXXHDPI", db.get(K.drawable.multipleDpiLocale, KQualifiers("cs", dpi = KDpi.XXXHDPI)))
    }
}
