package com.jibru.kostra.internal

import com.jibru.kostra.UnableToOpenResourceStream
import java.io.InputStream

internal actual fun loadResource(key: String): ByteArray = AndroidResourceImpl.getStream(key).readBytes()
internal fun openResource(key: String): InputStream = AndroidResourceImpl.getStream(key)

private object AndroidResourceImpl {
    fun getStream(path: String): InputStream {
        val classLoader = Thread.currentThread().contextClassLoader ?: (javaClass.classLoader)
        val resourceStream = classLoader.getResourceAsStream(path)
        return resourceStream ?: throw UnableToOpenResourceStream(path)
    }
}
