package com.jibru.kostra.appsample.jvm

import com.jibru.kostra.K
import com.jibru.kostra.Resources
import com.jibru.kostra.assetPath
import com.jibru.kostra.binaryInputStream
import com.jibru.kostra.defaultQualifiers
import com.jibru.kostra.Dpi
import com.jibru.kostra.plural
import com.jibru.kostra.string
import java.util.Locale
import javax.imageio.ImageIO

fun main() {
    val test = {
        println("-".repeat(32))
        println("Locale:${Locale.getDefault().toLanguageTag()}")
        println(Resources.string(K.string.action_add))
        println(Resources.plural(K.plural.bug_x, 0f, 0f))
        println(Resources.plural(K.plural.bug_x, 0.5f, 0.5f))
        println(Resources.plural(K.plural.bug_x, 1, 1))
        println(Resources.plural(K.plural.bug_x, 10, 10))

        ImageIO.read(Resources.binaryInputStream(K.drawable.capital_city)).also {
            val assetPath = Resources.assetPath(K.drawable.capital_city)
            println("$assetPath imageRes:${it.width}x${it.height}")
        }
        val xxxHdpiQualifiers = defaultQualifiers().copy(dpi = Dpi.XXXHDPI)
        ImageIO.read(Resources.binaryInputStream(K.drawable.capital_city, qualifiers = xxxHdpiQualifiers)).also {
            val assetPath = Resources.assetPath(K.drawable.capital_city, xxxHdpiQualifiers)
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
