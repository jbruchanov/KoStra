package com.jibru.kostra.internal

import com.jibru.kostra.BinaryResourceKey
import com.jibru.kostra.DrawableResourceKey
import com.jibru.kostra.PluralResourceKey
import com.jibru.kostra.ResourceContainer
import com.jibru.kostra.StringResourceKey

interface KostraResources {
    val string: Map<StringResourceKey, ResourceContainer>
    val plural: Map<PluralResourceKey, ResourceContainer>
    val drawable: Map<DrawableResourceKey, ResourceContainer>
    val binary: Map<BinaryResourceKey, ResourceContainer>
}

interface KostraResourceHider {
    companion object : KostraResourceHider
}

class AppResources(
    override val string: Map<StringResourceKey, ResourceContainer> = emptyMap(),
    override val plural: Map<PluralResourceKey, ResourceContainer> = emptyMap(),
    override val drawable: Map<DrawableResourceKey, ResourceContainer> = emptyMap(),
    override val binary: Map<BinaryResourceKey, ResourceContainer> = emptyMap(),
) : KostraResources
