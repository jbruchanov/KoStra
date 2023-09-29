package com.jibru.kostra.android.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.darkColors
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import com.jibru.kostra.compose.KostraCompose
import com.test.kostra.appsample.SampleScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        KostraCompose()

        setContent {
            MaterialTheme(colors = darkColors()) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CompositionLocalProvider(LocalRippleTheme provides MaterialRippleTheme) {
                        SampleScreen()
                    }
                }
            }
        }
    }
}

@Immutable
private object MaterialRippleTheme : RippleTheme {

    @Composable
    override fun defaultColor() = RippleTheme.defaultRippleColor(
        contentColor = LocalContentColor.current,
        lightTheme = MaterialTheme.colors.isLight,
    )

    @Composable
    fun rippleAlphaOrig() = RippleTheme.defaultRippleAlpha(
        contentColor = LocalContentColor.current,
        lightTheme = MaterialTheme.colors.isLight,
    )

    @Composable
    fun rippleAlphaCustom() = RippleAlpha(
        pressedAlpha = 0.10f + 0.45f,
        focusedAlpha = 0.12f + 0.45f,
        draggedAlpha = 0.08f + 0.45f,
        hoveredAlpha = 0.04f + 0.45f,
    )

    @Composable
    override fun rippleAlpha() = rippleAlphaCustom()
}
