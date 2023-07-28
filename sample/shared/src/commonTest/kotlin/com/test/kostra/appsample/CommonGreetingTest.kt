package com.test.kostra.appsample

import kotlin.test.Test
import kotlin.test.assertTrue

class CommonGreetingTest {

    @Test
    fun testExample() {
        val greeting = Greeting()
        assertTrue(greeting.greet().contains("Hello"), "Check 'Hello' is mentioned")
        println(greeting.res)
    }
}
