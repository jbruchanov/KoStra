package com.jibru.kostra.appsample.jvm

import com.jibru.kostra.K
import com.jibru.kostra.KDpi
import com.jibru.kostra.Resources
import com.jibru.kostra.assetPath
import com.jibru.kostra.binaryInputStream
import com.jibru.kostra.defaultQualifiers
import com.jibru.kostra.icu.FixedDecimal
import com.jibru.kostra.ordinal
import com.jibru.kostra.plural
import com.jibru.kostra.string
import java.util.Locale
import javax.imageio.ImageIO

fun main() {
    val test = {
        println("-".repeat(32))
        println("Current locale:${Locale.getDefault()}")
        println("Strings:")
        val items = listOf(K.string.actionAdd, K.string.actionRemove, K.string.color, K.string.plurals, K.string.ordinals)
        println(items.joinToString { Resources.string(it) })
        println("Plurals:")
        println(
            listOf(
                Resources.plural(K.plural.bugX, 0, 0f),
                Resources.plural(K.plural.bugX, FixedDecimal(0.5), 0.5f),
                Resources.plural(K.plural.bugX, 1, 1),
                Resources.plural(K.plural.bugX, 10, 10)
            ).joinToString()
        )
        println("Ordinals:")
        println((0..5).joinToString { Resources.ordinal(K.plural.dayX, it, it) })

        ImageIO.read(Resources.binaryInputStream(K.drawable.capitalCity)).also {
            val assetPath = Resources.assetPath(K.drawable.capitalCity)
            println("$assetPath imageRes:${it.width}x${it.height}")
        }
        val xxHdpiQualifiers = defaultQualifiers().copy(dpi = KDpi.XXHDPI)
        ImageIO.read(Resources.binaryInputStream(K.drawable.capitalCity, qualifiers = xxHdpiQualifiers)).also {
            val assetPath = Resources.assetPath(K.drawable.capitalCity, xxHdpiQualifiers)
            if (it != null) {
                println("$assetPath imageRes:${it.width}x${it.height}")
            } else {
                println("Unable to load $assetPath, (webp not supported by ImageIO!?)")
            }
        }
        Unit
    }

    Locale.setDefault(Locale("en", "US"))
    test()
    Locale.setDefault(Locale("en", "GB"))
    test()
    Locale.setDefault(Locale("cs"))
    test()
}
