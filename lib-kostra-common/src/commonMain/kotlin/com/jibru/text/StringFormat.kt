package com.jibru.text

import kotlin.jvm.JvmName
import kotlin.math.absoluteValue

/**
 * Simplified version for formatting strings.
 * Only supported conversion is 's', 'S'.
 *
 * Java docs https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html
 *
 */
@JvmName("sFormat1")
fun String.sFormat(vararg args: Any?): String = _sFormat(this, *args)

@JvmName("sFormat2")
fun sFormat(template: String, vararg args: Any?) = _sFormat(template, *args)

@Suppress("FunctionName")
private fun _sFormat(s: String, vararg args: Any?): String {
    if (args.isEmpty()) return s
    val al = mutableListOf<Formatter>()
    var i = 0
    val len: Int = s.length
    while (i < len) {
        val nextPercent: Int = s.indexOf('%', i)
        val nextNextPercent: Int = s.indexOf('%', nextPercent + 1)
        val pointingAtPercent = s[i] == '%'
        val escaping = pointingAtPercent && nextPercent + 1 == nextNextPercent
        i = if (pointingAtPercent && !escaping) {
            // We have a format specifier
            val fsp = FormatSpecifierParser(s, i + 1)
            al.add(fsp.getFormatSpecifier())
            fsp.getEndIdx()
        } else {
            // This is plain-text part, find the maximal plain-text
            // sequence and store it.
            var plainTextStart = i
            val plainTextEnd = when {
                nextPercent == -1 -> len
                //skip take '%%' as FixedString, to break pattern in next loop and will be taken as FixedString as well
                escaping -> {
                    //skip 1st "%" as it's escaping
                    plainTextStart++
                    nextNextPercent + 1
                }

                else -> nextPercent
            }
            al.add(FixedString(s, plainTextStart, plainTextEnd))
            plainTextEnd
        }
    }

    val sb = StringBuilder()
    var formatSpecifierIndex = 0
    al.onEach {
        it.append(sb, formatSpecifierIndex, *args)
        if (it is FormatSpecifier) {
            formatSpecifierIndex++
        }
    }
    return sb.toString()
}

private sealed class Formatter {
    abstract fun append(appendable: Appendable, index: Int, vararg args: Any?)
}

private class FixedString(private val s: String, private val start: Int, private val end: Int) : Formatter() {
    override fun append(appendable: Appendable, index: Int, vararg args: Any?) {
        appendable.append(s, start, end)
    }
}

private class FormatSpecifier(
    private val index: String?,
    private val flags: String,
    private val width: String?,
    private val precision: String?,
    private val tT: String?,
    private val conv: String,
) : Formatter() {

    private val argIndex: Int? = index?.toIntOrNull()?.let { it - 1 }

    init {
        require(conv == "s" || conv == "S") { "Only supported Conversion is 's' or 'S', found:$conv" }
    }

    override fun append(appendable: Appendable, index: Int, vararg args: Any?) {
        appendable.append(args[argIndex ?: index].toString().applyFormatting())
    }

    private fun String.applyFormatting(): String {
        var v = this@applyFormatting
        v = precision?.toIntOrNull()?.let { v.take(it) } ?: v
        v = width?.toDoubleOrNull()?.toInt()?.let {
            if (it >= 0) {
                v.padStart(it, ' ')
            } else {
                v.padEnd(it.absoluteValue, ' ')
            }
        } ?: v
        v = if (conv == "S") v.uppercase() else v
        return v
    }
}

/**
 * %[argument_index$][flags][width][.precision]conversion
 * JVM implementation
 */
private class FormatSpecifierParser(format: String, startIdx: Int) {
    private val format: String
    private var cursor: Int
    private val fs: FormatSpecifier
    private var index: String? = null
    private var flags: String
    private var width: String? = null
    private var precision: String? = null
    private var tT: String? = null
    private val conv: String

    init {
        this.format = format
        cursor = startIdx
        // Index
        if (nextIsInt()) {
            val nint = nextInt()
            if (peek() == '$') {
                index = nint
                advance()
            } else if (nint[0] == '0') {
                // This is a flag, skip to parsing flags.
                back(nint.length)
            } else {
                // This is the width, skip to parsing precision.
                width = nint
            }
        }
        // Flags
        flags = ""
        while (width == null && FLAGS.indexOf(peek()) >= 0) {
            flags += advance()
        }
        // Width
        if (width == null && nextIsInt()) {
            width = nextInt()
        }
        // Precision
        if (peek() == '.') {
            advance()
            if (!nextIsInt()) {
                throw IllegalFormatPrecisionException(peek())
            }
            precision = nextInt()
        }
        // tT
        if (peek() == 't' || peek() == 'T') {
            tT = advance().toString()
        }
        // Conversion
        conv = advance().toString()
        fs = FormatSpecifier(index, flags, width, precision, tT, conv)
    }

    private fun nextInt(): String {
        val strBegin = cursor
        var i = 0
        while (nextIsInt(i)) {
            advance()
            i++
        }
        return format.substring(strBegin, cursor)
    }

    private fun nextIsInt(pos: Int = 0): Boolean {
        if (isEnd()) return false
        val peek = peek()
        val next = format.getOrNull(cursor + 1)
        return peek.isDigit() ||
            //start of negative number
            pos == 0 &&
            peek == '-' &&
            next?.isDigit() == true
    }

    private fun peek(): Char = requireNotEnd { format[cursor] }

    private fun advance(): Char = requireNotEnd { format[cursor++] }

    private fun back(len: Int) {
        cursor -= len
    }

    private fun isEnd(): Boolean = cursor == format.length

    fun getFormatSpecifier(): FormatSpecifier = fs

    fun getEndIdx(): Int = cursor

    private inline fun <T> requireNotEnd(block: () -> T): T {
        if (isEnd()) throw UnknownFormatConversionException("End of String")
        return block()
    }

    companion object {
        private const val FLAGS = ",-(+# 0<"
    }
}

class IllegalFormatPrecisionException(val peek: Char) : Throwable()

class UnknownFormatConversionException(text: String) : Throwable(text)
