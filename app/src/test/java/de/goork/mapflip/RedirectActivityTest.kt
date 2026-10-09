package de.goork.mapflip

import android.content.Intent
import android.net.Uri
import de.goork.mapflip.data.PreferencesRepository
import de.goork.mapflip.navigation.TargetNavigationApp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class RedirectActivityTest {

    private lateinit var repository: PreferencesRepository

    @Before
    fun setUp() {
        val context = RuntimeEnvironment.getApplication()
        repository = PreferencesRepository.getInstance(context)
        repository.unpause()
        repository.setTargetApp(TargetNavigationApp.SYSTEM_PICKER)
    }

    @Test
    @Suppress("DEPRECATION")
    fun testRedirectAppleMapsUrlToSystemPicker() {
        val initialFlips = repository.successfulFlipCount
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://maps.apple.com/?ll=52.5200,13.4050&q=Berlin")
        }

        val controller = Robolectric.buildActivity(RedirectActivity::class.java, intent)
        val activity = controller.create().get()

        assertTrue(activity.isFinishing)
        val shadowActivity = shadowOf(activity)
        val startedIntent = shadowActivity.nextStartedActivity
        assertNotNull(startedIntent)
        assertEquals(Intent.ACTION_CHOOSER, startedIntent.action)
        val innerIntent = startedIntent.getParcelableExtra<Intent>(Intent.EXTRA_INTENT)
        assertNotNull(innerIntent)
        assertEquals(Intent.ACTION_VIEW, innerIntent?.action)
        assertTrue(innerIntent?.dataString?.startsWith("geo:") == true)
        assertEquals(initialFlips + 1, repository.successfulFlipCount)
    }

    @Test
    @Suppress("DEPRECATION")
    fun testRedirectGeoCoordinateUrl() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("geo:48.8566,2.3522?q=Paris")
        }

        val controller = Robolectric.buildActivity(RedirectActivity::class.java, intent)
        val activity = controller.create().get()

        assertTrue(activity.isFinishing)
        val shadowActivity = shadowOf(activity)
        val startedIntent = shadowActivity.nextStartedActivity
        assertNotNull(startedIntent)
        assertEquals(Intent.ACTION_CHOOSER, startedIntent.action)
        val innerIntent = startedIntent.getParcelableExtra<Intent>(Intent.EXTRA_INTENT)
        assertNotNull(innerIntent)
        assertEquals(Intent.ACTION_VIEW, innerIntent?.action)
        assertTrue(innerIntent?.dataString?.startsWith("geo:") == true)
    }

    @Test
    fun testShareSheetIntentRedirect() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Schau mal hier: https://maps.apple.com/?q=Brandenburger+Tor")
        }

        val controller = Robolectric.buildActivity(RedirectActivity::class.java, intent)
        val activity = controller.create().get()

        assertTrue(activity.isFinishing)
        val shadowActivity = shadowOf(activity)
        val startedIntent = shadowActivity.nextStartedActivity
        assertNotNull(startedIntent)
        assertEquals(Intent.ACTION_CHOOSER, startedIntent.action)
    }

    @Test
    fun testShareSheetWithEmptyTextDoesNotCrash() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "")
        }

        val controller = Robolectric.buildActivity(RedirectActivity::class.java, intent)
        val activity = controller.create().get()

        assertTrue(activity.isFinishing)
    }

    @Test
    fun testPausedStateForwardsOriginalUrlToBrowser() {
        repository.pauseIndefinitely()

        val url = "https://maps.apple.com/?q=Munich"
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }

        val controller = Robolectric.buildActivity(RedirectActivity::class.java, intent)
        val activity = controller.create().get()

        assertTrue(activity.isFinishing)
        val shadowActivity = shadowOf(activity)
        val startedIntent = shadowActivity.nextStartedActivity
        assertNotNull(startedIntent)
        assertEquals(Intent.ACTION_VIEW, startedIntent.action)
        assertEquals(url, startedIntent.dataString)
    }

    @Test
    fun testUnsupportedUrlForwardsOriginalUrl() {
        val url = "https://duckduckgo.com/?q=something+not+a+map"
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }

        val controller = Robolectric.buildActivity(RedirectActivity::class.java, intent)
        val activity = controller.create().get()

        assertTrue(activity.isFinishing)
        val shadowActivity = shadowOf(activity)
        val startedIntent = shadowActivity.nextStartedActivity
        assertNotNull(startedIntent)
        assertEquals(url, startedIntent.dataString)
    }

    @Test
    fun testFallbackWhenTargetAppNotInstalled() {
        // Organic Maps is not installed in the clean Robolectric test environment
        repository.setTargetApp(TargetNavigationApp.ORGANIC_MAPS)

        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://maps.apple.com/?q=Hamburg")
        }

        val controller = Robolectric.buildActivity(RedirectActivity::class.java, intent)
        val activity = controller.create().get()

        assertTrue(activity.isFinishing)
        val shadowActivity = shadowOf(activity)
        val startedIntent = shadowActivity.nextStartedActivity
        assertNotNull(startedIntent)
        // Since neither Organic Maps nor Google Maps are installed, it falls back to SYSTEM_PICKER chooser
        assertEquals(Intent.ACTION_CHOOSER, startedIntent.action)
    }
}
