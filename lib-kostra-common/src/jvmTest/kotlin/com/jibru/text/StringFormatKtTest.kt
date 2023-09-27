package com.jibru.text

import java.util.Locale
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class StringFormatKtTest {
    @Test
    fun jdkTest() {
        // https://github.com/openjdk/jdk/blob/master/test/jdk/java/util/Formatter/Basic-X.java.template#L374-L400
        //---------------------------------------------------------------------
        // %s
        //
        // General conversion applicable to any argument.
        //---------------------------------------------------------------------
        test("%s", "Hello, Duke", "Hello, Duke")
        test("%S", "HELLO, DUKE", "Hello, Duke")
        test("%20S", "         HELLO, DUKE", "Hello, Duke")
        test("%20s", "         Hello, Duke", "Hello, Duke")
        test("%20.5s", "               Hello", "Hello, Duke")
        test("%-20s", "Hello, Duke         ", "Hello, Duke")
        test("%-20.5s", "Hello               ", "Hello, Duke")

        test("%s", "null", null as Any?)

        val sb = StringBuffer("foo bar")
        test("%s", sb.toString(), sb)
        test("%S", sb.toString().uppercase(Locale.getDefault()), sb)

        //---------------------------------------------------------------------
        // %s - errors
        //---------------------------------------------------------------------
        //taken as FLAG
        testExclJvmFormat("%-s", "Hello, Duke", "Hello, Duke")
        testExclJvmFormat("%--s", "Hello, Duke", "Hello, Duke")
        testExclJvmFormat("%#s", "Hello, Duke", "Hello, Duke")
        /*
        None of these are errors due to feature less implementation
        tryCatch("%-s", MissingFormatWidthException::class.java)
        tryCatch("%--s", DuplicateFormatFlagsException::class.java)
        tryCatch("%#s", FormatFlagsConversionMismatchException::class.java, 0)
        tryCatch("%#s", FormatFlagsConversionMismatchException::class.java, 0.5f)
        tryCatch("%#s", FormatFlagsConversionMismatchException::class.java, "hello")
        tryCatch("%#s", FormatFlagsConversionMismatchException::class.java, null as Any?)
         */
    }

    @Test
    fun others() {
        test("X%sx", "XTestx", "Test")
        test("X%Sx", "XTESTx", "Test")
        test("%%s", "%s", "Test")
        test("x%%sX", "x%sX", "Test")
        test("%s,%S", "a,B", "a", "b")
        test("%1\$s,%2\$S,%1\$S,%2\$s", "a,B,A,b", "a", "b")
    }

    @Test
    fun indexes() {
        test("%2\$s %1\$s", "first second", "second", "first")
        test("%1\$s %2\$S %2\$s %1\$S", "a B b a", "a", "b")
    }

    private fun test(template: String, expected: String, vararg args: Any?) {
        assertEquals(expected, template.sFormat(*args))
        assertEquals(expected, template.format(*args))
    }

    private fun testExclJvmFormat(template: String, expected: String, vararg args: Any?) {
        assertEquals(expected, template.sFormat(*args))
    }
}
