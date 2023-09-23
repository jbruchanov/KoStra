package com.jibru.kostra.plugin

import com.jibru.kostra.KDpi
import com.jibru.kostra.KLocale
import com.jibru.kostra.KQualifiers
import com.jibru.kostra.plugin.ext.takeIfNotEmpty
import java.io.File

private val locales = java.util.Locale.getAvailableLocales().map { it.toLanguageTag().lowercase() }.toSortedSet()
private val dpiMap = KDpi.values().associateBy { it.qualifier }
private val dpiValues = dpiMap.keys

data class GroupQualifiers(
    val group: String,
    val qualifiers: KQualifiers,
)

const val QualifierDivider = "-"

internal fun File.groupQualifiers(anyLocale: Boolean = false): GroupQualifiers {
    val source = name
        .let { if (isFile) it.substringBeforeLast(".") else it }
        .lowercase()
    val group = source.substringBefore(QualifierDivider)
    val qualifiers = source.substringAfter(QualifierDivider, "")
        .takeIfNotEmpty()
        ?.split(QualifierDivider)
        ?.let { list ->
            val otherModifiers = list.toMutableSet()

            val strDpi = dpiValues.intersect(otherModifiers)
                .singleOrNull()
                ?.also { otherModifiers.remove(it) }

            val strLocale = (if (anyLocale) otherModifiers.firstOrNull() else locales.intersect(otherModifiers).singleOrNull())
                ?.also { otherModifiers.remove(it) }

            //looking for stuff like en-rUS
            val strLocaleRegion = strLocale
                ?.let { list.getOrNull(list.indexOf(it) + 1) }
                /*
                    https://developer.android.com/guide/topics/resources/providing-resources
                    The language is defined by a two-letter ISO 639-1 language code, optionally followed by a two-letter ISO 3166-1-alpha-2 region code (preceded by lowercase r).
                 */
                ?.takeIf {
                    val rPrefixRegion = it.startsWith("r") && it.length == 3
                    val twoCharRegion = it.length == 2
                    rPrefixRegion || (anyLocale && twoCharRegion)
                }
                ?.let {
                    val region = (if (it.startsWith("r")) it.drop(1) else it).take(2)
                    if (anyLocale || locales.contains("$strLocale-$region")) region else null
                }
                ?.also { otherModifiers.remove(it) }

            try {
                val locale = strLocale?.let { if (anyLocale) KLocale(it + (strLocaleRegion ?: "")) else KLocale(it, strLocaleRegion) } ?: KLocale.Undefined
                val dpi = strDpi?.let { dpiMap.getValue(it) } ?: KDpi.Undefined
                KQualifiers(locale = locale, dpi = dpi)
            } catch (e: Throwable) {
                throw IllegalArgumentException("Unable to parse GroupQualifiers, path:'$absolutePath'", e)
            }
        } ?: KQualifiers.Undefined

    return GroupQualifiers(group, qualifiers)
}
