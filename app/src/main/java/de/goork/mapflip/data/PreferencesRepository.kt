package de.goork.mapflip.data

import android.content.Context
import android.content.SharedPreferences
import de.goork.mapflip.AppConstants
import de.goork.mapflip.navigation.TargetNavigationApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar

data class UserPreferences(
    val language: String = "auto",
    val theme: String = "system",
    val targetApp: TargetNavigationApp = TargetNavigationApp.GOOGLE_MAPS,
    val isPaused: Boolean = false,
    val pausedUntilTimestamp: Long = 0L
)

/**
 * Single source of truth for persistent user preferences and application state.
 * Manages reactive state flow and provides thread-safe access.
 */
class PreferencesRepository internal constructor(private val prefs: SharedPreferences) {

    private constructor(context: Context) : this(
        context.applicationContext.getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE)
    )

    private val _preferences = MutableStateFlow(readCurrentPreferences())
    val preferences: StateFlow<UserPreferences> = _preferences.asStateFlow()

    private val prefChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
        _preferences?.value = readCurrentPreferences()
    }

    init {
        prefs.registerOnSharedPreferenceChangeListener(prefChangeListener)
    }

    fun getLanguage(): String = prefs.getString(AppConstants.PREFS_KEY_LANG, "auto") ?: "auto"

    fun setLanguage(language: String) {
        prefs.edit().putString(AppConstants.PREFS_KEY_LANG, language).apply()
        _preferences?.value = readCurrentPreferences()
    }

    fun getTheme(): String = prefs.getString(AppConstants.PREFS_KEY_THEME, "system") ?: "system"

    fun setTheme(theme: String) {
        prefs.edit().putString(AppConstants.PREFS_KEY_THEME, theme).apply()
        _preferences?.value = readCurrentPreferences()
    }

    fun getTargetApp(): TargetNavigationApp {
        val id = prefs.getString(AppConstants.PREFS_KEY_TARGET_APP, TargetNavigationApp.GOOGLE_MAPS.id)
        return TargetNavigationApp.fromId(id)
    }

    fun setTargetApp(targetApp: TargetNavigationApp) {
        prefs.edit().putString(AppConstants.PREFS_KEY_TARGET_APP, targetApp.id).apply()
        _preferences?.value = readCurrentPreferences()
    }

    fun isCurrentlyPaused(): Boolean {
        val isPaused = prefs.getBoolean(AppConstants.PREFS_KEY_PAUSED, false)
        if (!isPaused) return false

        val pausedUntil = prefs.getLong(PREFS_KEY_PAUSED_UNTIL, 0L)
        if (pausedUntil > 0L) {
            val now = System.currentTimeMillis()
            if (now >= pausedUntil) {
                // Pause expired, auto-resume in storage without triggering recursive state mutation
                prefs.edit()
                    .putBoolean(AppConstants.PREFS_KEY_PAUSED, false)
                    .putLong(PREFS_KEY_PAUSED_UNTIL, 0L)
                    .apply()
                return false
            }
        }
        return true
    }

    fun pauseIndefinitely() {
        setPauseState(paused = true, untilTimestamp = 0L)
    }

    fun pauseForDuration(durationMs: Long) {
        val untilTimestamp = System.currentTimeMillis() + durationMs
        setPauseState(paused = true, untilTimestamp = untilTimestamp)
    }

    fun pauseUntilTomorrowMorning() {
        val calendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 6)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        setPauseState(paused = true, untilTimestamp = calendar.timeInMillis)
    }

    fun unpause() {
        setPauseState(paused = false, untilTimestamp = 0L)
    }

    private fun setPauseState(paused: Boolean, untilTimestamp: Long) {
        prefs.edit()
            .putBoolean(AppConstants.PREFS_KEY_PAUSED, paused)
            .putLong(PREFS_KEY_PAUSED_UNTIL, untilTimestamp)
            .apply()
        _preferences?.value = readCurrentPreferences()
    }

    private fun readCurrentPreferences(): UserPreferences {
        val paused = isCurrentlyPaused()
        val pausedUntil = if (paused) prefs.getLong(PREFS_KEY_PAUSED_UNTIL, 0L) else 0L
        return UserPreferences(
            language = getLanguage(),
            theme = getTheme(),
            targetApp = getTargetApp(),
            isPaused = paused,
            pausedUntilTimestamp = pausedUntil
        )
    }

    companion object {
        const val PREFS_KEY_PAUSED_UNTIL = "paused_until"

        @Volatile
        private var INSTANCE: PreferencesRepository? = null

        fun getInstance(context: Context): PreferencesRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PreferencesRepository(context).also { INSTANCE = it }
            }
        }

        internal fun createForTesting(prefs: SharedPreferences): PreferencesRepository {
            return PreferencesRepository(prefs)
        }
    }
}
