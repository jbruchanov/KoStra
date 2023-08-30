package com.jibru.kostra.appsample.jvm

import com.jibru.kostra.Dpi
import com.jibru.kostra.K
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
        println("Locale:${Locale.getDefault().toLanguageTag()}")
        println(Resources.string(K.string.action_add))
        println("Plurals")
        println(Resources.plural(K.plural.bug_x, 0, 0f))
        println(Resources.plural(K.plural.bug_x, FixedDecimal(0.5), 0.5f))
        println(Resources.plural(K.plural.bug_x, 1, 1))
        println(Resources.plural(K.plural.bug_x, 10, 10))

        println("Ordinals")
        (0..5).forEach {
            println(Resources.ordinal(K.plural.day_x, it, it))
        }

        ImageIO.read(Resources.binaryInputStream(K.drawable.capital_city)).also {
            val assetPath = Resources.assetPath(K.drawable.capital_city)
            println("$assetPath imageRes:${it.width}x${it.height}")
        }
        val xxHdpiQualifiers = defaultQualifiers().copy(dpi = Dpi.XXHDPI)
        ImageIO.read(Resources.binaryInputStream(K.drawable.capital_city, qualifiers = xxHdpiQualifiers)).also {
            val assetPath = Resources.assetPath(K.drawable.capital_city, xxHdpiQualifiers)
            if (it != null) {
                println("$assetPath imageRes:${it.width}x${it.height}")
            } else {
                println("Unable to load $assetPath, (webp not supported in java!?)")
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
