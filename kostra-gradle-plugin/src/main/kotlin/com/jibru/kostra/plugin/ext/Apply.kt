package com.jibru.kostra.plugin.ext

internal inline fun <T> T.applyIf(predicate: Boolean, action: T.() -> Unit): T {
    if (predicate) run(action)
    return this
}

internal inline fun <T, V : Any> T.applyIfNotNull(value: V?, action: T.(V) -> Unit): T {
    if (value != null) this.action(value)
    return this
}
