package com.jibru.kostra.internal

import com.jibru.kostra.DrawableResourceKey
import com.jibru.kostra.PluralResourceKey
import com.jibru.kostra.ResourceContainer
import com.jibru.kostra.ResourceKey
import com.jibru.kostra.StringArrayResourceKey
import com.jibru.kostra.StringResourceKey

interface KostraResources {
    val strings: Map<StringResourceKey, ResourceContainer>
    val plurals: Map<PluralResourceKey, ResourceContainer>
    val stringArrays: Map<StringArrayResourceKey, ResourceContainer>
    val drawables: Map<DrawableResourceKey, ResourceContainer>
    val binary: Map<ResourceKey, ResourceContainer>
}
