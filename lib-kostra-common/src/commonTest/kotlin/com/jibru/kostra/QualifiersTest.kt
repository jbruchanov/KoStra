package com.jibru.kostra

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class QualifiersTest {

    @Test
    fun pack() {
        assertEquals(KLocale("cs"), Qualifiers("cs").locale)
        assertEquals(KLocale("cs", "cz"), Qualifiers("cs-CZ").locale)
        assertEquals(KLocale("cs"), Qualifiers("cs", Dpi.XXHDPI).locale)
        assertEquals(KLocale("cs", "cz"), Qualifiers("cs-CZ", Dpi.NoDpi).locale)

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
        assertEquals(Qualifiers(KLocale.Undefined, dpi = Dpi.XXHDPI), Qualifiers(KLocale.Undefined, dpi = Dpi.XXHDPI).withNoLocaleRegion())

        assertEquals(Qualifiers("en", dpi = Dpi.Undefined), Qualifiers("en-GB", dpi = Dpi.Undefined).withNoLocaleRegion())
        assertEquals(Qualifiers("en", dpi = Dpi.Undefined), Qualifiers("en", dpi = Dpi.Undefined).withNoLocaleRegion())
        assertEquals(Qualifiers(KLocale.Undefined, dpi = Dpi.Undefined), Qualifiers(KLocale.Undefined, dpi = Dpi.Undefined).withNoLocaleRegion())
    }

    @Test
    fun withNoLocale() {
        assertEquals(Qualifiers(dpi = Dpi.XXHDPI), Qualifiers("en-GB", dpi = Dpi.XXHDPI).withNoLocale())
        assertEquals(Qualifiers(dpi = Dpi.XXHDPI), Qualifiers("en", dpi = Dpi.XXHDPI).withNoLocale())
        assertEquals(Qualifiers(dpi = Dpi.XXHDPI), Qualifiers(KLocale.Undefined, dpi = Dpi.XXHDPI).withNoLocale())

        assertEquals(Qualifiers(dpi = Dpi.Undefined), Qualifiers("en-GB", dpi = Dpi.Undefined).withNoLocale())
        assertEquals(Qualifiers(dpi = Dpi.Undefined), Qualifiers("en", dpi = Dpi.Undefined).withNoLocale())
        assertEquals(Qualifiers(dpi = Dpi.Undefined), Qualifiers(KLocale.Undefined, dpi = Dpi.Undefined).withNoLocale())
    }

    @Test
    fun withNoDpi() {
        assertEquals(Qualifiers("en-GB", dpi = Dpi.Undefined), Qualifiers("en-GB", dpi = Dpi.XXHDPI).withNoDpi())
        assertEquals(Qualifiers("en", dpi = Dpi.Undefined), Qualifiers("en", dpi = Dpi.XXHDPI).withNoDpi())
        assertEquals(Qualifiers(KLocale.Undefined, dpi = Dpi.Undefined), Qualifiers(KLocale.Undefined, dpi = Dpi.XXHDPI).withNoDpi())

        assertEquals(Qualifiers("en-GB", dpi = Dpi.Undefined), Qualifiers("en-GB", dpi = Dpi.Undefined).withNoDpi())
        assertEquals(Qualifiers("en", dpi = Dpi.Undefined), Qualifiers("en", dpi = Dpi.Undefined).withNoDpi())
        assertEquals(Qualifiers(KLocale.Undefined, dpi = Dpi.Undefined), Qualifiers(KLocale.Undefined, dpi = Dpi.Undefined).withNoDpi())
    }
}
