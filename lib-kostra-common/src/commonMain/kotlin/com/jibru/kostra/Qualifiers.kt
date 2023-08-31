package com.jibru.kostra

import java.io.Serializable

@JvmInline
value class Qualifiers(val key: Int) : Serializable {
    constructor(locale: KLocale = KLocale.Undefined, dpi: Dpi = Dpi.Undefined) : this(pack(locale, dpi))
    constructor(locale: String, dpi: Dpi = Dpi.Undefined) : this(pack(KLocale(locale), dpi))

    val hasOnlyLocale get() = dpi == Dpi.Undefined
    val locale get() = KLocale(key shr Dpi.Bits)
    val dpi get() = Dpi.fromBits(key and Dpi.BitMask)

    fun withNoLocaleRegion() = Qualifiers(locale.language, dpi)

    fun withNoLocale() = Qualifiers(KLocale.Undefined, dpi)

    fun withNoDpi() = Qualifiers(locale, dpi = Dpi.Undefined)

    fun copy(locale: KLocale = this.locale, dpi: Dpi = this.dpi) = Qualifiers(locale, dpi)

    override fun toString(): String {
        return if (this == Undefined) "Qualifiers.Undefined" else "Qualifiers(locale=$locale, dpi=$dpi)"
    }

    companion object {
        val Undefined = Qualifiers(0)
    }
}

private fun pack(locale: KLocale, dpi: Dpi) = (locale.key shl Dpi.Bits) + dpi.key
