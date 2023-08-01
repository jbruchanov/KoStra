package com.jibru.kostra.internal

import com.jibru.kostra.internal.Dpi.Companion.next
import com.jibru.kostra.internal.Dpi.Companion.prev
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DpiTest {
    @Test
    fun prev() {
        assertEquals(Dpi.LDPI, Dpi.LDPI.prev())
        assertEquals(Dpi.LDPI, Dpi.MDPI.prev())
        assertEquals(Dpi.MDPI, Dpi.HDPI.prev())
        assertEquals(Dpi.HDPI, Dpi.XHDPI.prev())
        assertEquals(Dpi.XHDPI, Dpi.XXHDPI.prev())
        assertEquals(Dpi.XXHDPI, Dpi.XXXHDPI.prev())
    }

    @Test
    fun next() {
        assertEquals(Dpi.MDPI, Dpi.LDPI.next())
        assertEquals(Dpi.HDPI, Dpi.MDPI.next())
        assertEquals(Dpi.XHDPI, Dpi.HDPI.next())
        assertEquals(Dpi.XXHDPI, Dpi.XHDPI.next())
        assertEquals(Dpi.XXXHDPI, Dpi.XXHDPI.next())
        assertEquals(Dpi.XXXHDPI, Dpi.XXXHDPI.next())
    }

    @Test
    fun getClosest() {
        val data = listOf(
            0f to Dpi.NoDpi,
            Float.NaN to Dpi.Undefined,
            0.5f to Dpi.LDPI,
            1f to Dpi.MDPI,
            1.33f to Dpi.TVDPI,
            1.5f to Dpi.HDPI,
            2.0f to Dpi.XHDPI,
            3.0f to Dpi.XXHDPI,
            4.0f to Dpi.XXXHDPI,
            5.0f to Dpi.XXXHDPI,
        )
        data.forEach { (density, expectedDpi) ->
            assertEquals(expectedDpi, Dpi.getClosest(density))
        }
    }
}
