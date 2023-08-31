package com.jibru.kostra.internal

import com.jibru.kostra.Fixtures
import com.jibru.kostra.Fixtures.Resources.K
import com.jibru.kostra.MissingResourceException
import com.jibru.kostra.PluralResourceKey
import com.jibru.kostra.Plurals
import com.jibru.kostra.KQualifiers
import com.jibru.kostra.icu.FixedDecimal
import kotlin.test.assertEquals
import kotlin.test.Test
import kotlin.test.assertFailsWith

class PluralDatabaseTest {

    private val db = Fixtures.Resources.pluralResources.plural

    @Test
    fun get() {
        assertFailsWith<IllegalStateException> { db.get(K.plural.dog, KQualifiers.Undefined, 1, type = Plurals.Type.Plurals) }
        assertFailsWith<IllegalStateException> { db.get(K.plural.dog, KQualifiers.Undefined, 2, type = Plurals.Type.Plurals) }
        //this doesn't fall back as 2f in cs translates to 'few', but there is no definition in global for it
        assertFailsWith<MissingResourceException> { assertEquals("dogs", db.get(K.plural.dog, KQualifiers("cs-CZ"), 2, type = Plurals.Type.Plurals)) }
        assertEquals("dog-en", db.get(K.plural.dog, KQualifiers("en"), 1, type = Plurals.Type.Plurals))
        assertEquals("dogs-en", db.get(K.plural.dog, KQualifiers("en"), 2, type = Plurals.Type.Plurals))
        assertEquals("dog-en", db.get(K.plural.dog, KQualifiers("en-US"), 1, type = Plurals.Type.Plurals))
        assertEquals("dogs-en", db.get(K.plural.dog, KQualifiers("en-US"), 2, type = Plurals.Type.Plurals))
        assertEquals("dog-en-gb", db.get(K.plural.dog, KQualifiers("en-GB"), 1, type = Plurals.Type.Plurals))
        assertEquals("dogs-en-gb", db.get(K.plural.dog, KQualifiers("en-GB"), 2, type = Plurals.Type.Plurals))

        assertFailsWith<MissingResourceException> { db.get(K.plural.bug, KQualifiers("de"), 0, type = Plurals.Type.Plurals) }
        assertFailsWith<MissingResourceException> { db.get(K.plural.bug, KQualifiers("de"), 1, type = Plurals.Type.Plurals) }
        assertFailsWith<MissingResourceException> { db.get(K.plural.bug, KQualifiers("de"), 2, type = Plurals.Type.Plurals) }
        assertEquals("bug-en", db.get(K.plural.bug, KQualifiers("en"), 1, type = Plurals.Type.Plurals))
        assertEquals("bugs-en", db.get(K.plural.bug, KQualifiers("en"), 2, type = Plurals.Type.Plurals))
        assertEquals("bug-en", db.get(K.plural.bug, KQualifiers("en-NZ"), 1, type = Plurals.Type.Plurals))
        assertEquals("bugs-en", db.get(K.plural.bug, KQualifiers("en-NZ"), 2, type = Plurals.Type.Plurals))

        assertEquals("brouku", db.get(K.plural.bug, KQualifiers("cs-CZ"), FixedDecimal(0.5), type = Plurals.Type.Plurals))
        assertEquals("brouků", db.get(K.plural.bug, KQualifiers("cs"), 0, type = Plurals.Type.Plurals))
        assertEquals("brouku", db.get(K.plural.bug, KQualifiers("cs-CZ"), FixedDecimal(0.5), type = Plurals.Type.Plurals))
        assertEquals("brouk", db.get(K.plural.bug, KQualifiers("cs-CZ"), 1, type = Plurals.Type.Plurals))
        assertEquals("brouků", db.get(K.plural.bug, KQualifiers("cs"), 100, type = Plurals.Type.Plurals))

        assertFailsWith<IllegalStateException> { db.get(PluralResourceKey(10), KQualifiers.Undefined, 0, type = Plurals.Type.Plurals) }
        assertFailsWith<MissingResourceException> { db.get(PluralResourceKey(10), KQualifiers("de"), 0, type = Plurals.Type.Plurals) }
    }

    @Test
    fun `get+format`() {
        assertEquals("0 brouků", db.get(K.plural.bugFormat, KQualifiers("cs"), 0, type = Plurals.Type.Plurals, 0))
        assertEquals("0.5 brouku", db.get(K.plural.bugFormat, KQualifiers("cs-CZ"), FixedDecimal(0.5), type = Plurals.Type.Plurals, 0.5f))
        assertEquals("1 brouk", db.get(K.plural.bugFormat, KQualifiers("cs-CZ"), 1, type = Plurals.Type.Plurals, 1))
        assertEquals("20 brouků", db.get(K.plural.bugFormat, KQualifiers("cs"), 100, type = Plurals.Type.Plurals, 20))
    }
}
