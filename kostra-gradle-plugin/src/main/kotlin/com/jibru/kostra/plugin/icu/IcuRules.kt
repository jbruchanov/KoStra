@file:Suppress("UNCHECKED_CAST")

package com.jibru.kostra.plugin.icu

import com.ibm.icu.text.PluralRules
import com.jibru.kostra.icu.Operand
import java.lang.reflect.Field
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass

internal abstract class ReflectionWrapper(val ref: Any)

internal class IcuPluralRules(pluralRules: PluralRules) : ReflectionWrapper(pluralRules) {
    val rules: IcuRuleList by fieldObject(IcuRuleList::class)
}

internal class IcuRuleList(ref: Any) : ReflectionWrapper(ref) {
    val rules by fieldObject { (it as List<Any>).map { ref -> IcuRule(ref) } }
}

internal class IcuRule(ref: Any) : ReflectionWrapper(ref) {
    val keyword by field<String>()
    val constraint by fieldObject { IcuConstraint.create(it) }
}

internal sealed class IcuConstraint(ref: Any) : ReflectionWrapper(ref) {

    override fun toString(): String = "${javaClass::getName}:$ref"

    class IcuAndConstraint(ref: Any) : IcuBinaryConstraint(ref)

    class IcuOrConstraint(ref: Any) : IcuBinaryConstraint(ref)

    open class IcuBinaryConstraint(ref: Any) : IcuConstraint(ref) {
        val a by fieldObject { create(it) }
        val b by fieldObject { create(it) }
    }

    class IcuRangeConstraint(ref: Any) : IcuConstraint(ref) {
        val mod: Int by field()
        val inRange: Boolean by field()
        val integersOnly: Boolean by field()
        val lowerBound: Double by field()
        val upperBound: Double by field()
        val range_list: LongArray by field()
        val operand: Operand by fieldObject { Operand.valueOf((it as Enum<*>).name) }
    }

    class IcuNoConstraint(ref: Any) : IcuConstraint(ref)

    companion object {
        private const val PLURAL_RULES = "com.ibm.icu.text.PluralRules\$"

        fun create(ref: Any): IcuConstraint {
            return when (val className = ref::class.java.name.replace(PLURAL_RULES, "")) {
                "AndConstraint" -> IcuAndConstraint(ref)
                "OrConstraint" -> IcuOrConstraint(ref)
                "BinaryConstraint" -> IcuBinaryConstraint(ref)
                "RangeConstraint" -> IcuRangeConstraint(ref)
                "1" -> IcuNoConstraint(ref)
                else -> throw UnsupportedOperationException(className)
            }
        }
    }
}

private fun <O : Any> ReflectionWrapper.fieldObject(clazz: KClass<O>): ReadOnlyProperty<Any, O> = fieldObject {
    val ctor = clazz.constructors.first { ctor -> ctor.parameters.size == 1 }
    ctor.call(it)
}

private fun <O : Any> ReflectionWrapper.fieldObject(factory: (obj: Any) -> O): ReadOnlyProperty<Any, O> = ReadOnlyProperty { _, property ->
    val f = ref.findField(property.name)
    factory(f.get(ref))
}

private fun <O> ReflectionWrapper.field(): ReadOnlyProperty<Any, O> = ReadOnlyProperty { _, property ->
    val f = ref.findField(property.name)
    f.get(ref) as O
}

private fun Any.findField(name: String): Field {
    var clazz: Class<*>? = javaClass
    while (clazz != null) {
        val f = runCatching { clazz!!.getDeclaredField(name) }.getOrNull()
        f?.isAccessible = true
        if (f != null) return f
        clazz = clazz.superclass
    }
    throw NoSuchFieldException(name)
}
