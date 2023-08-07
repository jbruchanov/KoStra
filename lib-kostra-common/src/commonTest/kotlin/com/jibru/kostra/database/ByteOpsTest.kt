package com.jibru.kostra.database

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ByteOpsTest {

    @Test
    fun `ints WHEN 4 bytes`() {
        with(ByteOps4Bytes) {
            val byteArray = ByteArray(8)
            byteArray.writeInt(Int.MIN_VALUE, offset = 0)
            assertEquals(Int.MIN_VALUE, byteArray.readInt(offset = 0))

            byteArray.writeInt(-1, offset = 4)
            assertEquals(-1, byteArray.readInt(offset = 4))

            byteArray.writeInt(Int.MAX_VALUE, offset = 0)
            assertEquals(Int.MAX_VALUE, byteArray.readInt(offset = 0))
        }
    }

    @Test
    fun `ints WHEN 2 bytes`() {
        with(ByteOps2Bytes) {
            val byteArray = ByteArray(8)
            byteArray.writeInt(256, offset = 0)
            byteArray.writeInt(512, offset = 2)
            byteArray.writeInt(-1, offset = 4)
            assertEquals(256, byteArray.readInt(offset = 0))
            assertEquals(512, byteArray.readInt(offset = 2))
            assertEquals(65535, byteArray.readInt(offset = 4))

            byteArray.writeInt(65535, offset = 6)
            assertEquals(65535, byteArray.readInt(offset = 6))
        }
    }

    @Test
    fun longs() {
        with(ByteOps4Bytes) {
            val byteArray = ByteArray(64)
            byteArray.writeLong(Long.MIN_VALUE, offset = 0)
            assertEquals(Long.MIN_VALUE, byteArray.readLong(offset = 0))

            byteArray.writeLong(Long.MAX_VALUE, offset = 8)
            assertEquals(Long.MAX_VALUE, byteArray.readLong(offset = 8))

            byteArray.writeLong(0L, offset = 0)
            assertEquals(0, byteArray.readLong(offset = 0))

            byteArray.writeInt(Int.MAX_VALUE, offset = 20)
            assertEquals(Int.MAX_VALUE, byteArray.readInt(offset = 20))
            assertEquals(Int.MAX_VALUE.toLong(), byteArray.readLong(offset = 16))
        }
    }
}
