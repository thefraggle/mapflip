package de.goork.mapflip.service

import android.content.Context
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import de.goork.mapflip.AppConstants
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
        } else {
            prefs.edit()
                .putBoolean(AppConstants.PREFS_KEY_PAUSED, true)
                .putLong(PauseHelper.PREFS_KEY_PAUSED_UNTIL, 0L)
                .apply()
        }
        updateTileState()
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
                tile.subtitle = s.statusPaused
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