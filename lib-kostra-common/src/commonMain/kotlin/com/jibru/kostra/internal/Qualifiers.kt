package com.jibru.kostra.internal

@JvmInline
value class Qualifiers(val key: Int) {
    constructor(locale: Locale = Locale.Undefined, dpi: Dpi = Dpi.Undefined) : this(pack(locale, dpi))
    constructor(locale: String, dpi: Dpi = Dpi.Undefined) : this(pack(Locale(locale), dpi))

    val hasOnlyLocale get() = dpi == Dpi.Undefined
    val locale get() = Locale(key shr Dpi.Bits)
    val dpi get() = Dpi.fromBits(key and Dpi.BitMask)

    override fun toString(): String {
        return if (this == Undefined) "Qualifiers.Undefined" else "Qualifiers(locale=$locale, dpi=$dpi)"
    }

    companion object {
        val Undefined = Qualifiers(0)
    }
}

private fun pack(locale: Locale, dpi: Dpi) = (locale.key shl Dpi.Bits) + dpi.key
