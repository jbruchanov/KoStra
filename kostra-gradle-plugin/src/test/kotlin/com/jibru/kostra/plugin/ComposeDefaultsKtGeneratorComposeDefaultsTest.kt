package com.jibru.kostra.plugin

import com.google.common.truth.Truth.assertThat
import com.jibru.kostra.plugin.ext.minify
import com.jibru.kostra.plugin.task.ComposeDefaults
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import test.IOTestTools
import java.util.stream.Stream

class ComposeDefaultsKtGeneratorComposeDefaultsTest : IOTestTools {

    @Test
    @Disabled("creates test files")
    @Suppress("UNCHECKED_CAST")
    fun createConfigs() {
        val configs = ComposeDefaultsArgsProvider.Items

        configs.forEach { args ->
            val array = args.get()
            val fileName = array[0] as String
            val defaults = array[1] as List<ComposeDefaults>
            val internalVisibility = array[2] as Boolean
            val gen = ComposeDefaultsKtGenerator(kClassName = "com.sample.app.K", internalVisibility = internalVisibility)
            val result = gen.generateComposeDefaults(*defaults.toTypedArray())
            val f = testResourceFile(fileName)
            f.parentFile.mkdirs()
            f.writeText(result.toString())
        }
    }

    @ParameterizedTest(name = "generateComposeDefaults internal:{2} defaults:{1}")
    @ArgumentsSource(ComposeDefaultsArgsProvider::class)
    fun generateComposeDefaults(fileName: String, defaults: List<ComposeDefaults>, internalVisibility: Boolean) {
        val gen = ComposeDefaultsKtGenerator(kClassName = "com.sample.app.K", internalVisibility = internalVisibility)
        val result = gen.generateComposeDefaults(*defaults.toTypedArray())

        assertThat(result.toString()).isEqualTo(testResourceFile(fileName).readText())
    }

    @Test
    fun `generateCommonComposeDefaults WHEN modulePrefix defined`() {
        val gen = ComposeDefaultsKtGenerator(kClassName = "com.sample.app.K", modulePrefix = "ModulePrefix")
        val result = gen.generateComposeDefaults(*ComposeDefaults.values()).minify()

        assertThat(result.trim().split("ModulePrefixResources")).hasSize(25)
    }
}

private class ComposeDefaultsArgsProvider : ArgumentsProvider {
    override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
        return Stream.of(*Items)
    }

    companion object {
        val Items = arrayOf(
            Arguments.of("composeCommonPublic.kt", listOf(ComposeDefaults.Common), false),
            Arguments.of("composeGettersPublic.kt", listOf(ComposeDefaults.Getters), false),
            Arguments.of("composeAllPublic.kt", listOf(ComposeDefaults.Common, ComposeDefaults.Getters), false),
            Arguments.of("composeCommonInternal.kt", listOf(ComposeDefaults.Common), true),
            Arguments.of("composeGettersInternal.kt", listOf(ComposeDefaults.Getters), true),
            Arguments.of("composeAllInternal.kt", listOf(ComposeDefaults.Common, ComposeDefaults.Getters), true),
        )
    }
}
