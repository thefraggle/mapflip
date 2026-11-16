package de.goork.mapflip.baselineprofile

import android.content.Intent
import android.net.Uri
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Generates Baseline Profiles and Startup Profiles for MapFlip against the R8-minified release build.
 * Covers both MainActivity cold start / UI journeys and the RedirectActivity map-link hotpath.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class BaselineProfileGenerator {

    @get:Rule
    val rule = BaselineProfileRule()

    private val packageName: String
        get() = InstrumentationRegistry.getArguments().getString("targetAppId") ?: "de.goork.mapflip"

    @Test
    fun generateMainActivityAndUiProfile() {
        rule.collect(
            packageName = packageName,
            includeInStartupProfile = true
        ) {
            pressHome()
            startActivityAndWait()

            device.wait(Until.hasObject(By.text("MapFlip")), 5_000)

            // Scroll main screen to compile Compose layout & card rendering paths
            val scrollable = device.findObject(By.scrollable(true))
            scrollable?.scroll(Direction.DOWN, 0.8f)
            device.waitForIdle()
            scrollable?.scroll(Direction.UP, 0.8f)
            device.waitForIdle()

            // Exercise Link Tester & Settings bottom sheet paths if visible
            device.findObject(By.desc("Settings & About"))?.let { settingsBtn ->
                settingsBtn.click()
                device.waitForIdle()
                device.pressBack()
                device.waitForIdle()
            }
        }
    }

    @Test
    fun generateRedirectHotpathProfile() {
        rule.collect(
            packageName = packageName,
            includeInStartupProfile = true
        ) {
            pressHome()
            val redirectIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://maps.apple.com/?ll=52.5200,13.4050&q=Berlin")
            ).apply {
                setPackage(packageName)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivityAndWait(redirectIntent)
            device.waitForIdle()
        }
    }
}
