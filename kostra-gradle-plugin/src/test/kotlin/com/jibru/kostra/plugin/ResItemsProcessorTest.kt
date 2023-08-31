package com.jibru.kostra.plugin

import com.google.common.truth.Truth.assertThat
import com.jibru.kostra.Dpi
import com.jibru.kostra.KLocale
import com.jibru.kostra.icu.PluralCategory
import com.jibru.kostra.icu.PluralCategory.Companion.toPluralList
import com.jibru.kostra.Qualifiers
import org.junit.jupiter.api.Test
import java.io.File

class ResItemsProcessorTest {

    @Test
    fun filterDuplicatesByLatest() {
        val items = listOf(
            ResItem.StringRes("s2", "s2", Qualifiers.Undefined),
            ResItem.StringRes("s1", "s1", Qualifiers.Undefined),
            ResItem.StringRes("s3", "s3en", Qualifiers(KLocale("en"))),
            ResItem.StringRes("s1", "s1X", Qualifiers.Undefined),
            ResItem.Plurals("s1", mapOf(PluralCategory.One to "p1").toPluralList(), Qualifiers.Undefined),
            ResItem.Plurals("s1", mapOf(PluralCategory.Other to "p2").toPluralList(), Qualifiers.Undefined),
            ResItem.FileRes("s1", File("X"), Qualifiers.Undefined, group = "group1", File("")),
            ResItem.FileRes("s1", File("X"), Qualifiers.Undefined, group = "group2", File("")),
            ResItem.FileRes("s1", File("X2"), Qualifiers.Undefined, group = "group1", File("")),
        )

        with(ResItemsProcessor(items)) {
            assertThat(distinctItemsByDistinctKey)
                .isEqualTo(
                    listOf(
                        ResItem.StringRes("s2", "s2", Qualifiers.Undefined),
                        ResItem.StringRes("s1", "s1X", Qualifiers.Undefined),
                        ResItem.StringRes("s3", "s3en", Qualifiers(KLocale("en"))),
                        ResItem.Plurals("s1", mapOf(PluralCategory.Other to "p2").toPluralList(), Qualifiers.Undefined),
                        ResItem.FileRes("s1", File("X2"), Qualifiers.Undefined, group = "group1", File("")),
                        ResItem.FileRes("s1", File("X"), Qualifiers.Undefined, group = "group2", File("")),
                    ),
                )
        }
    }

    @Test
    fun itemsPerGroup() {
        val items = listOf(
            ResItem.StringRes("s2", "s2", Qualifiers.Undefined),
            ResItem.StringRes("s1", "s1", Qualifiers.Undefined),
            ResItem.StringRes("s3", "s3en", Qualifiers(KLocale("en"))),
            ResItem.Plurals("p1", mapOf(PluralCategory.One to "p1").toPluralList(), Qualifiers.Undefined),
            ResItem.Plurals("p2", mapOf(PluralCategory.Other to "p2otherEn").toPluralList(), Qualifiers(KLocale("en"))),
            ResItem.Plurals("p3", mapOf(PluralCategory.Zero to "p3zeroEnGb").toPluralList(), Qualifiers(KLocale("en", "gb"))),
            ResItem.FileRes("f3", File("X"), Qualifiers.Undefined, group = "group1", File("")),
        )

        with(ResItemsProcessor(items)) {
            assertThat(allItemsPerGroup).isEqualTo(
                mapOf(
                    "string" to listOf(
                        ResItem.StringRes("s1", "s1", Qualifiers.Undefined),
                        ResItem.StringRes("s2", "s2", Qualifiers.Undefined),
                        ResItem.StringRes("s3", "s3en", Qualifiers(KLocale("en"))),
                    ),
                    "plural" to listOf(
                        ResItem.Plurals("p1", mapOf(PluralCategory.One to "p1").toPluralList(), Qualifiers.Undefined),
                        ResItem.Plurals("p2", mapOf(PluralCategory.Other to "p2otherEn").toPluralList(), Qualifiers(KLocale("en"))),
                        ResItem.Plurals("p3", mapOf(PluralCategory.Zero to "p3zeroEnGb").toPluralList(), Qualifiers(KLocale("en", "gb"))),
                    ),
                    "group1" to listOf(
                        ResItem.FileRes("f3", File("X"), Qualifiers.Undefined, group = "group1", File("")),
                    ),
                ),
            )
        }
    }

