package test

import com.jibru.kostra.StringResourceKey
import com.jibru.kostra.internal.Locale
import com.jibru.kostra.internal.StringDatabase

class StringMemoryDatabase(private val data: Map<Locale, Map<StringResourceKey, String>>) : StringDatabase(emptyMap()) {
    override fun getValue(key: StringResourceKey, locale: Locale): String? {
        return data[locale]?.get(key).also {
            println("StringMemoryDatabase.getValue key:$key, locale:$locale, result:$it")
        }
    }
}
