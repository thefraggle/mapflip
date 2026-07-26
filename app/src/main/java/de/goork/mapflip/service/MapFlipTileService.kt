package de.goork.mapflip.service

import android.content.Context
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import de.goork.mapflip.ui.Strings

@RequiresApi(Build.VERSION_CODES.N)
class MapFlipTileService : TileService() {

    private val prefsName = "mapflip"
    private val prefsKeyPaused = "is_paused"
    private val prefsKeyLang = "lang"

    override fun onStartListening() {
        super.onStartListening()
        updateTileState()
    }

    override fun onClick() {
        super.onClick()
        val prefs = getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        val isPaused = prefs.getBoolean(prefsKeyPaused, false)
        val newPaused = !isPaused
        prefs.edit().putBoolean(prefsKeyPaused, newPaused).apply()
        updateTileState()
    }

    private fun updateTileState() {
        val tile = qsTile ?: return
        val prefs = getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        val isPaused = prefs.getBoolean(prefsKeyPaused, false)
        val langPref = prefs.getString(prefsKeyLang, "auto") ?: "auto"
        val langCode = Strings.resolveLanguage(langPref)
        val s = Strings.getStrings(langCode)

        if (isPaused) {
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