package com.jibru.kostra.internal

import com.jibru.kostra.UnableToOpenResourceStream
import java.io.InputStream

internal actual fun loadResource(key: String): InputStream = JvmResourceImpl.getStream(key)

private object JvmResourceImpl {
    fun getStream(path: String): InputStream {
        val classLoader = Thread.currentThread().contextClassLoader ?: (javaClass.classLoader)
        val resourceStream = classLoader.getResourceAsStream(path)
        return resourceStream ?: throw UnableToOpenResourceStream(path)
    }
}
