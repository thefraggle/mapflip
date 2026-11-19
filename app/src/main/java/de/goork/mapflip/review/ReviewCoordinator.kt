package de.goork.mapflip.review

import android.app.Activity
import android.content.Context
import de.goork.mapflip.analytics.Analytics
import de.goork.mapflip.data.PreferencesRepository

/**
 * Seam for flavor-specific store review prompting (Play Store vs FOSS no-op).
 */
interface ReviewPromptPort {
    fun launchReviewFlow(activity: Activity, onComplete: () -> Unit = {})
    fun openStoreListing(context: Context)
}

/**
 * Coordinates in-app review eligibility, cooldown persistence, and telemetry
 * behind one interface across both Play and FOSS flavors.
 */
class ReviewCoordinator(
    private val repository: PreferencesRepository,
    private val promptPort: ReviewPromptPort = ReviewHelper,
    private val clock: () -> Long = System::currentTimeMillis,
    private val trackEvent: (String, Map<String, Any>) -> Unit = Analytics::trackEvent
) {

    fun maybeRequestReview(
        activity: Activity,
        trigger: String = "auto_criteria_met"
    ): Boolean {
        val now = clock()
        val flips = repository.successfulFlipCount
        val installTime = repository.firstInstallTimestamp
        val lastPrompt = repository.lastReviewPromptTimestamp

        if (!isEligible(now, flips, installTime, lastPrompt)) {
            return false
        }

        repository.lastReviewPromptTimestamp = now
        trackEvent("review_prompt_triggered", mapOf("trigger" to trigger))
        promptPort.launchReviewFlow(activity)
        return true
    }

    fun openStoreListing(context: Context) {
        promptPort.openStoreListing(context)
    }

    private fun isEligible(
        now: Long,
        flips: Int,
        installTime: Long,
        lastPrompt: Long
    ): Boolean {
        val isInstalledLongEnough = (now >= installTime) && ((now - installTime) >= MIN_DAYS_AFTER_INSTALL_MS)
        val isCooldownPassed = lastPrompt == 0L || ((now >= lastPrompt) && ((now - lastPrompt) >= PROMPT_COOLDOWN_MS))
        val hasEnoughFlips = flips >= MIN_FLIPS_FOR_REVIEW
        return hasEnoughFlips && isInstalledLongEnough && isCooldownPassed
    }

    companion object {
        const val MIN_FLIPS_FOR_REVIEW = 5
        const val MIN_DAYS_AFTER_INSTALL_MS = 3 * 24 * 60 * 60 * 1000L // 3 days
        const val PROMPT_COOLDOWN_MS = 21 * 24 * 60 * 60 * 1000L // 21 days
    }
}
