package com.jibru.kostra.plugin.ext

import com.jibru.kostra.plugin.KostraPluginConfig
import org.gradle.api.Project

fun Project.hasComposePlugin() = plugins.any { it.javaClass.packageName.startsWith(KostraPluginConfig.ComposePluginPackage) }
