package com.jibru.kostra.plugin

import com.google.common.truth.Truth.assertThat
import com.jibru.kostra.plugin.ext.minify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIf
import test.RealProjectRef
import test.testResources

class ResourcesKtGeneratorResourcesTest {

    @Test
    fun allResourceFiles() = testResources {
        addStrings(
            "values/strings.xml",
            strings = mapOf("item1" to "src1Item1", "item2" to "src2Item2"),
            plurals = mapOf("dog" to mapOf("other" to "dogs", "one" to "dog \"%s\"")),
        )
        addStrings("values-en/strings.xml", strings = mapOf("item1" to "src1Item1En", "item2" to "src1Item2En"))
        addStrings("values-de/strings.xml", strings = mapOf("item1" to "src2Item1De", "item2" to "src2Item2De"))
        addFile("drawable/image.png")
        addFile("drawable-en-xxhdpi/image.png")
        addFile("audio/sound.mp3")
        addFile("audio-en-rGB/sound.mp3")
        addFile("audio-de-fun/sound.mp3")
        addFile("audio-cs/sound.mp4")

        buildResources()

        val items = FileResolver().resolve(resourcesRoot)
        val gen = ResourcesKtGenerator(items, className = "com.sample.app.K")

        val result = buildString {
            val files = listOf(
                gen.generateKClass(),
                gen.generateResources(),
            )

            val div = "-".repeat(16)
            files
                .filterNotNull()
                .forEach {
                    appendLine("$div ${it.packageName}.${it.name} $div")
                    appendLine(it.minify())
                }
        }

        assertThat(result.trim()).isEqualTo(
            """
            ---------------- com.sample.app.K ----------------
            @file:Suppress("ktlint")
            package com.sample.app
            import kotlin.Suppress
            import com.jibru.kostra.BinaryResourceKey as B
            import com.jibru.kostra.PainterResourceKey as D
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
              object audio {
                val sound: B = B(1)
              }
              object drawable {
                val image: D = D(2)
              }
            }
            ---------------- com.sample.app.Resources ----------------
            @file:Suppress("ktlint")
            package com.sample.app
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
                  KLocale(4_05_00_00) to "kresources/string-de.db",
                  KLocale(5_14_00_00) to "kresources/string-en.db",
                )
              ),
              plural = PluralDatabase(
                mapOf(
                  KLocale.Undefined to "kresources/plural-default.db",
                )
              ),
              binary = FileDatabase("kresources/binary.db"),
            )
            """.trimIndent(),
        )
    }

    @Test
    @EnabledIf("hasRealProjectLocation")
    fun test() {
        val items = FileResolver().resolve(RealProjectRef.resources()!!)
        val gen = ResourcesKtGenerator(items, resDbsFolderName = "com.sample.app.K")

        val result = buildString {
            val files = listOf(
                gen.generateKClass(),
                gen.generateResources(),
            )

            val div = "-".repeat(16)
            files
                .forEach {
                    appendLine("$div ${it.packageName}.${it.name} $div")
                    appendLine(it.minify())
                }
        }
        println(result)
    }

    private fun hasRealProjectLocation() = RealProjectRef.resources() != null
}
