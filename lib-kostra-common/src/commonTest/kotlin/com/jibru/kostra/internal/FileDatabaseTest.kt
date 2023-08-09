package com.jibru.kostra.internal

import com.jibru.kostra.Fixtures
import com.jibru.kostra.Fixtures.Resources.K
import com.jibru.kostra.MissingResourceException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
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

        assertEquals("multipleDpiLocaleDefault", db.get(K.drawable.multipleDpiLocale, Qualifiers.Undefined))
        assertEquals("multipleDpiLocaleEnXXHDPI", db.get(K.drawable.multipleDpiLocale, Qualifiers("en-US", dpi = Dpi.XXHDPI)))
        assertEquals("multipleDpiLocaleEnGBUndefined", db.get(K.drawable.multipleDpiLocale, Qualifiers("en-GB", dpi = Dpi.XXHDPI)))
        assertEquals("multipleDpiLocaleEnXXHDPI", db.get(K.drawable.multipleDpiLocale, Qualifiers("en", dpi = Dpi.XXHDPI)))
        assertEquals("multipleDpiLocaleXXXHDPI", db.get(K.drawable.multipleDpiLocale, Qualifiers("en-US", dpi = Dpi.XXXHDPI)))
        assertEquals("multipleDpiLocaleXXXHDPI", db.get(K.drawable.multipleDpiLocale, Qualifiers("en", dpi = Dpi.XXXHDPI)))

        assertEquals("multipleDpiLocaleDefault", db.get(K.drawable.multipleDpiLocale, Qualifiers("cs-CZ", dpi = Dpi.XXHDPI)))
        assertEquals("multipleDpiLocaleXXXHDPI", db.get(K.drawable.multipleDpiLocale, Qualifiers("cs", dpi = Dpi.XXXHDPI)))
    }
}
