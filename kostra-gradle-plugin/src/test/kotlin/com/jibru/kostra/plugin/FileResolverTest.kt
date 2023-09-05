package com.jibru.kostra.plugin

import com.google.common.truth.Truth.assertThat
import com.jibru.kostra.KDpi
import com.jibru.kostra.KLocale
import com.jibru.kostra.KQualifiers
import com.jibru.kostra.icu.PluralCategory
import com.jibru.kostra.icu.PluralCategory.Companion.toPluralList
import com.jibru.kostra.plugin.ext.takeIfNotEmpty
import java.io.File
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import test.resources
import test.testResources

class FileResolverTest {

    @Test
    fun `resolve simple`() = testResources {
        addFile("drawable/sample.xml")
        addStrings("values/strings.xml", strings = listOf("item1", "item2"))
        buildResources()

        val items = FileResolver().resolve(listOf(resourcesRoot))
        assertThat(items).hasSize(3)

        assertThat(items).containsExactly(
            ResItem.FileRes("sample", file("drawable/sample.xml"), KQualifiers.Undefined.key, ResItem.Drawable, root = resourcesRoot),
            ResItem.StringRes("item1", "item1", KQualifiers.Undefined.key),
            ResItem.StringRes("item2", "item2", KQualifiers.Undefined.key),
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
            ResItem.FileRes("sample", file("drawable/sample.xml"), KQualifiers.Undefined.key, ResItem.Drawable, root = resourcesRoot),
            ResItem.FileRes("sample", file("raw/sample.xml"), KQualifiers.Undefined.key, "raw", root = resourcesRoot),
            ResItem.FileRes("xyz", file("sound/xyz.mp3"), KQualifiers.Undefined.key, "sound", root = resourcesRoot),
            ResItem.StringRes("item1", "item1", KQualifiers.Undefined.key),
            ResItem.StringRes("item2", "item2", KQualifiers.Undefined.key),
        )
    }

    @Test
    fun `resolve WHEN multiple same strings THEN distinct by latest`() = testResources {
        addStrings("src1/strings.xml", strings = mapOf("item1" to "src1Item1", "item2" to "src1Item2"))
        addStrings("src2/strings.xml", strings = mapOf("item1" to "src2Item1", "item2" to "src2Item2"))
        buildResources()

        val items = FileResolver().resolve(listOf(resourcesRoot))
        assertThat(items).containsExactly(
            ResItem.StringRes("item1", "src2Item1", KQualifiers.Undefined.key),
            ResItem.StringRes("item2", "src2Item2", KQualifiers.Undefined.key),
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
            ResItem.FileRes("imagePng", file("drawable/imagePng.png"), KQualifiers.Undefined.key, group = ResItem.Drawable, root = resourcesRoot),
            ResItem.FileRes("imageWebp", file("drawable/imageWebp.webp"), KQualifiers.Undefined.key, group = ResItem.Drawable, root = resourcesRoot),
            ResItem.FileRes("imageJpg", file("drawable/imageJpg.jpg"), KQualifiers.Undefined.key, group = ResItem.Drawable, root = resourcesRoot),
            ResItem.FileRes("imageJpeg", file("drawable/imageJpeg.jpeg"), KQualifiers.Undefined.key, group = ResItem.Drawable, root = resourcesRoot),
            ResItem.FileRes("imageBmp", file("drawable/imageBmp.bmp"), KQualifiers.Undefined.key, group = ResItem.Drawable, root = resourcesRoot),
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
            ResItem.FileRes("imagePng", file("drawable/imagePng.png"), KQualifiers.Undefined.key, group = ResItem.Drawable, root = resourcesRoot),
            ResItem.FileRes("imageBin", file("drawable/imageBin.bin"), KQualifiers.Undefined.key, group = ResItem.Drawable, root = resourcesRoot),
            ResItem.FileRes("image", file("obrazky/image.png"), KQualifiers.Undefined.key, group = "obrazky", root = resourcesRoot),
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
            ResItem.FileRes("image", file("DRAWABLE/image.png"), KQualifiers.Undefined.key, group = ResItem.Drawable, root = resourcesRoot),
            ResItem.FileRes("meme", file("drawable_MEMES/MEME.bin"), KQualifiers.Undefined.key, group = ResItem.Drawable, root = resourcesRoot),
            ResItem.FileRes("icon", file("icon/icon.ICO"), KQualifiers.Undefined.key, group = ResItem.Drawable, root = resourcesRoot),
            ResItem.FileRes("icons", file("iCOns/ICONS.bmp"), KQualifiers.Undefined.key, group = ResItem.Drawable, root = resourcesRoot),
            ResItem.FileRes("iconky", file("iconky/iCONKy.bin"), KQualifiers.Undefined.key, group = ResItem.Drawable, root = resourcesRoot),
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
            ResItem.FileRes("flat", file("drawable/flat.webp"), KQualifiers.Undefined.key, group = ResItem.Drawable, root = resourcesRoot),
            ResItem.FileRes("tower", file("drawable/buildings/tower.webp"), KQualifiers.Undefined.key, group = ResItem.Drawable, root = resourcesRoot),
            ResItem.FileRes("car", file("drawable/cars/car.png"), KQualifiers.Undefined.key, group = ResItem.Drawable, root = resourcesRoot),
        )
    }

