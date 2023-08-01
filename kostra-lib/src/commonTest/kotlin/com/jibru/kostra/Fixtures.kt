package com.jibru.kostra

import com.jibru.kostra.internal.KostraResources
import com.jibru.kostra.internal.Locale
import com.jibru.kostra.internal.Qualifiers
import com.jibru.kostra.internal.ResourceItem

object Fixtures {

    object Resources {

        @Suppress("ktlint")
        object K {
            object string {
                val test1 = StringResourceKey("test1")
                val test2 = StringResourceKey("test2")
                val test3 = StringResourceKey("test3")
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

        fun create(
            strings: Map<StringResourceKey, ResourceContainer> = emptyMap(),
            plurals: Map<PluralResourceKey, ResourceContainer> = emptyMap(),
            stringArrays: Map<StringArrayResourceKey, ResourceContainer> = emptyMap(),
            drawables: Map<DrawableResourceKey, ResourceContainer> = emptyMap(),
            binary: Map<ResourceKey, ResourceContainer> = emptyMap(),
        ) = object : KostraResources {
            override val strings: Map<StringResourceKey, ResourceContainer> = strings
            override val plurals: Map<PluralResourceKey, ResourceContainer> = plurals
            override val stringArrays: Map<StringArrayResourceKey, ResourceContainer> = stringArrays
            override val drawables: Map<DrawableResourceKey, ResourceContainer> = drawables
            override val binary: Map<ResourceKey, ResourceContainer> = binary
        }

        private inline fun <reified T : ResourceKey> String.key(): T = when (T::class) {
            StringResourceKey::class -> StringResourceKey(this)
            StringArrayResourceKey::class -> StringArrayResourceKey(this)
            DrawableResourceKey::class -> DrawableResourceKey(this)
            BinaryResourceKey::class -> BinaryResourceKey(this)
            else -> throw UnsupportedOperationException("invalid output: ${T::class}")
        } as T
    }
}
