package com.jibru.kostra.plugin.icu

import com.squareup.kotlinpoet.FileSpec
import org.junit.jupiter.api.Test
import com.ibm.icu.text.PluralRules as Icu4JPluralRules

class RulesToKtConversionTest {
    @Test
    fun pluralRulesToProperty() {
        val rules = """
        one: i = 1 and v = 0 @integer 1
        few: i = 2..4 and v = 0 @integer 2~4
        many: v != 0 @decimal 0.0~1.5, 10.0, 100.0, 1000.0, 10000.0, 100000.0, 1000000.0, …
        other: @integer 0, 5~19, 100, 1000, 10000, 100000, 1000000, …
        """.trimIndent().split("\n")
        val lines = rules.joinToString(";")

        val pluralRules = IcuPluralRules(Icu4JPluralRules.createRules(lines)).toPluralRules()
        val p = pluralRules.toProperty("_plural_01")

        val f = FileSpec.builder("", "Test")
            .addProperty(p)
            .build()

        println(f)
    }
}
