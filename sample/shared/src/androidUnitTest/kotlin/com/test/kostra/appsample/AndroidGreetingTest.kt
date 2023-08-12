package com.test.kostra.appsample

import org.junit.Assert.assertTrue
import org.junit.Ignore
import org.junit.Test

class AndroidGreetingTest {

    @Test
    @Ignore
    fun testExample() {
        assertTrue("Check Android is mentioned", Greeting().greet().contains("Android"))
    }
}