    @Test
    fun `resolve WHEN drawable with DPI qualifiers`() = testResources {
        val dpis = KDpi.values().sortedBy { it.density }
        dpis.forEach { dpi ->
            val dashQualifier = dpi.qualifier.takeIfNotEmpty()?.let { "-$it" } ?: ""
            addFile("drawable$dashQualifier/image.png")
        }
        buildResources()

        val items = FileResolver().resolve(listOf(resourcesRoot))
        assertThat(items).containsExactly(
            ResItem.FileRes("image", file("drawable/image.png"), KQualifiers(dpi = KDpi.Undefined).key, group = ResItem.Drawable, root = resourcesRoot),
            ResItem.FileRes("image", file("drawable-nodpi/image.png"), KQualifiers(dpi = KDpi.NoDpi).key, group = ResItem.Drawable, root = resourcesRoot),
            ResItem.FileRes("image", file("drawable-ldpi/image.png"), KQualifiers(dpi = KDpi.LDPI).key, group = ResItem.Drawable, root = resourcesRoot),
            ResItem.FileRes("image", file("drawable-mdpi/image.png"), KQualifiers(dpi = KDpi.MDPI).key, group = ResItem.Drawable, root = resourcesRoot),
            ResItem.FileRes("image", file("drawable-tvdpi/image.png"), KQualifiers(dpi = KDpi.TVDPI).key, group = ResItem.Drawable, root = resourcesRoot),
            ResItem.FileRes("image", file("drawable-hdpi/image.png"), KQualifiers(dpi = KDpi.HDPI).key, group = ResItem.Drawable, root = resourcesRoot),
            ResItem.FileRes("image", file("drawable-xhdpi/image.png"), KQualifiers(dpi = KDpi.XHDPI).key, group = ResItem.Drawable, root = resourcesRoot),
            ResItem.FileRes("image", file("drawable-xxhdpi/image.png"), KQualifiers(dpi = KDpi.XXHDPI).key, group = ResItem.Drawable, root = resourcesRoot),
            ResItem.FileRes("image", file("drawable-xxxhdpi/image.png"), KQualifiers(dpi = KDpi.XXXHDPI).key, group = ResItem.Drawable, root = resourcesRoot),
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
            ResItem.FileRes("image", file("drawable/image.png"), KQualifiers.Undefined.key, group = ResItem.Drawable, root = resourcesRoot),
            ResItem.FileRes("image", file("drawable-en/image.png"), KQualifiers(locale = KLocale("en")).key, group = ResItem.Drawable, root = resourcesRoot),
            ResItem.FileRes(
                "image",
                file("drawable-en-rGB/image.png"),
                KQualifiers(locale = KLocale("en", "GB")).key,
                group = ResItem.Drawable,
                root = resourcesRoot,
            ),
            ResItem.FileRes(
                "image",
                file("drawable-hdpi-en-rGB/image.png"),
                KQualifiers(locale = KLocale("en", "GB"), dpi = KDpi.HDPI).key,
                group = ResItem.Drawable,
                root = resourcesRoot,
            ),
            ResItem.FileRes(
                "image",
                file("drawable-hdpi-en-rUS/image.png"),
                KQualifiers(locale = KLocale("en", "US"), dpi = KDpi.HDPI).key,
                group = ResItem.Drawable,
                root = resourcesRoot,
            ),
            ResItem.FileRes(
                "image",
                file("drawable-en-rGB-xxhdpi/image.png"),
                KQualifiers(locale = KLocale("en", "GB"), dpi = KDpi.XXHDPI).key,
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
            ResItem.StringRes("item1", "src2Item1", KQualifiers.Undefined.key),
        )

        assertThat(fileResolver.resolve(listOf(res2, res1))).containsExactly(
            ResItem.StringRes("item1", "src1Item1", KQualifiers.Undefined.key),
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
            ResItem.StringRes("item1", "src1Item1", KQualifiers.Undefined.key),
            ResItem.StringRes("item2", "src2Item2", KQualifiers.Undefined.key),

            ResItem.StringRes("item1", "src1Item1En", KQualifiers(locale = KLocale("en")).key),
            ResItem.StringRes("item2", "src1Item2En", KQualifiers(locale = KLocale("en")).key),

            ResItem.StringRes("item1", "src2Item1De", KQualifiers(locale = KLocale("de")).key),
            ResItem.StringRes("item2", "src2Item2De", KQualifiers(locale = KLocale("de")).key),

            ResItem.StringRes("item1", "src2Item1EnGb", KQualifiers(locale = KLocale("en", "GB")).key),
            ResItem.StringRes("item2", "src2Item2EnGb", KQualifiers(locale = KLocale("en", "GB")).key),
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
            ResItem.StringRes("item1", "item1", KQualifiers.Undefined.key),
            ResItem.StringRes("item1", "item1Cs", KQualifiers(locale = KLocale("cs")).key),

            ResItem.Plurals("dog", mapOf(PluralCategory.Other to "dogs", PluralCategory.One to "dog").toPluralList(), KQualifiers.Undefined.key),
            ResItem.Plurals(
                "dog",
                mapOf(PluralCategory.Other to "psů", PluralCategory.One to "pes", PluralCategory.Few to "psy", PluralCategory.Many to "psiska!").toPluralList(),
                KQualifiers(locale = KLocale("cs")).key,
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

    @Test
    fun testQualifierInnerFolder() = testResources {
        addFile("icons/home-xhdpi/sample.xml")
        addFile("icons/home-xxhdpi/sample.xml")

        buildResources()

        val items = FileResolver().resolve(resourcesRoot)

        assertThat(items).containsExactly(
            ResItem.FileRes("sample", file("icons/home-xhdpi/sample.xml"), KQualifiers(dpi = KDpi.XHDPI).key, "icons", resourcesRoot),
            ResItem.FileRes("sample", file("icons/home-xxhdpi/sample.xml"), KQualifiers(dpi = KDpi.XXHDPI).key, "icons", resourcesRoot),
        )
    }

    @Test
    fun `testQualifierInnerFolder Images`() = testResources {
        addFile("im/icons/icon1.xml")
        addFile("im/icons/icon2.png")
        addFile("im/icons-xxhdpi/icon2.jpg")
        addFile("im/icons/icon2-en-xxhdpi.webp")
        addFile("im/icons/icon2-xhdpi.jpg")
        //in conflict with ^, so later wins
        addFile("im-xhdpi/icons-nodpi/icon2.jpg")
        addFile("im/test.bin")

        buildResources()

        val items = FileResolver(
            config = FileResolverConfig(drawableGroups = setOf(".*".toRegex())),
        ).resolve(resourcesRoot)

        assertThat(items).containsExactly(
            ResItem.FileRes("icon1", file("im/icons/icon1.xml"), KQualifiers.Undefined.key, "drawable", resourcesRoot),
            ResItem.FileRes("icon2", file("im/icons/icon2.png"), KQualifiers.Undefined.key, "drawable", resourcesRoot),
            ResItem.FileRes("icon2", file("im/icons-xxhdpi/icon2.jpg"), KQualifiers(dpi = KDpi.XXHDPI).key, "drawable", resourcesRoot),
            ResItem.FileRes("icon2", file("im/icons/icon2-xhdpi.jpg"), KQualifiers(dpi = KDpi.XHDPI).key, "drawable", resourcesRoot),
            ResItem.FileRes("icon2", file("im-xhdpi/icons-nodpi/icon2.jpg"), KQualifiers(dpi = KDpi.NoDpi).key, "drawable", resourcesRoot),
            ResItem.FileRes("icon2", file("im/icons/icon2-en-xxhdpi.webp"), KQualifiers("en", dpi = KDpi.XXHDPI).key, "drawable", resourcesRoot),
            ResItem.FileRes("test", file("im/test.bin"), KQualifiers.Undefined.key, "im", resourcesRoot),
        )
    }

    @Test
    fun `resolve WHEN multiple qualifiers on folders THEN closest taken only`() = testResources {
        addFile("icons/group/sample.xml")
        addFile("icons-cs/group/sample.xml")
        addFile("icons/group-mdpi/sample.xml")
        addFile("icons-cs/group-xhdpi/sample.xml")
        addFile("icons-en/group-xxhdpi/sample.xml")

        buildResources()

        val items = FileResolver().resolve(resourcesRoot)

        assertThat(items).containsExactly(
            ResItem.FileRes("sample", file("icons/group/sample.xml"), KQualifiers.Undefined.key, "icons", resourcesRoot),
            ResItem.FileRes("sample", file("icons-cs/group/sample.xml"), KQualifiers("cs", KDpi.Undefined).key, "icons", resourcesRoot),
            ResItem.FileRes("sample", file("icons/group-mdpi/sample.xml"), KQualifiers(dpi = KDpi.MDPI).key, "icons", resourcesRoot),
            ResItem.FileRes("sample", file("icons-cs/group-xhdpi/sample.xml"), KQualifiers(dpi = KDpi.XHDPI).key, "icons", resourcesRoot),
            ResItem.FileRes("sample", file("icons-en/group-xxhdpi/sample.xml"), KQualifiers(dpi = KDpi.XXHDPI).key, "icons", resourcesRoot),
        )
    }

    @Test
    fun testQualifierOnImageFile() = testResources {
        addFile("icons/sample-xhdpi.xml")
        addFile("icons/sample-xxhdpi.png")

        buildResources()

        val items = FileResolver().resolve(resourcesRoot)

        assertThat(items).containsExactly(
            ResItem.FileRes("sample", file("icons/sample-xhdpi.xml"), KQualifiers(dpi = KDpi.XHDPI).key, "icons", resourcesRoot),
            ResItem.FileRes("sample", file("icons/sample-xxhdpi.png"), KQualifiers(dpi = KDpi.XXHDPI).key, "icons", resourcesRoot),
        )
    }

    @Test
    fun testQualifierOnImageFile2() = testResources {
        addFile("icons/sample-xhdpi-en-rUS.xml")
        addFile("icons/sample-xxhdpi-en-rGB.png")
        addFile("icons/sample-xxxhdpi-en.png")
        addFile("icons/sample-hdpi-cs.png")
        addFile("images/sample-xxxhdpi-en.png")

        buildResources()

        val items = FileResolver().resolve(resourcesRoot)

        assertThat(items).containsExactly(
            ResItem.FileRes("sample", file("icons/sample-xhdpi-en-rUS.xml"), KQualifiers("enUS", KDpi.XHDPI).key, "icons", resourcesRoot),
            ResItem.FileRes("sample", file("icons/sample-xxhdpi-en-rGB.png"), KQualifiers("enGB", KDpi.XXHDPI).key, "icons", resourcesRoot),
            ResItem.FileRes("sample", file("icons/sample-xxxhdpi-en.png"), KQualifiers("en", KDpi.XXXHDPI).key, "icons", resourcesRoot),
            ResItem.FileRes("sample", file("icons/sample-hdpi-cs.png"), KQualifiers("cs", KDpi.HDPI).key, "icons", resourcesRoot),
            ResItem.FileRes("sample", file("images/sample-xxxhdpi-en.png"), KQualifiers("en", KDpi.XXXHDPI).key, "drawable", resourcesRoot),
        )
    }

    @Test
    fun testQualifierOnStringFile() = testResources {
        addStrings(
            file = "strings/strings.xml",
            strings = mapOf("item1" to "item1"),
        )
        addStrings(
            file = "strings/strings-en.xml",
            strings = mapOf("item1" to "item1En"),
        )
        addStrings(
            file = "strings/strings-en-rGB.xml",
            strings = mapOf("item1" to "item1EnGB"),
        )
        addStrings(
            file = "strings/strings-cs.xml",
            strings = mapOf("item1" to "item1Cs"),
        )

        buildResources()

        val items = FileResolver().resolve(resourcesRoot)

        assertThat(items).containsExactly(
            ResItem.StringRes("item1", "item1", KQualifiers.Undefined.key),
            ResItem.StringRes("item1", "item1En", KQualifiers("en").key),
            ResItem.StringRes("item1", "item1EnGB", KQualifiers("enGB").key),
            ResItem.StringRes("item1", "item1Cs", KQualifiers("cs").key),
        )
    }
}