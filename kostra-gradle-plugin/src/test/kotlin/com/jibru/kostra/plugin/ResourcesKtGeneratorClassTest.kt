package com.jibru.kostra.plugin

import com.google.common.truth.Truth.assertThat
import com.jibru.kostra.KLocale
import com.jibru.kostra.KQualifiers
import com.jibru.kostra.icu.PluralCategory
import com.jibru.kostra.icu.PluralCategory.Companion.toPluralList
import com.jibru.kostra.plugin.ext.minify
import java.io.File
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIf
import test.RealProjectRef

class ResourcesKtGeneratorClassTest {

    @Test
    fun generateKClass() {
        val result = ResourcesKtGenerator(
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
            className = "com.sample.app.K",
        ).generateKClass().minify()

        assertThat(result).isEqualTo(
            """
            @file:Suppress("ktlint")
            package com.sample.app
            import kotlin.Suppress
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
        val gen = ResourcesKtGenerator(items = listOf(string("str1")), className = "ResQ")
        val result = gen.generateKClass().minify()
        assertThat(result).isEqualTo(
            """
            @file:Suppress("ktlint")
            import kotlin.Suppress
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
            items = listOf(
                ResItem.FileRes("imagePng", File("drawable/imagePng.png"), KQualifiers.Undefined.key, group = ResItem.Drawable, root = File(".")),
                ResItem.FileRes("imageBin", File("drawable/imageBin.bin"), KQualifiers.Undefined.key, group = ResItem.Drawable, root = File(".")),
            ),
            className = "K",
        )
        val result = gen.generateKClass().minify()
        assertThat(result).isEqualTo(
            """
            @file:Suppress("ktlint")
            import kotlin.Suppress
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
            ResItem.StringRes("item1", "item1", KQualifiers.Undefined.key),
            ResItem.StringRes("item2", "item2", KQualifiers.Undefined.key),
            ResItem.StringRes("item1", "item1Cs", KQualifiers(locale = KLocale("cs")).key),
            ResItem.StringRes("item2", "item2En", KQualifiers(locale = KLocale("en")).key),

            ResItem.Plurals("dog", ResItem.Plurals.EmptyItems, KQualifiers.Undefined.key),
            ResItem.Plurals("dog", ResItem.Plurals.EmptyItems, KQualifiers(locale = KLocale("cs")).key),
        )

        val result = ResourcesKtGenerator(items = items, className = "K").generateKClass().minify()
        assertThat(result).isEqualTo(
            """
            @file:Suppress("ktlint")
            import kotlin.Suppress
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
            ResItem.StringRes("item1", "item1", KQualifiers.Undefined.key),
            ResItem.StringRes("item2", "item2", KQualifiers.Undefined.key),
            ResItem.StringRes("item1", "item1Cs", KQualifiers(locale = KLocale("cs")).key),
            ResItem.StringRes("item2", "item2En", KQualifiers(locale = KLocale("en")).key),
            ResItem.StringRes("item2", "item2EnGb", KQualifiers(locale = KLocale("en-GB")).key),

            ResItem.Plurals("dog", ResItem.Plurals.EmptyItems, KQualifiers.Undefined.key),
            ResItem.Plurals("dog", ResItem.Plurals.EmptyItems, KQualifiers(locale = KLocale("cs")).key),

            ResItem.FileRes("image", File("x.png"), KQualifiers.Undefined.key, ResItem.Drawable, File("x")),
            ResItem.FileRes("sound", File("s.mp3"), KQualifiers.Undefined.key, "sound", File("x")),
        )

        val result = ResourcesKtGenerator(items = items, className = "K").generateResources().minify()
        assertThat(result).isEqualTo(
            """
            @file:Suppress("ktlint")
            import com.jibru.kostra.KAppResources
            import com.jibru.kostra.KLocale
            import com.jibru.kostra.`internal`.FileDatabase
            import com.jibru.kostra.`internal`.PluralDatabase
            import com.jibru.kostra.`internal`.StringDatabase
            import kotlin.Suppress
            val Resources: KAppResources = KAppResources(
              string = StringDatabase(
                mapOf(
                  KLocale.Undefined to "kresources/string-default.db",
                  KLocale(3_19_00_00) to "kresources/string-cs.db",
                  KLocale(5_14_00_00) to "kresources/string-en.db",
                  KLocale(5_14_07_02) to "kresources/string-engb.db",
                )
              ),
              plural = PluralDatabase(
                mapOf(
                  KLocale.Undefined to "kresources/plural-default.db",
                  KLocale(3_19_00_00) to "kresources/plural-cs.db",
                )
              ),
              binary = FileDatabase("kresources/binary.db"),
            )
            """.trimIndent(),
        )
    }

    @EnabledIf("hasRealProjectRef")
    @Test
    fun `generateKClassX`() {
        val items = FileResolver().resolve(RealProjectRef.resources()!!)
        val x = ResourcesKtGenerator(items = items, className = "K")
            .generateKClass().minify()
        println(x)
    }

    private fun hasRealProjectRef() = RealProjectRef.isDefined()
    private fun string(key: String) = ResItem.StringRes(key, value = "", qualifiersKey = KQualifiers.Undefined.key)
    private fun plurals(key: String, items: List<String?>) = ResItem.Plurals(key, items = items, qualifiersKey = KQualifiers.Undefined.key)
    private fun file(key: String, group: String) = ResItem.FileRes(key, File("X"), qualifiersKey = KQualifiers.Undefined.key, group = group, root = File("."))
}