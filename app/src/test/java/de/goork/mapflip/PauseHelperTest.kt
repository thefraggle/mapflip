package de.goork.mapflip

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

class PauseHelperTest {

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
    fun `daytime pauses until 6 AM on following day`() {
        val tz = TimeZone.getTimeZone("Europe/Berlin")
        val inputCal = createCalendar(2026, 9, 4, 14, 30, timeZone = tz)
        val resultMillis = PauseHelper.getTomorrowMorningTimestamp(inputCal.timeInMillis, tz)

        val resultCal = Calendar.getInstance(tz).apply { timeInMillis = resultMillis }
        assertEquals(2026, resultCal.get(Calendar.YEAR))
        assertEquals(Calendar.SEPTEMBER, resultCal.get(Calendar.MONTH))
        assertEquals(5, resultCal.get(Calendar.DAY_OF_MONTH))
        assertEquals(6, resultCal.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, resultCal.get(Calendar.MINUTE))
        assertEquals(0, resultCal.get(Calendar.SECOND))
        assertEquals(0, resultCal.get(Calendar.MILLISECOND))
        assertTrue("Result must be in the future", resultMillis > inputCal.timeInMillis)
    }

    @Test
    fun `late evening before midnight pauses until 6 AM next day`() {
        val tz = TimeZone.getTimeZone("UTC")
        val inputCal = createCalendar(2026, 9, 4, 23, 59, 59, timeZone = tz)
        val resultMillis = PauseHelper.getTomorrowMorningTimestamp(inputCal.timeInMillis, tz)

        val resultCal = Calendar.getInstance(tz).apply { timeInMillis = resultMillis }
        assertEquals(2026, resultCal.get(Calendar.YEAR))
        assertEquals(Calendar.SEPTEMBER, resultCal.get(Calendar.MONTH))
        assertEquals(5, resultCal.get(Calendar.DAY_OF_MONTH))
        assertEquals(6, resultCal.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, resultCal.get(Calendar.MINUTE))
        assertEquals(0, resultCal.get(Calendar.SECOND))
    }

    @Test
    fun `after midnight before 6 AM pauses until 6 AM of same day`() {
        val tz = TimeZone.getTimeZone("Europe/Berlin")
        val inputCal = createCalendar(2026, 9, 5, 2, 15, 0, timeZone = tz)
        val resultMillis = PauseHelper.getTomorrowMorningTimestamp(inputCal.timeInMillis, tz)

        val resultCal = Calendar.getInstance(tz).apply { timeInMillis = resultMillis }
        assertEquals(2026, resultCal.get(Calendar.YEAR))
        assertEquals(Calendar.SEPTEMBER, resultCal.get(Calendar.MONTH))
        assertEquals(5, resultCal.get(Calendar.DAY_OF_MONTH)) // Same day!
        assertEquals(6, resultCal.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, resultCal.get(Calendar.MINUTE))
        assertEquals(0, resultCal.get(Calendar.SECOND))
        assertTrue("Result must be in the future", resultMillis > inputCal.timeInMillis)
    }

    @Test
    fun `one second before 6 AM pauses until 6 AM of same day`() {
        val tz = TimeZone.getTimeZone("UTC")
        val inputCal = createCalendar(2026, 9, 5, 5, 59, 59, timeZone = tz)
        val resultMillis = PauseHelper.getTomorrowMorningTimestamp(inputCal.timeInMillis, tz)

        val resultCal = Calendar.getInstance(tz).apply { timeInMillis = resultMillis }
        assertEquals(5, resultCal.get(Calendar.DAY_OF_MONTH))
        assertEquals(6, resultCal.get(Calendar.HOUR_OF_DAY))
        assertEquals(1000L, resultMillis - inputCal.timeInMillis)
    }

    @Test
    fun `exactly at 6 AM pauses until 6 AM of following day`() {
        val tz = TimeZone.getTimeZone("UTC")
        val inputCal = createCalendar(2026, 9, 5, 6, 0, 0, timeZone = tz)
        val resultMillis = PauseHelper.getTomorrowMorningTimestamp(inputCal.timeInMillis, tz)

        val resultCal = Calendar.getInstance(tz).apply { timeInMillis = resultMillis }
        assertEquals(6, resultCal.get(Calendar.DAY_OF_MONTH)) // Next day
        assertEquals(6, resultCal.get(Calendar.HOUR_OF_DAY))
        assertEquals(24 * 3600 * 1000L, resultMillis - inputCal.timeInMillis)
    }

