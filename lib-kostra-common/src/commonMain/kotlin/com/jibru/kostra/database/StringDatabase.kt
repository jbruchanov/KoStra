package com.jibru.kostra.database

import com.jibru.kostra.database.ByteOps.Companion.readInt
import com.jibru.kostra.database.ByteOps.Companion.writeByte
import com.jibru.kostra.database.ByteOps.Companion.writeInt

/**
 * Structure:
 * HEADER:
 * 0 - 1: byte = version
 * 1 - 4: int -> length of offsets
 * 5 - m: int[, int] -> each int is relative offset from start of the data
 * DATA:
 * m - end: int(len), data(string)[,int,data]
 */
class StringDatabase(private var data: ByteArray = ByteArray(0)) {

    fun count() = data.readInt(VersionBytes)

    fun set(items: Collection<String?>) {
        val header = ByteArray(HeaderSize + (items.size * IndexSize))
        //this is only estimation, doesn't work for case: "👍".length == 2, but encodeToByteArray() return 4bytes!
        var data = ByteArray(items.sumOf { if (it != null) IndexSize + it.length else 0 })
        //version
        header.writeByte(1u, offset = 0)
        //count of items
        header.writeInt(items.size, offset = 1)

        var headerIndex = HeaderSize
        var dataIndex = 0

        items.forEachIndexed { index, string ->
            val strData = string?.encodeToByteArray()
            val newDataStep = if (strData == null) 0 else (strData.size + IndexSize/*string len*/)
            header.writeInt(if (strData == null) -1 else dataIndex, offset = headerIndex)
            if (strData != null) {
                data = ensureSize(data, dataIndex + newDataStep)
                data.writeInt(strData.size, offset = dataIndex)
                strData.copyInto(data, destinationOffset = dataIndex + IndexSize)
            }
            dataIndex += newDataStep
            headerIndex += IndexSize
        }

        this.data = ByteArray(header.size + dataIndex)
        header.copyInto(this.data)
        data.copyInto(this.data, destinationOffset = header.size, startIndex = 0, endIndex = dataIndex)
    }

    private fun ensureSize(data: ByteArray, size: Int): ByteArray {
        return if (size > data.size) {
            //low increase index, this can happen only with UTF8+ symbols
            ByteArray((size * 1.25).toInt()).also { data.copyInto(it) }
        } else {
            data
        }
    }

    fun get(index: Int): String? {
        require(index >= 0) { "Invalid index:$index" }
        require(data.size > HeaderSize) { "Invalid data, size:${data.size}" }

        val records = count()
        if (records == 0) return null
        require(index < records) { "Invalid index:$index, db has $records records!" }

        val dataStart = HeaderSize + (records * IndexSize)
        val recordRelativeOffset = data.readInt(offset = HeaderSize + (index * IndexSize))
        if (recordRelativeOffset == -1) return null

        val recordAbsoluteOffset = dataStart + recordRelativeOffset
        val dataLen = data.readInt(recordAbsoluteOffset)
        if (dataLen == 0) return ""

        val start = recordAbsoluteOffset + IndexSize
        val end = start + dataLen
        val value = data.decodeToString(start, endIndex = end)
        return value
    }

    fun save() = data.copyOf()

    companion object {
        private const val VersionBytes = 1
        private const val IndexSize = Int.SIZE_BYTES
        private const val HeaderSize = VersionBytes + IndexSize
    }
}
