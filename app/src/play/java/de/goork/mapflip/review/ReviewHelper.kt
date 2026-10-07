package de.goork.mapflip.review

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.google.android.play.core.review.ReviewManagerFactory
import de.goork.mapflip.analytics.Analytics
import de.goork.mapflip.data.PreferencesRepository

/**
 * Play Store Flavor ReviewHelper: Coordinates Google Play In-App Reviews politely and unobtrusively.
 */
object ReviewHelper {

    /**
     * Checks activity and timing criteria before requesting Google Play In-App Review.
     */
    fun maybeRequestReview(
        activity: Activity,
        repository: PreferencesRepository,
        trigger: String = "auto_criteria_met"
    ) {
        val now = System.currentTimeMillis()
        val flips = repository.successfulFlipCount
        val installTime = repository.firstInstallTimestamp
        val lastPrompt = repository.lastReviewPromptTimestamp

        if (ReviewEligibility.isEligibleForReview(now, flips, installTime, lastPrompt)) {
            repository.lastReviewPromptTimestamp = now
            launchReviewFlow(activity, trigger = trigger)
        }
    }

    /**
     * Directly triggers the In-App Review Flow.
     */
    fun launchReviewFlow(
        activity: Activity,
        trigger: String = "auto_criteria_met",
        onComplete: (() -> Unit)? = null
    ) {
        try {
            Analytics.trackEvent("review_prompt_triggered", mapOf("trigger" to trigger))
            val manager = ReviewManagerFactory.create(activity)
            val request = manager.requestReviewFlow()
            request.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val reviewInfo = task.result
                    val flow = manager.launchReviewFlow(activity, reviewInfo)
                    flow.addOnCompleteListener {
                        onComplete?.invoke()
                    }
                } else {
                    onComplete?.invoke()
                }
            }
        } catch (_: Throwable) {
            onComplete?.invoke()
        }
    }

    /**
     * Opens Play Store app listing as a direct fallback or manual feedback option.
     */
    fun openPlayStoreListing(context: Context) {
        val packageName = context.packageName
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            try {
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(webIntent)
            } catch (_: Exception) {}
        }
    }
}
