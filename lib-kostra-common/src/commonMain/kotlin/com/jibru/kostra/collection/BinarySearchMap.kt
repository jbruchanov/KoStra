package com.jibru.kostra.collection

/**
 * Very naive implementation of SparseArray for our case.
 * android.collections doesn't compile for native/ios atm and hardly ever will.
 */
class BinarySearchMap<T>(val size: Int) {
    private val keys = LongArray(size) { Long.MAX_VALUE }
    private val values = Array<Any?>(size) { null }
    private var head: Int = 0

    private fun set(index: Int, key: Long, item: T) {
        keys[index] = key
        values[index] = item
        require(key > (keys.getOrNull(index - 1) ?: Long.MIN_VALUE)) {
            "Invalid key:$key, this LongSparseArray expects put() to be called strictly in ordered way"
        }
    }

    fun containsKey(key: Long): Boolean = keys.binarySearch(key) >= 0

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
        val index = keys.binarySearch(key)
        require(index >= 0) { "Key:$key not found!" }
        values[index] = item
    }

    fun append(key: Long, item: T) {
        set(head++, key, item)
    }

    @Suppress("UNCHECKED_CAST")
    fun get(key: Long): T? {
        return values.getOrNull(keys.binarySearch(key)) as T?
    }
}
