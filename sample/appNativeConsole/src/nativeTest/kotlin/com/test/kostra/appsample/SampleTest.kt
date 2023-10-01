package com.test.kostra.appsample

import app.native.N
import app.native.stringResource
import kotlin.test.Ignore
import kotlin.test.Test

class SampleTest {
    @Test
    //doesn't work now, unclear how to set the working directory where the resources are copied to
    @Ignore
    fun test() {
        println(stringResource(N.string.action_add))
    }
}
