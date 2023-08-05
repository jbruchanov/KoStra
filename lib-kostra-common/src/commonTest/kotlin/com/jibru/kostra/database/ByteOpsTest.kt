package com.jibru.kostra.database

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ByteOpsTest : ByteOps {

    @Test
    fun ints() {
        val byteArray = ByteArray(8)
        byteArray.writeInt(Int.MIN_VALUE, offset = 0)
        assertEquals(Int.MIN_VALUE, byteArray.readInt(offset = 0))

        byteArray.writeInt(-1, offset = 4)
        assertEquals(-1, byteArray.readInt(offset = 4))

        byteArray.writeInt(Int.MAX_VALUE, offset = 0)
        assertEquals(Int.MAX_VALUE, byteArray.readInt(offset = 0))
    }
}
