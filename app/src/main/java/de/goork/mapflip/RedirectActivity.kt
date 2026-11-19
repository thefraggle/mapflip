package de.goork.mapflip

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import de.goork.mapflip.navigation.NavigationLauncher

/**
 * Transparent activity that silently intercepts and redirects supported map links,
 * coordinates, and Plus Codes to the user's preferred navigation app.
 *
 * If MapFlip is paused by the user, it forwards the original URL directly to a web browser.
 */
class RedirectActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val incomingUri = intent?.data
        val isShareSheet = intent?.action == Intent.ACTION_SEND
        val sharedText = if (isShareSheet) {
            intent?.getStringExtra(Intent.EXTRA_TEXT) ?: intent?.getStringExtra(Intent.EXTRA_SUBJECT)
        } else null

        NavigationLauncher.create(this).redirect(
            rawInput = sharedText,
            incomingUri = incomingUri,
            isShareSheet = isShareSheet
        )

        finish()
        suppressTransitionAnimation()
    }

    /** Suppress enter/exit animation so the redirect is invisible. */
    private fun suppressTransitionAnimation() {
        if (Build.VERSION.SDK_INT >= 34) {
            overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, 0, 0)
            overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, 0, 0)
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(0, 0)
        }
    }
}
