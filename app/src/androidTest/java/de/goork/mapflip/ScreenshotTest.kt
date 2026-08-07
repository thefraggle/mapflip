package de.goork.mapflip

import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import tools.fastlane.screengrab.Screengrab
import tools.fastlane.screengrab.locale.LocaleTestRule
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class ScreenshotTest {

    companion object {
        @ClassRule
        @JvmField
        val localeTestRule = LocaleTestRule()
    }

    @Test
    fun testTakeScreenshots() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // Configure SharedPreferences to show active status and unpaused state
        val prefs = context.getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE)
        
        // Sync displayed language with the system locale chosen by screengrab
        val currentLocale = Locale.getDefault()
        val langCode = currentLocale.language

        prefs.edit()
            .putBoolean("mock_links_active", true)
            .putBoolean(AppConstants.PREFS_KEY_PAUSED, false)
            .putLong(PauseHelper.PREFS_KEY_PAUSED_UNTIL, 0L)
            .putString(AppConstants.PREFS_KEY_LANG, langCode)
            .apply()

        // Launch MainActivity
        val scenario = ActivityScenario.launch(MainActivity::class.java)

        // Wait for Compose UI to fully render and settle
        Thread.sleep(2000)

        // Capture screenshot via Fastlane Screengrab
        Screengrab.screenshot("main_screen")

        scenario.close()
    }
}
