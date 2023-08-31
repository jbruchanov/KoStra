package com.jibru.kostra.internal

import com.jibru.kostra.Dpi
import com.jibru.kostra.Fixtures
import com.jibru.kostra.Fixtures.Resources.K
import com.jibru.kostra.MissingResourceException
import com.jibru.kostra.Qualifiers
import kotlin.test.assertEquals
import kotlin.test.Test
import kotlin.test.assertFailsWith

class FileDatabaseTest {

    private val db = Fixtures.Resources.drawableResources.binary

    @Test
    fun get() {
        assertEquals("undefinedDpiDefault", db.get(K.drawable.undefinedDpi, Qualifiers.Undefined))
        assertEquals("undefinedDpiDefault", db.get(K.drawable.undefinedDpi, Qualifiers("en-GB", Dpi.XXXHDPI)))

        assertEquals("xxHdpiOnly", db.get(K.drawable.xxHdpiOnly, Qualifiers("en-GB", Dpi.XXHDPI)))
        assertEquals("xxHdpiOnly", db.get(K.drawable.xxHdpiOnly, Qualifiers("en-US", Dpi.XXHDPI)))
        assertEquals("xxHdpiOnly", db.get(K.drawable.xxHdpiOnly, Qualifiers("en", Dpi.XXHDPI)))
        assertEquals("xxHdpiOnly", db.get(K.drawable.xxHdpiOnly, Qualifiers("cs-CZ", Dpi.XXHDPI)))
        assertEquals("xxHdpiOnly", db.get(K.drawable.xxHdpiOnly, Qualifiers("cs", Dpi.XXHDPI)))
        assertEquals("xxHdpiOnly", db.get(K.drawable.xxHdpiOnly, Qualifiers("", Dpi.XXHDPI)))

        assertFailsWith<MissingResourceException> { db.get(K.drawable.xxHdpiOnly, Qualifiers("en-GB", Dpi.MDPI)) }
        assertFailsWith<MissingResourceException> { db.get(K.drawable.xxHdpiOnly, Qualifiers("en", Dpi.MDPI)) }
        assertFailsWith<MissingResourceException> { db.get(K.drawable.xxHdpiOnly, Qualifiers("", Dpi.MDPI)) }

        assertEquals("multipleDpiDefault", db.get(K.drawable.multipleDpi, Qualifiers.Undefined))
        assertEquals("multipleDpiDefault", db.get(K.drawable.multipleDpi, Qualifiers("", Dpi.MDPI)))
        assertEquals("multipleDpiXXHDPI", db.get(K.drawable.multipleDpi, Qualifiers("en", Dpi.XXHDPI)))
        assertEquals("multipleDpiXXXHDPI", db.get(K.drawable.multipleDpi, Qualifiers("en-GB", Dpi.XXXHDPI)))

        assertEquals("multipleDpiKLocaleDefault", db.get(K.drawable.multipleDpiLocale, Qualifiers.Undefined))
        assertEquals("multipleDpiKLocaleEnXXHDPI", db.get(K.drawable.multipleDpiLocale, Qualifiers("en-US", dpi = Dpi.XXHDPI)))
        assertEquals("multipleDpiKLocaleEnGBUndefined", db.get(K.drawable.multipleDpiLocale, Qualifiers("en-GB", dpi = Dpi.XXHDPI)))
        assertEquals("multipleDpiKLocaleEnXXHDPI", db.get(K.drawable.multipleDpiLocale, Qualifiers("en", dpi = Dpi.XXHDPI)))
        assertEquals("multipleDpiKLocaleXXXHDPI", db.get(K.drawable.multipleDpiLocale, Qualifiers("en-US", dpi = Dpi.XXXHDPI)))
        assertEquals("multipleDpiKLocaleXXXHDPI", db.get(K.drawable.multipleDpiLocale, Qualifiers("en", dpi = Dpi.XXXHDPI)))

        assertEquals("multipleDpiKLocaleDefault", db.get(K.drawable.multipleDpiLocale, Qualifiers("cs-CZ", dpi = Dpi.XXHDPI)))
        assertEquals("multipleDpiKLocaleXXXHDPI", db.get(K.drawable.multipleDpiLocale, Qualifiers("cs", dpi = Dpi.XXXHDPI)))
    }
}
