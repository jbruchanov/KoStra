package com.jibru.kostra.internal

import com.jibru.kostra.Fixtures
import com.jibru.kostra.Fixtures.Resources.K
import com.jibru.kostra.KAppResources
import com.jibru.kostra.KQualifiers
import com.jibru.kostra.MissingResourceException
import com.jibru.kostra.PluralResourceKey
import com.jibru.kostra.Plurals
import com.jibru.kostra.icu.FixedDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PluralDatabaseTest {

    private val db = Fixtures.Resources.pluralResources
    private val csAndDefault = Fixtures.Resources.pluralResourcesCsAndDefault

    @Test
    fun get() {
        //fallback to default-other
        assertEquals("dogs", db.plural(K.plural.dog, "", 1))
        assertEquals("dogs", db.plural(K.plural.dog, "", 2))
        assertEquals("dogs", db.plural(K.plural.dog, "cs-CZ", 2))

        assertEquals("dog-en", db.plural(K.plural.dog, "en", 1))
        assertEquals("dogs-en", db.plural(K.plural.dog, "en", 2))
        assertEquals("dog-en", db.plural(K.plural.dog, "en-US", 1))
        assertEquals("dogs-en", db.plural(K.plural.dog, "en-US", 2))
        assertEquals("dog-en-gb", db.plural(K.plural.dog, "en-GB", 1))
        assertEquals("dogs-en-gb", db.plural(K.plural.dog, "en-GB", 2))

        assertFailsWith<MissingResourceException> { db.plural(K.plural.bug, "de", 0) }
        assertFailsWith<MissingResourceException> { db.plural(K.plural.bug, "de", 1) }
        assertFailsWith<MissingResourceException> { db.plural(K.plural.bug, "de", 2) }
        assertEquals("bug-en", db.plural(K.plural.bug, "en", 1))
        assertEquals("bugs-en", db.plural(K.plural.bug, "en", 2))
        assertEquals("bug-en", db.plural(K.plural.bug, "en-NZ", 1))
        assertEquals("bugs-en", db.plural(K.plural.bug, "en-NZ", 2))

        assertEquals("brouku", db.plural(K.plural.bug, "cs-CZ", 0.5))
        assertEquals("brouků", db.plural(K.plural.bug, "cs", 0))
        assertEquals("brouku", db.plural(K.plural.bug, "cs-CZ", 0.5))
        assertEquals("brouk", db.plural(K.plural.bug, "cs-CZ", 1))
        assertEquals("brouků", db.plural(K.plural.bug, "cs", 100))

        assertFailsWith<MissingResourceException> { db.plural(PluralResourceKey(10), "", 0) }
        assertFailsWith<MissingResourceException> { db.plural(PluralResourceKey(10), "de", 0) }
    }

    @Test
    fun `get+format`() {
        assertEquals("0 brouků", db.plural(K.plural.bugX, "cs", 0))
        assertEquals("0.5 brouku", db.plural(K.plural.bugX, "cs-CZ", 0.5))
        assertEquals("1 brouk", db.plural(K.plural.bugX, "cs-CZ", 1))
        assertEquals("20 brouků", db.plural(K.plural.bugX, "cs", 20))
    }

    @Test
    fun plurals() {
        assertEquals("0 bugs-en", csAndDefault.plural(K.plural.bugX, EN, 0))
        assertEquals("1 bug-en", csAndDefault.plural(K.plural.bugX, EN, 1))
        assertEquals("1.5 bugs-en", csAndDefault.plural(K.plural.bugX, EN, 1.5))
        assertEquals("2 bugs-en", csAndDefault.plural(K.plural.bugX, EN, 2))

        assertEquals("0 brouků", csAndDefault.plural(K.plural.bugX, CS, 0))
        assertEquals("1 brouk", csAndDefault.plural(K.plural.bugX, CS, 1))
        assertEquals("1.5 brouku", csAndDefault.plural(K.plural.bugX, CS, 1.5))
        assertEquals("2 brouci", csAndDefault.plural(K.plural.bugX, CS, 2))
        assertEquals("3 brouci", csAndDefault.plural(K.plural.bugX, CS, 3))
        assertEquals("10 brouků", csAndDefault.plural(K.plural.bugX, CS, 10))
    }

    @Test
    fun ordinals() {
        assertEquals("0th day", csAndDefault.ordinal(K.plural.dayX, EN, 0))
        assertEquals("1st day", csAndDefault.ordinal(K.plural.dayX, EN, 1))
        assertEquals("1.5th day", csAndDefault.ordinal(K.plural.dayX, EN, 1.5))
        assertEquals("2nd day", csAndDefault.ordinal(K.plural.dayX, EN, 2))
        assertEquals("3rd day", csAndDefault.ordinal(K.plural.dayX, EN, 3))
        assertEquals("10th day", csAndDefault.ordinal(K.plural.dayX, EN, 10))
        assertEquals("21st day", csAndDefault.ordinal(K.plural.dayX, EN, 21))
        assertEquals("22nd day", csAndDefault.ordinal(K.plural.dayX, EN, 22))
        assertEquals("23rd day", csAndDefault.ordinal(K.plural.dayX, EN, 23))

        assertEquals("0. den", csAndDefault.ordinal(K.plural.dayX, CS, 0))
        assertEquals("1. den", csAndDefault.ordinal(K.plural.dayX, CS, 1))
        assertEquals("1.5. den", csAndDefault.ordinal(K.plural.dayX, CS, 1.5))
        assertEquals("2. den", csAndDefault.ordinal(K.plural.dayX, CS, 2))
    }

    private fun KAppResources.plural(key: PluralResourceKey, locale: String, quantity: Number) =
        plural.get(key, KQualifiers(locale), FixedDecimal(quantity.toDouble()), Plurals.Type.Plurals, quantity)

    private fun KAppResources.ordinal(key: PluralResourceKey, locale: String, quantity: Number) =
        plural.get(key, KQualifiers(locale), FixedDecimal(quantity.toDouble()), Plurals.Type.Ordinals, quantity)

    companion object {
        private const val CS = "cs"
        private const val EN = "en"
    }
}
