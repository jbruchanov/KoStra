package com.jibru.kostra.plugin

import com.google.common.truth.Truth.assertThat
import com.jibru.kostra.Locale
import com.jibru.kostra.Qualifiers
import com.jibru.kostra.icu.PluralCategory
import com.jibru.kostra.icu.PluralCategory.Companion.toPluralList
import com.jibru.kostra.plugin.ext.minify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIf
import test.RealProjectRef
import java.io.File

class ResourcesKtGeneratorClassTest {

    @Test
    fun generateKClass() {
        val result = ResourcesKtGenerator(
            className = "com.sample.app.K",
            listOf(
                string("str1"),
                string("2str"),
                string("str1"),
                plurals("p2", mapOf(PluralCategory.Other to "other").toPluralList()),
                plurals("p1", mapOf(PluralCategory.One to "one").toPluralList()),
                file("img", group = ResItem.Drawable),
                file("icon2", group = ResItem.Drawable),
                file("audio1", group = "raw"),
                file("test", group = "_"),
            ),
        ).generateKClass().minify()

        assertThat(result).isEqualTo(
            """
            package com.sample.app
            import com.jibru.kostra.BinaryResourceKey as B
            import com.jibru.kostra.PainterResourceKey as D
            import com.jibru.kostra.PluralResourceKey as P
            import com.jibru.kostra.StringResourceKey as S
            object K {
              object string {
                val `2str`: S = S(0)
                val str1: S = S(1)
              }
              object plural {
                val p1: P = P(0)
                val p2: P = P(1)
              }
              object `_` {
                val test: B = B(1)
              }
              object drawable {
                val icon2: D = D(2)
                val img: D = D(3)
              }
              object raw {
                val audio1: B = B(4)
              }
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `generateKClass no package`() {
        val gen = ResourcesKtGenerator(className = "ResQ", items = listOf(string("str1")))
        val result = gen.generateKClass().minify()
        assertThat(result).isEqualTo(
            """
            import com.jibru.kostra.StringResourceKey as S
            object ResQ {
              object string {
                val str1: S = S(0)
              }
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `generateKClass WHEN drawable with unexpected extension`() {
        val gen = ResourcesKtGenerator(
            className = "K",
            items = listOf(
                ResItem.FileRes("imagePng", File("drawable/imagePng.png"), Qualifiers.Undefined, group = ResItem.Drawable, root = File(".")),
                ResItem.FileRes("imageBin", File("drawable/imageBin.bin"), Qualifiers.Undefined, group = ResItem.Drawable, root = File(".")),
            ),
        )
        val result = gen.generateKClass().minify()
        assertThat(result).isEqualTo(
            """
            import com.jibru.kostra.PainterResourceKey as D
            object K {
              object drawable {
                val imageBin: D = D(1)
                val imagePng: D = D(2)
              }
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `generateKClass WHEN multiple strings and plurals`() {
        val items = listOf(
            ResItem.StringRes("item1", "item1", Qualifiers.Undefined),
            ResItem.StringRes("item2", "item2", Qualifiers.Undefined),
            ResItem.StringRes("item1", "item1Cs", Qualifiers(locale = Locale("cs"))),
            ResItem.StringRes("item2", "item2En", Qualifiers(locale = Locale("en"))),

            ResItem.Plurals("dog", ResItem.Plurals.EmptyItems, Qualifiers.Undefined),
            ResItem.Plurals("dog", ResItem.Plurals.EmptyItems, Qualifiers(locale = Locale("cs"))),
        )

        val result = ResourcesKtGenerator(className = "K", items = items).generateKClass().minify()
        assertThat(result).isEqualTo(
            """
            import com.jibru.kostra.PluralResourceKey as P
            import com.jibru.kostra.StringResourceKey as S
            object K {
              object string {
                val item1: S = S(0)
                val item2: S = S(1)
              }
              object plural {
                val dog: P = P(0)
              }
            }
            """.trimIndent(),
        )
    }

    @Test
    fun generateResources() {
        val items = listOf(
            ResItem.StringRes("item1", "item1", Qualifiers.Undefined),
            ResItem.StringRes("item2", "item2", Qualifiers.Undefined),
            ResItem.StringRes("item1", "item1Cs", Qualifiers(locale = Locale("cs"))),
            ResItem.StringRes("item2", "item2En", Qualifiers(locale = Locale("en"))),
            ResItem.StringRes("item2", "item2EnGb", Qualifiers(locale = Locale("en-GB"))),

            ResItem.Plurals("dog", ResItem.Plurals.EmptyItems, Qualifiers.Undefined),
            ResItem.Plurals("dog", ResItem.Plurals.EmptyItems, Qualifiers(locale = Locale("cs"))),

            ResItem.FileRes("image", File("x.png"), Qualifiers.Undefined, ResItem.Drawable, File("x")),
            ResItem.FileRes("sound", File("s.mp3"), Qualifiers.Undefined, "sound", File("x")),
        )

        val result = ResourcesKtGenerator(className = "K", items = items).generateResources().minify()
        assertThat(result).isEqualTo(
            """
        import com.jibru.kostra.AppResources
        import com.jibru.kostra.Locale
        import com.jibru.kostra.`internal`.FileDatabase
        import com.jibru.kostra.`internal`.PluralDatabase
        import com.jibru.kostra.`internal`.StringDatabase
        val Resources: AppResources = AppResources(
          string = StringDatabase(
            mapOf(
              Locale.Undefined to "__kostra/string-default.db",
              Locale(3_19_00_00) to "__kostra/string-cs.db",
              Locale(5_14_00_00) to "__kostra/string-en.db",
              Locale(5_14_07_02) to "__kostra/string-engb.db",
            )
          ),
          plural = PluralDatabase(
            mapOf(
              Locale.Undefined to "__kostra/plural-default.db",
              Locale(3_19_00_00) to "__kostra/plural-cs.db",
            )
          ),
          binary = FileDatabase("__kostra/binary.db"),
        )
            """.trimIndent(),
        )
    }

    @EnabledIf("hasRealProjectRef")
    @Test
    fun `generateKClassX`() {
        val items = FileResolver().resolve(RealProjectRef.resources()!!)
        val x = ResourcesKtGenerator(className = "K", items = items)
            .generateKClass().minify()
        println(x)
    }

    private fun hasRealProjectRef() = RealProjectRef.isDefined()
    private fun string(key: String) = ResItem.StringRes(key, value = "", qualifiers = Qualifiers.Undefined)
    private fun plurals(key: String, items: List<String?>) = ResItem.Plurals(key, items = items, qualifiers = Qualifiers.Undefined)
    private fun file(key: String, group: String) = ResItem.FileRes(key, File("X"), qualifiers = Qualifiers.Undefined, group = group, root = File("."))
}
