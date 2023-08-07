package com.jibru.kostra.internal

import com.jibru.kostra.internal.Plural.Companion.toPluralList
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PluralTest {
    @Test
    fun toPluralList() {
        val result = Plural.values().associateWith { it.name }.toPluralList()
        assertEquals(Plural.values().map { it.name }, result)

        assertEquals(
            mapOf(Plural.One to "one", Plural.Other to "Other").toPluralList(),
            listOf(null, "one", null, null, null, "Other"),
        )

        assertEquals(mapOf(Plural.Zero to "X").toPluralList(), listOf("X", null, null, null, null, null))
        assertEquals(mapOf(Plural.One to "X").toPluralList(), listOf(null, "X", null, null, null, null))
        assertEquals(mapOf(Plural.Two to "X").toPluralList(), listOf(null, null, "X", null, null, null))
        assertEquals(mapOf(Plural.Few to "X").toPluralList(), listOf(null, null, null, "X", null, null))
        assertEquals(mapOf(Plural.Many to "X").toPluralList(), listOf(null, null, null, null, "X", null))
        assertEquals(mapOf(Plural.Other to "X").toPluralList(), listOf(null, null, null, null, null, "X"))
    }
}
