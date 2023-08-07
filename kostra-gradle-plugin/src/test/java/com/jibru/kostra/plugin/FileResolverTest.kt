package com.jibru.kostra.plugin

import com.google.common.truth.Truth.assertThat
import com.jibru.kostra.internal.Dpi
import com.jibru.kostra.internal.Locale
import com.jibru.kostra.internal.Plural
import com.jibru.kostra.internal.Plural.Companion.toPluralList
import com.jibru.kostra.internal.Qualifiers
import com.jibru.kostra.internal.ext.takeIfNotEmpty
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import test.resources
import test.testResources
import java.io.File

class FileResolverTest {

    @Test
    fun `resolve simple`() = testResources {
        addFile("drawable/sample.xml")
        addStrings("values/strings.xml", strings = listOf("item1", "item2"))
        buildResources()

        val items = FileResolver().resolve(listOf(resourcesRoot))
        assertThat(items).hasSize(3)

        assertThat(items).containsExactly(
            ResItem.FileRes("sample", File(resourcesRoot, "drawable/sample.xml"), Qualifiers.Undefined, ResItem.Drawable, root = resourcesRoot),
            ResItem.StringRes("item1", "item1", Qualifiers.Undefined),
            ResItem.StringRes("item2", "item2", Qualifiers.Undefined),
        )
    }

    @Test
    fun `resolve multipleGroups`() = testResources {
        addFile("drawable/sample.xml")
        addFile("raw/sample.xml")
        addFile("sound/xyz.mp3")
        addStrings("string/strings.xml", strings = listOf("item1", "item2"))
        buildResources()

        val items = FileResolver().resolve(listOf(resourcesRoot))
        assertThat(items).containsExactly(
            ResItem.FileRes("sample", File(resourcesRoot, "drawable/sample.xml"), Qualifiers.Undefined, ResItem.Drawable, root = resourcesRoot),
            ResItem.FileRes("sample", File(resourcesRoot, "raw/sample.xml"), Qualifiers.Undefined, "raw", root = resourcesRoot),
            ResItem.FileRes("xyz", File(resourcesRoot, "sound/xyz.mp3"), Qualifiers.Undefined, "sound", root = resourcesRoot),
            ResItem.StringRes("item1", "item1", Qualifiers.Undefined),
            ResItem.StringRes("item2", "item2", Qualifiers.Undefined),
        )
    }

    @Test
    fun `resolve WHEN multiple same strings THEN distinct by latest`() = testResources {
        addStrings("src1/strings.xml", strings = mapOf("item1" to "src1Item1", "item2" to "src1Item2"))
        addStrings("src2/strings.xml", strings = mapOf("item1" to "src2Item1", "item2" to "src2Item2"))
        buildResources()

        val items = FileResolver().resolve(listOf(resourcesRoot))
        assertThat(items).containsExactly(
            ResItem.StringRes("item1", "src2Item1", Qualifiers.Undefined),
            ResItem.StringRes("item2", "src2Item2", Qualifiers.Undefined),
        )
    }

    @Test
    fun `resolve WHEN different image types`() = testResources {
        addFile("drawable/imagePng.png")
        addFile("drawable/imageWebp.webp")
        addFile("drawable/imageJpg.jpg")
        addFile("drawable/imageJpeg.jpeg")
        addFile("drawable/imageBmp.bmp")
        buildResources()

        val items = FileResolver().resolve(listOf(resourcesRoot))
        assertThat(items).containsExactly(
            ResItem.FileRes("imagePng", File(resourcesRoot, "drawable/imagePng.png"), Qualifiers.Undefined, group = ResItem.Drawable, root = resourcesRoot),
            ResItem.FileRes("imageWebp", File(resourcesRoot, "drawable/imageWebp.webp"), Qualifiers.Undefined, group = ResItem.Drawable, root = resourcesRoot),
            ResItem.FileRes("imageJpg", File(resourcesRoot, "drawable/imageJpg.jpg"), Qualifiers.Undefined, group = ResItem.Drawable, root = resourcesRoot),
            ResItem.FileRes("imageJpeg", File(resourcesRoot, "drawable/imageJpeg.jpeg"), Qualifiers.Undefined, group = ResItem.Drawable, root = resourcesRoot),
            ResItem.FileRes("imageBmp", File(resourcesRoot, "drawable/imageBmp.bmp"), Qualifiers.Undefined, group = ResItem.Drawable, root = resourcesRoot),
        )
    }

