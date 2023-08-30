package com.jibru.kostra.internal

import com.jibru.kostra.Fixtures
import com.jibru.kostra.Fixtures.Resources.K
import com.jibru.kostra.MissingResourceException
import com.jibru.kostra.PluralResourceKey
import com.jibru.kostra.Qualifiers
import com.jibru.kostra.icu.FixedDecimal
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class PluralDatabaseTest {

    private val db = Fixtures.Resources.pluralResources.plural

    @Test
    fun get() {
        assertEquals("dog", db.get(K.plural.dog, Qualifiers.Undefined, 1))
        assertEquals("dogs", db.get(K.plural.dog, Qualifiers.Undefined, 2))
        assertEquals("dog", db.get(K.plural.dog, Qualifiers("cs"), 1))
        //this doesn't fall back as 2f in cs translates to 'few', but there is no definition in global for it
        assertFailsWith<MissingResourceException> { assertEquals("dogs", db.get(K.plural.dog, Qualifiers("cs-CZ"), 2)) }
        assertEquals("dog-en", db.get(K.plural.dog, Qualifiers("en"), 1))
        assertEquals("dogs-en", db.get(K.plural.dog, Qualifiers("en"), 2))
        assertEquals("dog-en", db.get(K.plural.dog, Qualifiers("en-US"), 1))
        assertEquals("dogs-en", db.get(K.plural.dog, Qualifiers("en-US"), 2))
        assertEquals("dog-en-gb", db.get(K.plural.dog, Qualifiers("en-GB"), 1))
        assertEquals("dogs-en-gb", db.get(K.plural.dog, Qualifiers("en-GB"), 2))

        assertFailsWith<MissingResourceException> { db.get(K.plural.bug, Qualifiers.Undefined, 0) }
        assertFailsWith<MissingResourceException> { db.get(K.plural.bug, Qualifiers.Undefined, 1) }
        assertFailsWith<MissingResourceException> { db.get(K.plural.bug, Qualifiers.Undefined, 2) }
        assertEquals("bug-en", db.get(K.plural.bug, Qualifiers("en"), 1))
        assertEquals("bugs-en", db.get(K.plural.bug, Qualifiers("en"), 2))
        assertEquals("bug-en", db.get(K.plural.bug, Qualifiers("en-NZ"), 1))
        assertEquals("bugs-en", db.get(K.plural.bug, Qualifiers("en-NZ"), 2))

        assertEquals("brouku", db.get(K.plural.bug, Qualifiers("cs-CZ"), FixedDecimal(0.5)))
        assertEquals("brouků", db.get(K.plural.bug, Qualifiers("cs"), 0))
        assertEquals("brouku", db.get(K.plural.bug, Qualifiers("cs-CZ"), FixedDecimal(0.5)))
        assertEquals("brouk", db.get(K.plural.bug, Qualifiers("cs-CZ"), 1))
        assertEquals("brouků", db.get(K.plural.bug, Qualifiers("cs"), 100))

        assertFailsWith<MissingResourceException> { db.get(PluralResourceKey(10), Qualifiers.Undefined, 0) }
    }

    @Test
    fun `get+format`() {
        assertEquals("0 brouků", db.get(K.plural.bugFormat, Qualifiers("cs"), 0, 0))
        assertEquals("0.5 brouku", db.get(K.plural.bugFormat, Qualifiers("cs-CZ"), FixedDecimal(0.5), 0.5f))
        assertEquals("1 brouk", db.get(K.plural.bugFormat, Qualifiers("cs-CZ"), 1, 1))
        assertEquals("20 brouků", db.get(K.plural.bugFormat, Qualifiers("cs"), 100, 20))
    }
}
