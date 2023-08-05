package com.jibru.kostra.internal

data class Qualifiers(
    val locale: Locale = Locale.Undefined,
    val dpi: Dpi = Dpi.Undefined,
    val others: Set<String> = emptySet(),
) {
    val hasOnlyLocale = dpi == Dpi.Undefined && others.isEmpty()

    val key by lazy {
        if (this == Undefined) {
            ""
        } else {
            buildString {
                if (locale != Locale.Undefined) {
                    append(locale.languageRegion)
                }
                if (dpi != Dpi.Undefined) {
                    if (isNotEmpty()) append("_")
                    append(dpi.qualifier)
                }
                if (others.isNotEmpty()) {
                    if (isNotEmpty()) append("_")
                    //trivial hash doing sum on unicode values over each char with index offset
                    val hash = others.sumOf { it.foldIndexed(0L) { i: Int, acc, v -> (acc + v.code.toLong()) * (i * 10) } } % Short.MAX_VALUE
                    val len = others.sumOf { it.length }
                    val key = "${others.size}$len$hash"
                    append(key)
                }
            }
        }
    }

    override fun toString(): String {
        return if (this == Undefined) "Qualifiers.Undefined" else "Qualifiers(key='$key', locale=$locale, dpi=$dpi, others=$others)"
    }

    companion object {
        val Undefined = Qualifiers()
    }
}
