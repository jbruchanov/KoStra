package com.jibru.kostra

import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.Test

class KLocaleTest {
    @Test
    fun pack() {
        assertEquals(0, KLocale.Undefined.key)
        assertEquals(0, KLocale("", null).key)
        assertNotEquals(0, KLocale("en", null).key)
        assertNotEquals(0, KLocale("aa", null).key)
        assertNotEquals(0, KLocale("en", "").key)
        assertNotEquals(0, KLocale("en", "US").key)
        assertNotEquals(0, KLocale("AA", "ZZ").key)
        assertNotEquals(0, KLocale("en-US").key)
        assertNotEquals(0, KLocale("aa-AA").key)
        assertNotEquals(0, KLocale("zz-ZZ").key)
        assertNotEquals(0, KLocale("en-rUS").key)
        assertNotEquals(0, KLocale("aa-rAA").key)
        assertNotEquals(0, KLocale("zz-rZZ").key)

        assertFailsWith<IllegalArgumentException> { KLocale("e", null) }
        assertFailsWith<IllegalArgumentException> { KLocale("enG", null) }
        assertFailsWith<IllegalArgumentException> { KLocale("enGB", null) }
        assertFailsWith<IllegalArgumentException> { KLocale("en", "u") }
        assertFailsWith<IllegalArgumentException> { KLocale("en", "uSS") }
        assertFailsWith<IllegalArgumentException> { KLocale("!!") }
    }

    @Test
    fun packAny() {
        assertEquals(0, KLocale.Undefined.key)
        assertNotEquals(0, KLocale("ab").key)
        assertNotEquals(0, KLocale("abc").key)
        assertNotEquals(0, KLocale("abcd").key)
    }

    @Test
    fun languageRegion() {
        assertEquals("", KLocale.Undefined.language)
        assertEquals("cs", KLocale("CS").language)
        assertEquals("cs", KLocale("CS", "CZ").language)
        assertEquals(null, KLocale.Undefined.region)
        assertEquals("en", KLocale("en-rUS").language)
        assertEquals("cs", KLocale("CS", "CZ").language)
        assertEquals("aa", KLocale("aa").language)
        assertEquals("aa", KLocale("aa", "AA").language)
        assertEquals("zz", KLocale("zz").language)
        assertEquals("zz", KLocale("zz", "zz").language)

        assertEquals(null, KLocale.Undefined.region)
        assertEquals(null, KLocale("CS").region)
        assertEquals("cz", KLocale("CS", "CZ").region)
        assertEquals("aa", KLocale("aa", "aa").region)
        assertEquals("aa", KLocale("aa", "AA").region)
        assertEquals("zz", KLocale("zz", "zz").region)

        assertEquals("", KLocale.Undefined.languageRegion)
        assertEquals("enus", KLocale("en-rUS").languageRegion)
        assertEquals("cscz", KLocale("CS", "CZ").languageRegion)
        assertEquals("aa", KLocale("aa").languageRegion)
        assertEquals("aaaa", KLocale("aa", "AA").languageRegion)
        assertEquals("zz", KLocale("zz").languageRegion)
        assertEquals("zzzz", KLocale("zz", "zz").languageRegion)
    }

    @Test
    fun equalsLanguage() {
        assertTrue(KLocale.Undefined.equalsLanguage(KLocale.Undefined))
        assertTrue(KLocale("en").equalsLanguage(KLocale("EN")))
        assertTrue(KLocale("en", "US").equalsLanguage(KLocale("en", "US")))
        assertTrue(KLocale("en", "GB").equalsLanguage(KLocale("EN", "gb")))

        assertFalse(KLocale.Undefined.equalsLanguage(KLocale("cs")))
        assertFalse(KLocale("en").equalsLanguage(KLocale.Undefined))
        assertFalse(KLocale("en").equalsLanguage(KLocale("cs")))
    }

    @Test
    fun languageLocale() {
        assertEquals(KLocale.Undefined, KLocale.Undefined.languageLocale())
        assertEquals(KLocale("en"), KLocale("en").languageLocale())
        assertEquals(KLocale("en"), KLocale("en", "GB").languageLocale())
    }

    @Test
    fun hasRegion() {
        assertFalse(KLocale.Undefined.hasRegion())
        assertFalse(KLocale("en").hasRegion())
        assertTrue(KLocale("en", "gb").hasRegion())
    }
}
