package com.jibru.kostra.collection

internal fun LongArray.binarySearch(value: Long, fromIndex: Int = 0, toIndex: Int = this.size): Int {
    //JVM implementation
    var low: Int = fromIndex
    var high: Int = toIndex - 1

    while (low <= high) {
        val mid = low + high ushr 1
        val midVal: Long = this[mid]
        if (midVal < value) {
            low = mid + 1
        } else if (midVal > value) {
            high = mid - 1
        } else {
            // key found
            return mid
        }
    }
    return -(low + 1)
}
