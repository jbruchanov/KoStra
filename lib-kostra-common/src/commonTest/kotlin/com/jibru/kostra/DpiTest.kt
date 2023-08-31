package com.jibru.kostra

import com.jibru.kostra.KDpi.Companion.next
import com.jibru.kostra.KDpi.Companion.prev
import kotlin.test.assertEquals
import kotlin.test.Test

class DpiTest {
    @Test
    fun prev() {
        assertEquals(KDpi.LDPI, KDpi.LDPI.prev())
        assertEquals(KDpi.LDPI, KDpi.MDPI.prev())
        assertEquals(KDpi.MDPI, KDpi.HDPI.prev())
        assertEquals(KDpi.HDPI, KDpi.XHDPI.prev())
        assertEquals(KDpi.XHDPI, KDpi.XXHDPI.prev())
        assertEquals(KDpi.XXHDPI, KDpi.XXXHDPI.prev())
    }

    @Test
    fun next() {
        assertEquals(KDpi.MDPI, KDpi.LDPI.next())
        assertEquals(KDpi.HDPI, KDpi.MDPI.next())
        assertEquals(KDpi.XHDPI, KDpi.HDPI.next())
        assertEquals(KDpi.XXHDPI, KDpi.XHDPI.next())
        assertEquals(KDpi.XXXHDPI, KDpi.XXHDPI.next())
        assertEquals(KDpi.XXXHDPI, KDpi.XXXHDPI.next())
    }

    @Test
    fun getClosest() {
        val data = listOf(
            0f to KDpi.NoDpi,
            Float.NaN to KDpi.Undefined,
            0.5f to KDpi.LDPI,
            1f to KDpi.MDPI,
            1.33f to KDpi.TVDPI,
            1.5f to KDpi.HDPI,
            2.0f to KDpi.XHDPI,
            3.0f to KDpi.XXHDPI,
            4.0f to KDpi.XXXHDPI,
            5.0f to KDpi.XXXHDPI,
        )
        data.forEach { (density, expectedDpi) ->
            assertEquals(expectedDpi, KDpi.getClosest(density))
        }
    }
}
