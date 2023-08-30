package com.jibru.kostra.icu

import kotlin.math.floor
import kotlin.math.pow

/**
 * https://unicode-org.github.io/icu-docs/apidoc/released/icu4j/com/ibm/icu/text/PluralRules.IFixedDecimal.html
 */
interface IFixedDecimal {
    /**
     * Returns the value corresponding to the specified operand (n, i, f, t, v, or w).
     * If the operand is 'n', returns a double; otherwise, returns an integer.
     *
     */
    fun getPluralOperand(operand: Operand): Double

    val isNaN: Boolean

    val isInfinite: Boolean

    /**
     * Whether the number has no nonzero fraction digits.
     */
    val hasIntegerValue: Boolean
}

class FixedDecimal : Number, Comparable<FixedDecimal>, IFixedDecimal {
    val source: Double
    val visibleDecimalDigitCount: Int
    val visibleDecimalDigitCountWithoutTrailingZeros: Int
    val decimalDigits: Long
    val decimalDigitsWithoutTrailingZeros: Long
    val integerValue: Long
    override val hasIntegerValue: Boolean
    val isNegative: Boolean
    val exponent: Int
    val baseFactor: Int

    constructor(n: Double, v: Int, f: Long, e: Int, c: Int) {
        isNegative = n < 0
        source = if (isNegative) -n else n
        visibleDecimalDigitCount = v
        decimalDigits = f
        integerValue = if (n > MAX) MAX else source.toLong()
        var initExpVal = e
        if (initExpVal == 0) {
            initExpVal = c
        }
        exponent = initExpVal
        hasIntegerValue = source == integerValue.toDouble()
        // check values. TODO make into unit test.
        // comment from ICU4j
        //long visiblePower = (int) Math.pow(10, v);
        //if (fractionalDigits > visiblePower) {
        //    throw new IllegalArgumentException();
        //}
        //double fraction = intValue + (fractionalDigits / (double) visiblePower);
        //if (fraction != source) {
        //    double diff = Math.abs(fraction - source)/(Math.abs(fraction) + Math.abs(source));
        //    if (diff > 0.00000001d) {
        //        throw new IllegalArgumentException();
        //    }
        //}
        if (f == 0L) {
            decimalDigitsWithoutTrailingZeros = 0
            visibleDecimalDigitCountWithoutTrailingZeros = 0
        } else {
            var fdwtz = f
            var trimmedCount = v
            while (fdwtz % 10 == 0L) {
                fdwtz /= 10
                --trimmedCount
            }
            decimalDigitsWithoutTrailingZeros = fdwtz
            visibleDecimalDigitCountWithoutTrailingZeros = trimmedCount
        }
        baseFactor = 10.0.pow(v.toDouble()).toInt()
    }

    constructor(n: Double, v: Int, f: Long, e: Int) : this(n, v, f, e, e)
    constructor(n: Double, v: Int, f: Long) : this(n, v, f, 0)
    constructor(n: Double, v: Int) : this(n, v, getFractionalDigits(n, v).toLong())
    constructor(n: Double) : this(n, decimals(n))
    constructor(n: Long) : this(n.toDouble(), 0)

    override fun getPluralOperand(operand: Operand): Double {
        return when (operand) {
            Operand.n -> if (exponent == 0) source else source * 10.0.pow(exponent.toDouble())
            Operand.i -> toInt().toDouble()
            Operand.f -> decimalDigits.toDouble()
            Operand.t -> decimalDigitsWithoutTrailingZeros.toDouble()
            Operand.v -> visibleDecimalDigitCount.toDouble()
            Operand.w -> visibleDecimalDigitCountWithoutTrailingZeros.toDouble()
            Operand.e -> exponent.toDouble()
            Operand.c -> exponent.toDouble()
            else -> toDouble()
        }
    }

