package com.jibru.kostra.database

import com.jibru.kostra.collection.BinarySearchMap
import kotlin.math.absoluteValue

private enum class StorageType(val value: UInt) {
    Undefined(0u),
    List(1u),
    KeyValue(2u),
}

/**
 * Structure:
 * HEADER:
 * 0 - 1: byte = version
 * 1 - 2: byte = type
 * 2 - 6: int -> length of keys (List has just reference for data to have O(1) reading,
 *               KeyValue has an actual key (negative for null string) and key/value are matching based on own position/index)
 * Data Offsets:
 * 6 - m: number[, number] -> each int is relative offset (int) from start of the data or key(long).
 * Data Strings:
 * m+1 - end: int(len), data(string)[,int,data]
 */
class BinaryDatabase internal constructor(private var data: ByteArray = ByteArray(0), bytesPerInt: Int) : ByteOps by ByteOps.Default(bytesPerInt), Database {

    constructor(data: ByteArray = ByteArray(0)) : this(data, bytesPerInt = 4)

    private val nullConstant = if (bytesPerInt == 2) 65535 else -1
    private val headerSize = HeaderBytes + bytesPerInt
    private var type = StorageType.Undefined
    private val keySize
        get() = when (type) {
            StorageType.KeyValue -> BytesPerLong
            StorageType.List -> bytesPerInt
            else -> throw UnsupportedOperationException("keySize unknown due to undefined type:$type")
        }

    private var version: UInt = 1u

    init {
        if (data.size > 2) {
            val type = data.readByteAsUInt(1)
            this.type = StorageType.entries.first { it.value == type }
        }
    }

    override fun count() = if (data.size >= (HeaderBytes + Int.SIZE_BYTES)) data.readInt(HeaderBytes) else 0

    fun setList(items: Collection<String?>) {
        setItems(items.mapIndexed { index, s -> StringItem.IntKey(index, s) }, refBytes = this.bytesPerInt)
        type = StorageType.List
        data.writeByte(type.value, offset = 1)
    }

    fun setPairs(items: Collection<Pair<Long, String?>>) {
        val converted = items
            .map { StringItem.LongKey(it.first, it.second) }
            .sortedBy { it.key }
        setItems(converted, refBytes = BytesPerLong)
        type = StorageType.KeyValue
        data.writeByte(type.value, offset = 1)
    }

    private fun setItems(items: Collection<StringItem>, refBytes: Int) {
        val header = ByteArray(headerSize + (items.size * refBytes))
        //this is only estimation, doesn't work for case: "👍".length == 2, but encodeToByteArray() return 4bytes!
        var data = ByteArray(items.sumOf { if (it.valueNull) 0 else refBytes + it.valueLength })
        //version
        header.writeByte(version, offset = 0)
        //type saved after this step
        header.writeInt(validateInt(items.size), offset = 2)

        var headerIndex = headerSize
        var dataIndex = 0

        items.forEachIndexed { _, item ->
            val string = item.value
            val strData = string?.encodeToByteArray()
            when (item) {
                is StringItem.IntKey -> header.writeInt(if (strData == null) nullConstant else dataIndex, offset = headerIndex)
                is StringItem.LongKey -> {
                    //negative key is detection for null string, but in this case we need a key stored
                    require(item.key >= 0) { "$item must have positive key" }
                    header.writeLong(if (strData == null) -item.key else item.key, offset = headerIndex)
                }
            }
            if (strData != null) {
                data = ensureSize(data, dataIndex + strData.size + bytesPerInt)
                //write size of the strings
                data.writeInt(strData.size, offset = dataIndex)
                dataIndex += bytesPerInt
                //write string
                strData.copyInto(data, destinationOffset = dataIndex)
            }

            dataIndex += strData?.size ?: 0
            headerIndex += refBytes
            validateInt(headerSize + dataIndex)
        }

        //remove adding after last item
        this.data = ByteArray(header.size + dataIndex)
        header.copyInto(this.data)
        data.copyInto(this.data, destinationOffset = header.size, startIndex = 0, endIndex = dataIndex)
    }

