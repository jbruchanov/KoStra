package com.jibru.kostra.collection

/**
 * Very naive implementation of SparseArray for our case.
 * android.collections doesn't compile for native/ios atm and hardly ever will.
 */
@Suppress("ReplaceSizeZeroCheckWithIsEmpty")
internal class BinarySearchMap<T>(override val size: Int) : Map<Long, T> {
    private val _keys = LongArray(size) { Long.MAX_VALUE }
    private val _values = Array<Any?>(size) { null }
    private var head: Int = 0

    private fun set(index: Int, key: Long, item: T) {
        _keys[index] = key
        _values[index] = item
        require(key > (_keys.getOrNull(index - 1) ?: Long.MIN_VALUE)) {
            "Invalid key:$key, this LongSparseArray expects put() to be called strictly in ordered way"
        }
    }

    override val keys: Set<Long> = _keys.toSet()

    @Suppress("UNCHECKED_CAST")
    override val values: Collection<T> = _values.toList() as Collection<T>

    @Suppress("UNCHECKED_CAST")
    override val entries: Set<Map.Entry<Long, T>> = buildSet {
        for (i in 0..<size) {
            add(Entry(_keys[i], _values[i] as T))
        }
    }

    override fun containsValue(value: T): Boolean = _values.contains(value)

    override fun isEmpty(): Boolean = size == 0

    override fun containsKey(key: Long): Boolean = _keys.binarySearch(key) >= 0

    /**
     * Put must be called strictly in ordered way for keys.
     * Meaning any other following call must have key >= previousKey
     */
    fun put(key: Long, item: T) {
        set(head++, key, item)
    }

    /**
     * Set value for a particular key.
     * The key must exists already.
     */
    fun set(key: Long, item: T) {
        val index = _keys.binarySearch(key)
        require(index >= 0) { "Key:$key not found!" }
        _values[index] = item
    }

    fun append(key: Long, item: T) {
        set(head++, key, item)
    }

    @Suppress("UNCHECKED_CAST")
    override fun get(key: Long): T? {
        return _values.getOrNull(_keys.binarySearch(key)) as T?
    }

    private class Entry<T>(override val key: Long, override val value: T) : Map.Entry<Long, T>
}
