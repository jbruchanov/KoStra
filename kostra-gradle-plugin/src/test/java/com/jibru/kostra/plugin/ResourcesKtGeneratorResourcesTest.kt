package com.jibru.kostra.plugin

import com.jibru.kostra.database.StringDatabase
import com.jibru.kostra.internal.ext.takeIfNotEmpty
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIf
import test.testResources
import java.io.File
import java.io.FileOutputStream
import java.util.Properties
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class ResourcesKtGeneratorResourcesTest {

    //currently no way to go
    @Test
    @Disabled
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
                gen.generateResourceProviders(),
            ) + gen.generateResourceClasses()

            val div = "-".repeat(16)
            files
                .filterNotNull()
                .forEach {
                    appendLine("$div ${it.packageName}.${it.name} $div")
                    appendLine(it.toString())
                }
        }
    }

    @Test
    @EnabledIf("hasRealProjectLocation")
    fun test() {
        val sources = File(realProjectResources()!!).listFiles()!!.filter { it.name.startsWith("res") }
        val items = FileResolver().resolve(sources)
        val keysWithQualifiers = items.filter { it is ResItem.StringRes }
            .groupBy { it.key }
            .toSortedMap()
            .mapValues { it.value.associateBy { it.qualifiers.locale.languageRegion } }

        val locales = keysWithQualifiers.map { it.value.values.map { it.qualifiers.locale.languageRegion } }
            .flatten()
            .distinct()

        val valuesPerLocale = locales.map {
            it to keysWithQualifiers.mapValues { locale -> (locale.value[it] as? StringValueResItem)?.value }
        }

        val compress = true
        val db = StringDatabase()
        valuesPerLocale.forEach { (locale, items) ->
            val values = items.values
            db.set(values)
            val l = locale.takeIfNotEmpty() ?: "default"
            val data = db.save()
            File("build/$l.db").writeBytes(data)
            if (compress) {
                ZipOutputStream(FileOutputStream(File("build/$l.db.zip"))).apply {
                    putNextEntry(ZipEntry("$l.db"))
                    write(data)
                    closeEntry()
                    close()
                }
            }
        }
    }

    private fun StringDatabase.getAll() = buildList<String?> {
        val db = this@getAll
        for (i in 0 until db.count()) {
            add(db.get(i))
        }
    }

    private fun hasRealProjectLocation() = realProjectResources() != null
    private fun realProjectResources() = File("../local.properties")
        .takeIf { it.exists() }
        ?.let {
            Properties().apply { load(it.bufferedReader()) }["realProjectResources"]?.toString()
        }
}
