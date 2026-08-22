package de.goork.mapflip

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import de.goork.mapflip.ui.MainScreen
import de.goork.mapflip.ui.theme.MapFlipTheme

import android.content.Context
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.DisposableEffect

class MainActivity : ComponentActivity() {
    private val showPauseDialogState = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleIntent(intent)
        setContent {
            val prefs = remember { getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE) }
            var themePref by remember {
                mutableStateOf(prefs.getString(AppConstants.PREFS_KEY_THEME, "system") ?: "system")
            }

            DisposableEffect(Unit) {
                val listener = android.content.SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                    if (key == AppConstants.PREFS_KEY_THEME) {
                        themePref = prefs.getString(AppConstants.PREFS_KEY_THEME, "system") ?: "system"
                    }
                }
                prefs.registerOnSharedPreferenceChangeListener(listener)
                onDispose {
                    prefs.unregisterOnSharedPreferenceChangeListener(listener)
                }
            }

            MapFlipTheme(themePref = themePref) {
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
