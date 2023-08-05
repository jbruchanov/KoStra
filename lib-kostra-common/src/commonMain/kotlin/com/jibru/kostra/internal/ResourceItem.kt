package com.jibru.kostra.internal

import com.jibru.kostra.ResourceKey

data class ResourceItem<T>(
    val key: ResourceKey,
    val value: T,
    val qualifiers: Qualifiers,
)