    @Test
    fun `resolve WHEN different image configuration with default settings`() = testResources {
        addFile("drawable/imagePng.png")
        addFile("drawable/imageBin.bin")
        addFile("obrazky/image.png")
        buildResources()

        val items = FileResolver().resolve(listOf(resourcesRoot))
        assertThat(items).containsExactly(
            ResItem.FileRes("imagePng", File(resourcesRoot, "drawable/imagePng.png"), Qualifiers.Undefined, group = ResItem.Drawable, root = resourcesRoot),
            ResItem.FileRes("imageBin", File(resourcesRoot, "drawable/imageBin.bin"), Qualifiers.Undefined, group = ResItem.Drawable, root = resourcesRoot),
            ResItem.FileRes("image", File(resourcesRoot, "obrazky/image.png"), Qualifiers.Undefined, group = "obrazky", root = resourcesRoot),
        )
    }

    @Test
    fun `resolve WHEN different image configuration with custom settings`() = testResources {
        addFile("DRAWABLE/image.png")
        addFile("drawable_MEMES/MEME.bin")
        addFile("icon/icon.ICO")
        addFile("iCOns/ICONS.bmp")
        addFile("iconky/iCONKy.bin")
        buildResources()

        val default = FileResolverConfig()
        val items = FileResolver(
            config = FileResolverConfig(
                keyMapper = { key, _ -> key.lowercase() },
                drawableGroups = default.drawableGroups + setOf("icon.*".toRegex()),
                drawableExtensions = default.drawableExtensions + setOf("bin", "ico"),
            ),
        ).resolve(listOf(resourcesRoot))

        assertThat(items).containsExactly(
            ResItem.FileRes("image", File(resourcesRoot, "DRAWABLE/image.png"), Qualifiers.Undefined, group = ResItem.Drawable, root = resourcesRoot),
            ResItem.FileRes("meme", File(resourcesRoot, "drawable_MEMES/MEME.bin"), Qualifiers.Undefined, group = ResItem.Drawable, root = resourcesRoot),
            ResItem.FileRes("icon", File(resourcesRoot, "icon/icon.ICO"), Qualifiers.Undefined, group = ResItem.Drawable, root = resourcesRoot),
            ResItem.FileRes("icons", File(resourcesRoot, "iCOns/ICONS.bmp"), Qualifiers.Undefined, group = ResItem.Drawable, root = resourcesRoot),
            ResItem.FileRes("iconky", File(resourcesRoot, "iconky/iCONKy.bin"), Qualifiers.Undefined, group = ResItem.Drawable, root = resourcesRoot),
        )
    }

    @Test
    fun `resolve WHEN deeper structure`() = testResources {
        addFile("drawable/flat.webp")
        addFile("drawable/buildings/tower.webp")
        addFile("drawable/cars/car.png")
        buildResources()

        val items = FileResolver().resolve(listOf(resourcesRoot))
        assertThat(items).containsExactly(
            ResItem.FileRes("flat", File(resourcesRoot, "drawable/flat.webp"), Qualifiers.Undefined, group = ResItem.Drawable, root = resourcesRoot),
            ResItem.FileRes("tower", File(resourcesRoot, "drawable/buildings/tower.webp"), Qualifiers.Undefined, group = ResItem.Drawable, root = resourcesRoot),
            ResItem.FileRes("car", File(resourcesRoot, "drawable/cars/car.png"), Qualifiers.Undefined, group = ResItem.Drawable, root = resourcesRoot),
        )
    }

