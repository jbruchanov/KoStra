package test

import com.jibru.kostra.PluralResourceKey
import com.jibru.kostra.internal.Locale
import com.jibru.kostra.internal.Plural
import com.jibru.kostra.internal.PluralDatabase

class PluralMemoryDatabase(private val data: Map<Locale, Map<PluralResourceKey, Map<Plural, String>>>) : PluralDatabase(emptyMap()) {

    override fun getValue(key: PluralResourceKey, locale: Locale, plural: Plural): String? {
        return data[locale]?.get(key)?.get(plural).also {
            println("PluralMemoryDatabase.getValue key:$key, locale:$locale, plural:$plural result:$it")
        }
    }
}
