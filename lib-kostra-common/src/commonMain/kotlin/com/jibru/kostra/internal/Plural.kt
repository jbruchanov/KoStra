package com.jibru.kostra.internal

enum class Plural(val index: Int, val key: String) {
    Zero(0, "zero"),
    One(1, "one"),
    Two(2, "two"),
    Few(3, "few"),
    Many(4, "many"),
    Other(5, "other"),
    ;

    companion object {

        val Map = values().associateBy { it.key }
        fun Map<Plural, String>.toPluralList() = Map.values.map { this[it] }
    }
}
