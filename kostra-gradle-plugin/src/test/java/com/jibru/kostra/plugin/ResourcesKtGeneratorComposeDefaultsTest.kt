package com.jibru.kostra.plugin

import com.google.common.truth.Truth.assertThat
import com.jibru.kostra.plugin.ext.minify
import org.junit.jupiter.api.Test

class ResourcesKtGeneratorComposeDefaultsTest {

    @Test
    fun generateComposeDefaults() {
        val gen = ResourcesKtGenerator("com.sample.app.K", emptyList())
        val result = gen.generateComposeDefaults().minify()

        assertThat(result.trim()).isEqualTo(
            """
            @file:Suppress("NOTHING_TO_INLINE")
            package com.sample.app
            import androidx.compose.runtime.Composable
            import androidx.compose.ui.graphics.painter.Painter
            import com.jibru.kostra.AssetResourceKey
            import com.jibru.kostra.DrawableResourceKey
            import com.jibru.kostra.PluralResourceKey
            import com.jibru.kostra.StringResourceKey
            import com.jibru.kostra.compose.assetPath
            import com.jibru.kostra.compose.painter
            import com.jibru.kostra.compose.plural
            import com.jibru.kostra.compose.string
            import kotlin.Any
            import kotlin.Float
            import kotlin.Int
            import kotlin.String
            import kotlin.Suppress
            @Composable
            inline fun stringResource(key: StringResourceKey): String = Resources.string(key)
            @Composable
            inline fun stringResource(key: StringResourceKey, vararg formatArgs: Any): String =
                Resources.string(key, *formatArgs)
            @Composable
            inline fun pluralStringResource(key: PluralResourceKey, quantity: Float): String =
                Resources.plural(key, quantity)
            @Composable
            inline fun pluralStringResource(key: PluralResourceKey, quantity: Int): String =
                Resources.plural(key, quantity)
            @Composable
            inline fun pluralStringResource(
              key: PluralResourceKey,
              quantity: Float,
              vararg formatArgs: Any,
            ): String = Resources.plural(key, quantity, *formatArgs)
            @Composable
            inline fun pluralStringResource(
              key: PluralResourceKey,
              quantity: Int,
              vararg formatArgs: Any,
            ): String = Resources.plural(key, quantity, *formatArgs)
            @Composable
            inline fun painterResource(key: DrawableResourceKey): Painter = Resources.painter(key)
            @Composable
            inline fun assetPath(key: AssetResourceKey): String = Resources.assetPath(key)
            """.trimIndent(),
        )
    }
}
