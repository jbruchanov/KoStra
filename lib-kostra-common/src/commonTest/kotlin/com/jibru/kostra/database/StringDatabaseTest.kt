package com.jibru.kostra.database

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class StringDatabaseTest {

    private val items = listOf("He11o", "World!", null, "👍", "🇬🇧", "")

    @Test
    fun `setData WHEN 2 bytes per Int`() {
        val db = StringDatabase(bytesPerInt = 2)
        db.set(items)
        items.forEachIndexed { index, s ->
            assertEquals(s, db.get(index), "Expected '$s' at index:$index")
        }
    }

    @Test
    fun `setData WHEN 4 bytes per Int`() {
        val db = StringDatabase()
        db.set(items)
        items.forEachIndexed { index, s ->
            assertEquals(s, db.get(index), "Expected '$s' at index:$index")
        }
    }

    @Test
    fun `store WHEN 2 bytes per Int`() {
        val db = StringDatabase(bytesPerInt = 2)
        db.set(items)
        val data = db.save()

        val file = File("build/db.bin").apply { deleteOnExit() }
        file.writeBytes(data)
        val db2 = StringDatabase(file.readBytes(), bytesPerInt = 2)
        for (i in 0 until db2.count()) {
            val s = items[i % items.size]
            assertEquals(s, db.get(i), "Expected '$s' at index:$i")
        }
    }

    @Test
    fun `store WHEN 4 bytes per Int`() {
        val items = List(1024) { items[it % items.size] }

        val db = StringDatabase()
        db.set(items)
        val data = db.save()

        val file = File("build/db.bin").apply { deleteOnExit() }
        file.writeBytes(data)
        val db2 = StringDatabase(file.readBytes())
        for (i in 0 until db2.count()) {
            val s = items[i % items.size]
            assertEquals(s, db.get(i), "Expected '$s' at index:$i")
        }
    }
}
