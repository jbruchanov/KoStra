package com.jibru.kostra.database

interface ByteOps {
    fun ByteArray.readInt(offset: Int) =
        (this[offset].toInt() and 0xFF shl 24) or
            (this[offset + 1].toInt() and 0xFF shl 16) or
            (this[offset + 2].toInt() and 0xFF shl 8) or
            (this[offset + 3].toInt() and 0xFF)

    fun ByteArray.writeInt(value: Int, offset: Int) {
        this[offset] = (value shr 24).toByte()
        this[offset + 1] = (value shr 16).toByte()
        this[offset + 2] = (value shr 8).toByte()
        this[offset + 3] = value.toByte()
    }

    fun ByteArray.readByteAsUInt(offset: Int): Int = this[offset].toUByte().toInt()
    fun ByteArray.writeByte(value: UInt, offset: Int) {
        require(value < Byte.MAX_VALUE.toUInt())
        this[offset] = value.toByte()
    }

    fun ByteArray.readIntArray(offset: Int, size: Int) =
        IntArray(size) { readInt(offset + (it * Int.SIZE_BYTES)) }

    companion object : ByteOps
}
