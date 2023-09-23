package com.jibru.kostra.plugin

import com.google.common.truth.Truth.assertThat
import com.jibru.kostra.plugin.ext.minify
import com.jibru.kostra.plugin.task.ComposeDefaults
import org.junit.jupiter.api.Test

class ComposeDefaultsKtGeneratorComposeDefaultsTest {

    @Test
    fun generateCommonComposeDefaults() {
        val gen = ComposeDefaultsKtGenerator(kClassName = "com.sample.app.K")
        val result = gen.generateComposeDefaults(composeDefaults = ComposeDefaults.Common).minify()

        assertThat(result.trim()).isEqualTo(
            """
            @file:Suppress("NOTHING_TO_INLINE", "ktlint")
            package com.sample.app
            import androidx.compose.runtime.Composable
            import androidx.compose.ui.graphics.painter.Painter
            import com.jibru.kostra.AssetResourceKey
            import com.jibru.kostra.PainterResourceKey
            import com.jibru.kostra.PluralResourceKey
            import com.jibru.kostra.StringResourceKey
            import com.jibru.kostra.compose.assetPath
            import com.jibru.kostra.compose.ordinal
            import com.jibru.kostra.compose.painter
            import com.jibru.kostra.compose.plural
            import com.jibru.kostra.compose.string
            import com.jibru.kostra.icu.IFixedDecimal
            import kotlin.Any
            import kotlin.Int
            import kotlin.String
            import kotlin.Suppress
            @Composable
            inline fun stringResource(key: StringResourceKey): String = Resources.string(key)
            @Composable
            inline fun stringResource(key: StringResourceKey, vararg formatArgs: Any): String =
                Resources.string(key, *formatArgs)
            @Composable
            inline fun pluralStringResource(key: PluralResourceKey, quantity: IFixedDecimal): String =
                Resources.plural(key, quantity)
            @Composable
            inline fun pluralStringResource(key: PluralResourceKey, quantity: Int): String =
                Resources.plural(key, quantity)
            @Composable
            inline fun pluralStringResource(
              key: PluralResourceKey,
              quantity: IFixedDecimal,
              vararg formatArgs: Any,
            ): String = Resources.plural(key, quantity, *formatArgs)
            @Composable
            inline fun pluralStringResource(
              key: PluralResourceKey,
              quantity: Int,
              vararg formatArgs: Any,
            ): String = Resources.plural(key, quantity, *formatArgs)
            @Composable
            inline fun ordinalStringResource(key: PluralResourceKey, quantity: IFixedDecimal): String =
                Resources.ordinal(key, quantity)
            @Composable
            inline fun ordinalStringResource(key: PluralResourceKey, quantity: Int): String =
                Resources.ordinal(key, quantity)
            @Composable
            inline fun ordinalStringResource(
              key: PluralResourceKey,
              quantity: IFixedDecimal,
              vararg formatArgs: Any,
            ): String = Resources.ordinal(key, quantity, *formatArgs)
            @Composable
            inline fun ordinalStringResource(
              key: PluralResourceKey,
              quantity: Int,
              vararg formatArgs: Any,
            ): String = Resources.ordinal(key, quantity, *formatArgs)
            @Composable
            inline fun painterResource(key: PainterResourceKey): Painter = Resources.painter(key)
            @Composable
            inline fun assetPath(key: AssetResourceKey): String = Resources.assetPath(key)
            """.trimIndent(),
        )
    }

    @Test
    fun generateSvgComposeDefaults() {
        val gen = ComposeDefaultsKtGenerator(kClassName = "com.sample.app.K")
        val result = gen.generateComposeDefaults(composeDefaults = ComposeDefaults.Svg).minify()

        assertThat(result.trim()).isEqualTo(
            """
            @file:Suppress("NOTHING_TO_INLINE", "ktlint")
            package com.sample.app
            import androidx.compose.runtime.Composable
            import androidx.compose.ui.graphics.painter.Painter
            import com.jibru.kostra.PainterResourceKey
            import com.jibru.kostra.compose.svgPainter
            import kotlin.Suppress
            @Composable
            inline fun svgPainterResource(key: PainterResourceKey): Painter = Resources.svgPainter(key)
            """.trimIndent(),
        )
    }
}
