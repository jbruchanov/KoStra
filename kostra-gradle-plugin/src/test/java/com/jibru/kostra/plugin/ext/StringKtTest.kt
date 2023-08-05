package com.jibru.kostra.plugin.ext

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledOnOs
import org.junit.jupiter.api.condition.OS

class StringKtTest {
    @Test
    fun pathNormalise() {
        assertThat("/test/item.xml".pathNormalise()).isEqualTo("/test/item.xml")
        assertThat("test/item.xml".pathNormalise()).isEqualTo("test/item.xml")
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    fun pathNormaliseWindows() {
        assertThat("\\test\\item.xml".pathNormalise()).isEqualTo("/test/item.xml")
        assertThat("test\\item.xml".pathNormalise()).isEqualTo("test/item.xml")
    }
}
