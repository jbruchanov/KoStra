package com.jibru.kostra.collection

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BinarySearchMapTest {

    @Test
    fun `set get`() {
        val map = BinarySearchMap<String>(5)
        (0 until map.size).forEach {
            map.put(it.toLong(), it.toString())
        }
        (0 until map.size).forEach {
            assertEquals(it.toString(), map.get(it.toLong()))
        }
    }

    @Test
    fun contains() {
        val map = BinarySearchMap<String>(5)
        (0 until map.size).forEach {
            map.put(it.toLong(), it.toString())
        }
        assertFalse(map.containsKey(-1))
        assertTrue(map.containsKey(0L))
        assertTrue(map.containsKey(1L))
        assertTrue(map.containsKey(2L))
        assertTrue(map.containsKey(3L))
        assertTrue(map.containsKey(4L))
        assertFalse(map.containsKey(5L))
    }

    @Test
    fun append() {
        val map = BinarySearchMap<String>(5)
        (0 until map.size).forEach {
            map.append(it.toLong(), it.toString())
        }
        assertFalse(map.containsKey(-1))
        assertTrue(map.containsKey(0L))
        assertTrue(map.containsKey(1L))
        assertTrue(map.containsKey(2L))
        assertTrue(map.containsKey(3L))
        assertTrue(map.containsKey(4L))
        assertFalse(map.containsKey(5L))
    }

    @Test
    fun `put WHEN not sorted THEN exception`() {
        val map = BinarySearchMap<String>(5)
        map.put(10, "10")
        assertFailsWith<IllegalArgumentException> { map.put(5, "5") }
    }

    @Test
    fun `set WHEN exists THEN overwrites`() {
        val map = BinarySearchMap<String>(5)
        map.put(10, "10")
        assertEquals("10", map.get(10))
        map.set(10, "10x")
        assertEquals("10x", map.get(10))
    }
}
