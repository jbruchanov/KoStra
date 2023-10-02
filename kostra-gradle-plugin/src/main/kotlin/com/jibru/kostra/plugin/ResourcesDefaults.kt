package com.jibru.kostra.plugin

enum class ResourcesDefaults {
    ComposeCommon,
    ComposeGetters,
    Common,
    Getters,
    ExplicitGetters,
    ;

    val isCommon get() = this == ComposeCommon || this == Common
    val isGetter get() = this == ComposeGetters || this == Getters || this == ExplicitGetters
    val composable get() = this == ComposeCommon || this == ComposeGetters

    val useQualifierProvider get() = this != ExplicitGetters

    companion object {
        val AllCompose = listOf(ComposeCommon, ComposeGetters, ExplicitGetters)
        val AllBasic = listOf(Common, Getters)
    }
}
