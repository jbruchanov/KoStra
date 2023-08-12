package com.jibru.kostra.plugin

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import java.io.File

abstract class KostraPluginExtension {
    abstract val packageName: Property<String>
    abstract val kClassName: Property<String>
    abstract val resourceDirs: ListProperty<File>
    abstract val output: Property<File>
}
