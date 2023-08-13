package com.jibru.kostra.plugin

import groovy.lang.Closure
import org.gradle.api.Action
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import java.io.File
import java.util.regex.Pattern

abstract class KostraPluginExtension {
    abstract val packageName: Property<String>
    abstract val kClassName: Property<String>
    abstract val resourceDirs: ListProperty<File>
    abstract val output: Property<File>

    @get:Nested
    abstract val androidResources: AndroidResourcesExtension

    fun androidResources(action: Action<in AndroidResourcesExtension>) {
        action.execute(androidResources)
    }
}

abstract class AndroidResourcesExtension {
    @get:Optional
    abstract val keyMapper: Property<Closure<String>>

    @get:Optional
    abstract val stringFiles: Property<Collection<Pattern>>

    @get:Optional
    abstract val drawableGroups: Property<Collection<Pattern>>

    @get:Optional
    abstract val drawableExtensions: Property<Collection<String>>
}
