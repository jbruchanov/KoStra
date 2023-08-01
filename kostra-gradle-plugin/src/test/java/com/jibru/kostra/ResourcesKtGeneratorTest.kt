package com.jibru.kostra

import com.google.common.truth.Truth.assertThat
import com.jibru.kostra.internal.Qualifiers
import com.jibru.kostra.plugin.ResItem
import com.jibru.kostra.plugin.ResourcesKtGenerator
import org.junit.jupiter.api.Test
import java.io.File

class ResourcesKtGeneratorTest {

    @Test
    fun `generateKClass no package`() {
        val gen = ResourcesKtGenerator("", "ResQ")
        val result = gen.generateKClass(listOf(string("str1"))).trim()
        assertThat(result).isEqualTo(
            """
            import com.jibru.kostra.StringResourceKey

            public object ResQ {
              public object string {
                public val str1: StringResourceKey = StringResourceKey("str1")
              }
            }
            """.trimIndent(),
        )
    }

    @Test
    fun test() {
        val gen = ResourcesKtGenerator("com.sample.app", "K")
        val result = gen.generateKClass(
            listOf(
                string("str1"),
                string("2str"),
                stringArray("class"),
                file("img", category = "Drawable"),
                file("icon2", category = "drawable"),
                file("audio1", category = "raw"),
                file("test", category = "_"),
            ),
        ).trim()
        println(result)
    }

    @Test
    fun generateKClass() {
        val gen = ResourcesKtGenerator("com.sample.app", "K")
        val result = gen.generateKClass(
            listOf(
                string("str1"),
                string("2str"),
                stringArray("class"),
                file("img", category = "Drawable"),
                file("icon2", category = "drawable"),
                file("audio1", category = "raw"),
                file("test", category = "_"),
            ),
        ).trim()
        assertThat(result).isEqualTo(
            """
            package com.sample.app

            import com.jibru.kostra.BinaryResourceKey
            import com.jibru.kostra.DrawableResourceKey
            import com.jibru.kostra.StringArrayResourceKey
            import com.jibru.kostra.StringResourceKey

            public object K {
              public object `_` {
                public val test: BinaryResourceKey = BinaryResourceKey("test")
              }
              public object drawable {
                public val icon2: DrawableResourceKey = DrawableResourceKey("icon2")
                public val img: BinaryResourceKey = BinaryResourceKey("img")
              }
              public object raw {
                public val audio1: BinaryResourceKey = BinaryResourceKey("audio1")
              }
              public object string {
                public val `2str`: StringResourceKey = StringResourceKey("2str")
                public val str1: StringResourceKey = StringResourceKey("str1")
              }
              public object stringArray {
                public val `class`: StringArrayResourceKey = StringArrayResourceKey("class")
              }
            }
            """.trimIndent(),
        )
    }

    private fun string(key: String) = ResItem.StringRes(key, value = "", qualifiers = Qualifiers.Undefined)
    private fun stringArray(key: String) = ResItem.StringArray(key, items = emptyList(), qualifiers = Qualifiers.Undefined)
    private fun file(key: String, category: String) = ResItem.FileRes(key, File("X"), qualifiers = Qualifiers.Undefined, category = category)
}
