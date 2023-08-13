package com.jibru.kostra.appsample.jvm

import com.jibru.kostra.binaryInputStream
import com.jibru.kostra.internal.Dpi
import com.jibru.kostra.internal.Qualifiers
import com.sample.app.K
import com.sample.app.Resources
import com.test.kostra.appsample.Greeting
import java.util.Locale
import javax.imageio.ImageIO

fun main() {
    val test = {
        println(Greeting().greet())
        ImageIO.read(Resources.binaryInputStream(K.images.london)).also {
            println("London imageRes:${it.width}x${it.height}")
        }
        ImageIO.read(Resources.binaryInputStream(K.images.london, qualifiers = Qualifiers(dpi = Dpi.XXXHDPI))).also {
            println("London imageRes:${it.width}x${it.height}")
        }
        Unit
    }
    println("Locale:${Locale.getDefault().toLanguageTag()}")
    test()
    Locale.setDefault(Locale("cs"))
    println("-".repeat(32))
    println("Locale:${Locale.getDefault().toLanguageTag()}")
    test()
}
