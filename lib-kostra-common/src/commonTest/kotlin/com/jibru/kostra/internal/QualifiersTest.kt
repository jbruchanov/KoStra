package com.jibru.kostra.internal

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class QualifiersTest {

    @Test
    fun pack() {
        assertEquals(Locale("cs"), Qualifiers("cs").locale)
        assertEquals(Locale("cs", "cz"), Qualifiers("cs-CZ").locale)
        assertEquals(Locale("cs"), Qualifiers("cs", Dpi.XXHDPI).locale)
        assertEquals(Locale("cs", "cz"), Qualifiers("cs-CZ", Dpi.NoDpi).locale)

        assertEquals(Dpi.Undefined, Qualifiers("cs").dpi)
        assertEquals(Dpi.Undefined, Qualifiers("cs-CZ").dpi)
        assertEquals(Dpi.XXHDPI, Qualifiers("cs", Dpi.XXHDPI).dpi)
        assertEquals(Dpi.NoDpi, Qualifiers("cs-CZ", Dpi.NoDpi).dpi)
    }

    @Test
    fun hasOnlyLocale() {
        assertTrue(Qualifiers("en").hasOnlyLocale)
        assertTrue(Qualifiers("en-GB").hasOnlyLocale)
        assertFalse(Qualifiers("en-GB", Dpi.XXHDPI).hasOnlyLocale)
        Dpi.values().forEach {
            val expected = it == Dpi.Undefined
            assertEquals(expected, Qualifiers("en", it).hasOnlyLocale)
        }
    }

    @Test
    fun withNoLocaleRegion() {
        assertEquals(Qualifiers("en", dpi = Dpi.XXHDPI), Qualifiers("en-GB", dpi = Dpi.XXHDPI).withNoLocaleRegion())
        assertEquals(Qualifiers("en", dpi = Dpi.XXHDPI), Qualifiers("en", dpi = Dpi.XXHDPI).withNoLocaleRegion())
        assertEquals(Qualifiers(Locale.Undefined, dpi = Dpi.XXHDPI), Qualifiers(Locale.Undefined, dpi = Dpi.XXHDPI).withNoLocaleRegion())

        assertEquals(Qualifiers("en", dpi = Dpi.Undefined), Qualifiers("en-GB", dpi = Dpi.Undefined).withNoLocaleRegion())
        assertEquals(Qualifiers("en", dpi = Dpi.Undefined), Qualifiers("en", dpi = Dpi.Undefined).withNoLocaleRegion())
        assertEquals(Qualifiers(Locale.Undefined, dpi = Dpi.Undefined), Qualifiers(Locale.Undefined, dpi = Dpi.Undefined).withNoLocaleRegion())
    }

    @Test
    fun withNoLocale() {
        assertEquals(Qualifiers(dpi = Dpi.XXHDPI), Qualifiers("en-GB", dpi = Dpi.XXHDPI).withNoLocale())
        assertEquals(Qualifiers(dpi = Dpi.XXHDPI), Qualifiers("en", dpi = Dpi.XXHDPI).withNoLocale())
        assertEquals(Qualifiers(dpi = Dpi.XXHDPI), Qualifiers(Locale.Undefined, dpi = Dpi.XXHDPI).withNoLocale())

        assertEquals(Qualifiers(dpi = Dpi.Undefined), Qualifiers("en-GB", dpi = Dpi.Undefined).withNoLocale())
        assertEquals(Qualifiers(dpi = Dpi.Undefined), Qualifiers("en", dpi = Dpi.Undefined).withNoLocale())
        assertEquals(Qualifiers(dpi = Dpi.Undefined), Qualifiers(Locale.Undefined, dpi = Dpi.Undefined).withNoLocale())
    }

    @Test
    fun withNoDpi() {
        assertEquals(Qualifiers("en-GB", dpi = Dpi.Undefined), Qualifiers("en-GB", dpi = Dpi.XXHDPI).withNoDpi())
        assertEquals(Qualifiers("en", dpi = Dpi.Undefined), Qualifiers("en", dpi = Dpi.XXHDPI).withNoDpi())
        assertEquals(Qualifiers(Locale.Undefined, dpi = Dpi.Undefined), Qualifiers(Locale.Undefined, dpi = Dpi.XXHDPI).withNoDpi())

        assertEquals(Qualifiers("en-GB", dpi = Dpi.Undefined), Qualifiers("en-GB", dpi = Dpi.Undefined).withNoDpi())
        assertEquals(Qualifiers("en", dpi = Dpi.Undefined), Qualifiers("en", dpi = Dpi.Undefined).withNoDpi())
        assertEquals(Qualifiers(Locale.Undefined, dpi = Dpi.Undefined), Qualifiers(Locale.Undefined, dpi = Dpi.Undefined).withNoDpi())
    }
}
