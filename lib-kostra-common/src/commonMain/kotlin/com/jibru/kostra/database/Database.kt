package com.jibru.kostra.database

import com.jibru.kostra.collection.BinarySearchMap

internal interface Database {

    fun count(): Int
    fun getListValue(index: Int): String?
    fun toLongSparseArray(): BinarySearchMap<String?>
}
