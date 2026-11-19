package de.goork.mapflip.review

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import de.goork.mapflip.data.PreferencesRepository
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReviewCoordinatorTest {

    private class InMemorySharedPreferences(initial: Map<String, Any?> = emptyMap()) : SharedPreferences {
        private val data = initial.toMutableMap()

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

    private class FakeReviewPromptPort : ReviewPromptPort {
        var launchCount = 0
        var storeListingOpenedCount = 0

        override fun launchReviewFlow(activity: Activity, onComplete: () -> Unit) {
            launchCount++
            onComplete()
        }

        override fun openStoreListing(context: Context) {
            storeListingOpenedCount++
        }
    }

    private val oneDay = 24 * 60 * 60 * 1000L
    private val now = 100 * oneDay

    private fun createCoordinator(
        flips: Int,
        installTime: Long,
        lastPrompt: Long,
        currentNow: Long = now,
        events: MutableList<Pair<String, Map<String, Any>>> = mutableListOf()
    ): Triple<ReviewCoordinator, PreferencesRepository, FakeReviewPromptPort> {
        val prefs = InMemorySharedPreferences(
            mapOf(
                PreferencesRepository.PREFS_KEY_SUCCESSFUL_FLIP_COUNT to flips,
                PreferencesRepository.PREFS_KEY_FIRST_INSTALL_TS to installTime,
                PreferencesRepository.PREFS_KEY_LAST_REVIEW_PROMPT_TS to lastPrompt
            )
        )
        val repo = PreferencesRepository.createForTesting(prefs, clock = { currentNow })
        val port = FakeReviewPromptPort()
        val coordinator = ReviewCoordinator(
            repository = repo,
            promptPort = port,
            clock = { currentNow },
            trackEvent = { name, props -> events.add(name to props) }
        )
        return Triple(coordinator, repo, port)
    }

    @Test
    fun `not eligible when less than minimum flips`() {
        val (coordinator, repo, port) = createCoordinator(
            flips = ReviewCoordinator.MIN_FLIPS_FOR_REVIEW - 1,
            installTime = now - (ReviewCoordinator.MIN_DAYS_AFTER_INSTALL_MS + 1000L),
            lastPrompt = 0L
        )
        assertFalse(coordinator.maybeRequestReview(mockk(relaxed = true)))
        assertEquals(0, port.launchCount)
        assertEquals(0L, repo.lastReviewPromptTimestamp)
    }

    @Test
    fun `not eligible when not installed long enough`() {
        val (coordinator, _, port) = createCoordinator(
            flips = ReviewCoordinator.MIN_FLIPS_FOR_REVIEW,
            installTime = now - (ReviewCoordinator.MIN_DAYS_AFTER_INSTALL_MS - 1000L),
            lastPrompt = 0L
        )
        assertFalse(coordinator.maybeRequestReview(mockk(relaxed = true)))
        assertEquals(0, port.launchCount)
    }

    @Test
    fun `not eligible when cooldown not passed`() {
        val lastPrompt = now - (ReviewCoordinator.PROMPT_COOLDOWN_MS - 1000L)
        val (coordinator, repo, port) = createCoordinator(
            flips = ReviewCoordinator.MIN_FLIPS_FOR_REVIEW,
            installTime = now - (10 * oneDay),
            lastPrompt = lastPrompt
        )
        assertFalse(coordinator.maybeRequestReview(mockk(relaxed = true)))
        assertEquals(0, port.launchCount)
        assertEquals(lastPrompt, repo.lastReviewPromptTimestamp)
    }

    @Test
    fun `eligible first time launches flow persists timestamp and tracks event`() {
        val events = mutableListOf<Pair<String, Map<String, Any>>>()
        val (coordinator, repo, port) = createCoordinator(
            flips = ReviewCoordinator.MIN_FLIPS_FOR_REVIEW,
            installTime = now - (ReviewCoordinator.MIN_DAYS_AFTER_INSTALL_MS + 1000L),
            lastPrompt = 0L,
            events = events
        )
        assertTrue(coordinator.maybeRequestReview(mockk(relaxed = true), trigger = "app_resume"))
        assertEquals(1, port.launchCount)
        assertEquals(now, repo.lastReviewPromptTimestamp)
        assertEquals(listOf("review_prompt_triggered" to mapOf("trigger" to "app_resume")), events)
    }

    @Test
    fun `eligible after cooldown launches flow and updates timestamp`() {
        val (coordinator, repo, port) = createCoordinator(
            flips = ReviewCoordinator.MIN_FLIPS_FOR_REVIEW + 5,
            installTime = now - (30 * oneDay),
            lastPrompt = now - (ReviewCoordinator.PROMPT_COOLDOWN_MS + 1000L)
        )
        assertTrue(coordinator.maybeRequestReview(mockk(relaxed = true)))
        assertEquals(1, port.launchCount)
        assertEquals(now, repo.lastReviewPromptTimestamp)
    }

    @Test
    fun `not eligible when clock skew before install time`() {
        val (coordinator, _, port) = createCoordinator(
            flips = ReviewCoordinator.MIN_FLIPS_FOR_REVIEW,
            installTime = now + (10 * oneDay),
            lastPrompt = 0L
        )
        assertFalse(coordinator.maybeRequestReview(mockk(relaxed = true)))
        assertEquals(0, port.launchCount)
    }

    @Test
    fun `not eligible when clock skew before last prompt`() {
        val (coordinator, _, port) = createCoordinator(
            flips = ReviewCoordinator.MIN_FLIPS_FOR_REVIEW,
            installTime = now - (30 * oneDay),
            lastPrompt = now + (5 * oneDay)
        )
        assertFalse(coordinator.maybeRequestReview(mockk(relaxed = true)))
        assertEquals(0, port.launchCount)
    }

    @Test
    fun `openStoreListing delegates to prompt port`() {
        val (coordinator, _, port) = createCoordinator(0, now, 0L)
        coordinator.openStoreListing(mockk(relaxed = true))
        assertEquals(1, port.storeListingOpenedCount)
    }
}
