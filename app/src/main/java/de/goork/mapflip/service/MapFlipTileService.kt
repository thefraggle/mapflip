package de.goork.mapflip.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import de.goork.mapflip.AppConstants
import de.goork.mapflip.MainActivity
import de.goork.mapflip.PauseHelper
import de.goork.mapflip.ui.Strings

@RequiresApi(Build.VERSION_CODES.N)
class MapFlipTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        updateTileState()
    }

    override fun onClick() {
        super.onClick()
        val currentlyPaused = PauseHelper.isCurrentlyPaused(this)
        val prefs = getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE)
        if (currentlyPaused) {
            prefs.edit()
                .putBoolean(AppConstants.PREFS_KEY_PAUSED, false)
                .putLong(PauseHelper.PREFS_KEY_PAUSED_UNTIL, 0L)
                .apply()
            updateTileState()
        } else {
            val intent = Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra("show_pause_dialog", true)
            }
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
    }

    private fun updateTileState() {
        val tile = qsTile ?: return
        val currentlyPaused = PauseHelper.isCurrentlyPaused(this)
        val prefs = getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE)
        val langPref = prefs.getString(AppConstants.PREFS_KEY_LANG, "auto") ?: "auto"
        val langCode = Strings.resolveLanguage(langPref)
        val s = Strings.getStrings(langCode)

        if (currentlyPaused) {
            tile.state = Tile.STATE_INACTIVE
            tile.label = s.headline
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val pausedUntil = prefs.getLong(PauseHelper.PREFS_KEY_PAUSED_UNTIL, 0L)
                if (pausedUntil > 0L) {
                    val timeStr = android.text.format.DateFormat.getTimeFormat(this).format(java.util.Date(pausedUntil))
                    tile.subtitle = "${s.statusPaused} ($timeStr)"
                } else {
                    tile.subtitle = s.statusPaused
                }
            }
        } else {
            tile.state = Tile.STATE_ACTIVE
            tile.label = s.headline
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                tile.subtitle = s.statusActive
            }
        }
        tile.updateTile()
    }
}