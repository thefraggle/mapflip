package de.goork.mapflip

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle

/**
 * Transparent activity that silently redirects Apple Maps links to Google Maps.
 *
 * Registered in the manifest for `maps.apple.com` URLs. When triggered,
 * it converts the URL via [AppleMapsParser] and forwards to Google Maps.
 * If MapFlip is paused by the user, it forwards the original URL to a browser.
 * Uses no-animation transitions to remain completely invisible to the user.
 */
class RedirectActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("mapflip", Context.MODE_PRIVATE)
        val isPaused = prefs.getBoolean("is_paused", false)
        val appleUrl = intent?.data?.toString()

        if (appleUrl != null) {
            if (isPaused) {
                // When paused: forward original URL to non-MapFlip apps (e.g. browser)
                forwardOriginalUrl(intent?.data!!)
            } else {
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
        }

        finish()
        suppressTransitionAnimation()
    }

    /**
     * Forwards original Apple Maps URL to standard browser when redirect is paused,
     * avoiding infinite loops with MapFlip itself.
     */
    private fun forwardOriginalUrl(uri: Uri) {
        val browserIntent = Intent(Intent.ACTION_VIEW, uri).apply {
            addCategory(Intent.CATEGORY_BROWSABLE)
        }
        val resolved = packageManager.queryIntentActivities(browserIntent, 0)
            .firstOrNull { it.activityInfo.packageName != packageName }

        if (resolved != null) {
            browserIntent.setClassName(resolved.activityInfo.packageName, resolved.activityInfo.name)
            try {
                startActivity(browserIntent)
            } catch (_: Exception) {}
        } else {
            try {
                startActivity(Intent.createChooser(browserIntent, null))
            } catch (_: Exception) {}
        }
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
