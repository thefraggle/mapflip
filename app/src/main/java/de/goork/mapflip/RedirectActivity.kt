package de.goork.mapflip

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle

/**
 * Transparent activity that silently redirects Apple Maps links to Google Maps.
 *
 * Registered in the manifest for `maps.apple.com` URLs. When triggered,
 * it converts the URL via [AppleMapsParser] and forwards to Google Maps.
 * Uses no-animation transitions to remain completely invisible to the user.
 */
class RedirectActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appleUrl = intent?.data?.toString()
        if (appleUrl != null) {
            val googleUri = AppleMapsParser.convert(appleUrl)
            try {
                // Try Google Maps app first
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(googleUri)).apply {
                    setPackage(GOOGLE_MAPS_PACKAGE)
                })
            } catch (_: ActivityNotFoundException) {
                // Fallback: open in any available maps app or browser
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(googleUri)))
            }
        }

        finish()
        suppressTransitionAnimation()
    }

    /** Suppress enter/exit animation so the redirect is invisible. */
    private fun suppressTransitionAnimation() {
        if (Build.VERSION.SDK_INT >= 34) {
            overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, 0, 0)
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(0, 0)
        }
    }

    companion object {
        private const val GOOGLE_MAPS_PACKAGE = "com.google.android.apps.maps"
    }
}
