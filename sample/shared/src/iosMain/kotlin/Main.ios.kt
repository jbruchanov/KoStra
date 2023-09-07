import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import com.jibru.kostra.compose.LocalQualifiers
import com.jibru.kostra.compose.assetPath
import com.sample.app.K
import com.sample.app.Resources
import com.test.kostra.appsample.SampleScreen
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    return ComposeUIViewController {
        MaterialTheme(colors = darkColors()) {
            Surface(modifier = Modifier.fillMaxSize()) {
                Column {
                    //crashing
                    Text(Resources.assetPath(K.drawable.capital_city))
                    //this one is OK
                    Text(Resources.assetPath(K.drawable.capital_city, LocalQualifiers.current))
                    SampleScreen()
                }
            }
        }
    }
}


