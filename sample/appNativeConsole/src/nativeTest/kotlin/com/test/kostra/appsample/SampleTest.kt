package com.test.kostra.appsample

import com.jibru.kostra.K
import com.jibru.kostra.Resources
import com.jibru.kostra.string
import kotlin.test.Ignore
import kotlin.test.Test

class SampleTest {
    @Test
    //doesn't work now, unclear how to set the working directory where the resources are copied to
    @Ignore
    fun test() {
        println(Resources.string(K.string.action_add))
    }
}
