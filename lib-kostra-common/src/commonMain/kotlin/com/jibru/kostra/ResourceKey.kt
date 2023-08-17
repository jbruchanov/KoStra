package com.jibru.kostra

interface ResourceKey {
    val key: Int
}

interface AssetResourceKey : ResourceKey

@JvmInline
value class StringResourceKey(override val key: Int) : ResourceKey

@JvmInline
value class PluralResourceKey(override val key: Int) : ResourceKey

@JvmInline
value class PainterResourceKey(override val key: Int) : AssetResourceKey

@JvmInline
value class BinaryResourceKey(override val key: Int) : AssetResourceKey
