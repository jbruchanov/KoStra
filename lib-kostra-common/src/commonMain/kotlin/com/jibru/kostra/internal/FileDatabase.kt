package com.jibru.kostra.internal

import com.jibru.kostra.AssetResourceKey
import com.jibru.kostra.Dpi
import com.jibru.kostra.FileReferences
import com.jibru.kostra.Locale
import com.jibru.kostra.MissingResourceException
import com.jibru.kostra.Qualifiers
import com.jibru.kostra.database.BinaryDatabase

open class FileDatabase(database: String) : FileReferences {
    private val data by lazy {
        BinaryDatabase(loadResource(database)).toLongSparseArray()
    }

    protected open fun getValue(key: AssetResourceKey, qualifiers: Qualifiers): String? {
        val dbKey = (key.key.toLong() shl Int.SIZE_BITS) or qualifiers.key.toLong()
        return data.get(dbKey)
    }

    override fun get(key: AssetResourceKey, qualifiers: Qualifiers): String {
        //langRegion[+dpi]
        return qualifiers.locale.takeIf { it.hasRegion() }
            ?.let { getValue(key, qualifiers) ?: getValue(key, qualifiers.withNoDpi()) }
            //lang[+dpi]
            ?: qualifiers.locale.takeIf { it != Locale.Undefined }
                ?.let { qualifiers.withNoLocaleRegion() }
                ?.let { localeLang -> getValue(key, localeLang) ?: getValue(key, localeLang.withNoDpi()) }
            //just dpi
            ?: qualifiers.dpi.takeIf { it != Dpi.Undefined }?.let { getValue(key, Qualifiers(dpi = it)) }
            //just locale
            ?: qualifiers.locale.takeIf { it != Locale.Undefined }?.let { getValue(key, Qualifiers(locale = it)) }
            //fallback
            ?: getValue(key, Qualifiers.Undefined)
            ?: throw MissingResourceException(key, qualifiers, "file")
    }
}
