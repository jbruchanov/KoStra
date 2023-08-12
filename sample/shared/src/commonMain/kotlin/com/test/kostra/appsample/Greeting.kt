package com.test.kostra.appsample

import com.jibru.kostra.plural
import com.jibru.kostra.string
import com.sample.app.K
import com.sample.app.Resources

class Greeting {
    private val platform: Platform = getPlatform()

    fun greet(): String {
        println(Resources.string(K.string.action_add))
        println(Resources.plural(K.plural.bug_x, 0f, 0f))
        println(Resources.plural(K.plural.bug_x, 0.5f, 0.5f))
        println(Resources.plural(K.plural.bug_x, 1, 1))
        println(Resources.plural(K.plural.bug_x, 10, 10))
        return "Hello, ${platform.name}!"
    }
}
