package com.jibru.kostra.plugin

import com.google.common.truth.Truth.assertThat
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class KostraPluginTest {
    @Test
    @Disabled
    fun test() {
        val project: Project = ProjectBuilder.builder().build()
        project.pluginManager.apply("kostra")
        assertThat(project.tasks.findByName("testKostra")).isNotNull()
    }
}
