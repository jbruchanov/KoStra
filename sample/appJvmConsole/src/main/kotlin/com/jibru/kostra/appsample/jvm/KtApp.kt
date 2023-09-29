package com.jibru.kostra.appsample.jvm

import app.K
import app.Resources
import com.jibru.kostra.KDpi
import com.jibru.kostra.assetPath
import com.jibru.kostra.binaryInputStream
import com.jibru.kostra.defaultQualifiers
import com.jibru.kostra.icu.FixedDecimal
import com.jibru.kostra.ordinal
import com.jibru.kostra.plural
import com.jibru.kostra.string
import com.sample.lib1.KLib1
import com.sample.lib1.Lib1Resources
import java.util.Locale
import javax.imageio.ImageIO

fun main() {
    val test = {
        println("-".repeat(32))
        println("Current locale:${Locale.getDefault().toLanguageTag()}")
        println("Strings:")
        val items = listOf(K.string.actionAdd, K.string.actionRemove, K.string.color, K.string.plurals, K.string.ordinals).map { { Resources.string(it) } } +
            listOf(KLib1.string.lib1_text).map { { Lib1Resources.string(it) } }
        println(items.map { it() })
        println("Plurals:")
        println(
            listOf(
                Resources.plural(K.plural.bugX, 0, 0),
                Resources.plural(K.plural.bugX, FixedDecimal(0.5), 0.5f),
                Resources.plural(K.plural.bugX, 1, 1),
                Resources.plural(K.plural.bugX, 2, 2),
                Resources.plural(K.plural.bugX, 3, 3),
                Resources.plural(K.plural.bugX, 4, 4),
                Resources.plural(K.plural.bugX, 5, 5),
                Resources.plural(K.plural.bugX, 10, 10),
            ).joinToString(),
        )
        println("Ordinals:")
        println((0..5).joinToString { Resources.ordinal(K.plural.dayX, it, it) })

        ImageIO.read(Resources.binaryInputStream(K.images.capitalCity)).also {
            val assetPath = Resources.assetPath(K.images.capitalCity)
            println("$assetPath imageRes:${it.width}x${it.height}")
        }
        val xxHdpiQualifiers = defaultQualifiers().copy(dpi = KDpi.XXHDPI)
        ImageIO.read(Resources.binaryInputStream(K.images.capitalCity, qualifiers = xxHdpiQualifiers)).also {
            val assetPath = Resources.assetPath(K.images.capitalCity, xxHdpiQualifiers)
            if (it != null) {
                println("$assetPath imageRes:${it.width}x${it.height}")
            } else {
                println("Unable to load $assetPath, (webp not supported by ImageIO!?)")
            }
        }
        Unit
    }

    val codes = listOf("ar", "cs", "en", "enGB", "enUS", "he", "hi", "ja", "ko", "ru", "th")
    codes.forEach {
        val (lang, country) = it.padEnd(4, ' ').windowed(2, 2)
        Locale.setDefault(Locale(lang.trim(), country.trim()))
        test()
    }
}
