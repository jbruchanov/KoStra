package com.jibru.kostra.plugin

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import test.IOTestTools.readTextFile
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
        val gen = ResourcesKtGenerator(
            "com.sample.app",
            "K",
            items,
        )

        val result = buildString {
            val files = listOf(
                gen.generateKClass(),
                gen.generateSupportResources(),
                gen.generateCreateAppResources(),
            ) + gen.generateResourceClasses()

            val div = "-".repeat(16)
            files
                .filterNotNull()
                .forEach {
                    appendLine("$div ${it.packageName}.${it.name} $div")
                    appendLine(it.toString())
                }
        }

        assertThat(result).isEqualTo(readTextFile("resources.txt"))
    }
}
