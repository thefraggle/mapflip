package de.goork.mapflip

import android.content.Context
import java.util.Calendar

object PauseHelper {
    const val PREFS_KEY_PAUSED_UNTIL = "paused_until"

    /**
     * Checks if the app is currently paused, handling timed pause expiration.
     */
    fun isCurrentlyPaused(context: Context): Boolean {
        val prefs = context.getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE)
        val isPaused = prefs.getBoolean(AppConstants.PREFS_KEY_PAUSED, false)
        if (!isPaused) return false

        val pausedUntil = prefs.getLong(PREFS_KEY_PAUSED_UNTIL, 0L)
        if (pausedUntil > 0L) {
            val now = System.currentTimeMillis()
            if (now >= pausedUntil) {
                // Pause expired, auto-resume
                prefs.edit()
                    .putBoolean(AppConstants.PREFS_KEY_PAUSED, false)
                    .putLong(PREFS_KEY_PAUSED_UNTIL, 0L)
                    .apply()
                return false
            }
        }
        return true
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
