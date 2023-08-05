package com.jibru.kostra.internal

import com.jibru.kostra.internal.ext.takeIfNotEmpty

class Locale(language: String, region: String?) {

    constructor(languageRegion: String) : this(languageRegion.substringBefore("-"), languageRegion.substringAfter("-", "").takeIfNotEmpty())

    val language: String = language.lowercase()
    val region: String? = region?.lowercase()?.let { if (it.startsWith("r")) it.substring(1) else it }
    val languageRegion = if (this.region == null) this.language else "${this.language}${this.region}"

    init {
        if (language.isNotEmpty() && region != null) {
            require(region.isNotEmpty()) { "Empty region" }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Locale

        if (language != other.language) return false
        if (region != other.region) return false

        return true
    }

    fun equalsLanguage(other: Locale) = this.language == other.language

    override fun hashCode(): Int {
        var result = language.hashCode()
        result = 31 * result + (region?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return if (this == Undefined) "Locale.Undefined" else "Locale(l='$language', r=$region)"
    }

    companion object {
        val Undefined = Locale("", null)
    }
}
