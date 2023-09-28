package com.jibru.kostra.plugin.ext

internal inline fun <T> T.applyIf(predicate: Boolean, action: T.() -> T): T {
    return if (predicate) run(action) else this
}

internal inline fun <T, V : Any> T.applyIfNotNull(value: V?, action: T.(V) -> T): T {
    return if (value != null) this.action(value) else this
}
