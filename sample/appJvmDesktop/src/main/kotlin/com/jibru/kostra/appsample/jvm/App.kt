package com.jibru.kostra.appsample.jvm

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.sample.app.K
import com.sample.app.assetPath
import com.sample.app.painterResource
import com.test.kostra.appsample.SampleScreen
import java.awt.Toolkit

fun main() {
    App.start()
}

object App {
    fun start() = application {
        val resolution = Toolkit.getDefaultToolkit().screenSize
        Window(
            onCloseRequest = ::exitApplication,
            title = "Kostra JVM Compose Sample App",
            state = rememberWindowState(size = DpSize((resolution.width * 0.5).dp, (resolution.height * 0.75).dp)),
        ) {
            MaterialTheme(colors = darkColors()) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SampleScreen {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(assetPath(K.flagssvg.country_flag))
                        val height = 64.dp
                        Image(
                            painter = painterResource(K.flagssvg.country_flag),
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.size(height * 4 / 3f, height)
                        )
                    }
                }
            }
        }
    }
}
