package com.jibru.kostra.plugin

import com.google.common.truth.Truth.assertThat
import com.jibru.kostra.internal.Locale
import com.jibru.kostra.internal.Plural
import com.jibru.kostra.internal.Plural.Companion.toPluralList
import com.jibru.kostra.internal.Qualifiers
import org.junit.jupiter.api.Test
import java.io.File

class ResourcesKtGeneratorKClassTest {

    @Test
    fun generateKClass() {
        val result = ResourcesKtGenerator(
            packageName = "com.sample.app",
            className = "K",
            listOf(
                string("str1"),
                string("2str"),
                file("img", group = "Drawable"),
                file("icon2", group = "drawable"),
                file("audio1", group = "raw"),
                file("test", group = "_"),
            ),
        ).generateKClass().toString().trim()

        assertThat(result).isEqualTo(
            """
            package com.sample.app

            import com.jibru.kostra.BinaryResourceKey
            import com.jibru.kostra.DrawableResourceKey
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
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `generateKClass no package`() {
        val gen = ResourcesKtGenerator(packageName = "", className = "ResQ", items = listOf(string("str1")))
        val result = gen.generateKClass().toString().trim()
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
    fun `generateKClass WHEN drawable with unexpected extension`() {
        val gen = ResourcesKtGenerator(
            packageName = "",
            items = listOf(
                ResItem.FileRes("imagePng", File("drawable/imagePng.png"), Qualifiers.Undefined, group = ResItem.Drawable, root = File(".")),
                ResItem.FileRes("imageBin", File("drawable/imageBin.bin"), Qualifiers.Undefined, group = ResItem.Drawable, root = File(".")),
            ),
        )
        val result = gen.generateKClass().toString().trim()
        assertThat(result).isEqualTo(
            """
            import com.jibru.kostra.DrawableResourceKey

            public object K {
              public object drawable {
                public val imageBin: DrawableResourceKey = DrawableResourceKey("imageBin")

                public val imagePng: DrawableResourceKey = DrawableResourceKey("imagePng")
              }
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `generateKClass WHEN strings and plurals`() {
        val items = listOf(
            ResItem.StringRes("item1", "item1", Qualifiers.Undefined),
            ResItem.StringRes("item1", "item1Fun", Qualifiers(others = setOf("fun"))),
            ResItem.StringRes("item1", "item1Cs", Qualifiers(locale = Locale("cs"))),
            ResItem.StringRes("item1", "item1FunCs", Qualifiers(locale = Locale("cs"), others = setOf("fun"))),

            ResItem.Plurals("dog", mapOf(Plural.Other to "dogs", Plural.One to "dog").toPluralList(), Qualifiers.Undefined),
            ResItem.Plurals("dog", mapOf(Plural.Other to "doggos", Plural.One to "doggo").toPluralList(), Qualifiers(others = setOf("fun"))),
            ResItem.Plurals(
                "dog",
                mapOf(Plural.Other to "psů", Plural.One to "pes", Plural.Few to "psy", Plural.Other to "psiska!").toPluralList(),
                Qualifiers(locale = Locale("cs")),
            ),
            ResItem.Plurals(
                "dog",
                mapOf(Plural.Other to "!psů!", Plural.One to "!pes!", Plural.Many to "!psy!", Plural.Other to "!psiska!").toPluralList(),
                Qualifiers(
                    locale = Locale("cs"),
                    others = setOf("fun"),
                ),
            ),
        )

        val result = ResourcesKtGenerator(packageName = "", items = items).generateKClass().toString().trim()
        assertThat(result).isEqualTo(
            """
            import com.jibru.kostra.PluralResourceKey
            import com.jibru.kostra.StringResourceKey

            public object K {
              public object plural {
                public val dog: PluralResourceKey = PluralResourceKey("dog")
              }

              public object string {
                public val item1: StringResourceKey = StringResourceKey("item1")
              }
            }
            """.trimIndent(),
        )
    }

    private fun string(key: String) = ResItem.StringRes(key, value = "", qualifiers = Qualifiers.Undefined)
    private fun file(key: String, group: String) = ResItem.FileRes(key, File("X"), qualifiers = Qualifiers.Undefined, group = group, root = File("."))
}
