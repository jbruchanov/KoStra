package com.jibru.kostra.internal

import com.jibru.kostra.MissingResourceException
import com.jibru.kostra.StringResourceKey
import com.jibru.kostra.Strings
import com.jibru.kostra.database.BinaryDatabase
import com.jibru.kostra.database.Database

open class StringDatabase(localeDatabases: Map<Locale, String>) : Strings {
    private val dbs: Map<Locale, Lazy<Database>> = localeDatabases.mapValues { (_, file) ->
        lazy { BinaryDatabase(loadResource(file).readBytes()) }
    }

    protected open fun getValue(key: StringResourceKey, locale: Locale): String? {
        return dbs[locale]?.value?.getListValue(key.key)
    }

    override fun get(key: StringResourceKey, qualifiers: Qualifiers): String {
        //try locale+region if exists
        return qualifiers.locale.takeIf { it.hasRegion() }?.let { getValue(key, qualifiers.locale) }
            //try locale only
            ?: qualifiers.locale.takeIf { it != Locale.Undefined }?.let { getValue(key, qualifiers.locale.languageLocale()) }
            //fallback
            ?: getValue(key, Locale.Undefined)
            ?: throw MissingResourceException(key, qualifiers, "string")
    }
}
