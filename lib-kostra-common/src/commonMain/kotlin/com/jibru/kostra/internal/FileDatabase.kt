package com.jibru.kostra.internal

import com.jibru.kostra.AssetResourceKey
import com.jibru.kostra.FileReferences
import com.jibru.kostra.KDpi
import com.jibru.kostra.KLocale
import com.jibru.kostra.KQualifiers
import com.jibru.kostra.MissingResourceException
import com.jibru.kostra.database.BinaryDatabase

open class FileDatabase(database: String) : FileReferences {
    private val data by lazy {
        BinaryDatabase(loadResource(database)).toBinarySearchMap()
    }

    protected open fun getValue(key: AssetResourceKey, qualifiers: KQualifiers): String? {
        val dbKey = (key.key.toLong() shl Int.SIZE_BITS) or qualifiers.key.toLong()
        return data.get(dbKey)
    }

    override fun get(key: AssetResourceKey, qualifiers: KQualifiers): String {
        //langRegion[+dpi]
        return qualifiers.locale.takeIf { it.hasRegion() }
            ?.let { getValue(key, qualifiers) ?: getValue(key, qualifiers.withNoDpi()) }
            //lang[+dpi]
            ?: qualifiers.locale.takeIf { it != KLocale.Undefined }
                ?.let { qualifiers.withNoLocaleRegion() }
                ?.let { localeLang -> getValue(key, localeLang) ?: getValue(key, localeLang.withNoDpi()) }
            //just dpi
            ?: qualifiers.dpi.takeIf { it != KDpi.Undefined }?.let { getValue(key, KQualifiers(dpi = it)) }
            //just locale
            ?: qualifiers.locale.takeIf { it != KLocale.Undefined }?.let { getValue(key, KQualifiers(locale = it)) }
            //fallback
            ?: getValue(key, KQualifiers.Undefined)
            ?: throw MissingResourceException(key, qualifiers, "file")
    }
}