    fun getKeyLong(index: Int): Long {
        require(0 <= index && index < count()) { "Invalid index:$index, count:${count()}" }
        require(data.size > headerSize) { "Invalid data, size:${data.size}" }
        val keyOffset = headerSize + (index * BytesPerLong)
        return data.readLong(keyOffset).absoluteValue
    }

    private fun getKeyInt(index: Int, bytesPerRef: Int): Int {
        require(0 <= index && index < count()) { "Invalid index:$index, count:${count()}" }
        require(data.size > headerSize) { "Invalid data, size:${data.size}" }
        val keyOffset = headerSize + (index * bytesPerRef)
        return data.readInt(keyOffset)
    }

    override fun getListValue(index: Int): String? = get(index, if (type == StorageType.KeyValue) BytesPerLong else bytesPerInt)

    private fun get(index: Int, bytesPerRef: Int): String? {
        val records = count()
        if (records == 0) return null

        require(type == StorageType.List) { "Get by index is supported only for List type" }
        require(index >= 0) { "Invalid index:$index" }
        require(data.size > headerSize) { "Invalid data, size:${data.size}" }
        require(index < records) { "Invalid index:$index, db has $records records!" }

        val dataAbsoluteOffset = headerSize + (records * bytesPerRef)
        val recordRelativeOffset = getKeyInt(index, bytesPerRef)
        if (recordRelativeOffset == nullConstant) return null

        val recordAbsoluteOffset = dataAbsoluteOffset + recordRelativeOffset
        val dataLen = data.readInt(recordAbsoluteOffset)
        if (dataLen == 0) return ""

        val start = recordAbsoluteOffset + bytesPerRef
        val end = start + dataLen
        val value = data.decodeToString(startIndex = start, endIndex = end)
        return value
    }
    //endregion

    fun forEach(block: (key: Long, String?) -> Unit) {
        val count = count()
        if (count == 0) return

        val keySize = this.keySize
        var dataAbsoluteOffset = headerSize + (count * keySize)

        for (i in 0..<count) {
            when (type) {
                StorageType.List -> block(i.toLong(), getListValue(i))
                StorageType.KeyValue -> {
                    val key = data.readLong(offset = headerSize + (i * keySize))
                    if (key < 0) {
                        block(-key, null)
                    } else {
                        val strLen = data.readInt(dataAbsoluteOffset)
                        dataAbsoluteOffset += bytesPerInt
                        val end = dataAbsoluteOffset + strLen
                        val str = data.decodeToString(startIndex = dataAbsoluteOffset, endIndex = end)
                        block(key, str)
                        dataAbsoluteOffset = end
                    }
                }

                else -> throw UnsupportedOperationException("type:$type")
            }
        }
    }

    fun save() = data.copyOf()

    fun toList() = buildList(count()) {
        this@BinaryDatabase.forEach { _, s -> add(s) }
    }

    fun toMap() = buildMap(count()) {
        this@BinaryDatabase.forEach { key, s -> put(key, s) }
    }

    override fun toBinarySearchMap(): Map<Long, String?> = BinarySearchMap<String?>(count()).apply {
        this@BinaryDatabase.forEach { key, s -> put(key, s) }
    }

    private fun ensureSize(data: ByteArray, size: Int): ByteArray {
        return if (size > data.size) {
            // this can happen only with UTF8+ symbols
            ByteArray((size * 1.5).toInt()).also { data.copyInto(it) }
        } else {
            data
        }
    }

    companion object {
        private const val HeaderBytes = 1 /*version*/ + 1 /*type*/
        private const val BytesPerLong = Long.SIZE_BYTES
    }
}

private sealed class StringItem {
    abstract val value: String?
    val valueNull get() = value == null
    val valueLength get() = value?.length ?: 0

    data class IntKey(val key: Int, override val value: String?) : StringItem()

    data class LongKey(val key: Long, override val value: String?) : StringItem()
}
