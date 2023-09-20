package test

import com.jibru.kostra.icu.FixedDecimal
import com.jibru.kostra.icu.IFixedDecimal

sealed class PluralsSample : Iterable<IFixedDecimal> {

    abstract val text: String

    override fun toString(): String {
        return "${this::class.java.simpleName}:$text"
    }

    class I(private val value: Long, override val text: String) : PluralsSample() {
        override fun iterator(): Iterator<IFixedDecimal> = iterator { yield(FixedDecimal(value)) }
    }

    class IRange(private val range: LongRange, override val text: String) : PluralsSample() {
        override fun iterator(): Iterator<IFixedDecimal> = range.map { FixedDecimal(it) }.iterator()
    }

    class F(private val value: Double, override val text: String) : PluralsSample() {
        override fun iterator(): Iterator<IFixedDecimal> = iterator { yield(FixedDecimal(value)) }
    }

    class FRange(private val range: ClosedFloatingPointRange<Double>, override val text: String) : PluralsSample() {
        override fun iterator(): Iterator<IFixedDecimal> = iterator(step = 0.125)

        fun iterator(step: Double): Iterator<IFixedDecimal> = iterator {
            var now = range.start
            while (now < range.endInclusive) {
                yield(FixedDecimal(now))
                now += step
            }
            yield(FixedDecimal(range.endInclusive))
        }
    }

    companion object {
        fun parse(text: String): List<PluralsSample> {
            return text.split("@").filter { it.startsWith("integer") || it.startsWith("decimal") }
                .map { group ->
                    val items = group.substringAfter("@").trim().split(",? ".toRegex()).map { it.trim() }.filter { it.isNotEmpty() && it != "…" }
                    if (group.startsWith("integer")) {
                        items.drop(1).map {
                            when {
                                it.contains("~") -> it.split("~").let { v -> IRange(v[0].toLong()..v[1].toLong(), it) }
                                else -> {
                                    val v = if (it.contains("c")) it.replace("c", "e").toDouble().toLong().toString() else it
                                    I(v.toLong(), it)
                                }
                            }
                        }
                    } else if (group.startsWith("decimal")) {
                        items.drop(1).map {
                            when {
                                it.contains("~") -> it.split("~").let { v -> FRange(v[0].toDouble().rangeTo(v[1].toDouble()), it) }
                                else -> F(it.replace("c", "e").toDouble(), it)
                            }
                        }
                    } else {
                        throw UnsupportedOperationException(group)
                    }
                }
                .flatten()
        }
    }
}
