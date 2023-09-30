package com.jibru.kostra.plugin

import com.jibru.kostra.plugin.ext.lowerCasedWith
import com.jibru.kostra.plugin.task.ComposeDefaults
import groovy.lang.Closure
import org.gradle.api.Action
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import java.io.File

abstract class KostraPluginExtension {
    abstract val className: Property<String>
    abstract val autoConfig: Property<Boolean>
    abstract val useFileWatcher: Property<Boolean>
    abstract val composeDefaults: ListProperty<ComposeDefaults>
    abstract val strictLocale: Property<Boolean>
    abstract val modulePrefix: Property<String>
    abstract val internalVisibility: Property<Boolean>

    val outputDatabaseDirName: Provider<String>
        get() = modulePrefix.map { it.lowerCasedWith(KostraPluginConfig.ResourceDbFolderName) }.orElse(KostraPluginConfig.ResourceDbFolderName)

    @get:Internal
    abstract val resourceDirs: ListProperty<File>

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
            keyMapper = keyMapper.orNull ?: defaults.keyMapper,
            stringFiles = stringFiles.get().toSet(),
            painterGroups = painterGroups.get().toSet(),
            imageExtensions = painterExtensions.get().toSet(),
            strictLocale = strictLocale.get(),
            modulePrefix = modulePrefix.getOrElse(""),
        )
    }
}

typealias KeyMapper = (String, File) -> String

abstract class AndroidResourcesExtension {

    @get:Optional
    abstract val keyMapper: Property<KeyMapper>

    abstract val stringFiles: ListProperty<String>

    abstract val painterGroups: ListProperty<String>

    abstract val painterExtensions: ListProperty<String>

    @get:Optional
    abstract val resourceDirs: ListProperty<File>

    fun keyMapper(lambda: KeyMapper) {
        keyMapper.set(lambda)
    }

    fun keyMapper(closure: Closure<String>) {
        val wrapper: KeyMapper = { key, file ->
            closure.call(key, file)
        }
        keyMapper.set(wrapper)
    }
}
