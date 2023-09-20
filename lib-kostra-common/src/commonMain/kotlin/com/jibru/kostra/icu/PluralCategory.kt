package com.jibru.kostra.icu

enum class PluralCategory(val index: Int, val keyword: String) {
    Zero(0, "zero"),
    One(1, "one"),
    Two(2, "two"),
    Few(3, "few"),
    Many(4, "many"),
    Other(5, "other"),
    ;

    companion object {
        val map = entries.associateBy { it.keyword }

        val size = map.size

        fun of(keyword: String) = map.getValue(keyword)

        fun Map<PluralCategory, String>.toPluralList() = map.values.map { this[it] }
    }
}
