package com.jibru.kostra.plugin.ext

inline fun <I, O> Iterable<I>.setOf(selector: (I) -> O): Set<O> {
    val result = mutableSetOf<O>()
    val iterator = iterator()
    while (iterator.hasNext()) {
        val v = selector(iterator.next())
        result.add(v)
    }
    return result
}
