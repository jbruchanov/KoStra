package com.test.kostra.appsample

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
