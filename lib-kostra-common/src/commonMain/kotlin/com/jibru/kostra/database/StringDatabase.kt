package com.jibru.kostra.database

/**
 * Structure:
 * HEADER:
 * 0 - 1: byte = version
 * 1 - 4: int -> length of offsets
 * 5 - m: int[, int] -> each int is relative offset from start of the data
 * DATA:
 * m+1 - end: int(len), data(string)[,int,data]
 */
class StringDatabase(private var data: ByteArray = ByteArray(0), bytesPerInt: Int = 4) : ByteOps by ByteOps.Default(bytesPerInt) {

    private val nullConstant = if (bytesPerInt == 2) 65535 else -1
    private val headerSize = VersionBytes + bytesPerInt
    fun count() = data.readInt(VersionBytes)

    fun set(items: Collection<String?>) {
        val header = ByteArray(headerSize + (items.size * bytesPerInt))
        //this is only estimation, doesn't work for case: "👍".length == 2, but encodeToByteArray() return 4bytes!
        var data = ByteArray(items.sumOf { if (it != null) bytesPerInt + it.length else 0 })
        //version
        header.writeByte(1u, offset = 0)
        //count of items
        header.writeInt(validateInt(items.size), offset = 1)

        var headerIndex = headerSize
        var dataIndex = 0

        items.forEachIndexed { index, string ->
            val strData = string?.encodeToByteArray()
            val newDataStep = if (strData == null) 0 else (strData.size + bytesPerInt/*string len*/)
            header.writeInt(if (strData == null) nullConstant else dataIndex, offset = headerIndex)
            if (strData != null) {
                data = ensureSize(data, dataIndex + newDataStep)
                data.writeInt(strData.size, offset = dataIndex)
                strData.copyInto(data, destinationOffset = dataIndex + bytesPerInt)
            }
            dataIndex += newDataStep
            headerIndex += bytesPerInt
            validateInt(headerSize + dataIndex)
        }

        this.data = ByteArray(header.size + dataIndex)
        header.copyInto(this.data)
        data.copyInto(this.data, destinationOffset = header.size, startIndex = 0, endIndex = dataIndex)
    }

    private fun ensureSize(data: ByteArray, size: Int): ByteArray {
        return if (size > data.size) {
            // this can happen only with UTF8+ symbols
            ByteArray((size * 1.5).toInt()).also { data.copyInto(it) }
        } else {
            data
        }
    }

    fun get(index: Int): String? {
        val records = count()
        if (records == 0) return null

        require(index >= 0) { "Invalid index:$index" }
        require(data.size > headerSize) { "Invalid data, size:${data.size}" }
        require(index < records) { "Invalid index:$index, db has $records records!" }

        val dataAbsoluteOffset = headerSize + (records * bytesPerInt)
        val recordRelativeOffset = data.readInt(offset = headerSize + (index * bytesPerInt))
        if (recordRelativeOffset == nullConstant) return null

        val recordAbsoluteOffset = dataAbsoluteOffset + recordRelativeOffset
        val dataLen = data.readInt(recordAbsoluteOffset)
        if (dataLen == 0) return ""

        val start = recordAbsoluteOffset + bytesPerInt/*dataLen*/
        val end = start + dataLen
        val value = data.decodeToString(startIndex = start, endIndex = end)
        return value
    }

    fun save() = data.copyOf()

    companion object {
        private const val VersionBytes = 1
    }
}
