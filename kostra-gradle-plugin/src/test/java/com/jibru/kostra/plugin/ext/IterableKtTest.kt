package com.jibru.kostra.plugin.ext

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class IterableKtTest {
    @Test
    fun distinctByLast() {
        val list = listOf(1 to "A", 2 to "B", 3 to "A", 4 to "B", 5 to "A")
        val result = list.distinctByLast { it.second }
        assertEquals(listOf(5 to "A", 4 to "B"), result)
    }
}
