package com.jibru.kostra.internal

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class LocaleTest {
    @Test
    fun pack() {
        assertEquals(0, Locale.Undefined.key)
        assertEquals(0, Locale("", null).key)
        assertNotEquals(0, Locale("en", null).key)
        assertNotEquals(0, Locale("aa", null).key)
        assertNotEquals(0, Locale("en", "").key)
        assertNotEquals(0, Locale("en", "US").key)
        assertNotEquals(0, Locale("AA", "ZZ").key)
        assertNotEquals(0, Locale("en-US").key)
        assertNotEquals(0, Locale("aa-AA").key)
        assertNotEquals(0, Locale("zz-ZZ").key)
        assertNotEquals(0, Locale("en-rUS").key)
        assertNotEquals(0, Locale("aa-rAA").key)
        assertNotEquals(0, Locale("zz-rZZ").key)

        assertFailsWith<IllegalArgumentException> { Locale("e", null) }
        assertFailsWith<IllegalArgumentException> { Locale("enG", null) }
        assertFailsWith<IllegalArgumentException> { Locale("enGB", null) }
        assertFailsWith<IllegalArgumentException> { Locale("en", "u") }
        assertFailsWith<IllegalArgumentException> { Locale("en", "uSS") }
        assertFailsWith<IllegalArgumentException> { Locale("!!") }
    }

    @Test
    fun languageRegion() {
        assertEquals("", Locale.Undefined.language)
        assertEquals("cs", Locale("CS").language)
        assertEquals("cs", Locale("CS", "CZ").language)
        assertEquals(null, Locale.Undefined.region)
        assertEquals("en", Locale("en-rUS").language)
        assertEquals("cs", Locale("CS", "CZ").language)
        assertEquals("aa", Locale("aa").language)
        assertEquals("aa", Locale("aa", "AA").language)
        assertEquals("zz", Locale("zz").language)
        assertEquals("zz", Locale("zz", "zz").language)

        assertEquals(null, Locale.Undefined.region)
        assertEquals(null, Locale("CS").region)
        assertEquals("cz", Locale("CS", "CZ").region)
        assertEquals("aa", Locale("aa", "aa").region)
        assertEquals("aa", Locale("aa", "AA").region)
        assertEquals("zz", Locale("zz", "zz").region)

        assertEquals("", Locale.Undefined.languageRegion)
        assertEquals("enus", Locale("en-rUS").languageRegion)
        assertEquals("cscz", Locale("CS", "CZ").languageRegion)
        assertEquals("aa", Locale("aa").languageRegion)
        assertEquals("aaaa", Locale("aa", "AA").languageRegion)
        assertEquals("zz", Locale("zz").languageRegion)
        assertEquals("zzzz", Locale("zz", "zz").languageRegion)
    }

    @Test
    fun equalsLanguage() {
        assertTrue(Locale.Undefined.equalsLanguage(Locale.Undefined))
        assertTrue(Locale("en").equalsLanguage(Locale("EN")))
        assertTrue(Locale("en", "US").equalsLanguage(Locale("en", "US")))
        assertTrue(Locale("en", "GB").equalsLanguage(Locale("EN", "gb")))

        assertFalse(Locale.Undefined.equalsLanguage(Locale("cs")))
        assertFalse(Locale("en").equalsLanguage(Locale.Undefined))
        assertFalse(Locale("en").equalsLanguage(Locale("cs")))
    }
}
