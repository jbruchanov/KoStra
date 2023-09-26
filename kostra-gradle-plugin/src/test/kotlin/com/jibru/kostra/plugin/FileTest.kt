package com.jibru.kostra.plugin

import com.google.common.truth.Truth.assertThat
import com.jibru.kostra.KDpi
import com.jibru.kostra.KLocale
import com.jibru.kostra.KQualifiers
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import java.io.File

class FileTest {

    @Test
    fun groupQualifiers() {
        val list = listOf(
            f() to KQualifiers.Undefined,
            f("en") to KQualifiers(KLocale("en")),
            f("en-rUS") to KQualifiers(KLocale("en", "rUS")),
            f("xxhdpi") to KQualifiers(dpi = KDpi.XXHDPI),
            f("en", "xhdpi") to KQualifiers(locale = KLocale("en"), dpi = KDpi.XHDPI),
            f("en-rGB", "xxhdpi") to KQualifiers(locale = KLocale("en", "gb"), dpi = KDpi.XXHDPI),
            f("en-rGB", "xxhdpi", "land") to KQualifiers(locale = KLocale("en", "gb"), dpi = KDpi.XXHDPI),
            f("land", "en") to KQualifiers(KLocale("en")),
            f("xxhdpi", "en", "xyz") to KQualifiers(locale = KLocale("en"), dpi = KDpi.XXHDPI),
            f("123", "tvdpi", "456", "en-rGB") to KQualifiers(locale = KLocale("en", "gb"), dpi = KDpi.TVDPI),
        )

        assertAll(
            list.map { (file, expected) ->
                Executable {
                    val (group, qualifiers) = file.groupQualifiers()
                    assertEquals(expected, qualifiers)
                    assertEquals(if (qualifiers.dpi != KDpi.Undefined) "drawable" else "value", group)
                }
            },
        )
    }

    @Test
    fun `groupQualifiers WHEN multiple same groups`() {
        assertThat(f("en", "cs").groupQualifiers().qualifiers).isEqualTo(KQualifiers("en"))
        assertThat(f("xhdpi", "xhdpi").groupQualifiers().qualifiers).isEqualTo(KQualifiers(dpi = KDpi.XHDPI))
        assertThat(f("en", "xxhdpi", "cs", "xhdpi").groupQualifiers().qualifiers).isEqualTo(KQualifiers("en", dpi = KDpi.XXHDPI))
    }

    private fun f(vararg qualifiers: String): File {
        val value = qualifiers.joinToString(prefix = "-", separator = "-")
        val group = if (qualifiers.any { it.endsWith("dpi") }) "drawable" else "value"
        return File("$group$value")
    }
}
