package com.jibru.kostra

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.String

class ResourcesKtGeneratorTest {

    @Test
    fun `generateKClass no package`() {
        val gen = ResourcesKtGenerator("", "ResQ")
        val result = gen.generateKClass(listOf(string("str1"))).trim()
        assertThat(result).isEqualTo(
            """
            import kotlin.String

            public object ResQ {
              public object string {
                public const val str1: String = "str1"
              }
            }
            """.trimIndent(),
        )
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

            import kotlin.String

            public object K {
              public object `_` {
                public const val test: String = "test"
              }
              public object drawable {
                public const val icon2: String = "icon2"
                public const val img: String = "img"
              }
              public object raw {
                public const val audio1: String = "audio1"
              }
              public object string {
                public const val `2str`: String = "2str"
                public const val str1: String = "str1"
              }
              public object stringArray {
                public const val `class`: String = "class"
              }
            }
            """.trimIndent(),
        )
    }

    private fun string(key: String) = ResourceItem.StringRes(key, value = "", locale = "")
    private fun stringArray(key: String) = ResourceItem.StringArray(key, items = emptyList(), locale = "")
    private fun file(key: String, category: String) = ResourceItem.FileRes(key, File("X"), locale = "", category = category)
}
