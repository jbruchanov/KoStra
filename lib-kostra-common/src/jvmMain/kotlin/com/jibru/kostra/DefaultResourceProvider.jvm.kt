package com.jibru.kostra

import com.jibru.kostra.internal.KostraResources
import com.jibru.kostra.internal.Qualifiers
import com.jibru.kostra.internal.openResource
import java.io.InputStream

fun KostraResources.binaryInputStream(key: AssetResourceKey, qualifiers: Qualifiers = defaultQualifiers()): InputStream =
    openResource(binary.get(key, qualifiers))
