package de.goork.mapflip.review

import android.app.Activity
import android.content.Context

/**
 * FOSS flavor no-op adapter for [ReviewPromptPort].
 * Zero Google Play dependencies, no store prompts.
 */
object ReviewHelper : ReviewPromptPort {

    override fun launchReviewFlow(
        activity: Activity,
        onComplete: () -> Unit
    ) {
        onComplete()
    }

    override fun openStoreListing(context: Context) {
        // No-op for FOSS
    }
}
