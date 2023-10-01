package com.jibru.kostra

import com.jibru.kostra.internal.openResource
import java.io.InputStream

fun KResources.binaryInputStream(key: AssetResourceKey, qualifiers: KQualifiers): InputStream =
    openResource(binary.get(key, qualifiers))
