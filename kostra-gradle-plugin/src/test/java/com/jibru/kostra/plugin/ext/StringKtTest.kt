package com.jibru.kostra.plugin.ext

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class StringKtTest {
    @Test
    fun pathNormalise() {
        assertThat("/test/item.xml".pathNormalise()).isEqualTo("/test/item.xml")
        assertThat("test/item.xml".pathNormalise()).isEqualTo("test/item.xml")
        assertThat("\\test\\item.xml".pathNormalise()).isEqualTo("/test/item.xml")
        assertThat("test\\item.xml".pathNormalise()).isEqualTo("test/item.xml")
    }
}
