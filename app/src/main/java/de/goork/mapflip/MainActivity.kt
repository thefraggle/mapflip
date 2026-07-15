package de.goork.mapflip

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import de.goork.mapflip.ui.MainScreen
import de.goork.mapflip.ui.theme.MapFlipTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MapFlipTheme {
                MainScreen()
            }
        }
    }
}
