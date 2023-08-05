package com.test.kostra.appsample

import com.jibru.kostra.AssetResourceKey
import com.jibru.kostra.BinaryResourceKey
import com.jibru.kostra.DrawableResourceKey
import com.jibru.kostra.PluralResourceKey
import com.jibru.kostra.ResourceContainer
import com.jibru.kostra.StringResourceKey
import com.jibru.kostra.internal.KostraResources

class Greeting {
    private val platform: Platform = getPlatform()

    val res = object : KostraResources {
        override val string: Map<StringResourceKey, ResourceContainer> = emptyMap()
        override val plural: Map<PluralResourceKey, ResourceContainer> = emptyMap()
        override val drawable: Map<DrawableResourceKey, ResourceContainer> = emptyMap()
        override val binary: Map<BinaryResourceKey, ResourceContainer> = emptyMap()
    }

    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}