    @Test
    fun stringsForDb() {
        val items = listOf(
            ResItem.StringRes("s2", "s2", Qualifiers.Undefined),
            ResItem.StringRes("s1", "s1", Qualifiers.Undefined),
            ResItem.StringRes("s3", "s3en", Qualifiers(KLocale("en"))),
            ResItem.StringRes("s4", "s3enUS", Qualifiers(KLocale("en", "US"))),
            ResItem.Plurals("p1", mapOf(PluralCategory.One to "p1").toPluralList(), Qualifiers.Undefined),
        )

        with(ResItemsProcessor(items)) {
            val testItems = stringsForDbs
            assertThat(testItems).isEqualTo(
                mapOf(
                    KLocale.Undefined to listOf("s1", "s2", null, null),
                    KLocale("en") to listOf(null, null, "s3en", null),
                    KLocale("en", "us") to listOf(null, null, null, "s3enUS"),
                ),
            )
        }
    }

    @Test
    fun pluralsForDb() {
        val items = listOf(
            ResItem.StringRes("s2", "s2", Qualifiers.Undefined),
            ResItem.Plurals("p1", mapOf(PluralCategory.One to "p1").toPluralList(), Qualifiers.Undefined),
            ResItem.Plurals("p2", mapOf(PluralCategory.Other to "p2otherEn", PluralCategory.One to "p2oneEn").toPluralList(), Qualifiers(KLocale("en"))),
            ResItem.Plurals("p3", mapOf(PluralCategory.Zero to "p3zeroEnGb").toPluralList(), Qualifiers(KLocale("en", "gb"))),
            ResItem.FileRes("f3", File("X"), Qualifiers.Undefined, group = "group1", File("")),
        )

        with(ResItemsProcessor(items)) {
            assertThat(pluralsForDbs).isEqualTo(
                mapOf(
                    KLocale.Undefined to listOfNulls(18, mapOf(1 to "p1")),
                    KLocale("en") to listOfNulls(
                        18,
                        mapOf(
                            PluralCategory.size + (PluralCategory.Other.index) to "p2otherEn",
                            PluralCategory.size + (PluralCategory.One.index) to "p2oneEn",
                        ),
                    ),
                    KLocale("en", "gb") to listOfNulls(18, mapOf((2 * PluralCategory.size) + (PluralCategory.Zero.index) to "p3zeroEnGb")),
                ),
            )
        }
    }

    @Test
    fun otherItemsPerGroup() {
        val items = listOf(
            ResItem.StringRes("s2", "s2", Qualifiers.Undefined),
            ResItem.Plurals("p1", mapOf(PluralCategory.One to "p1").toPluralList(), Qualifiers.Undefined),
            ResItem.FileRes("f1", File("X"), Qualifiers.Undefined, group = "group", File("")),
            ResItem.FileRes("f1", File("X"), Qualifiers(locale = KLocale("en")), group = "group", File("")),
            ResItem.FileRes("f1", File("X"), Qualifiers(dpi = Dpi.HDPI), group = "group", File("")),
            ResItem.FileRes("f2", File("X"), Qualifiers.Undefined, group = "group2", File("")),
        )

        with(ResItemsProcessor(items)) {
            assertThat(otherItemsPerGroupPerKey).isEqualTo(
                mapOf(
                    "group" to mapOf(
                        ResItemKeyDbKey("f1", 1) to listOf(
                            ResItem.FileRes("f1", File("X"), Qualifiers.Undefined, group = "group", File("")),
                            ResItem.FileRes("f1", File("X"), Qualifiers(locale = KLocale("en")), group = "group", File("")),
                            ResItem.FileRes("f1", File("X"), Qualifiers(dpi = Dpi.HDPI), group = "group", File("")),
                        ),
                    ),
                    "group2" to mapOf(
                        ResItemKeyDbKey("f2", 2) to listOf(
                            ResItem.FileRes("f2", File("X"), Qualifiers.Undefined, group = "group2", File("")),
                        ),
                    ),
                ),
            )
        }
    }

    private fun listOfNulls(size: Int, values: Map<Int, String>) = List(size) { values[it] }
}
