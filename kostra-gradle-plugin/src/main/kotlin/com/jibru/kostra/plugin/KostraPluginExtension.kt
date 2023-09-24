package com.jibru.kostra.plugin

import groovy.lang.Closure
import org.gradle.api.Action
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import java.io.File

abstract class KostraPluginExtension {
    abstract val className: Property<String>
    abstract val resourceDirs: ListProperty<File>
    abstract val outputDir: Property<File>
    abstract val outputDatabaseDirName: Property<String>
    abstract val autoConfig: Property<Boolean>
    abstract val useFileWatcher: Property<Boolean>
    abstract val composeDefaults: Property<Boolean>
    abstract val strictLocale: Property<Boolean>

    @get:Nested
    abstract val androidResources: AndroidResourcesExtension

    @Internal
    fun allResourceDirs(): List<File> = resourceDirs.get() + androidResources.resourceDirs.get()

    fun androidResources(action: Action<in AndroidResourcesExtension>) {
        action.execute(androidResources)
    }

    fun toFileResolverConfig(): FileResolverConfig = with(androidResources) {
        val defaults = FileResolverConfig.Defaults
        return FileResolverConfig(
            keyMapper = keyMapper.orNull?.let { closure -> { key, file -> closure.call(key, file) } }
                ?: keyMapperKt.orNull
                ?: defaults.keyMapper,
            stringFiles = stringFiles.get().toSet(),
            painterGroups = painterGroups.get().toSet(),
            imageExtensions = painterExtensions.get().toSet(),
            strictLocale = strictLocale.get(),
        )
    }
}

abstract class AndroidResourcesExtension {
    //TODO: is there a way to have keyMapper working from kt/groovy ?
    @get:Optional
    abstract val keyMapperKt: Property<(String, File) -> String>

    @get:Optional
    abstract val keyMapper: Property<Closure<String>>

    abstract val stringFiles: ListProperty<String>

    abstract val painterGroups: ListProperty<String>

    abstract val painterExtensions: ListProperty<String>

    @get:Optional
    abstract val resourceDirs: ListProperty<File>
}
