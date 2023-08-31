package test

import com.jibru.kostra.PluralResourceKey
import com.jibru.kostra.KLocale
import com.jibru.kostra.icu.PluralCategory
import com.jibru.kostra.internal.PluralDatabase

class PluralMemoryDatabase(private val data: Map<KLocale, Map<PluralResourceKey, Map<PluralCategory, String>>>) : PluralDatabase(emptyMap()) {

    override fun getValue(key: PluralResourceKey, locale: KLocale, plural: PluralCategory): String? {
        return data[locale]?.get(key)?.get(plural).also {
            println("PluralMemoryDatabase.getValue key:$key, locale:$locale, plural:$plural result:$it")
        }
    }
}
