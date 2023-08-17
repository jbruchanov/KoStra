package com.jibru.kostra

@Suppress("ktlint:standard:no-semi")
enum class Dpi(val key: Int, val density: Float, val qualifier: String) {
    Undefined(0, Float.NaN, ""),
    NoDpi(1, 0f, "nodpi"),
    LDPI(2, 0.75f, "ldpi"),
    MDPI(3, 1f, "mdpi"),
    HDPI(4, 1.5f, "hdpi"),
    XHDPI(5, 2f, "xhdpi"),
    XXHDPI(6, 3f, "xxhdpi"),
    XXXHDPI(7, 4f, "xxxhdpi"),
    TVDPI(8, 1.33f, "tvdpi"),
    ;

    companion object {
        const val Bits = 4
        const val BitMask = 0xF

        @Suppress("EnumValuesSoftDeprecate")
        private val values = values()

        fun fromBits(bits: Int): Dpi = values[bits]
        fun Dpi.next() = values[(this.ordinal + 1).coerceAtMost(XXXHDPI.ordinal)]
        fun Dpi.prev() = values[(this.ordinal - 1).coerceAtLeast(LDPI.ordinal)]
        fun getClosest(density: Float): Dpi {
            if (density.isNaN()) return Undefined
            return when {
                density == 0f -> NoDpi
                density >= 3.5f -> XXXHDPI
                density >= 2.5f -> XXHDPI
                density >= 1.75f -> XHDPI
                density >= 1.35f -> HDPI
                density >= 1.3f -> TVDPI
                density >= 0.875 -> MDPI
                density >= 0.5f -> LDPI
                else -> Undefined
            }
        }
    }
}
