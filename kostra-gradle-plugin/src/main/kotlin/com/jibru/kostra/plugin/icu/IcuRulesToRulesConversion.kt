package com.jibru.kostra.plugin.icu

import com.jibru.kostra.icu.Constraint
import com.jibru.kostra.icu.PluralCategory
import com.jibru.kostra.icu.PluralRules
import com.jibru.kostra.icu.Rule

internal fun IcuPluralRules.toPluralRules() = PluralRules(rules.toRuleList())
internal fun IcuRuleList.toRuleList() = this.rules.map { it.toRule() }
internal fun IcuRule.toRule() = Rule(category = PluralCategory.of(keyword), constraint = constraint.toConstraint())
internal fun IcuConstraint.toConstraint(): Constraint = when (this) {
    is IcuConstraint.IcuNoConstraint -> Constraint.None
    is IcuConstraint.IcuAndConstraint -> Constraint.And(a.toConstraint(), b.toConstraint())
    is IcuConstraint.IcuOrConstraint -> Constraint.Or(a.toConstraint(), b.toConstraint())
    is IcuConstraint.IcuRangeConstraint -> Constraint.Range(
        mod = mod,
        inRange = inRange,
        operand = operand,
        integersOnly = integersOnly,
        lowerBound = lowerBound,
        upperBound = upperBound,
        rangeList = range_list,
    )

    else -> throw UnsupportedOperationException("Unhandled case for $this")
}
