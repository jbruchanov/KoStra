package com.jibru.kostra.internal

import com.jibru.kostra.Fixtures
import com.jibru.kostra.Fixtures.Resources.K
import com.jibru.kostra.MissingResourceException
import com.jibru.kostra.Qualifiers
import com.jibru.kostra.StringResourceKey
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class StringDatabaseTest {

    private val db = Fixtures.Resources.stringResources.string

    @Test
    fun get() {
        assertEquals("test1Default", db.get(K.string.test1, Qualifiers.Undefined))
        assertEquals("test1Default", db.get(K.string.test1, Qualifiers("en")))
        assertEquals("test1Default", db.get(K.string.test1, Qualifiers("en-GB")))
        assertEquals("test1Default", db.get(K.string.test1, Qualifiers("en-US")))
        assertEquals("test1Default", db.get(K.string.test1, Qualifiers("cs")))
        assertEquals("test1Default", db.get(K.string.test1, Qualifiers("cs-CZ")))

        assertEquals("test2Default", db.get(K.string.test2, Qualifiers.Undefined))
        assertEquals("test2EN", db.get(K.string.test2, Qualifiers("en")))
        assertEquals("test2EN", db.get(K.string.test2, Qualifiers("en-GB")))
        assertEquals("test2enUS", db.get(K.string.test2, Qualifiers("en-US")))
        assertEquals("test2Default", db.get(K.string.test2, Qualifiers("cs")))
        assertEquals("test2Default", db.get(K.string.test2, Qualifiers("cs-CZ")))

        assertFailsWith<MissingResourceException> { db.get(K.string.test3, Qualifiers.Undefined) }
        assertEquals("test3EN", db.get(K.string.test3, Qualifiers("en")))
        assertEquals("test3EN", db.get(K.string.test3, Qualifiers("en-GB")))
        assertEquals("test3enUS", db.get(K.string.test3, Qualifiers("en-US")))
        assertFailsWith<MissingResourceException> { db.get(K.string.test3, Qualifiers("cs")) }
        assertFailsWith<MissingResourceException> { db.get(K.string.test3, Qualifiers("cs-CZ")) }

        assertFailsWith<MissingResourceException> { db.get(StringResourceKey(-1), Qualifiers.Undefined) }
    }

    @Test
    fun `get+format`() {
        assertEquals("test2Default 0", db.get(K.string.test2Format, Qualifiers.Undefined, "0"))
        assertEquals("test2EN 1", db.get(K.string.test2Format, Qualifiers("en"), 1))
        assertEquals("test2EN 2.0", db.get(K.string.test2Format, Qualifiers("en-GB"), 2f))
        assertEquals("test2enUS 3.0", db.get(K.string.test2Format, Qualifiers("en-US"), 3.0))
        assertEquals("test2Default [4, 5]", db.get(K.string.test2Format, Qualifiers("cs"), listOf("4", 5)))
        assertEquals("test2Default StringResourceKey(key=5)", db.get(K.string.test2Format, Qualifiers("cs-CZ"), StringResourceKey(5)))
    }
}
