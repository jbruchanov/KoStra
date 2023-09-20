package com.jibru.kostra.icu

class PluralRules(val rules: List<Rule>) {

    fun select(number: Float): PluralCategory = select(number.toDouble())

    fun select(number: Double): PluralCategory = select(FixedDecimal(number))

    fun select(number: Int): PluralCategory = select(number.toLong())

    fun select(number: Long): PluralCategory = select(FixedDecimal(number))

    fun select(n: IFixedDecimal): PluralCategory {
        if (n.isInfinite || n.isNaN) {
            return PluralCategory.Other
        }
        return rules.firstOrNull { it.appliesTo(n) }?.category ?: PluralCategory.Other
    }
}

class Rule(
    val category: PluralCategory,
    val constraint: Constraint,
) {
    fun appliesTo(n: IFixedDecimal) = constraint.isFulfilled(n)
}
