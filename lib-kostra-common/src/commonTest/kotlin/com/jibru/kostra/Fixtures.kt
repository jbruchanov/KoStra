package com.jibru.kostra

import com.jibru.kostra.internal.AppResources
import com.jibru.kostra.internal.Dpi
import com.jibru.kostra.internal.Locale
import com.jibru.kostra.internal.Plural
import com.jibru.kostra.internal.Qualifiers
import test.FileMemoryReferences
import test.PluralMemoryDatabase
import test.StringMemoryDatabase

object Fixtures {

    @Suppress("ClassName")
    object Resources {

        @Suppress("ktlint")
        object K {
            object string {
                val test1 = StringResourceKey(1)
                val test2 = StringResourceKey(2)
                val test3 = StringResourceKey(3)
            }

            object plural {
                val dog = PluralResourceKey(1)
                val bug = PluralResourceKey(2)
            }

            object drawable {
                val undefinedDpi = DrawableResourceKey(1)
                val xxHdpiOnly = DrawableResourceKey(2)
                val multipleDpi = DrawableResourceKey(3)
                val multipleDpiLocale = DrawableResourceKey(4)
            }
        }

        val stringResources = create(
            strings = StringMemoryDatabase(
                mapOf(
                    Locale.Undefined to mapOf(
                        K.string.test1 to "test1Default",
                        K.string.test2 to "test2Default",
                    ),
                    Locale("en") to mapOf(
                        K.string.test2 to "test2EN",
                        K.string.test3 to "test3EN",
                    ),
                    Locale("en-US") to mapOf(
                        K.string.test2 to "test2enUS",
                        K.string.test3 to "test3enUS",
                    ),
                ),
            ),
        )

        val pluralResources = create(
            plurals = PluralMemoryDatabase(
                mapOf(
                    Locale.Undefined to mapOf(
                        K.plural.dog to mapOf(Plural.Other to "dogs", Plural.One to "dog"),
                    ),
                    Locale("en") to mapOf(
                        K.plural.dog to mapOf(Plural.Other to "dogs-en", Plural.One to "dog-en"),
                        K.plural.bug to mapOf(Plural.Other to "bugs-en", Plural.One to "bug-en"),
                    ),
                    Locale("en-GB") to mapOf(
                        K.plural.dog to mapOf(Plural.Other to "dogs-en-gb", Plural.One to "dog-en-gb"),
                    ),

                    Locale("cs") to mapOf(
                        K.plural.bug to mapOf(
                            Plural.One to "brouk",
                            Plural.Few to "brouci",
                            Plural.Many to "brouku", //1.5
                            Plural.Other to "brouků", //100
                        ),
                    ),
                ),
            ),
        )

        val drawableResources = create(
            files = FileMemoryReferences(
                mapOf(
                    Qualifiers.Undefined to mapOf(
                        K.drawable.undefinedDpi to "undefinedDpiDefault",
                        K.drawable.multipleDpi to "multipleDpiDefault",
                        K.drawable.multipleDpiLocale to "multipleDpiLocaleDefault",
                    ),
                    Qualifiers(dpi = Dpi.XXHDPI) to mapOf(
                        K.drawable.xxHdpiOnly to "xxHdpiOnly",
                        K.drawable.multipleDpi to "multipleDpiXXHDPI",
                    ),
                    Qualifiers(dpi = Dpi.XXXHDPI) to mapOf(
                        K.drawable.multipleDpi to "multipleDpiXXXHDPI",
                        K.drawable.multipleDpiLocale to "multipleDpiLocaleXXXHDPI",
                    ),
                    Qualifiers("en", Dpi.XXHDPI) to mapOf(
                        K.drawable.multipleDpiLocale to "multipleDpiLocaleEnXXHDPI",
                    ),
                    Qualifiers("en-GB", Dpi.Undefined) to mapOf(
                        K.drawable.multipleDpiLocale to "multipleDpiLocaleEnGBUndefined",
                    ),
                    Qualifiers("en-GB", Dpi.XXXHDPI) to mapOf(
                        K.drawable.multipleDpiLocale to "multipleDpiLocaleEnGBXXXHDPI",
                    ),
                ),
            ),
        )

        private fun create(
            strings: Strings = Strings,
            plurals: Plurals = Plurals,
            files: FileReferences = FileReferences,
        ) = AppResources(strings, plurals, files)
    }
}
