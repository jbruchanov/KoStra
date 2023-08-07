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
}