    @Test
    fun `crosses month boundary correctly`() {
        val tz = TimeZone.getTimeZone("UTC")
        val inputCal = createCalendar(2026, 4, 30, 22, 0, timeZone = tz)
        val resultMillis = PauseHelper.getTomorrowMorningTimestamp(inputCal.timeInMillis, tz)

        val resultCal = Calendar.getInstance(tz).apply { timeInMillis = resultMillis }
        assertEquals(Calendar.MAY, resultCal.get(Calendar.MONTH))
        assertEquals(1, resultCal.get(Calendar.DAY_OF_MONTH))
        assertEquals(6, resultCal.get(Calendar.HOUR_OF_DAY))
    }

    @Test
    fun `crosses leap year boundary correctly`() {
        val tz = TimeZone.getTimeZone("UTC")
        val leapCal = createCalendar(2024, 2, 28, 20, 0, timeZone = tz)
        val leapResult = PauseHelper.getTomorrowMorningTimestamp(leapCal.timeInMillis, tz)
        val leapResultCal = Calendar.getInstance(tz).apply { timeInMillis = leapResult }
        assertEquals(29, leapResultCal.get(Calendar.DAY_OF_MONTH))
        assertEquals(Calendar.FEBRUARY, leapResultCal.get(Calendar.MONTH))

        val nonLeapCal = createCalendar(2025, 2, 28, 20, 0, timeZone = tz)
        val nonLeapResult = PauseHelper.getTomorrowMorningTimestamp(nonLeapCal.timeInMillis, tz)
        val nonLeapResultCal = Calendar.getInstance(tz).apply { timeInMillis = nonLeapResult }
        assertEquals(1, nonLeapResultCal.get(Calendar.DAY_OF_MONTH))
        assertEquals(Calendar.MARCH, nonLeapResultCal.get(Calendar.MONTH))
    }

    @Test
    fun `crosses year end boundary correctly`() {
        val tz = TimeZone.getTimeZone("UTC")
        val inputCal = createCalendar(2026, 12, 31, 23, 45, timeZone = tz)
        val resultMillis = PauseHelper.getTomorrowMorningTimestamp(inputCal.timeInMillis, tz)

        val resultCal = Calendar.getInstance(tz).apply { timeInMillis = resultMillis }
        assertEquals(2027, resultCal.get(Calendar.YEAR))
        assertEquals(Calendar.JANUARY, resultCal.get(Calendar.MONTH))
        assertEquals(1, resultCal.get(Calendar.DAY_OF_MONTH))
        assertEquals(6, resultCal.get(Calendar.HOUR_OF_DAY))
    }

    @Test
    fun `works reliably across different world timezones`() {
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
            val eveningResult = PauseHelper.getTomorrowMorningTimestamp(eveningCal.timeInMillis, tz)
            val eveningResultCal = Calendar.getInstance(tz).apply { timeInMillis = eveningResult }
            assertEquals("Hour must be 6 in $zoneId", 6, eveningResultCal.get(Calendar.HOUR_OF_DAY))
            assertEquals("Minute must be 0 in $zoneId", 0, eveningResultCal.get(Calendar.MINUTE))
            assertEquals("Day must be 16 in $zoneId", 16, eveningResultCal.get(Calendar.DAY_OF_MONTH))

            val nightCal = createCalendar(2026, 6, 15, 3, 0, timeZone = tz)
            val nightResult = PauseHelper.getTomorrowMorningTimestamp(nightCal.timeInMillis, tz)
            val nightResultCal = Calendar.getInstance(tz).apply { timeInMillis = nightResult }
            assertEquals("Hour must be 6 in $zoneId", 6, nightResultCal.get(Calendar.HOUR_OF_DAY))
            assertEquals("Minute must be 0 in $zoneId", 0, nightResultCal.get(Calendar.MINUTE))
            assertEquals("Day must be 15 in $zoneId", 15, nightResultCal.get(Calendar.DAY_OF_MONTH))
        }
    }
}
