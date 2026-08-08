package de.goork.mapflip

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import de.goork.mapflip.ui.MainScreen
import de.goork.mapflip.ui.theme.MapFlipTheme

class MainActivity : ComponentActivity() {
    private val showPauseDialogState = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleIntent(intent)
        setContent {
            MapFlipTheme {
                MainScreen(
                    showPauseDialogDefault = showPauseDialogState.value,
                    onPauseDialogDismissed = { showPauseDialogState.value = false }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val show = intent?.getBooleanExtra("show_pause_dialog", false) ?: false
        if (show) {
            showPauseDialogState.value = true
        }
    }
}
