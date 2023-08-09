package com.jibru.kostra.database

import androidx.collection.LongSparseArray

internal interface Database {
    fun getListValue(index: Int): String?
    fun toLongSparseArray(): LongSparseArray<String?>
}
