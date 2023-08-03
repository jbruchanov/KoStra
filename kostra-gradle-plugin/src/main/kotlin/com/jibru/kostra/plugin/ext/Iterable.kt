package com.jibru.kostra.plugin.ext

inline fun <T, K> Iterable<T>.distinctByLast(selector: (T) -> K): List<T> {
    val map = LinkedHashMap<K, T>()
    for (e in this) {
        val key = selector(e)
        map[key] = e
    }
    return map.values.toList()
}
