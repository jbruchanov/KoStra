package com.jibru.kostra.plugin

import com.google.common.truth.Truth.assertThat
import com.jibru.kostra.BinaryResourceKey
import com.jibru.kostra.KDpi
import com.jibru.kostra.KLocale
import com.jibru.kostra.KQualifiers
import com.jibru.kostra.icu.PluralCategory
import com.jibru.kostra.icu.PluralCategory.Companion.toPluralList
import java.io.File
import org.junit.jupiter.api.Test

class ResItemsProcessorTest {

    @Test
    fun filterDuplicatesByLatest() {
        val items = listOf(
            ResItem.StringRes("s2", "s2", KQualifiers.Undefined.key),
            ResItem.StringRes("s1", "s1", KQualifiers.Undefined.key),
            ResItem.StringRes("s3", "s3en", KQualifiers(KLocale("en")).key),
            ResItem.StringRes("s1", "s1X", KQualifiers.Undefined.key),
            ResItem.Plurals("s1", mapOf(PluralCategory.One to "p1").toPluralList(), KQualifiers.Undefined.key),
            ResItem.Plurals("s1", mapOf(PluralCategory.Other to "p2").toPluralList(), KQualifiers.Undefined.key),
            ResItem.FileRes("s1", File("X"), KQualifiers.Undefined.key, group = "group1", File("")),
            ResItem.FileRes("s1", File("X"), KQualifiers.Undefined.key, group = "group2", File("")),
            ResItem.FileRes("s1", File("X2"), KQualifiers.Undefined.key, group = "group1", File("")),
        )

        with(ResItemsProcessor(items)) {
            assertThat(distinctItemsByDistinctKey)
                .isEqualTo(
                    listOf(
                        ResItem.StringRes("s2", "s2", KQualifiers.Undefined.key),
                        ResItem.StringRes("s1", "s1X", KQualifiers.Undefined.key),
                        ResItem.StringRes("s3", "s3en", KQualifiers(KLocale("en")).key),
                        ResItem.Plurals("s1", mapOf(PluralCategory.Other to "p2").toPluralList(), KQualifiers.Undefined.key),
                        ResItem.FileRes("s1", File("X2"), KQualifiers.Undefined.key, group = "group1", File("")),
                        ResItem.FileRes("s1", File("X"), KQualifiers.Undefined.key, group = "group2", File("")),
                    ),
                )
        }
    }

    @Test
    fun itemsPerGroup() {
        val items = listOf(
            ResItem.StringRes("s2", "s2", KQualifiers.Undefined.key),
            ResItem.StringRes("s1", "s1", KQualifiers.Undefined.key),
            ResItem.StringRes("s3", "s3en", KQualifiers(KLocale("en")).key),
            ResItem.Plurals("p1", mapOf(PluralCategory.One to "p1").toPluralList(), KQualifiers.Undefined.key),
            ResItem.Plurals("p2", mapOf(PluralCategory.Other to "p2otherEn").toPluralList(), KQualifiers(KLocale("en")).key),
            ResItem.Plurals("p3", mapOf(PluralCategory.Zero to "p3zeroEnGb").toPluralList(), KQualifiers(KLocale("en", "gb")).key),
            ResItem.FileRes("f3", File("X"), KQualifiers.Undefined.key, group = "group1", File("")),
        )

        with(ResItemsProcessor(items)) {
            assertThat(allItemsPerGroup).isEqualTo(
                mapOf(
                    "string" to listOf(
                        ResItem.StringRes("s1", "s1", KQualifiers.Undefined.key),
                        ResItem.StringRes("s2", "s2", KQualifiers.Undefined.key),
                        ResItem.StringRes("s3", "s3en", KQualifiers(KLocale("en")).key),
                    ),
                    "plural" to listOf(
                        ResItem.Plurals("p1", mapOf(PluralCategory.One to "p1").toPluralList(), KQualifiers.Undefined.key),
                        ResItem.Plurals("p2", mapOf(PluralCategory.Other to "p2otherEn").toPluralList(), KQualifiers(KLocale("en")).key),
                        ResItem.Plurals("p3", mapOf(PluralCategory.Zero to "p3zeroEnGb").toPluralList(), KQualifiers(KLocale("en", "gb")).key),
                    ),
                    "group1" to listOf(
                        ResItem.FileRes("f3", File("X"), KQualifiers.Undefined.key, group = "group1", File("")),
                    ),
                ),
            )
        }
    }

    @Test
    fun stringsForDb() {
        val items = listOf(
            ResItem.StringRes("s2", "s2", KQualifiers.Undefined.key),
            ResItem.StringRes("s1", "s1", KQualifiers.Undefined.key),
            ResItem.StringRes("s3", "s3en", KQualifiers(KLocale("en")).key),
            ResItem.StringRes("s4", "s3enUS", KQualifiers(KLocale("en", "US")).key),
            ResItem.Plurals("p1", mapOf(PluralCategory.One to "p1").toPluralList(), KQualifiers.Undefined.key),
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
            ResItem.StringRes("s2", "s2", KQualifiers.Undefined.key),
            ResItem.Plurals("p1", mapOf(PluralCategory.One to "p1").toPluralList(), KQualifiers.Undefined.key),
            ResItem.Plurals("p2", mapOf(PluralCategory.Other to "p2otherEn", PluralCategory.One to "p2oneEn").toPluralList(), KQualifiers(KLocale("en")).key),
            ResItem.Plurals("p3", mapOf(PluralCategory.Zero to "p3zeroEnGb").toPluralList(), KQualifiers(KLocale("en", "gb")).key),
            ResItem.FileRes("f3", File("X"), KQualifiers.Undefined.key, group = "group1", File("")),
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
            ResItem.StringRes("s2", "s2", KQualifiers.Undefined.key),
            ResItem.Plurals("p1", mapOf(PluralCategory.One to "p1").toPluralList(), KQualifiers.Undefined.key),
            ResItem.FileRes("f1", File("X"), KQualifiers.Undefined.key, group = "group", File("")),
            ResItem.FileRes("f1", File("X"), KQualifiers(locale = KLocale("en")).key, group = "group", File("")),
            ResItem.FileRes("f1", File("X"), KQualifiers(dpi = KDpi.HDPI).key, group = "group", File("")),
            ResItem.FileRes("f2", File("X"), KQualifiers.Undefined.key, group = "group2", File("")),
        )

        with(ResItemsProcessor(items)) {
            assertThat(otherItemsPerGroupPerKey).isEqualTo(
                mapOf(
                    "group" to mapOf(
                        ResItemKeyDbKey("f1", 1, BinaryResourceKey::class) to listOf(
                            ResItem.FileRes("f1", File("X"), KQualifiers.Undefined.key, group = "group", File("")),
                            ResItem.FileRes("f1", File("X"), KQualifiers(locale = KLocale("en")).key, group = "group", File("")),
                            ResItem.FileRes("f1", File("X"), KQualifiers(dpi = KDpi.HDPI).key, group = "group", File("")),
                        ),
                    ),
                    "group2" to mapOf(
                        ResItemKeyDbKey("f2", 2, BinaryResourceKey::class) to listOf(
                            ResItem.FileRes("f2", File("X"), KQualifiers.Undefined.key, group = "group2", File("")),
                        ),
                    ),
                ),
            )
        }
    }

    private fun listOfNulls(size: Int, values: Map<Int, String>) = List(size) { values[it] }
}