    @Test
    fun `resolve WHEN drawable with DPI qualifiers`() = testResources {
        val dpis = Dpi.values().sortedBy { it.density }
        dpis.forEach { dpi ->
            val dashQualifier = dpi.qualifier.takeIfNotEmpty()?.let { "-$it" } ?: ""
            addFile("drawable$dashQualifier/image.png")
        }
        buildResources()

        val items = FileResolver().resolve(listOf(resourcesRoot))
        assertThat(items).containsExactly(
            ResItem.FileRes("image", File(resourcesRoot, "drawable/image.png"), Qualifiers(dpi = Dpi.Undefined), group = ResItem.Drawable, root = resourcesRoot),
            ResItem.FileRes("image", File(resourcesRoot, "drawable-nodpi/image.png"), Qualifiers(dpi = Dpi.NoDpi), group = ResItem.Drawable, root = resourcesRoot),
            ResItem.FileRes("image", File(resourcesRoot, "drawable-ldpi/image.png"), Qualifiers(dpi = Dpi.LDPI), group = ResItem.Drawable, root = resourcesRoot),
            ResItem.FileRes("image", File(resourcesRoot, "drawable-mdpi/image.png"), Qualifiers(dpi = Dpi.MDPI), group = ResItem.Drawable, root = resourcesRoot),
            ResItem.FileRes("image", File(resourcesRoot, "drawable-tvdpi/image.png"), Qualifiers(dpi = Dpi.TVDPI), group = ResItem.Drawable, root = resourcesRoot),
            ResItem.FileRes("image", File(resourcesRoot, "drawable-hdpi/image.png"), Qualifiers(dpi = Dpi.HDPI), group = ResItem.Drawable, root = resourcesRoot),
            ResItem.FileRes("image", File(resourcesRoot, "drawable-xhdpi/image.png"), Qualifiers(dpi = Dpi.XHDPI), group = ResItem.Drawable, root = resourcesRoot),
            ResItem.FileRes("image", File(resourcesRoot, "drawable-xxhdpi/image.png"), Qualifiers(dpi = Dpi.XXHDPI), group = ResItem.Drawable, root = resourcesRoot),
            ResItem.FileRes("image", File(resourcesRoot, "drawable-xxxhdpi/image.png"), Qualifiers(dpi = Dpi.XXXHDPI), group = ResItem.Drawable, root = resourcesRoot),
        )
    }

    @Test
    fun `resolve WHEN when multiple qualifiers`() = testResources {
        addFile("drawable/image.png")
        addFile("drawable-en/image.png")
        addFile("drawable-en-rGB/image.png")
        addFile("drawable-hdpi-en-rGB/image.png")
        addFile("drawable-hdpi-en-rUS/image.png")
        addFile("drawable-en-rGB-xxhdpi/image.png")
        addFile("drawable-en-rGB-q1-xxhdpi/image.png")
        addFile("drawable-en-rGB-q1-xxhdpi-q2/image.png")

        buildResources()

        val items = FileResolver().resolve(listOf(resourcesRoot))
        assertThat(items).containsExactly(
            ResItem.FileRes("image", File(resourcesRoot, "drawable/image.png"), Qualifiers.Undefined, group = ResItem.Drawable, root = resourcesRoot),
            ResItem.FileRes("image", File(resourcesRoot, "drawable-en/image.png"), Qualifiers(locale = Locale("en")), group = ResItem.Drawable, root = resourcesRoot),
            ResItem.FileRes("image", File(resourcesRoot, "drawable-en-rGB/image.png"), Qualifiers(locale = Locale("en", "GB")), group = ResItem.Drawable, root = resourcesRoot),
            ResItem.FileRes(
                "image",
                File(resourcesRoot, "drawable-hdpi-en-rGB/image.png"),
                Qualifiers(locale = Locale("en", "GB"), dpi = Dpi.HDPI),
                group = ResItem.Drawable,
                root = resourcesRoot,
            ),
            ResItem.FileRes(
                "image",
                File(resourcesRoot, "drawable-hdpi-en-rUS/image.png"),
                Qualifiers(locale = Locale("en", "US"), dpi = Dpi.HDPI),
                group = ResItem.Drawable,
                root = resourcesRoot,
            ),
            ResItem.FileRes(
                "image",
                File(resourcesRoot, "drawable-en-rGB-xxhdpi/image.png"),
                Qualifiers(locale = Locale("en", "GB"), dpi = Dpi.XXHDPI),
                group = ResItem.Drawable,
                root = resourcesRoot,
            ),
        )
    }

