package com.jibru.kostra

import java.io.Serializable

@JvmInline
value class Qualifiers(val key: Int) : Serializable {
    constructor(locale: Locale = Locale.Undefined, dpi: Dpi = Dpi.Undefined) : this(pack(locale, dpi))
    constructor(locale: String, dpi: Dpi = Dpi.Undefined) : this(pack(Locale(locale), dpi))

    val hasOnlyLocale get() = dpi == Dpi.Undefined
    val locale get() = Locale(key shr Dpi.Bits)
    val dpi get() = Dpi.fromBits(key and Dpi.BitMask)

    fun withNoLocaleRegion() = Qualifiers(locale.language, dpi)

    fun withNoLocale() = Qualifiers(Locale.Undefined, dpi)

    fun withNoDpi() = Qualifiers(locale, dpi = Dpi.Undefined)

    fun copy(locale: Locale = this.locale, dpi: Dpi = this.dpi) = Qualifiers(locale, dpi)

    override fun toString(): String {
        return if (this == Undefined) "Qualifiers.Undefined" else "Qualifiers(locale=$locale, dpi=$dpi)"
    }

    companion object {
        val Undefined = Qualifiers(0)
    }
}

private fun pack(locale: Locale, dpi: Dpi) = (locale.key shl Dpi.Bits) + dpi.key
