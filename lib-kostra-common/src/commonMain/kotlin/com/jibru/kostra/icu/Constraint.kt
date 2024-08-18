package com.jibru.kostra.icu

interface IConstraint {
    /*
     * Returns true if the number fulfills the constraint.
     * @param n the number to test, >= 0.
     */
    fun isFulfilled(number: IFixedDecimal): Boolean
}

sealed class Constraint : IConstraint {
    abstract class BinaryConstraint : Constraint() {
        abstract val a: Constraint
        abstract val b: Constraint
    }

    data object None : Constraint() {
        override fun isFulfilled(number: IFixedDecimal): Boolean = true
    }

    class And(override val a: Constraint, override val b: Constraint) : BinaryConstraint() {
        override fun isFulfilled(number: IFixedDecimal): Boolean = a.isFulfilled(number) && b.isFulfilled(number)
    }

    class Or(override val a: Constraint, override val b: Constraint) : BinaryConstraint() {
        override fun isFulfilled(number: IFixedDecimal): Boolean = a.isFulfilled(number) || b.isFulfilled(number)
    }

    class Range(
        val mod: Int,
        val inRange: Boolean,
        val operand: Operand,
        val integersOnly: Boolean,
        val lowerBound: Double,
        val upperBound: Double,
        val rangeList: LongArray? = null,
    ) : Constraint() {

        //copy of com.ibm.icu.text.PluralRules.RangeConstraint.isFulfilled
        override fun isFulfilled(number: IFixedDecimal): Boolean {
            var n: Double = number.getPluralOperand(operand)
            if (integersOnly &&
                n - n.toLong() != 0.0 ||
                operand == Operand.j &&
                number.getPluralOperand(Operand.v) != 0.0
            ) {
                return !inRange
            }
            if (mod != 0) {
                n %= mod // java % handles double numerator the way we want
            }
            var test = n in lowerBound..upperBound
            if (test && rangeList != null) {
                test = false
                var i = 0
                while (!test && i < rangeList.size) {
                    test = n >= rangeList[i] && n <= rangeList[i + 1]
                    i += 2
                }
            }
            return inRange == test
        }
    }
}
