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

        val prefs = getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE)
        val isPaused = prefs.getBoolean(AppConstants.PREFS_KEY_PAUSED, false)
        val dataUri = intent?.data

        if (dataUri != null) {
            val appleUrl = dataUri.toString()
            if (isPaused) {
                // When paused: forward original URL to non-MapFlip apps (e.g. browser)
                forwardOriginalUrl(dataUri)
            } else {
                val googleUri = AppleMapsParser.convert(appleUrl)
                try {
                    // Try Google Maps app first
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(googleUri)).apply {
                        setPackage(AppConstants.GOOGLE_MAPS_PACKAGE)
                    })
                } catch (_: ActivityNotFoundException) {
                    // Fallback: open in any available maps app or browser
                    try {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(googleUri)))
                    } catch (_: Exception) {}
                } catch (_: Exception) {}
            }
        }

        finish()
        suppressTransitionAnimation()
    }

    /**
     * Forwards original Apple Maps URL to standard web browser when redirect is paused.
     * Uses generic HTTPS query to discover browser package name with CATEGORY_APP_BROWSER fallback.
     */
    private fun forwardOriginalUrl(uri: Uri) {
        val genericWebIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com")).apply {
            addCategory(Intent.CATEGORY_BROWSABLE)
        }

        val browserPackage = try {
            packageManager.queryIntentActivities(genericWebIntent, 0)
                .map { it.activityInfo.packageName }
                .firstOrNull { it != packageName }
        } catch (_: Exception) {
            null
        }

        if (browserPackage != null) {
            val targetIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                addCategory(Intent.CATEGORY_BROWSABLE)
                setPackage(browserPackage)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                startActivity(targetIntent)
                return
            } catch (_: Exception) {}
        }

        // Fallback: browser selector intent
        try {
            val selectorIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_APP_BROWSER)
            }
            val browserIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                selector = selectorIntent
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(browserIntent)
        } catch (_: Exception) {}
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
}
