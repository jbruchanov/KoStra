package com.test.kostra.appsample

import com.jibru.kostra.DrawableResourceKey
import com.jibru.kostra.PluralResourceKey
import com.jibru.kostra.ResourceContainer
import com.jibru.kostra.ResourceKey
import com.jibru.kostra.StringResourceKey
import com.jibru.kostra.internal.KostraResources

class Greeting {
    private val platform: Platform = getPlatform()

    val res = object : KostraResources {
        override val strings: Map<StringResourceKey, ResourceContainer> = emptyMap()
        override val plurals: Map<PluralResourceKey, ResourceContainer> = emptyMap()
        override val drawables: Map<DrawableResourceKey, ResourceContainer> = emptyMap()
        override val binary: Map<ResourceKey, ResourceContainer> = emptyMap()
    }

    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}
