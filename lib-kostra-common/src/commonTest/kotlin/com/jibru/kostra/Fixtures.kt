@file:Suppress("ktlint:standard:discouraged-comment-location")

package com.jibru.kostra

import com.jibru.kostra.icu.PluralCategory
import test.DKey
import test.FileMemoryReferences
import test.PKey
import test.PluralMemoryDatabase
import test.SKey
import test.StringMemoryDatabase

object Fixtures {

    @Suppress("ClassName")
    object Resources {

        @Suppress("ktlint")
        object K {
            object string {
                val test1: StringResourceKey = SKey(1)
                val test2: StringResourceKey = SKey(2)
                val test3: StringResourceKey = SKey(3)
                val test2Format: StringResourceKey = SKey(4)
            }

            object plural {
                val dog: PluralResourceKey = PKey(1)
                val bug: PluralResourceKey = PKey(2)
                val bugX: PluralResourceKey = PKey(3)
                val dayX: PluralResourceKey = PKey(4)
            }

            object painter {
                val undefinedDpi: PainterResourceKey = DKey(1)
                val xxHdpiOnly: PainterResourceKey = DKey(2)
                val multipleDpi: PainterResourceKey = DKey(3)
                val multipleDpiLocale: PainterResourceKey = DKey(4)
            }
        }

        val stringResources = create(
            strings = StringMemoryDatabase(
                mapOf(
                    KLocale.Undefined to mapOf(
                        K.string.test1 to "test1Default",
                        K.string.test2 to "test2Default",
                        K.string.test2Format to "test2Default %s",
                    ),
                    KLocale("en") to mapOf(
                        K.string.test2 to "test2EN",
                        K.string.test2Format to "test2EN %s",
                        K.string.test3 to "test3EN",
                    ),
                    KLocale("en-US") to mapOf(
                        K.string.test2 to "test2enUS",
                        K.string.test2Format to "test2enUS %s",
                        K.string.test3 to "test3enUS",
                    ),
                ),
            ),
        )

        val pluralResources = create(
            plurals = PluralMemoryDatabase(
                mapOf(
                    KLocale.Undefined to mapOf(
                        K.plural.dog to mapOf(PluralCategory.Other to "dogs", PluralCategory.One to "dog"),
                    ),
                    KLocale("en") to mapOf(
                        K.plural.dog to mapOf(PluralCategory.Other to "dogs-en", PluralCategory.One to "dog-en"),
                        K.plural.bug to mapOf(PluralCategory.Other to "bugs-en", PluralCategory.One to "bug-en"),
                        K.plural.bugX to mapOf(PluralCategory.Other to "%s bugs-en", PluralCategory.One to "%s bug-en"),
                        K.plural.dayX to mapOf(
                            PluralCategory.One to "%sst day",
                            PluralCategory.Two to "%snd day",
                            PluralCategory.Few to "%srd day",
                            PluralCategory.Other to "%sth day",
                        ),
                    ),
                    KLocale("en-GB") to mapOf(
                        K.plural.dog to mapOf(PluralCategory.Other to "dogs-en-gb", PluralCategory.One to "dog-en-gb"),
                    ),

                    KLocale("cs") to mapOf(
                        K.plural.bug to mapOf(
                            PluralCategory.One to "brouk",
                            PluralCategory.Few to "brouci",
                            PluralCategory.Many to "brouku", //1.5
                            PluralCategory.Other to "brouků", //100
                        ),
                        K.plural.bugX to mapOf(
                            PluralCategory.One to "%s brouk",
                            PluralCategory.Few to "%s brouci",
                            PluralCategory.Many to "%s brouku", //1.5
                            PluralCategory.Other to "%s brouků", //100
                        ),
                        K.plural.dayX to mapOf(
                            PluralCategory.Other to "%s. den",
                        ),
                    ),
                ),
            ),
        )

        val pluralResourcesCsAndDefault = create(
            plurals = PluralMemoryDatabase(
                mapOf(
                    KLocale.Undefined to pluralResources.memPlurals().data.getValue(KLocale("en")),
                    KLocale("cs") to pluralResources.memPlurals().data.getValue(KLocale("cs")),
                ),
            ),
        )

        val painterResources = create(
            files = FileMemoryReferences(
                mapOf(
                    KQualifiers.Undefined to mapOf(
                        K.painter.undefinedDpi to "undefinedDpiDefault",
                        K.painter.multipleDpi to "multipleDpiDefault",
                        K.painter.multipleDpiLocale to "multipleDpiKLocaleDefault",
                    ),
                    KQualifiers(dpi = KDpi.XXHDPI) to mapOf(
                        K.painter.xxHdpiOnly to "xxHdpiOnly",
                        K.painter.multipleDpi to "multipleDpiXXHDPI",
                    ),
                    KQualifiers(dpi = KDpi.XXXHDPI) to mapOf(
                        K.painter.multipleDpi to "multipleDpiXXXHDPI",
                        K.painter.multipleDpiLocale to "multipleDpiKLocaleXXXHDPI",
                    ),
                    KQualifiers("en", KDpi.XXHDPI) to mapOf(
                        K.painter.multipleDpiLocale to "multipleDpiKLocaleEnXXHDPI",
                    ),
                    KQualifiers("en-GB", KDpi.Undefined) to mapOf(
                        K.painter.multipleDpiLocale to "multipleDpiKLocaleEnGBUndefined",
                    ),
                    KQualifiers("en-GB", KDpi.XXXHDPI) to mapOf(
                        K.painter.multipleDpiLocale to "multipleDpiKLocaleEnGBXXXHDPI",
                    ),
                ),
            ),
        )

        private fun create(
            strings: Strings = Strings,
            plurals: Plurals = Plurals,
            files: FileReferences = FileReferences,
        ) = KAppResources(strings, plurals, files)

        private fun KAppResources.memStrings() = string as StringMemoryDatabase

        private fun KAppResources.memPlurals() = plural as PluralMemoryDatabase
    }
}