    @Test
    fun `resolve WHEN string THEN distinct by latest`() {
        val res1 = File("build/resources-test1")
        val res2 = File("build/resources-test2")

        resources(res1, autoDelete = true) {
            addStrings("values/strings.xml", strings = mapOf("item1" to "src1Item1"))
        }.buildResources()

        resources(res2, autoDelete = true) {
            addStrings("values/strings.xml", strings = mapOf("item1" to "src2Item1"))
        }.buildResources()

        val fileResolver = FileResolver()

        assertThat(fileResolver.resolve(listOf(res1, res2))).containsExactly(
            ResItem.StringRes("item1", "src2Item1", Qualifiers.Undefined),
        )

        assertThat(fileResolver.resolve(listOf(res2, res1))).containsExactly(
            ResItem.StringRes("item1", "src1Item1", Qualifiers.Undefined),
        )
    }

    @Test
    fun `resolve WHEN multiple res sources THEN distinct by latest`() {
        val res1 = File("build/resources-test1")
        val res2 = File("build/resources-test2")

        resources(res1, autoDelete = true) {
            addStrings("values/strings.xml", strings = mapOf("item1" to "src1Item1", "item2" to "src2Item2"))
            addStrings("values-en/strings.xml", strings = mapOf("item1" to "src1Item1En", "item2" to "src1Item2En"))
        }.buildResources()

        resources(res2, autoDelete = true) {
            addStrings("values/strings.xml", strings = mapOf("item1" to "src2Item1", "item2" to "src2Item2"))
            addStrings("values-de/strings.xml", strings = mapOf("item1" to "src2Item1De", "item2" to "src2Item2De"))
            addStrings("values-en-rGB/strings.xml", strings = mapOf("item1" to "src2Item1EnGb", "item2" to "src2Item2EnGb"))
        }.buildResources()

        val items = FileResolver().resolve(listOf(res2, res1))

        assertThat(items).containsExactly(
            ResItem.StringRes("item1", "src1Item1", Qualifiers.Undefined),
            ResItem.StringRes("item2", "src2Item2", Qualifiers.Undefined),

            ResItem.StringRes("item1", "src1Item1En", Qualifiers(locale = Locale("en"))),
            ResItem.StringRes("item2", "src1Item2En", Qualifiers(locale = Locale("en"))),

            ResItem.StringRes("item1", "src2Item1De", Qualifiers(locale = Locale("de"))),
            ResItem.StringRes("item2", "src2Item2De", Qualifiers(locale = Locale("de"))),

            ResItem.StringRes("item1", "src2Item1EnGb", Qualifiers(locale = Locale("en", "GB"))),
            ResItem.StringRes("item2", "src2Item2EnGb", Qualifiers(locale = Locale("en", "GB"))),
        )
    }

    @Test
    fun `resolve WHEN plurals`() = testResources {
        addStrings(
            file = "values/strings.xml",
            strings = mapOf("item1" to "item1"),
            plurals = mapOf("dog" to mapOf("other" to "dogs", "one" to "dog")),
        )

        addStrings(
            file = "values-cs/strings.xml",
            strings = mapOf("item1" to "item1Cs"),
            plurals = mapOf("dog" to mapOf("other" to "psů", "one" to "pes", "few" to "psy", "many" to "psiska!")),
        )

        buildResources()

        val items = FileResolver().resolve(resourcesRoot)

        assertThat(items).containsExactly(
            ResItem.StringRes("item1", "item1", Qualifiers.Undefined),
            ResItem.StringRes("item1", "item1Cs", Qualifiers(locale = Locale("cs"))),

            ResItem.Plurals("dog", mapOf(Plural.Other to "dogs", Plural.One to "dog").toPluralList(), Qualifiers.Undefined),
            ResItem.Plurals(
                "dog",
                mapOf(Plural.Other to "psů", Plural.One to "pes", Plural.Few to "psy", Plural.Many to "psiska!").toPluralList(),
                Qualifiers(locale = Locale("cs")),
            ),
        )
    }

    @Test
    fun `resolve WHEN dpi qualifiers THEN fails`() {
        testResources {
            addStrings(
                file = "values-xhdpi/strings.xml",
                strings = mapOf("item1" to "item1"),
                plurals = mapOf("dog" to mapOf("other" to "dogs", "one" to "dog")),
            )
            buildResources()
            assertThrows<IllegalStateException> { FileResolver().resolve(resourcesRoot) }
        }
    }
}
