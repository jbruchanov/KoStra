package com.jibru.kostra

import com.jibru.kostra.internal.Dpi
import com.jibru.kostra.internal.KostraResources
import com.jibru.kostra.internal.Locale
import com.jibru.kostra.internal.Qualifiers
import com.jibru.kostra.internal.ResourceItem

object Fixtures {

    @Suppress("ClassName")
    object Resources {

        @Suppress("ktlint")
        object K {
            object string {
                val test1 = StringResourceKey("test1")
                val test2 = StringResourceKey("test2")
                val test3 = StringResourceKey("test3")
            }

            object drawable {
                val undefinedDpi = DrawableResourceKey("undefinedDpi")
                val xxHdpiOnly = DrawableResourceKey("xxHdpiOnly")
                val multipleDpi = DrawableResourceKey("multipleDpi")
                val multipleDpiLocale = DrawableResourceKey("multipleDpiLocale")
            }
        }

        val stringResources = create(
            strings = mapOf(
                /* Default */
                K.string.test1.let { key -> key to ResourceContainer.Value(key, value = ResourceItem(key, value = "Simple", qualifiers = Qualifiers.Undefined)) },
                /* Default + Locale variants */
                K.string.test2.let { key ->
                    key to ResourceContainer.Value(
                        key,
                        values = listOf(
                            ResourceItem(key, value = "Default", qualifiers = Qualifiers.Undefined),
                            ResourceItem(key, value = "EN", qualifiers = Qualifiers(locale = Locale("en"))),
                            ResourceItem(key, value = "US", qualifiers = Qualifiers(locale = Locale("en-us"))),
                        ),
                    )
                },
                /* Locale variants only */
                K.string.test3.let { key ->
                    key to ResourceContainer.Value(
                        key,
                        values = listOf(
                            ResourceItem(key, value = "EN", qualifiers = Qualifiers(locale = Locale("en"))),
                        ),
                    )
                },
            ),
        )

        val drawableResources = create(
            drawables = mapOf(
                K.drawable.undefinedDpi.let { key ->
                    key to ResourceContainer.Value(
                        key = key,
                        value = ResourceItem(key, value = "undefined", qualifiers = Qualifiers.Undefined),
                    )
                },
                K.drawable.xxHdpiOnly.let { key ->
                    key to ResourceContainer.Value(
                        key = key,
                        value = ResourceItem(key, value = "XXHDPI", qualifiers = Qualifiers(locale = Locale.Undefined, dpi = Dpi.XXHDPI)),
                    )
                },
                K.drawable.multipleDpi.let { key ->
                    key to ResourceContainer.Value(
                        key = key,
                        values = listOf(
                            ResourceItem(key, value = "Fallback", qualifiers = Qualifiers(locale = Locale.Undefined, dpi = Dpi.Undefined)),
                            ResourceItem(key, value = "XXHDPI", qualifiers = Qualifiers(locale = Locale.Undefined, dpi = Dpi.XXHDPI)),
                            ResourceItem(key, value = "XXXHDPI", qualifiers = Qualifiers(locale = Locale.Undefined, dpi = Dpi.XXXHDPI)),
                        ),
                    )
                },
                K.drawable.multipleDpiLocale.let { key ->
                    key to ResourceContainer.Value(
                        key = key,
                        values = listOf(
                            ResourceItem(key, value = "enGB, XXHDPI", qualifiers = Qualifiers(locale = Locale("en", "GB"), dpi = Dpi.XXHDPI)),
                            ResourceItem(key, value = "enGB, Undefined", qualifiers = Qualifiers(locale = Locale("en", "GB"), dpi = Dpi.Undefined)),
                            ResourceItem(key, value = "en XXHDPI", qualifiers = Qualifiers(locale = Locale("en"), dpi = Dpi.XXHDPI)),
                            ResourceItem(key, value = "NoLocale, XXXHDPI", qualifiers = Qualifiers(locale = Locale.Undefined, dpi = Dpi.XXXHDPI)),
                            ResourceItem(key, value = "Fallback", qualifiers = Qualifiers(locale = Locale.Undefined, dpi = Dpi.Undefined)),
                        ),
                    )
                },
            ),
        )

        private fun create(
            strings: Map<StringResourceKey, ResourceContainer> = emptyMap(),
            plurals: Map<PluralResourceKey, ResourceContainer> = emptyMap(),
            drawables: Map<DrawableResourceKey, ResourceContainer> = emptyMap(),
            binary: Map<AssetResourceKey, ResourceContainer> = emptyMap(),
        ) = object : KostraResources {
            override val string: Map<StringResourceKey, ResourceContainer> = strings
            override val plural: Map<PluralResourceKey, ResourceContainer> = plurals
            override val drawable: Map<DrawableResourceKey, ResourceContainer> = drawables
            override val binary: Map<AssetResourceKey, ResourceContainer> = binary
        }
    }
}
