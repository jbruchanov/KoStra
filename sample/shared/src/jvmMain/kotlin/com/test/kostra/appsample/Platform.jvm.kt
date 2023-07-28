package com.test.kostra.appsample

class JvmPlatform : Platform {
    override val name: String = "jvm-${System.getProperty("java.version")}"
}

actual fun getPlatform(): Platform = JvmPlatform()
