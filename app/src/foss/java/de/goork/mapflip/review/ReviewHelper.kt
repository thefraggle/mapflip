package de.goork.mapflip.review

import android.app.Activity
import android.content.Context
import de.goork.mapflip.data.PreferencesRepository

/**
 * FOSS Flavor ReviewHelper stub: 100% No-Op.
 * Zero Google Play dependencies, no store prompts.
 */
object ReviewHelper {

    fun maybeRequestReview(
        activity: Activity,
        repository: PreferencesRepository,
        trigger: String = "auto_criteria_met"
    ) {
        // No-op for FOSS
    }

    fun launchReviewFlow(
        activity: Activity,
        trigger: String = "auto_criteria_met",
        onComplete: (() -> Unit)? = null
    ) {
        onComplete?.invoke()
    }

    fun openPlayStoreListing(context: Context) {
        // No-op for FOSS
    }
}
