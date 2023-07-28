package com.test.kostra.appsample

import com.jibru.kostra.KostraResource

class Greeting {
    private val platform: Platform = getPlatform()

    val res = object : KostraResource {}

    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}
