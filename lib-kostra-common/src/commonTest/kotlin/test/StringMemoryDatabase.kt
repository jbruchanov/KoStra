package test

import com.jibru.kostra.KLocale
import com.jibru.kostra.StringResourceKey
import com.jibru.kostra.internal.StringDatabase

class StringMemoryDatabase(internal val data: Map<KLocale, Map<StringResourceKey, String>>) : StringDatabase(emptyMap()) {
    override fun getValue(key: StringResourceKey, locale: KLocale): String? {
        return data[locale]?.get(key).also {
            println("StringMemoryDatabase.getValue key:$key, locale:$locale, result:$it")
        }
    }
}
