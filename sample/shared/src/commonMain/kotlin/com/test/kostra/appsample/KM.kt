@file:Suppress("ClassName")

package com.test.kostra.appsample

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.jibru.kostra.compose.painter
import com.jibru.kostra.compose.string
import com.sample.app.IK
import com.sample.app.K
import com.sample.lib1.IKLib1
import com.sample.lib1.KLib1
import com.sample.lib2.IKLib2
import com.sample.lib2.KLib2
import com.sample.lib2.Lib2Resources
import com.sample.lib2.PainterResourceKey as Lib2PainterResourceKey
import com.sample.lib2.StringResourceKey as Lib2StringResourceKey

object KM {
    object string :
        IKLib1.string by KLib1.string,
        IKLib2.string by KLib2.string,
        IK.string by K.string

    object images :
        IKLib1.root by KLib1.root,
        IKLib2.root by KLib2.root,
        IK.images by K.images
}

//Lib2 doesn't know anything about compose, so let's create simple getter
//to create relation Lib2StringResourceKey -> Lib2Resources
@Composable
fun Lib2StringResourceKey.get(): String = Lib2Resources.string(this)

@Composable
fun Lib2PainterResourceKey.get(): Painter = Lib2Resources.painter(this)

//more getters here with more usage like in ComposeResourceProvider.kt
