package de.goork.mapflip.data

import android.content.SharedPreferences
import de.goork.mapflip.AppConstants
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

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
        override fun putInt(key: String, value: Int): SharedPreferences.Editor {
            temp[key] = value
            removed.remove(key)
            return this
        }
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

    @Test
    fun testIncrementSuccessfulFlips() {
        val prefs = FakeSharedPreferences()
        val repo = PreferencesRepository.createForTesting(prefs)

        org.junit.Assert.assertEquals(0, repo.successfulFlipCount)
        val afterFirst = repo.incrementSuccessfulFlips()
        org.junit.Assert.assertEquals(1, afterFirst)
        org.junit.Assert.assertEquals(1, repo.successfulFlipCount)

        val afterSecond = repo.incrementSuccessfulFlips()
        org.junit.Assert.assertEquals(2, afterSecond)
        org.junit.Assert.assertEquals(2, repo.successfulFlipCount)
    }

    @Test
    fun testFirstInstallTimestampInitializesAndPersists() {
        val prefs = FakeSharedPreferences()
        val repo = PreferencesRepository.createForTesting(prefs)

        val initialTs = repo.firstInstallTimestamp
        org.junit.Assert.assertTrue(initialTs > 0L)

        // Reading again should return the same timestamp
        val secondRead = repo.firstInstallTimestamp
        org.junit.Assert.assertEquals(initialTs, secondRead)
    }

    @Test
    fun testLastReviewPromptTimestamp() {
        val prefs = FakeSharedPreferences()
        val repo = PreferencesRepository.createForTesting(prefs)

        org.junit.Assert.assertEquals(0L, repo.lastReviewPromptTimestamp)
        val testTs = 1700000000000L
        repo.lastReviewPromptTimestamp = testTs
        org.junit.Assert.assertEquals(testTs, repo.lastReviewPromptTimestamp)
    }

    @Test
    fun testPauseForDurationAndUnpauseUpdatesStateFlow() {
        val prefs = FakeSharedPreferences()
        val repo = PreferencesRepository.createForTesting(prefs)

        assertFalse(repo.isCurrentlyPaused())
        assertFalse(repo.preferences.value.isPaused)

        repo.pauseForDuration(3_600_000L)
        org.junit.Assert.assertTrue(repo.isCurrentlyPaused())
        org.junit.Assert.assertTrue(repo.preferences.value.isPaused)
        org.junit.Assert.assertTrue(repo.preferences.value.pausedUntilTimestamp > System.currentTimeMillis())

        repo.unpause()
        assertFalse(repo.isCurrentlyPaused())
        assertFalse(repo.preferences.value.isPaused)
        org.junit.Assert.assertEquals(0L, repo.preferences.value.pausedUntilTimestamp)
    }

    @Test
    fun testLanguageThemeAndTargetAppUpdatesStateFlow() {
        val prefs = FakeSharedPreferences()
        val repo = PreferencesRepository.createForTesting(prefs)

        repo.setLanguage("de")
        org.junit.Assert.assertEquals("de", repo.getLanguage())
        org.junit.Assert.assertEquals("de", repo.preferences.value.language)

        repo.setTheme("dark")
        org.junit.Assert.assertEquals("dark", repo.getTheme())
        org.junit.Assert.assertEquals("dark", repo.preferences.value.theme)

        repo.setTargetApp(de.goork.mapflip.navigation.TargetNavigationApp.ORGANIC_MAPS)
        org.junit.Assert.assertEquals(de.goork.mapflip.navigation.TargetNavigationApp.ORGANIC_MAPS, repo.getTargetApp())
        org.junit.Assert.assertEquals(de.goork.mapflip.navigation.TargetNavigationApp.ORGANIC_MAPS, repo.preferences.value.targetApp)
    }

    private fun createCalendar(
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int,
        second: Int = 0,
        timeZone: TimeZone = TimeZone.getTimeZone("UTC")
    ): Calendar {
        return Calendar.getInstance(timeZone).apply {
            set(year, month - 1, day, hour, minute, second)
            set(Calendar.MILLISECOND, 0)
        }
    }

    @Test
    fun `pauseUntilTomorrowMorning daytime pauses until 6 AM on following day and expires at 6 AM`() {
        val tz = TimeZone.getTimeZone("Europe/Berlin")
        val inputCal = createCalendar(2026, 9, 4, 14, 30, timeZone = tz)
        var now = inputCal.timeInMillis
        val repo = PreferencesRepository.createForTesting(
            prefs = FakeSharedPreferences(),
            clock = { now },
            timeZoneProvider = { tz }
        )

        repo.pauseUntilTomorrowMorning()
        assertTrue(repo.isCurrentlyPaused())
        assertTrue(repo.preferences.value.isPaused)

        val resultMillis = repo.preferences.value.pausedUntilTimestamp
        val resultCal = Calendar.getInstance(tz).apply { timeInMillis = resultMillis }
        assertEquals(2026, resultCal.get(Calendar.YEAR))
        assertEquals(Calendar.SEPTEMBER, resultCal.get(Calendar.MONTH))
        assertEquals(5, resultCal.get(Calendar.DAY_OF_MONTH))
        assertEquals(6, resultCal.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, resultCal.get(Calendar.MINUTE))
        assertEquals(0, resultCal.get(Calendar.SECOND))
        assertEquals(0, resultCal.get(Calendar.MILLISECOND))
        assertTrue("Result must be in the future", resultMillis > inputCal.timeInMillis)

        // Advances clock to 6:00 AM -> auto-expires
        now = resultMillis
        repo.refreshPauseState()
        assertFalse(repo.isCurrentlyPaused())
        assertFalse(repo.preferences.value.isPaused)
        assertEquals(0L, repo.preferences.value.pausedUntilTimestamp)
    }

    @Test
    fun `pauseUntilTomorrowMorning late evening before midnight pauses until 6 AM next day`() {
        val tz = TimeZone.getTimeZone("UTC")
        val inputCal = createCalendar(2026, 9, 4, 23, 59, 59, timeZone = tz)
        val repo = PreferencesRepository.createForTesting(FakeSharedPreferences(), { inputCal.timeInMillis }, { tz })

        repo.pauseUntilTomorrowMorning()
        val resultCal = Calendar.getInstance(tz).apply { timeInMillis = repo.preferences.value.pausedUntilTimestamp }
        assertEquals(2026, resultCal.get(Calendar.YEAR))
        assertEquals(Calendar.SEPTEMBER, resultCal.get(Calendar.MONTH))
        assertEquals(5, resultCal.get(Calendar.DAY_OF_MONTH))
        assertEquals(6, resultCal.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, resultCal.get(Calendar.MINUTE))
        assertEquals(0, resultCal.get(Calendar.SECOND))
    }

    @Test
    fun `pauseUntilTomorrowMorning after midnight before 6 AM pauses until 6 AM of same day`() {
        val tz = TimeZone.getTimeZone("Europe/Berlin")
        val inputCal = createCalendar(2026, 9, 5, 2, 15, 0, timeZone = tz)
        val repo = PreferencesRepository.createForTesting(FakeSharedPreferences(), { inputCal.timeInMillis }, { tz })

        repo.pauseUntilTomorrowMorning()
        val resultMillis = repo.preferences.value.pausedUntilTimestamp
        val resultCal = Calendar.getInstance(tz).apply { timeInMillis = resultMillis }
        assertEquals(2026, resultCal.get(Calendar.YEAR))
        assertEquals(Calendar.SEPTEMBER, resultCal.get(Calendar.MONTH))
        assertEquals(5, resultCal.get(Calendar.DAY_OF_MONTH))
        assertEquals(6, resultCal.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, resultCal.get(Calendar.MINUTE))
        assertEquals(0, resultCal.get(Calendar.SECOND))
        assertTrue("Result must be in the future", resultMillis > inputCal.timeInMillis)
    }

    @Test
    fun `pauseUntilTomorrowMorning one second before 6 AM pauses until 6 AM of same day`() {
        val tz = TimeZone.getTimeZone("UTC")
        val inputCal = createCalendar(2026, 9, 5, 5, 59, 59, timeZone = tz)
        val repo = PreferencesRepository.createForTesting(FakeSharedPreferences(), { inputCal.timeInMillis }, { tz })

        repo.pauseUntilTomorrowMorning()
        val resultMillis = repo.preferences.value.pausedUntilTimestamp
        val resultCal = Calendar.getInstance(tz).apply { timeInMillis = resultMillis }
        assertEquals(5, resultCal.get(Calendar.DAY_OF_MONTH))
        assertEquals(6, resultCal.get(Calendar.HOUR_OF_DAY))
        assertEquals(1000L, resultMillis - inputCal.timeInMillis)
    }

    @Test
    fun `pauseUntilTomorrowMorning exactly at 6 AM pauses until 6 AM of following day`() {
        val tz = TimeZone.getTimeZone("UTC")
        val inputCal = createCalendar(2026, 9, 5, 6, 0, 0, timeZone = tz)
        val repo = PreferencesRepository.createForTesting(FakeSharedPreferences(), { inputCal.timeInMillis }, { tz })

        repo.pauseUntilTomorrowMorning()
        val resultMillis = repo.preferences.value.pausedUntilTimestamp
        val resultCal = Calendar.getInstance(tz).apply { timeInMillis = resultMillis }
        assertEquals(6, resultCal.get(Calendar.DAY_OF_MONTH))
        assertEquals(6, resultCal.get(Calendar.HOUR_OF_DAY))
        assertEquals(24 * 3600 * 1000L, resultMillis - inputCal.timeInMillis)
    }

    @Test
    fun `pauseUntilTomorrowMorning crosses month leap year and year end boundaries`() {
        val tz = TimeZone.getTimeZone("UTC")

        val monthCal = createCalendar(2026, 4, 30, 22, 0, timeZone = tz)
        val monthRepo = PreferencesRepository.createForTesting(FakeSharedPreferences(), { monthCal.timeInMillis }, { tz })
        monthRepo.pauseUntilTomorrowMorning()
        val monthResult = Calendar.getInstance(tz).apply { timeInMillis = monthRepo.preferences.value.pausedUntilTimestamp }
        assertEquals(Calendar.MAY, monthResult.get(Calendar.MONTH))
        assertEquals(1, monthResult.get(Calendar.DAY_OF_MONTH))

        val leapCal = createCalendar(2024, 2, 28, 20, 0, timeZone = tz)
        val leapRepo = PreferencesRepository.createForTesting(FakeSharedPreferences(), { leapCal.timeInMillis }, { tz })
        leapRepo.pauseUntilTomorrowMorning()
        val leapResult = Calendar.getInstance(tz).apply { timeInMillis = leapRepo.preferences.value.pausedUntilTimestamp }
        assertEquals(29, leapResult.get(Calendar.DAY_OF_MONTH))
        assertEquals(Calendar.FEBRUARY, leapResult.get(Calendar.MONTH))

        val nonLeapCal = createCalendar(2025, 2, 28, 20, 0, timeZone = tz)
        val nonLeapRepo = PreferencesRepository.createForTesting(FakeSharedPreferences(), { nonLeapCal.timeInMillis }, { tz })
        nonLeapRepo.pauseUntilTomorrowMorning()
        val nonLeapResult = Calendar.getInstance(tz).apply { timeInMillis = nonLeapRepo.preferences.value.pausedUntilTimestamp }
        assertEquals(1, nonLeapResult.get(Calendar.DAY_OF_MONTH))
        assertEquals(Calendar.MARCH, nonLeapResult.get(Calendar.MONTH))

        val yearEndCal = createCalendar(2026, 12, 31, 23, 45, timeZone = tz)
        val yearEndRepo = PreferencesRepository.createForTesting(FakeSharedPreferences(), { yearEndCal.timeInMillis }, { tz })
        yearEndRepo.pauseUntilTomorrowMorning()
        val yearEndResult = Calendar.getInstance(tz).apply { timeInMillis = yearEndRepo.preferences.value.pausedUntilTimestamp }
        assertEquals(2027, yearEndResult.get(Calendar.YEAR))
        assertEquals(Calendar.JANUARY, yearEndResult.get(Calendar.MONTH))
        assertEquals(1, yearEndResult.get(Calendar.DAY_OF_MONTH))
    }

    @Test
    fun `pauseUntilTomorrowMorning works reliably across different world timezones`() {
        val testZones = listOf(
            "Pacific/Honolulu",
            "America/Los_Angeles",
            "America/New_York",
            "Europe/London",
            "Europe/Berlin",
            "Asia/Tokyo",
            "Pacific/Auckland"
        )

        for (zoneId in testZones) {
            val tz = TimeZone.getTimeZone(zoneId)
            val eveningCal = createCalendar(2026, 6, 15, 21, 0, timeZone = tz)
            val eveningRepo = PreferencesRepository.createForTesting(FakeSharedPreferences(), { eveningCal.timeInMillis }, { tz })
            eveningRepo.pauseUntilTomorrowMorning()
            val eveningResultCal = Calendar.getInstance(tz).apply { timeInMillis = eveningRepo.preferences.value.pausedUntilTimestamp }
            assertEquals("Hour must be 6 in $zoneId", 6, eveningResultCal.get(Calendar.HOUR_OF_DAY))
            assertEquals("Minute must be 0 in $zoneId", 0, eveningResultCal.get(Calendar.MINUTE))
            assertEquals("Day must be 16 in $zoneId", 16, eveningResultCal.get(Calendar.DAY_OF_MONTH))

            val nightCal = createCalendar(2026, 6, 15, 3, 0, timeZone = tz)
            val nightRepo = PreferencesRepository.createForTesting(FakeSharedPreferences(), { nightCal.timeInMillis }, { tz })
            nightRepo.pauseUntilTomorrowMorning()
            val nightResultCal = Calendar.getInstance(tz).apply { timeInMillis = nightRepo.preferences.value.pausedUntilTimestamp }
            assertEquals("Hour must be 6 in $zoneId", 6, nightResultCal.get(Calendar.HOUR_OF_DAY))
            assertEquals("Minute must be 0 in $zoneId", 0, nightResultCal.get(Calendar.MINUTE))
            assertEquals("Day must be 15 in $zoneId", 15, nightResultCal.get(Calendar.DAY_OF_MONTH))
        }
    }
}
