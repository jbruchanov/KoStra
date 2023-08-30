package com.jibru.kostra.internal

import com.jibru.kostra.Fixtures
import com.jibru.kostra.Fixtures.Resources.K
import com.jibru.kostra.MissingResourceException
import com.jibru.kostra.PluralResourceKey
import com.jibru.kostra.Plurals
import com.jibru.kostra.Qualifiers
import com.jibru.kostra.icu.FixedDecimal
import kotlin.test.assertEquals
import kotlin.test.Test
import kotlin.test.assertFailsWith

class PluralDatabaseTest {

    private val db = Fixtures.Resources.pluralResources.plural

    @Test
    fun get() {
        assertFailsWith<IllegalStateException> { db.get(K.plural.dog, Qualifiers.Undefined, 1, type = Plurals.Type.Plurals) }
        assertFailsWith<IllegalStateException> { db.get(K.plural.dog, Qualifiers.Undefined, 2, type = Plurals.Type.Plurals) }
        //this doesn't fall back as 2f in cs translates to 'few', but there is no definition in global for it
        assertFailsWith<MissingResourceException> { assertEquals("dogs", db.get(K.plural.dog, Qualifiers("cs-CZ"), 2, type = Plurals.Type.Plurals)) }
        assertEquals("dog-en", db.get(K.plural.dog, Qualifiers("en"), 1, type = Plurals.Type.Plurals))
        assertEquals("dogs-en", db.get(K.plural.dog, Qualifiers("en"), 2, type = Plurals.Type.Plurals))
        assertEquals("dog-en", db.get(K.plural.dog, Qualifiers("en-US"), 1, type = Plurals.Type.Plurals))
        assertEquals("dogs-en", db.get(K.plural.dog, Qualifiers("en-US"), 2, type = Plurals.Type.Plurals))
        assertEquals("dog-en-gb", db.get(K.plural.dog, Qualifiers("en-GB"), 1, type = Plurals.Type.Plurals))
        assertEquals("dogs-en-gb", db.get(K.plural.dog, Qualifiers("en-GB"), 2, type = Plurals.Type.Plurals))

        assertFailsWith<MissingResourceException> { db.get(K.plural.bug, Qualifiers("de"), 0, type = Plurals.Type.Plurals) }
        assertFailsWith<MissingResourceException> { db.get(K.plural.bug, Qualifiers("de"), 1, type = Plurals.Type.Plurals) }
        assertFailsWith<MissingResourceException> { db.get(K.plural.bug, Qualifiers("de"), 2, type = Plurals.Type.Plurals) }
        assertEquals("bug-en", db.get(K.plural.bug, Qualifiers("en"), 1, type = Plurals.Type.Plurals))
        assertEquals("bugs-en", db.get(K.plural.bug, Qualifiers("en"), 2, type = Plurals.Type.Plurals))
        assertEquals("bug-en", db.get(K.plural.bug, Qualifiers("en-NZ"), 1, type = Plurals.Type.Plurals))
        assertEquals("bugs-en", db.get(K.plural.bug, Qualifiers("en-NZ"), 2, type = Plurals.Type.Plurals))

        assertEquals("brouku", db.get(K.plural.bug, Qualifiers("cs-CZ"), FixedDecimal(0.5), type = Plurals.Type.Plurals))
        assertEquals("brouků", db.get(K.plural.bug, Qualifiers("cs"), 0, type = Plurals.Type.Plurals))
        assertEquals("brouku", db.get(K.plural.bug, Qualifiers("cs-CZ"), FixedDecimal(0.5), type = Plurals.Type.Plurals))
        assertEquals("brouk", db.get(K.plural.bug, Qualifiers("cs-CZ"), 1, type = Plurals.Type.Plurals))
        assertEquals("brouků", db.get(K.plural.bug, Qualifiers("cs"), 100, type = Plurals.Type.Plurals))

        assertFailsWith<IllegalStateException> { db.get(PluralResourceKey(10), Qualifiers.Undefined, 0, type = Plurals.Type.Plurals) }
        assertFailsWith<MissingResourceException> { db.get(PluralResourceKey(10), Qualifiers("de"), 0, type = Plurals.Type.Plurals) }
    }

    @Test
    fun `get+format`() {
        assertEquals("0 brouků", db.get(K.plural.bugFormat, Qualifiers("cs"), 0, type = Plurals.Type.Plurals, 0))
        assertEquals("0.5 brouku", db.get(K.plural.bugFormat, Qualifiers("cs-CZ"), FixedDecimal(0.5), type = Plurals.Type.Plurals, 0.5f))
        assertEquals("1 brouk", db.get(K.plural.bugFormat, Qualifiers("cs-CZ"), 1, type = Plurals.Type.Plurals, 1))
        assertEquals("20 brouků", db.get(K.plural.bugFormat, Qualifiers("cs"), 100, type = Plurals.Type.Plurals, 20))
    }
}
