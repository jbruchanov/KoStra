package com.jibru.kostra

import com.jibru.kostra.internal.loadResource

fun KResources.assetPath(key: AssetResourceKey, qualifiers: KQualifiers): String =
    this.binary.get(key, qualifiers)

fun KResources.binaryByteArray(key: AssetResourceKey, qualifiers: KQualifiers): ByteArray =
    loadResource(binary.get(key, qualifiers))
