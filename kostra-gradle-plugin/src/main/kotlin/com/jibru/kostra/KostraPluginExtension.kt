package com.jibru.kostra

import org.gradle.api.provider.Property

interface KostraPluginExtension {
    val message: Property<String>
}
