package com.jibru.kostra.plugin.ext

import com.jibru.kostra.plugin.KostraPluginConfig
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

fun Project.hasComposePlugin() = plugins.any { it.javaClass.packageName.startsWith(KostraPluginConfig.ComposePluginPackage) }

fun Project.jvmPlugin() = extensions.findByType(JavaPluginExtension::class.java)

fun Project.hasJvmPlugin() = jvmPlugin() != null

fun Project.jvmMainSourceSet() = jvmPlugin()?.sourceSets?.findByName("main")

fun Project.kmpPlugin() = extensions.findByType(KotlinMultiplatformExtension::class.java)

fun Project.hasKmpPlugin() = kmpPlugin() != null

fun Project.kmpMainSourceSet() = kmpPlugin()?.sourceSets?.findByName("commonMain")
