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
     * If called between midnight and 6:00 AM, pauses until 6:00 AM of the current day.
     * If called at or after 6:00 AM, pauses until 6:00 AM of the following day.
     */
    fun getTomorrowMorningTimestamp(
        fromMillis: Long = System.currentTimeMillis(),
        timeZone: java.util.TimeZone = java.util.TimeZone.getDefault()
    ): Long {
        val calendar = Calendar.getInstance(timeZone).apply {
            timeInMillis = fromMillis
            val currentHour = get(Calendar.HOUR_OF_DAY)
            if (currentHour >= 6) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
            set(Calendar.HOUR_OF_DAY, 6)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }
}
