package com.jibru.kostra.internal

import com.jibru.kostra.Fixtures
import com.jibru.kostra.Fixtures.Resources.K
import com.jibru.kostra.MissingResourceException
import com.jibru.kostra.PluralResourceKey
import com.jibru.kostra.Qualifiers
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class PluralDatabaseTest {

    private val db = Fixtures.Resources.pluralResources.plural

    @Test
    fun get() {
        assertEquals("dog", db.get(K.plural.dog, Qualifiers.Undefined, 1f))
        assertEquals("dogs", db.get(K.plural.dog, Qualifiers.Undefined, 2f))
        assertEquals("dog", db.get(K.plural.dog, Qualifiers("cs"), 1f))
        //this doesn't fall back as 2f in cs translates to 'few', but there is no definition in global for it
        assertFailsWith<MissingResourceException> { assertEquals("dogs", db.get(K.plural.dog, Qualifiers("cs-CZ"), 2f)) }
        assertEquals("dog-en", db.get(K.plural.dog, Qualifiers("en"), 1f))
        assertEquals("dogs-en", db.get(K.plural.dog, Qualifiers("en"), 2f))
        assertEquals("dog-en", db.get(K.plural.dog, Qualifiers("en-US"), 1f))
        assertEquals("dogs-en", db.get(K.plural.dog, Qualifiers("en-US"), 2f))
        assertEquals("dog-en-gb", db.get(K.plural.dog, Qualifiers("en-GB"), 1f))
        assertEquals("dogs-en-gb", db.get(K.plural.dog, Qualifiers("en-GB"), 2f))

        assertFailsWith<MissingResourceException> { db.get(K.plural.bug, Qualifiers.Undefined, 0f) }
        assertFailsWith<MissingResourceException> { db.get(K.plural.bug, Qualifiers.Undefined, 1f) }
        assertFailsWith<MissingResourceException> { db.get(K.plural.bug, Qualifiers.Undefined, 2f) }
        assertEquals("bug-en", db.get(K.plural.bug, Qualifiers("en"), 1f))
        assertEquals("bugs-en", db.get(K.plural.bug, Qualifiers("en"), 2f))
        assertEquals("bug-en", db.get(K.plural.bug, Qualifiers("en-NZ"), 1f))
        assertEquals("bugs-en", db.get(K.plural.bug, Qualifiers("en-NZ"), 2f))

        assertEquals("brouku", db.get(K.plural.bug, Qualifiers("cs-CZ"), 0.5f))
        assertEquals("brouků", db.get(K.plural.bug, Qualifiers("cs"), 0f))
        assertEquals("brouku", db.get(K.plural.bug, Qualifiers("cs-CZ"), 0.5f))
        assertEquals("brouk", db.get(K.plural.bug, Qualifiers("cs-CZ"), 1f))
        assertEquals("brouků", db.get(K.plural.bug, Qualifiers("cs"), 100f))

        assertFailsWith<MissingResourceException> { db.get(PluralResourceKey(10), Qualifiers.Undefined, 0f) }
    }

    @Test
    fun `get+format`() {
        assertEquals("0 brouků", db.get(K.plural.bugFormat, Qualifiers("cs"), 0f, 0))
        assertEquals("0.5 brouku", db.get(K.plural.bugFormat, Qualifiers("cs-CZ"), 0.5f, 0.5f))
        assertEquals("1 brouk", db.get(K.plural.bugFormat, Qualifiers("cs-CZ"), 1f, 1))
        assertEquals("20 brouků", db.get(K.plural.bugFormat, Qualifiers("cs"), 100f, 20))
    }
}
