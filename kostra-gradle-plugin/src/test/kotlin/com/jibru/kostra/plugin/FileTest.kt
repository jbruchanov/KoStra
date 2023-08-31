package com.jibru.kostra.plugin

import com.jibru.kostra.Dpi
import com.jibru.kostra.KLocale
import com.jibru.kostra.Qualifiers
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import java.io.File

class FileTest {

    @Test
    fun groupQualifiers() {
        val list = listOf(
            f() to Qualifiers.Undefined,
            f("en") to Qualifiers(KLocale("en")),
            f("en-rUS") to Qualifiers(KLocale("en", "rUS")),
            f("xxhdpi") to Qualifiers(dpi = Dpi.XXHDPI),
            f("en", "xhdpi") to Qualifiers(locale = KLocale("en"), dpi = Dpi.XHDPI),
            f("en-rGB", "xxhdpi") to Qualifiers(locale = KLocale("en", "gb"), dpi = Dpi.XXHDPI),
            f("en-rGB", "xxhdpi", "land") to Qualifiers(locale = KLocale("en", "gb"), dpi = Dpi.XXHDPI),
            f("land", "en") to Qualifiers(KLocale("en")),
            f("xxhdpi", "en", "xyz") to Qualifiers(locale = KLocale("en"), dpi = Dpi.XXHDPI),
            f("123", "tvdpi", "456", "en-rGB") to Qualifiers(locale = KLocale("en", "gb"), dpi = Dpi.TVDPI),
        )

        assertAll(
            list.map { (file, expected) ->
                Executable {
                    val (group, qualifiers) = file.groupQualifiers()
                    assertEquals(expected, qualifiers)
                    assertEquals(if (qualifiers.dpi != Dpi.Undefined) "drawable" else "value", group)
                }
            },
        )
    }

    private fun f(vararg qualifiers: String): File {
        val value = qualifiers.joinToString(prefix = "-", separator = "-")
        val group = if (qualifiers.any { it.endsWith("dpi") }) "drawable" else "value"
        return File("$group$value")
    }
}
