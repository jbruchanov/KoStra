package com.jibru.kostra.internal

@Suppress("ktlint:standard:no-semi")
enum class Dpi(val density: Float, val qualifier: String) {
    Undefined(Float.NaN, ""),
    NoDpi(0f, "nodpi"),
    LDPI(0.75f, "ldpi"),
    MDPI(1f, "mdpi"),
    HDPI(1.5f, "hdpi"),
    XHDPI(2f, "xhdpi"),
    XXHDPI(3f, "xxhdpi"),
    XXXHDPI(4f, "xxxhdpi"),
    TVDPI(1.33f, "tvdpi"),
    ;

    companion object {
        @Suppress("EnumValuesSoftDeprecate")
        private val values = values()

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
