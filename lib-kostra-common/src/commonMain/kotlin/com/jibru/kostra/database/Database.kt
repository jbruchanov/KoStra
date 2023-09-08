package com.jibru.kostra.database

internal interface Database {

    fun count(): Int
    fun getListValue(index: Int): String?
    fun toBinarySearchMap(): Map<Long, String?>
}
