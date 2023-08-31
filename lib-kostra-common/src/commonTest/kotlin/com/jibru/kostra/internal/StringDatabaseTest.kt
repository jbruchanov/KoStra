package com.jibru.kostra.internal

import com.jibru.kostra.Fixtures
import com.jibru.kostra.Fixtures.Resources.K
import com.jibru.kostra.MissingResourceException
import com.jibru.kostra.KQualifiers
import com.jibru.kostra.StringResourceKey
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class StringDatabaseTest {

    private val db = Fixtures.Resources.stringResources.string

    @Test
    fun get() {
        assertEquals("test1Default", db.get(K.string.test1, KQualifiers.Undefined))
        assertEquals("test1Default", db.get(K.string.test1, KQualifiers("en")))
        assertEquals("test1Default", db.get(K.string.test1, KQualifiers("en-GB")))
        assertEquals("test1Default", db.get(K.string.test1, KQualifiers("en-US")))
        assertEquals("test1Default", db.get(K.string.test1, KQualifiers("cs")))
        assertEquals("test1Default", db.get(K.string.test1, KQualifiers("cs-CZ")))

        assertEquals("test2Default", db.get(K.string.test2, KQualifiers.Undefined))
        assertEquals("test2EN", db.get(K.string.test2, KQualifiers("en")))
        assertEquals("test2EN", db.get(K.string.test2, KQualifiers("en-GB")))
        assertEquals("test2enUS", db.get(K.string.test2, KQualifiers("en-US")))
        assertEquals("test2Default", db.get(K.string.test2, KQualifiers("cs")))
        assertEquals("test2Default", db.get(K.string.test2, KQualifiers("cs-CZ")))

        assertFailsWith<MissingResourceException> { db.get(K.string.test3, KQualifiers.Undefined) }
        assertEquals("test3EN", db.get(K.string.test3, KQualifiers("en")))
        assertEquals("test3EN", db.get(K.string.test3, KQualifiers("en-GB")))
        assertEquals("test3enUS", db.get(K.string.test3, KQualifiers("en-US")))
        assertFailsWith<MissingResourceException> { db.get(K.string.test3, KQualifiers("cs")) }
        assertFailsWith<MissingResourceException> { db.get(K.string.test3, KQualifiers("cs-CZ")) }

        assertFailsWith<MissingResourceException> { db.get(StringResourceKey(-1), KQualifiers.Undefined) }
    }

    @Test
    fun `get+format`() {
        assertEquals("test2Default 0", db.get(K.string.test2Format, KQualifiers.Undefined, "0"))
        assertEquals("test2EN 1", db.get(K.string.test2Format, KQualifiers("en"), 1))
        assertEquals("test2EN 2.0", db.get(K.string.test2Format, KQualifiers("en-GB"), 2f))
        assertEquals("test2enUS 3.0", db.get(K.string.test2Format, KQualifiers("en-US"), 3.0))
        assertEquals("test2Default [4, 5]", db.get(K.string.test2Format, KQualifiers("cs"), listOf("4", 5)))
        assertEquals("test2Default StringResourceKey(key=5)", db.get(K.string.test2Format, KQualifiers("cs-CZ"), StringResourceKey(5)))
    }
}
