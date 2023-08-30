package com.jibru.kostra.plugin

import com.jibru.kostra.plugin.ext.setOf
import groovy.lang.Closure
import java.io.File
import java.util.regex.Pattern
import org.gradle.api.Action
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional

abstract class KostraPluginExtension {
    abstract val className: Property<String>
    abstract val resourceDirs: ListProperty<File>
    abstract val outputDir: Property<File>
    abstract val autoConfig: Property<Boolean>
    abstract val useFileWatcher: Property<Boolean>
    abstract val composeDefaults: Property<Boolean>

    @get:Nested
    abstract val androidResources: AndroidResourcesExtension

    @Internal
    fun allResourceDirs(): List<File> = resourceDirs.get() + androidResources.resourceDirs.get()

    fun androidResources(action: Action<in AndroidResourcesExtension>) {
        action.execute(androidResources)
    }
}

abstract class AndroidResourcesExtension {
    //TODO: is there a way to have keyMapper working from kt/groovy ?
    @get:Optional
    abstract val keyMapperKt: Property<(String, File) -> String>

    @get:Optional
    abstract val keyMapper: Property<Closure<String>>

    @get:Optional
    abstract val stringFiles: Property<Collection<Pattern>>

    @get:Optional
    abstract val drawableGroups: Property<Collection<Pattern>>

    @get:Optional
    abstract val drawableExtensions: Property<Collection<String>>

    @get:Optional
    abstract val resourceDirs: ListProperty<File>

    fun toFileResolverConfig(): FileResolverConfig {
        val defaults = FileResolverConfig.Defaults
        return FileResolverConfig(
            keyMapper = keyMapper.orNull?.let { closure -> { key, file -> closure.call(key, file) } }
                ?: keyMapperKt.orNull
                ?: defaults.keyMapper,
            stringFiles = stringFiles.orNull?.setOf { v -> v.toRegex() } ?: defaults.stringFiles,
            drawableGroups = drawableGroups.orNull?.setOf { v -> v.toRegex() } ?: defaults.drawableGroups,
            drawableExtensions = drawableExtensions.orNull?.toSet() ?: defaults.drawableExtensions,
        )
    }
}
