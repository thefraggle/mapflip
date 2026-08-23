package de.goork.mapflip

import android.content.Context
import de.goork.mapflip.data.PreferencesRepository
import java.util.Calendar

object PauseHelper {
    const val PREFS_KEY_PAUSED_UNTIL = PreferencesRepository.PREFS_KEY_PAUSED_UNTIL

    /**
     * Checks if the app is currently paused, delegating to [PreferencesRepository].
     */
    fun isCurrentlyPaused(context: Context): Boolean {
        return PreferencesRepository.getInstance(context).isCurrentlyPaused()
    }

    /**
     * Helper to compute next morning at 6:00 AM timestamp.
     */
    fun getTomorrowMorningTimestamp(): Long {
        val calendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 6)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }
}
