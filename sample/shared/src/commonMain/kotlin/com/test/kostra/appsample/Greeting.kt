package com.test.kostra.appsample

import com.jibru.kostra.icu.FixedDecimal
import com.sample.app.K
import com.sample.app.get

class Greeting {
    private val platform: Platform = getPlatform()

    fun greet(): String {
        println(K.string.action_add.get())
        println(K.plural.bug_x.get(0, 0))
        println(K.plural.bug_x.get(FixedDecimal(0.5), 0.5f))
        println(K.plural.bug_x.get(1, 1))
        println(K.plural.bug_x.get(10, 10))
        return "Hello, ${platform.name}!"
    }
}
