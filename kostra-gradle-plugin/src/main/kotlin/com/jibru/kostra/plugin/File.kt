package com.jibru.kostra.plugin

import com.jibru.kostra.Dpi
import com.jibru.kostra.KLocale
import com.jibru.kostra.Qualifiers
import com.jibru.kostra.ext.takeIfNotEmpty
import java.io.File

private val locales = java.util.Locale.getAvailableLocales().map { it.toLanguageTag().lowercase() }.toSortedSet()
private val dpiMap = Dpi.values().associateBy { it.qualifier }
private val dpiValues = dpiMap.keys

data class GroupQualifiers(
    val group: String,
    val qualifiers: Qualifiers,
)

const val QualifierDivider = "-"

fun File.ext() = name.substringAfterLast(".", "")

internal fun File.groupQualifiers(): GroupQualifiers {
    val source = name.lowercase()
    val group = source.substringBefore(QualifierDivider)
    val qualifiers = source.substringAfter(QualifierDivider, "")
        .takeIfNotEmpty()
        ?.split(QualifierDivider)
        ?.let { list ->
            val otherModifiers = list.toMutableSet()
            val strLocale = locales.intersect(otherModifiers).singleOrNull()
                ?.also { otherModifiers.remove(it) }

            //looking for stuff like en-rUS
            val strLocaleRegion = strLocale
                ?.let { list.getOrNull(list.indexOf(it) + 1) }
                /*
                    https://developer.android.com/guide/topics/resources/providing-resources
                    The language is defined by a two-letter ISO 639-1 language code, optionally followed by a two-letter ISO 3166-1-alpha-2 region code (preceded by lowercase r).
                 */
                ?.takeIf { it.startsWith("r") && it.length >= 3 }
                ?.let {
                    val javaLocaleTag = "$strLocale-${it.substring(1)}"
                    if (locales.contains(javaLocaleTag)) it else null
                }
                ?.also { otherModifiers.remove(it) }

            val strDpi = dpiValues.intersect(otherModifiers)
                .singleOrNull()
                ?.also { otherModifiers.remove(it) }

            val locale = strLocale?.let { KLocale(it, strLocaleRegion) } ?: KLocale.Undefined
            val dpi = strDpi?.let { dpiMap.getValue(it) } ?: Dpi.Undefined
            Qualifiers(locale = locale, dpi = dpi)
        } ?: Qualifiers.Undefined

    return GroupQualifiers(group, qualifiers)
}
