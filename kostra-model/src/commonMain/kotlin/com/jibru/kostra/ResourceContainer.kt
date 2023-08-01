package com.jibru.kostra

import com.jibru.kostra.internal.ResourceItem

sealed class ResourceContainer {
    data class Value(
        val key: ResourceKey,
        val values: List<ResourceItem<Any>>,
    ) : ResourceContainer() {
        constructor(key: ResourceKey, value: ResourceItem<Any>) : this(key, listOf(value))
    }

    //currently unused
    data class Reference(val key: ResourceKey) : ResourceContainer()

    fun asValueContainer() = this as? Value ?: throw IllegalStateException("Reference not supported!")
}
