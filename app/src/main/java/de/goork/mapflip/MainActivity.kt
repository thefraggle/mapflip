package de.goork.mapflip

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.goork.mapflip.analytics.Analytics
import de.goork.mapflip.data.PreferencesRepository
import de.goork.mapflip.ui.MainScreen
import de.goork.mapflip.ui.theme.MapFlipTheme

class MainActivity : ComponentActivity() {
    private val showPauseDialogState = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleIntent(intent)
        Analytics.trackEvent("app_open")

        val repository = PreferencesRepository.getInstance(applicationContext)

        setContent {
            val userPreferences by repository.preferences.collectAsStateWithLifecycle()

            MapFlipTheme(themePref = userPreferences.theme) {
                MainScreen(
                    repository = repository,
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
