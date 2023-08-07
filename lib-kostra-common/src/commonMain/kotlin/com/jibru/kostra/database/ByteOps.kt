package com.jibru.kostra.database

interface ByteOps {

    val bytesPerInt: Int
    fun ByteArray.readInt(offset: Int): Int

    fun ByteArray.writeInt(value: Int, offset: Int)

    fun ByteArray.readLong(offset: Int): Long = with(ByteOps4Bytes) {
        (readInt(offset).toLong() shl Int.SIZE_BITS) or (readInt(offset + Int.SIZE_BYTES).toUInt().toLong())
    }

    fun ByteArray.writeLong(value: Long, offset: Int) = with(ByteOps4Bytes) {
        writeInt((value ushr Int.SIZE_BITS).toInt(), offset)
        writeInt((value and 0xFFFFFFFF).toInt(), offset + Int.SIZE_BYTES)
    }

    fun ByteArray.readByteAsUInt(offset: Int): UInt = this[offset].toUByte().toUInt()
    fun ByteArray.writeByte(value: UInt, offset: Int) {
        require(value < Byte.MAX_VALUE.toUInt())
        this[offset] = value.toByte()
    }

    fun ByteArray.readIntArray(offset: Int, size: Int) =
        IntArray(size) { readInt(offset + (it * Int.SIZE_BYTES)) }

    fun ByteArray.readLongArray(offset: Int, size: Int) =
        LongArray(size) { readLong(offset + (it * Long.SIZE_BYTES)) }

    fun validateInt(value: Int): Int

    class Default(override val bytesPerInt: Int) : ByteOps {

        private val delegate: ByteOps = when (bytesPerInt) {
            4 -> ByteOps4Bytes
            2 -> ByteOps2Bytes
            else -> throw UnsupportedOperationException("Unsupported bytesPerInt:$bytesPerInt")
        }

        override fun validateInt(value: Int) = delegate.validateInt(value)

        override fun ByteArray.readInt(offset: Int): Int = with(delegate) { readInt(offset) }
        override fun ByteArray.writeInt(value: Int, offset: Int) = with(delegate) { writeInt(value, offset) }
    }
}

internal object ByteOps4Bytes : ByteOps {

    override fun validateInt(value: Int) = value

    override val bytesPerInt = 4
    override fun ByteArray.readInt(offset: Int) =
        (this[offset].toInt() and 0xFF shl 24) or
            (this[offset + 1].toInt() and 0xFF shl 16) or
            (this[offset + 2].toInt() and 0xFF shl 8) or
            (this[offset + 3].toInt() and 0xFF)

    override fun ByteArray.writeInt(value: Int, offset: Int) {
        this[offset] = (value shr 24).toByte()
        this[offset + 1] = (value shr 16).toByte()
        this[offset + 2] = (value shr 8).toByte()
        this[offset + 3] = value.toByte()
    }
}

internal object ByteOps2Bytes : ByteOps {

    override fun validateInt(value: Int): Int {
        require(value == -1 || value in 0..65535) { "Value:$value must be in 2bytes range [0-65535] or -1" }
        return value
    }

    override val bytesPerInt = 2
    override fun ByteArray.readInt(offset: Int) =
        (this[offset].toInt() and 0xFF shl 8) or
            (this[offset + 1].toInt() and 0xFF)

    override fun ByteArray.writeInt(value: Int, offset: Int) {
        val v = value and 0xFFFF
        this[offset] = (v shr 8).toByte()
        this[offset + 1] = v.toByte()
    }
}
