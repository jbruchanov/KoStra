package com.jibru.kostra.internal

import com.jibru.kostra.Fixtures
import com.jibru.kostra.Fixtures.Resources.K
import com.jibru.kostra.MissingResourceException
import com.jibru.kostra.StringResourceKey
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
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
}
