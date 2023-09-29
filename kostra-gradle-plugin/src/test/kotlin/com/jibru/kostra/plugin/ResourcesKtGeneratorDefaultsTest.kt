package com.jibru.kostra.plugin

import org.junit.jupiter.api.Test

class ResourcesKtGeneratorDefaultsTest {

    @Test
    fun generateResourceProviders() {
        val result = ResourcesKtGenerator(emptyList()).generateResourceProviders()
        println(result)
    }
}
