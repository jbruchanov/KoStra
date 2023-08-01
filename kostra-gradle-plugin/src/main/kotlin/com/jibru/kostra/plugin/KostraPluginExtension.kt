package com.jibru.kostra.plugin

import org.gradle.api.provider.Property

interface KostraPluginExtension {
    val message: Property<String>
}
