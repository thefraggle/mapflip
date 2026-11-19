package de.goork.mapflip.navigation

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import de.goork.mapflip.data.PreferencesRepository
import de.goork.mapflip.parser.ParsedLocation
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class NavigationLauncherTest {

    private class InMemorySharedPreferences : SharedPreferences {
        private val data = mutableMapOf<String, Any?>()

        private inner class Editor : SharedPreferences.Editor {
            private val pending = mutableMapOf<String, Any?>()
            private val removed = mutableSetOf<String>()

            override fun putString(key: String, value: String?) = apply { pending[key] = value; removed.remove(key) }
            override fun putStringSet(key: String, values: Set<String>?) = this
            override fun putInt(key: String, value: Int) = apply { pending[key] = value; removed.remove(key) }
            override fun putLong(key: String, value: Long) = apply { pending[key] = value; removed.remove(key) }
            override fun putFloat(key: String, value: Float) = this
            override fun putBoolean(key: String, value: Boolean) = apply { pending[key] = value; removed.remove(key) }
            override fun remove(key: String) = apply { removed.add(key); pending.remove(key) }
            override fun clear() = apply { pending.clear(); data.clear() }
            override fun commit() = true.also { apply() }
            override fun apply() {
                removed.forEach { data.remove(it) }
                data.putAll(pending)
            }
        }

        override fun getAll(): MutableMap<String, *> = data
        override fun getString(key: String, defValue: String?): String? = (data[key] as? String) ?: defValue
        override fun getStringSet(key: String, defValues: MutableSet<String>?): MutableSet<String>? = null
        override fun getInt(key: String, defValue: Int): Int = (data[key] as? Int) ?: defValue
        override fun getLong(key: String, defValue: Long): Long = (data[key] as? Long) ?: defValue
        override fun getFloat(key: String, defValue: Float): Float = (data[key] as? Float) ?: defValue
        override fun getBoolean(key: String, defValue: Boolean): Boolean = (data[key] as? Boolean) ?: defValue
        override fun contains(key: String): Boolean = data.containsKey(key)
        override fun edit(): SharedPreferences.Editor = Editor()
        override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {}
        override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {}
    }

    private class FakeSystemLauncherPort(
        private val installedApps: Set<TargetNavigationApp> = setOf(TargetNavigationApp.GOOGLE_MAPS),
        private val hasExternalGeoHandlers: Boolean = false,
        private val defaultBrowserPackage: String? = "org.mozilla.firefox",
        private val throwOnFirstStart: Throwable? = null
    ) : SystemLauncherPort {
        val startedIntents = mutableListOf<Intent>()
        val toasts = mutableListOf<Pair<String, Boolean>>()
        private var startAttempts = 0

        override fun isAppInstalled(app: TargetNavigationApp): Boolean = app in installedApps

        override fun buildTargetIntent(location: ParsedLocation, targetApp: TargetNavigationApp): Intent {
            return NavigationIntentBuilder.buildIntent(location, targetApp, context = null)
        }

        override fun buildChooserIntent(location: ParsedLocation): Intent {
            return NavigationIntentBuilder.buildGenericGeoIntent(location, createChooser = true)
        }

        override fun startActivity(intent: Intent) {
            startAttempts++
            if (startAttempts == 1 && throwOnFirstStart != null) {
                throw throwOnFirstStart
            }
            startedIntents.add(intent)
        }

        override fun hasExternalGeoHandlers(geoUriString: String): Boolean = hasExternalGeoHandlers
        override fun resolveDefaultBrowserPackage(): String? = defaultBrowserPackage
        override fun showToast(message: String, longDuration: Boolean) {
            toasts.add(message to longDuration)
        }
    }

    @Test
    fun `redirect launches installed target app and increments flip count`() {
        val repo = PreferencesRepository.createForTesting(InMemorySharedPreferences()).apply {
            setTargetApp(TargetNavigationApp.ORGANIC_MAPS)
        }
        val system = FakeSystemLauncherPort(installedApps = setOf(TargetNavigationApp.ORGANIC_MAPS))
        val events = mutableListOf<Pair<String, Map<String, Any>>>()
        val launcher = NavigationLauncher(repo, system) { name, props -> events.add(name to props) }

        val result = launcher.redirect(
            rawInput = null,
            incomingUri = Uri.parse("https://maps.apple.com/?ll=52.52,13.405"),
            isShareSheet = false
        )

        assertTrue(result.launched)
        assertEquals(TargetNavigationApp.ORGANIC_MAPS, result.effectiveTargetApp)
        assertEquals(1, repo.successfulFlipCount)
        assertEquals(1, system.startedIntents.size)
        assertEquals(1, system.toasts.size)
        assertTrue(events.any { it.first == "redirect_performed" && it.second["target_app"] == "organic_maps" })
    }

    @Test
    fun `redirect falls back to Google Maps when configured app is not installed`() {
        val repo = PreferencesRepository.createForTesting(InMemorySharedPreferences()).apply {
            setTargetApp(TargetNavigationApp.WAZE)
        }
        val system = FakeSystemLauncherPort(installedApps = setOf(TargetNavigationApp.GOOGLE_MAPS))
        val events = mutableListOf<Pair<String, Map<String, Any>>>()
        val launcher = NavigationLauncher(repo, system) { name, props -> events.add(name to props) }

        val result = launcher.redirect(
            rawInput = null,
            incomingUri = Uri.parse("https://maps.apple.com/?ll=52.52,13.405"),
            isShareSheet = false
        )

        assertTrue(result.launched)
        assertEquals(TargetNavigationApp.GOOGLE_MAPS, result.effectiveTargetApp)
        assertTrue(events.any { it.first == "redirect_fallback" && it.second["fallback_app"] == "google_maps" })
        assertEquals(2, system.toasts.size) // fallback toast + redirect toast
    }

    @Test
    fun `redirect falls back to Waze web URL when Waze throws ActivityNotFoundException without geo handlers`() {
        val repo = PreferencesRepository.createForTesting(InMemorySharedPreferences()).apply {
            setTargetApp(TargetNavigationApp.WAZE)
        }
        val system = FakeSystemLauncherPort(
            installedApps = setOf(TargetNavigationApp.WAZE),
            hasExternalGeoHandlers = false,
            throwOnFirstStart = ActivityNotFoundException()
        )
        val launcher = NavigationLauncher(repo, system) { _, _ -> }

        val result = launcher.redirect(
            rawInput = null,
            incomingUri = Uri.parse("https://maps.apple.com/?ll=52.52,13.405"),
            isShareSheet = false
        )

        assertTrue(result.launched)
        assertEquals(1, system.startedIntents.size)
        val launched = system.startedIntents.single()
        assertTrue(launched.dataString?.startsWith("https://www.waze.com/ul") == true)
        assertEquals("org.mozilla.firefox", launched.`package`)
    }

    @Test
    fun `redirect to Mapy Cz pins package even for mapy com and Google short WebFallback URLs`() {
        val repo = PreferencesRepository.createForTesting(InMemorySharedPreferences()).apply {
            setTargetApp(TargetNavigationApp.MAPY_CZ)
        }
        val system = FakeSystemLauncherPort(installedApps = setOf(TargetNavigationApp.MAPY_CZ))
        val launcher = NavigationLauncher(repo, system) { _, _ -> }

        val mapyComShort = launcher.redirect(
            rawInput = null,
            incomingUri = Uri.parse("https://mapy.com/s/abc123"),
            isShareSheet = false
        )
        assertTrue(mapyComShort.launched)
        assertEquals(TargetNavigationApp.MAPY_CZ.packageName, system.startedIntents.last().`package`)

        val gooGlShort = launcher.redirect(
            rawInput = null,
            incomingUri = Uri.parse("https://maps.app.goo.gl/xyz"),
            isShareSheet = false
        )
        assertTrue(gooGlShort.launched)
        assertEquals(TargetNavigationApp.MAPY_CZ.packageName, system.startedIntents.last().`package`)
    }

    @Test
    fun `redirect forwards original URL to default browser when paused without incrementing flips`() {
        val repo = PreferencesRepository.createForTesting(InMemorySharedPreferences()).apply {
            pauseIndefinitely()
        }
        val system = FakeSystemLauncherPort(defaultBrowserPackage = "org.mozilla.firefox")
        val events = mutableListOf<Pair<String, Map<String, Any>>>()
        val launcher = NavigationLauncher(repo, system) { name, props -> events.add(name to props) }

        val result = launcher.redirect(
            rawInput = null,
            incomingUri = Uri.parse("https://maps.apple.com/?ll=52.52,13.405"),
            isShareSheet = false
        )

        assertTrue(result.launched)
        assertEquals(0, repo.successfulFlipCount)
        assertEquals("org.mozilla.firefox", system.startedIntents.single().`package`)
        assertEquals(listOf("redirect_paused"), events.map { it.first })
    }

    @Test
    fun `redirect rejects unsupported shared text and tracks failure`() {
        val repo = PreferencesRepository.createForTesting(InMemorySharedPreferences())
        val system = FakeSystemLauncherPort()
        val events = mutableListOf<Pair<String, Map<String, Any>>>()
        val launcher = NavigationLauncher(repo, system) { name, props -> events.add(name to props) }

        val result = launcher.redirect(
            rawInput = "Just a normal text message",
            incomingUri = null,
            isShareSheet = true
        )

        assertFalse(result.launched)
        assertEquals(0, system.startedIntents.size)
        assertEquals("unsupported_shared_text", events.single().second["reason"])
    }

    @Test
    fun `launchFromUi shows error toast when launch fails`() {
        val repo = PreferencesRepository.createForTesting(InMemorySharedPreferences())
        val system = FakeSystemLauncherPort(
            installedApps = setOf(TargetNavigationApp.GOOGLE_MAPS),
            hasExternalGeoHandlers = false,
            defaultBrowserPackage = null,
            throwOnFirstStart = RuntimeException("boom")
        )
        val launcher = NavigationLauncher(repo, system) { _, _ -> }

        val result = launcher.launchFromUi(
            url = "geo:52.52,13.405",
            configuredApp = TargetNavigationApp.GOOGLE_MAPS,
            eventName = "link_tester_used"
        )

        assertFalse(result.launched)
        assertEquals(1, system.toasts.size)
        assertFalse(system.toasts.single().second)
    }
}
