package com.jibru.kostra

interface ResourceKey {
    val key: String
}

interface AssetResourceKey : ResourceKey

@JvmInline
value class StringArrayResourceKey(override val key: String) : ResourceKey

@JvmInline
value class StringResourceKey(override val key: String) : ResourceKey

@JvmInline
value class PluralResourceKey(override val key: String) : ResourceKey

@JvmInline
value class DrawableResourceKey(override val key: String) : AssetResourceKey

@JvmInline
value class BinaryResourceKey(override val key: String) : AssetResourceKey
