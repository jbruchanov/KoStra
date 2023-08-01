@file:Suppress("unused")

package com.jibru.kostra.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.slf4j.LoggerFactory

class KostraPlugin : Plugin<Project> {

    private val logger = LoggerFactory.getLogger(KostraPlugin::class.java)

    override fun apply(target: Project) {
        val extension = target.extensions.create("kostra", KostraPluginExtension::class.java)
        extension.message.set("Init message")

        target.task("testKostra") { task ->
            task.group = "kostra"
            task.doLast {
                println("KostraPlugin")
                println("msg:" + extension.message.orNull)
            }
        }
    }
}
