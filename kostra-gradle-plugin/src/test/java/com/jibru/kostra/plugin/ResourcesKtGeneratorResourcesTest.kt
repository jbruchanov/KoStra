package com.jibru.kostra.plugin

import com.google.common.truth.Truth.assertThat
import com.jibru.kostra.plugin.ext.minify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIf
import test.RealProjectRef
import test.testResources

class ResourcesKtGeneratorResourcesTest {

    //currently no way to go
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
        val gen = ResourcesKtGenerator(
            "com.sample.app",
            "K",
            items,
        )

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
            package com.sample.app
            import com.jibru.kostra.BinaryResourceKey as B
            import com.jibru.kostra.DrawableResourceKey as D
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
            package com.sample.app
            import com.jibru.kostra.`internal`.AppResources
            import com.jibru.kostra.`internal`.FileDatabase
            import com.jibru.kostra.`internal`.Locale
            import com.jibru.kostra.`internal`.PluralDatabase
            import com.jibru.kostra.`internal`.StringDatabase
            val Resources: AppResources = AppResources(
              string = StringDatabase(
                mapOf(
                  Locale.Undefined to "__kostra/string-default.db",
                  Locale(4_05_00_00) to "__kostra/string-de.db",
                  Locale(5_14_00_00) to "__kostra/string-en.db",
                )
              ),
              plural = PluralDatabase(
                mapOf(
                  Locale.Undefined to "__kostra/plural-default.db",
                )
              ),
              binary = FileDatabase("__kostra/binary.db"),
            )
            """.trimIndent(),
        )
    }

    @Test
    @EnabledIf("hasRealProjectLocation")
    fun test() {
        val items = FileResolver().resolve(RealProjectRef.resources()!!)
        val gen = ResourcesKtGenerator(
            "com.sample.app",
            "K",
            items,
        )

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