    /**
     * We're not going to care about NaN.
     */
    override operator fun compareTo(other: FixedDecimal): Int {
        if (exponent != other.exponent) {
            return if (toDouble() < other.toDouble()) -1 else 1
        }
        if (integerValue != other.integerValue) {
            return if (integerValue < other.integerValue) -1 else 1
        }
        if (source != other.source) {
            return if (source < other.source) -1 else 1
        }
        if (visibleDecimalDigitCount != other.visibleDecimalDigitCount) {
            return if (visibleDecimalDigitCount < other.visibleDecimalDigitCount) -1 else 1
        }
        val diff: Long = decimalDigits - other.decimalDigits
        return if (diff != 0L) {
            if (diff < 0) -1 else 1
        } else {
            0
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }
        if (other === this) {
            return true
        }
        if (other !is FixedDecimal) {
            return false
        }
        return source == other.source && visibleDecimalDigitCount == other.visibleDecimalDigitCount && decimalDigits == other.decimalDigits && exponent == other.exponent
    }

    override fun hashCode(): Int {
        // TODO Auto-generated method stub
        return (decimalDigits + 37 * (visibleDecimalDigitCount + (37 * source).toInt())).toInt()
    }

    override fun toString(): String {
        val baseString = String.format("FD:%." + visibleDecimalDigitCount + "f", source)
        return if (exponent != 0) {
            baseString + "e" + exponent
        } else {
            baseString
        }
    }

    override fun toInt(): Int = toLong().toInt()

    override fun toLong(): Long {
        return if (exponent == 0) {
            integerValue
        } else {
            (10.0.pow(exponent.toDouble()) * integerValue).toLong()
        }
    }

    override fun toFloat(): Float {
        return (source * 10.0.pow(exponent.toDouble())).toFloat()
    }

    override fun toDouble(): Double {
        return (if (isNegative) -source else source) * 10.0.pow(exponent.toDouble())
    }

    override fun toByte(): Byte = toInt().toByte()

    override fun toShort(): Short = toInt().toShort()

    override val isNaN: Boolean get() = source.isNaN()

    override val isInfinite: Boolean get() = source.isInfinite()

    companion object {
        const val MAX = 1E18.toLong()

        fun createWithExponent(n: Double, v: Int, e: Int): FixedDecimal {
            return FixedDecimal(n, v, getFractionalDigits(n, v).toLong(), e)
        }

        private fun getFractionalDigits(n: Double, v: Int): Int {
            var n = n
            return if (v == 0) {
                0
            } else {
                if (n < 0) {
                    n = -n
                }
                val baseFactor = 10.0.pow(v.toDouble()).toInt()
                val scaled = Math.round(n * baseFactor)
                (scaled % baseFactor).toInt()
            }
        }

        private const val MAX_INTEGER_PART: Long = 1000000000

        /**
         * Return a guess as to the number of decimals that would be displayed. This is only a guess; callers should
         * always supply the decimals explicitly if possible. Currently, it is up to 6 decimals (without trailing zeros).
         * Returns 0 for infinities and nans.
         */
        private fun decimals(n: Double): Int {
            // Ugly...
            var n = n
            if (n.isInfinite() || n.isNaN()) {
                return 0
            }
            if (n < 0) {
                n = -n
            }
            if (n == floor(n)) {
                return 0
            }
            return if (n < MAX_INTEGER_PART) {
                val temp = (n * 1000000).toLong() % 1000000 // get 6 decimals
                var mask = 10
                var digits = 6
                while (digits > 0) {
                    if (temp % mask != 0L) {
                        return digits
                    }
                    mask *= 10
                    --digits
                }
                0
            } else {
                throw UnsupportedOperationException("JVM dependent code, use explicit ctor")
                /*
                val buf = String.format(Locale.ENGLISH, "%1.15e", n)
                val ePos = buf.lastIndexOf('e')
                var expNumPos = ePos + 1
                if (buf[expNumPos] == '+') {
                    expNumPos++
                }
                val exponentStr = buf.substring(expNumPos)
                val exponent = exponentStr.toInt()
                var numFractionDigits = ePos - 2 - exponent
                if (numFractionDigits < 0) {
                    return 0
                }
                var i = ePos - 1
                while (numFractionDigits > 0) {
                    if (buf[i] != '0') {
                        break
                    }
                    --numFractionDigits
                    --i
                }
                numFractionDigits
                 */
            }
        }
    }
}
