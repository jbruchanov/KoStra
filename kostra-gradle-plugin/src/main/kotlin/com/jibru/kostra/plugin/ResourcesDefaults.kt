package com.jibru.kostra.plugin

enum class ResourcesDefaults {
    ComposeCommon,
    ComposeGetters,
    Common,
    Getters,
    ;

    val isCommon get() = this == ComposeCommon || this == Common
    val isGetter get() = this == ComposeGetters || this == Getters
    val composable get() = this == ComposeCommon || this == ComposeGetters

    companion object {
        val AllCompose = listOf(ComposeCommon, ComposeGetters)
        val AllBasic = listOf(Common, Getters)
    }
}
