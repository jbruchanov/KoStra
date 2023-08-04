package com.jibru.kostra.internal

import com.jibru.kostra.Fixtures
import com.jibru.kostra.Fixtures.Resources.K
import com.jibru.kostra.MissionResourceException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class KostraResProvidersDensityTest : KostraResProviders {

    private val undefined = Qualifiers.Undefined
    private val xhdpi = Qualifiers(locale = Locale.Undefined, dpi = Dpi.XHDPI)
    private val xxhdpi = Qualifiers(locale = Locale.Undefined, dpi = Dpi.XXHDPI)
    private val xxxhdpi = Qualifiers(locale = Locale.Undefined, dpi = Dpi.XXXHDPI)
    private val en_xxhdpi = Qualifiers(locale = Locale("en", null), dpi = Dpi.XXHDPI)
    private val enUS_xxhdpi = Qualifiers(locale = Locale("en", "us"), dpi = Dpi.XXHDPI)
    private val enGB_xxhdpi = Qualifiers(locale = Locale("en", "gb"), dpi = Dpi.XXHDPI)
    private val en_xxxhdpi = Qualifiers(locale = Locale("en", null), dpi = Dpi.XXXHDPI)
    private val enUS_xxxhdpi = Qualifiers(locale = Locale("en", "us"), dpi = Dpi.XXXHDPI)
    private val enGB_xxxhdpi = Qualifiers(locale = Locale("en", "gb"), dpi = Dpi.XXXHDPI)
    private val csCZ_xhdpi = Qualifiers(locale = Locale("cs", "CZ"), dpi = Dpi.XHDPI)
    private val csCZ_xxxhdpi = Qualifiers(locale = Locale("cs", "CZ"), dpi = Dpi.XXXHDPI)

    @Test
    fun `painterResource WHEN all undefined THEN works with any`() {
        with(Fixtures.Resources.drawableResources) {
            val allQualifiers = listOf(undefined, xhdpi, xxhdpi, en_xxhdpi, enGB_xxxhdpi)
            allQualifiers.forEach {
                assertEquals("undefined", painterResource(K.drawable.undefinedDpi, it).value)
            }
        }
    }

    @Test
    fun `painterResource WHEN specified DPI THEN works with expected only`() {
        with(Fixtures.Resources.drawableResources) {
            assertEquals("XXHDPI", painterResource(K.drawable.xxHdpiOnly, xxhdpi).value)
            assertEquals("XXHDPI", painterResource(K.drawable.xxHdpiOnly, en_xxhdpi).value)
            assertEquals("XXHDPI", painterResource(K.drawable.xxHdpiOnly, enUS_xxhdpi).value)
        }
    }

    @Test
    fun `painterResource WHEN specified different DPI THEN fails`() {
        with(Fixtures.Resources.drawableResources) {
            assertFailsWith<MissionResourceException> { painterResource(K.drawable.xxHdpiOnly, xhdpi).value }
            assertFailsWith<MissionResourceException> { painterResource(K.drawable.xxHdpiOnly, xxxhdpi).value }
        }
    }

    @Test
    fun `painterResource WHEN multiple different DPI THEN works as expected`() {
        with(Fixtures.Resources.drawableResources) {
            assertEquals("XXHDPI", painterResource(K.drawable.multipleDpi, xxhdpi).value)
            assertEquals("XXHDPI", painterResource(K.drawable.multipleDpi, en_xxhdpi).value)
            assertEquals("XXHDPI", painterResource(K.drawable.multipleDpi, enUS_xxhdpi).value)

            assertEquals("XXXHDPI", painterResource(K.drawable.multipleDpi, xxxhdpi).value)
            assertEquals("XXXHDPI", painterResource(K.drawable.multipleDpi, en_xxxhdpi).value)
            assertEquals("XXXHDPI", painterResource(K.drawable.multipleDpi, enUS_xxxhdpi).value)
            assertEquals("XXXHDPI", painterResource(K.drawable.multipleDpi, enGB_xxxhdpi).value)

            assertEquals("Fallback", painterResource(K.drawable.multipleDpi, xhdpi).value)
            assertEquals("Fallback", painterResource(K.drawable.multipleDpi, undefined).value)
        }
    }

    @Test
    fun `painterResource WHEN multiple different DPI and locales THEN works as expected`() {
        with(Fixtures.Resources.drawableResources) {
            assertEquals("Fallback", painterResource(K.drawable.multipleDpi, undefined).value)
            assertEquals("Fallback", painterResource(K.drawable.multipleDpi, xhdpi).value)
            assertEquals("XXHDPI", painterResource(K.drawable.multipleDpi, enUS_xxhdpi).value)
            assertEquals("enGB, Undefined", painterResource(K.drawable.multipleDpiLocale, enGB_xxxhdpi).value)
            assertEquals("en XXHDPI", painterResource(K.drawable.multipleDpiLocale, enUS_xxhdpi).value)
            assertEquals("enGB, XXHDPI", painterResource(K.drawable.multipleDpiLocale, enGB_xxhdpi).value)
            assertEquals("enGB, Undefined", painterResource(K.drawable.multipleDpiLocale, enGB_xxxhdpi).value)
            assertEquals("Fallback", painterResource(K.drawable.multipleDpiLocale, csCZ_xhdpi).value)
            assertEquals("NoLocale, XXXHDPI", painterResource(K.drawable.multipleDpiLocale, csCZ_xxxhdpi).value)
        }
    }
}
