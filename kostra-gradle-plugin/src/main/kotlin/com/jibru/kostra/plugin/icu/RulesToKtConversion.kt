package com.jibru.kostra.plugin.icu

import com.jibru.kostra.icu.Constraint
import com.jibru.kostra.icu.Operand
import com.jibru.kostra.icu.PluralCategory
import com.jibru.kostra.icu.PluralRules
import com.jibru.kostra.icu.Rule
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.asTypeName

internal fun PluralRules.toProperty(name: String, comment: String? = null): PropertySpec {
    val typePluralRules = PluralRules::class.asTypeName()
    return PropertySpec
        .builder(name, typePluralRules, KModifier.PRIVATE)
        .delegate(
            CodeBlock.builder()
                .addStatement("lazy(LazyThreadSafetyMode.NONE) { %T(", typePluralRules)
                .indent()
                .apply {
                    if (comment != null) {
                        addStatement("/*%L*/", comment)
                    }
                }
                .addStatement("rules = listOf(")
                .indent()
                .apply {
                    rules.forEachIndexed { index, rule ->
                        //skip any last if it's Other & with None constraint
                        //handled in select() if nothing what matches found
                        val skip = rules.size - 1 == index && rule.category == PluralCategory.Other && rule.constraint == Constraint.None
                        if (!skip) {
                            add(rule.toCodeBlock())
                            add(",\n")
                        }
                    }
                }
                .unindent()
                .addStatement(")")
                .unindent()
                .addStatement(")}")
                .build(),
        )
        .build()
}

internal fun Rule.toCodeBlock() = CodeBlock.builder()
    .add("%T(category = %T.%L, ", Rule::class.asTypeName(), PluralCategory::class.asTypeName(), category)
    .add("constraint = ")
    .add(constraint.toCodeBlock())
    .add(")")
    .build()

internal fun Constraint.toCodeBlock(): CodeBlock = CodeBlock.builder()
    .apply {
        indent()
        when (val constraint = this@toCodeBlock) {
            is Constraint.None -> add("%T.%L", Constraint::class.asTypeName(), Constraint.None)
            is Constraint.BinaryConstraint -> {
                addStatement("%T(", constraint::class.asTypeName())
                add("%L,\n", constraint.a.toCodeBlock())
                add("%L", constraint.b.toCodeBlock())
                add(")")
            }

            is Constraint.Range -> {
                add("%T(", Constraint.Range::class.asTypeName())
                with(constraint) {
                    add("%L, %L, %T.%L, %L, %L, %L", mod, inRange, Operand::class.asTypeName(), operand, integersOnly, lowerBound, upperBound)
                    val rangeList = rangeList
                    if (rangeList != null) {
                        indent()
                        add(", longArrayOf(%L)", rangeList.joinToString())
                        unindent()
                    }
                }
                add(")")
            }

            else -> throw UnsupportedOperationException("Unhandled case for $this")
        }
        unindent()
    }
    .build()
