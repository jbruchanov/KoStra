package com.jibru.kostra.internal

import com.jibru.kostra.icu.PluralCategory
import com.jibru.kostra.icu.PluralCategory.Companion.toPluralList
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PluralTest {
    @Test
    fun toPluralList() {
        val result = PluralCategory.values().associateWith { it.name }.toPluralList()
        assertEquals(PluralCategory.values().map { it.name }, result)

        assertEquals(
            mapOf(PluralCategory.One to "one", PluralCategory.Other to "Other").toPluralList(),
            listOf(null, "one", null, null, null, "Other"),
        )

        assertEquals(mapOf(PluralCategory.Zero to "X").toPluralList(), listOf("X", null, null, null, null, null))
        assertEquals(mapOf(PluralCategory.One to "X").toPluralList(), listOf(null, "X", null, null, null, null))
        assertEquals(mapOf(PluralCategory.Two to "X").toPluralList(), listOf(null, null, "X", null, null, null))
        assertEquals(mapOf(PluralCategory.Few to "X").toPluralList(), listOf(null, null, null, "X", null, null))
        assertEquals(mapOf(PluralCategory.Many to "X").toPluralList(), listOf(null, null, null, null, "X", null))
        assertEquals(mapOf(PluralCategory.Other to "X").toPluralList(), listOf(null, null, null, null, null, "X"))
    }
}
