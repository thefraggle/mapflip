package de.goork.mapflip.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import de.goork.mapflip.AppConstants
import de.goork.mapflip.MainActivity
import de.goork.mapflip.PauseHelper
import de.goork.mapflip.analytics.Analytics
import de.goork.mapflip.data.PreferencesRepository
import de.goork.mapflip.ui.Strings
import de.goork.mapflip.util.DomainVerificationHelper

@RequiresApi(Build.VERSION_CODES.N)
class MapFlipTileService : TileService() {

    private val repository by lazy { PreferencesRepository.getInstance(applicationContext) }

    override fun onStartListening() {
        super.onStartListening()
        updateTileState()
    }

    override fun onClick() {
        super.onClick()
        val linksEnabled = DomainVerificationHelper.checkLinksEnabled(this)

        if (linksEnabled == false) {
            Analytics.trackEvent("tile_clicked", mapOf("action" to "open_settings_setup"))
            val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Intent(
                    Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS,
                    Uri.parse("package:$packageName")
                ).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            } else {
                Intent(this, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }
            launchIntent(intent)
            return
        }

        val currentlyPaused = repository.isCurrentlyPaused()
        if (currentlyPaused) {
            Analytics.trackEvent("tile_clicked", mapOf("action" to "unpause"))
            repository.unpause()
            updateTileState()
        } else {
            Analytics.trackEvent("tile_clicked", mapOf("action" to "open_pause_dialog"))
            val intent = Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra("show_pause_dialog", true)
            }
            launchIntent(intent)
        }
    }

    private fun launchIntent(intent: Intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            startActivityAndCollapse(pendingIntent)
        } else {
            @Suppress("DEPRECATION")
            startActivityAndCollapse(intent)
        }
    }

    private fun updateTileState() {
        val tile = qsTile ?: return
        val currentlyPaused = repository.isCurrentlyPaused()
        val langPref = repository.getLanguage()
        val langCode = Strings.resolveLanguage(langPref)
        val s = Strings.getStrings(langCode)
        val linksEnabled = DomainVerificationHelper.checkLinksEnabled(this)

        tile.label = s.headline

        if (linksEnabled == false) {
            tile.state = Tile.STATE_INACTIVE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                tile.subtitle = s.tileSetupRequired
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                tile.stateDescription = s.tileSetupRequired
            }
        } else if (currentlyPaused) {
            tile.state = Tile.STATE_INACTIVE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val pausedUntil = repository.preferences.value.pausedUntilTimestamp
                if (pausedUntil > 0L) {
                    val timeStr = android.text.format.DateFormat.getTimeFormat(this).format(java.util.Date(pausedUntil))
                    tile.subtitle = "${s.statusPaused} ($timeStr)"
                } else {
                    tile.subtitle = s.statusPaused
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                tile.stateDescription = s.statusPaused
            }
        } else {
            tile.state = Tile.STATE_ACTIVE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                tile.subtitle = s.statusActive
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                tile.stateDescription = s.statusActive
            }
        }
        tile.updateTile()
    }
}