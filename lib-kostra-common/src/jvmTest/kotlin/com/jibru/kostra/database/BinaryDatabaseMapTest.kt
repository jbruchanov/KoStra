package com.jibru.kostra.database

import com.jibru.kostra.collection.BinarySearchMap
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryDatabaseMapTest {

    private val items = listOf(
        0x2FFAAFFCC to "v=0x2FFAAFFCC",
        0x1FFAABBCC to "v=0x1FFAABBCC",
        0x3FFAABBCC to null,
        0x1FFAAFFCC to "v=0x1FFAAFFCC",
        0x2FFAABBCC to "v=0x1FFAABBCC",
    )

    private val db = BinaryDatabase().apply { setPairs(items) }

    @Test
    fun setPairs() {
        assertEquals(items.size, db.count())
        assertEquals(db.toMap(), items.toMap())
    }

    @Test
    fun save() {
        val file = File("build/test.db")
        file.writeBytes(db.save())

        val db2 = BinaryDatabase(file.readBytes())
        assertEquals(db2.toMap(), items.toMap())
    }

    @Test
    fun getKeyLong() {
        val sortedItems = items.sortedBy { it.first }
        for (i in 0..<db.count()) {
            assertEquals(sortedItems[i].first, db.getKeyLong(i))
        }
    }

    @Test
    fun toSparseArray() {
        val expected = BinarySearchMap<String?>(items.size).apply {
            items.sortedBy { it.first }.forEach { (key, value) ->
                append(key, value)
            }
        }
        //equality on 2 sparse arrays doesn't work
        val sparseArray = db.toBinarySearchMap()
        items.forEach { (key, value) ->
            assertEquals(value, sparseArray.get(key))
        }
    }
}
