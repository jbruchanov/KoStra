package com.jibru.kostra.internal

data class Qualifiers(
    val locale: Locale = Locale.Undefined,
    val dpi: Dpi = Dpi.Undefined,
    val others: Set<String> = emptySet(),
) {
    companion object {
        val Undefined = Qualifiers()
    }
}
