package com.jibru.kostra.plugin.icu

import com.ibm.icu.text.PluralRules
import com.jibru.kostra.KLocale
import com.jibru.kostra.icu.FixedDecimal
import com.jibru.kostra.icu.PluralCategory
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import test.PluralsSample

class IcuTest {

    @Test
    fun select() {
        val rules = """
        one: i = 1 and v = 0 @integer 1
        few: i = 2..4 and v = 0 @integer 2~4
        many: v != 0 @decimal 0.0~1.5, 10.0, 100.0, 1000.0, 10000.0, 100000.0, 1000000.0, …
        other: @integer 0, 5~19, 100, 1000, 10000, 100000, 1000000, …
        """.trimIndent().split("\n")

        val lines = rules.joinToString(";")

        val icu4jPluralRules = PluralRules.createRules(lines)
        val icuPluralRules = IcuPluralRules(icu4jPluralRules)
        val pluralRules = icuPluralRules.toPluralRules()
        listOf(0.0, 0.01, 1.0, 2.0, 0.5, 1000.0, 5.0, 10.01, 19.0, 20.0).forEach {
            val vIcu4j = icu4jPluralRules.select(it)
            val v = pluralRules.select(it)
            println("$it $vIcu4j $v")
        }
    }

    @Test
    fun selectAllPlurals() {
        assertAllValues(IcuPluralsDownloader().loadPlurals().data)
    }

    @Test
    fun selectAllOrdinals() {
        assertAllValues(IcuPluralsDownloader().loadOrdinals().data)
    }

    private fun assertAllValues(data: Map<KLocale, Map<PluralCategory, String>>) {
        data.toSortedMap()
            .forEach { locale, records ->
                val icu4jRules = PluralRules.createRules(records.map { "${it.key.keyword}: ${it.value}" }.joinToString(";"))
                val rules = IcuPluralRules(icu4jRules).toPluralRules()
                val samples = records.values.map { PluralsSample.parse(it) }
                    .flatten()
                    .distinctBy { it.text }.map { it.iterator().asSequence().toList() }
                    .flatten()
                    .distinct()

                samples.forEach {
                    val icu4key = icu4jRules.select((it as FixedDecimal).toDouble())
                    val category = rules.select(it)
                    Assertions.assertEquals(icu4key, category.keyword, "$it $icu4key $category, $icu4jRules")
                }
            }
    }
}
