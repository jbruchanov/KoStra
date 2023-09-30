package com.jibru.kostra.plugin

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class ResourcesKtGeneratorDefaultsTest {

    @Test
    fun `generateResourceProviders WHEN public & jvmInline`() {
        val result = ResourcesKtGenerator(emptyList())
            .generateResourceProviders(addJvmInline = true)
            .trim()

        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `generateResourceProviders WHEN no jvmInline`() {
        val result = ResourcesKtGenerator(emptyList())
            .generateResourceProviders(addJvmInline = false)
            .trim()

        assertThat(result).doesNotContain("JvmInline")
    }

    @Test
    fun `generateResourceProviders WHEN internal`() {
        val result = ResourcesKtGenerator(emptyList(), internalVisibility = true)
            .generateResourceProviders()
            .trim()

        assertThat(result).isEqualTo(expected.replace("value class", "internal value class"))
    }

    private val expected = """
            @file:Suppress("ktlint")

            package app

            import kotlin.Suppress
            import kotlin.jvm.JvmInline
            import com.jibru.kostra.AssetResourceKey as A
            import com.jibru.kostra.BinaryResourceKey as B
            import com.jibru.kostra.PainterResourceKey as D
            import com.jibru.kostra.PluralResourceKey as P
            import com.jibru.kostra.StringResourceKey as S

            interface AssetResourceKey : A
            @JvmInline
            value class StringResourceKey(override val key: Int) : S
            @JvmInline
            value class PluralResourceKey(override val key: Int) : P
            @JvmInline
            value class PainterResourceKey(override val key: Int) : D, AssetResourceKey
            @JvmInline
            value class BinaryResourceKey(override val key: Int) : B, AssetResourceKey
    """.trimIndent()
}
