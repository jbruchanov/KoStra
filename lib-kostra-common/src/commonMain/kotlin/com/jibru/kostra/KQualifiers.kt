package com.jibru.kostra

import java.io.Serializable

@JvmInline
value class KQualifiers(val key: Int) : Serializable {
    constructor(locale: KLocale = KLocale.Undefined, dpi: KDpi = KDpi.Undefined) : this(pack(locale, dpi))
    constructor(locale: String, dpi: KDpi = KDpi.Undefined) : this(pack(KLocale(locale), dpi))

    val hasOnlyLocale get() = dpi == KDpi.Undefined
    val locale get() = KLocale(key shr KDpi.Bits)
    val dpi get() = KDpi.fromBits(key and KDpi.BitMask)

    fun withNoLocaleRegion() = KQualifiers(locale.language, dpi)

    fun withNoLocale() = KQualifiers(KLocale.Undefined, dpi)

    fun withNoDpi() = KQualifiers(locale, dpi = KDpi.Undefined)

    fun copy(locale: KLocale = this.locale, dpi: KDpi = this.dpi) = KQualifiers(locale, dpi)

    override fun toString(): String {
        return if (this == Undefined) "Qualifiers.Undefined" else "Qualifiers(locale=$locale, dpi=$dpi)"
    }

    companion object {
        val Undefined = KQualifiers(0)
    }
}

private fun pack(locale: KLocale, dpi: KDpi) = (locale.key shl KDpi.Bits) + dpi.key
