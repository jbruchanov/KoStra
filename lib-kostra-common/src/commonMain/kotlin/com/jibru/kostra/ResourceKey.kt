package com.jibru.kostra

interface ResourceKey {
    val key: Int
}

interface AssetResourceKey : ResourceKey

interface StringResourceKey : ResourceKey

interface PluralResourceKey : ResourceKey

interface PainterResourceKey : AssetResourceKey

interface BinaryResourceKey : AssetResourceKey
