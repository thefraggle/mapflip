package de.goork.mapflip.data

import android.content.SharedPreferences
import de.goork.mapflip.AppConstants
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Test

class PreferencesRepositoryTest {

    private class FakeEditor(private val data: MutableMap<String, Any?>) : SharedPreferences.Editor {
        private val temp = mutableMapOf<String, Any?>()
        private val removed = mutableSetOf<String>()

        override fun putString(key: String, value: String?): SharedPreferences.Editor {
            temp[key] = value
            removed.remove(key)
            return this
        }
        override fun putStringSet(key: String, values: Set<String>?): SharedPreferences.Editor = this
        override fun putInt(key: String, value: Int): SharedPreferences.Editor = this
        override fun putLong(key: String, value: Long): SharedPreferences.Editor {
            temp[key] = value
            removed.remove(key)
            return this
        }
        override fun putFloat(key: String, value: Float): SharedPreferences.Editor = this
        override fun putBoolean(key: String, value: Boolean): SharedPreferences.Editor {
            temp[key] = value
            removed.remove(key)
            return this
        }
        override fun remove(key: String): SharedPreferences.Editor {
            removed.add(key)
            temp.remove(key)
            return this
        }
        override fun clear(): SharedPreferences.Editor {
            temp.clear()
            data.clear()
            return this
        }
        override fun commit(): Boolean {
            apply()
            return true
        }
        override fun apply() {
            for (k in removed) data.remove(k)
            data.putAll(temp)
        }
    }

    private class FakeSharedPreferences(initialData: Map<String, Any?> = emptyMap()) : SharedPreferences {
        val data = initialData.toMutableMap()

        override fun getAll(): MutableMap<String, *> = data
        override fun getString(key: String, defValue: String?): String? = (data[key] as? String) ?: defValue
        override fun getStringSet(key: String, defValues: MutableSet<String>?): MutableSet<String>? = null
        override fun getInt(key: String, defValue: Int): Int = (data[key] as? Int) ?: defValue
        override fun getLong(key: String, defValue: Long): Long = (data[key] as? Long) ?: defValue
        override fun getFloat(key: String, defValue: Float): Float = (data[key] as? Float) ?: defValue
        override fun getBoolean(key: String, defValue: Boolean): Boolean = (data[key] as? Boolean) ?: defValue
        override fun contains(key: String): Boolean = data.containsKey(key)
        override fun edit(): SharedPreferences.Editor = FakeEditor(data)
        override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {}
        override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {}
    }

    @Test
    fun testInitializationWithExpiredPauseDoesNotCrash() {
        val prefs = FakeSharedPreferences(
            mapOf(
                AppConstants.PREFS_KEY_PAUSED to true,
                PreferencesRepository.PREFS_KEY_PAUSED_UNTIL to (System.currentTimeMillis() - 60000L) // expired 1 min ago
            )
        )
        val repo = PreferencesRepository.createForTesting(prefs)
        assertNotNull(repo)
        assertFalse(repo.isCurrentlyPaused())
    }

    @Test
    fun testActiveTimedPauseReturnsTrue() {
        val futureTimestamp = System.currentTimeMillis() + 3600000L // 1 hour in future
        val prefs = FakeSharedPreferences(
            mapOf(
                AppConstants.PREFS_KEY_PAUSED to true,
                PreferencesRepository.PREFS_KEY_PAUSED_UNTIL to futureTimestamp
            )
        )
        val repo = PreferencesRepository.createForTesting(prefs)
        assertNotNull(repo)
        org.junit.Assert.assertTrue(repo.isCurrentlyPaused())
    }

    @Test
    fun testIndefinitePauseReturnsTrue() {
        val prefs = FakeSharedPreferences(
            mapOf(
                AppConstants.PREFS_KEY_PAUSED to true,
                PreferencesRepository.PREFS_KEY_PAUSED_UNTIL to 0L
            )
        )
        val repo = PreferencesRepository.createForTesting(prefs)
        assertNotNull(repo)
        org.junit.Assert.assertTrue(repo.isCurrentlyPaused())
    }
}
