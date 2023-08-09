package com.jibru.kostra.internal

internal actual fun loadResource(key: String): ByteArray {
    return ClassLoader.getSystemResource(key).readBytes()
}
