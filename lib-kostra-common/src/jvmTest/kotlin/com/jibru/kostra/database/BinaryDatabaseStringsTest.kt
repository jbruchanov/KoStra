package com.jibru.kostra.database

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryDatabaseStringsTest {

    private val items = listOf("He11o", "World!", null, "👍", "🇬🇧", "")

    @Test
    fun `setData WHEN 2 bytes per Int`() {
        val db = BinaryDatabase(bytesPerInt = 2)
        db.setList(items)
        items.forEachIndexed { index, s ->
            assertEquals(s, db.getListValue(index), "Expected '$s' at index:$index")
        }
    }

    @Test
    fun `setData WHEN 4 bytes per Int`() {
        val db = BinaryDatabase()
        db.setList(items)
        items.forEachIndexed { index, s ->
            assertEquals(s, db.getListValue(index), "Expected '$s' at index:$index")
        }
    }

    @Test
    fun `store WHEN 2 bytes per Int`() {
        val db = BinaryDatabase(bytesPerInt = 2)
        db.setList(items)
        val data = db.save()

        val file = File("build/db.bin").apply { deleteOnExit() }
        file.writeBytes(data)
        val db2 = BinaryDatabase(file.readBytes(), bytesPerInt = 2)
        for (i in 0 until db2.count()) {
            val s = items[i % items.size]
            assertEquals(s, db.getListValue(i), "Expected '$s' at index:$i")
        }
    }

    @Test
    fun `store WHEN 4 bytes per Int`() {
        val items = List(1) { items[it % items.size] }

        val db = BinaryDatabase()
        db.setList(items)
        val data = db.save()

        val file = File("build/db.bin").apply { deleteOnExit() }
        file.writeBytes(data)
        val db2 = BinaryDatabase(file.readBytes())
        for (i in 0 until db2.count()) {
            val s = items[i % items.size]
            assertEquals(s, db.getListValue(i), "Expected '$s' at index:$i")
        }
    }

    @Test
    fun toList() {
        val db = BinaryDatabase()
        db.setList(items)
        assertEquals(items, db.toList())
    }
}
