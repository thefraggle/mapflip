package de.goork.mapflip

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import de.goork.mapflip.data.PreferencesRepository
import de.goork.mapflip.navigation.NavigationIntentBuilder
import de.goork.mapflip.parser.UniversalMapParser

/**
 * Transparent activity that silently intercepts and redirects map links (Apple, Bing, OSM, Yandex)
 * to the user's preferred navigation app (Google Maps, Waze, Organic Maps, OsmAnd, or System Picker).
 *
 * If MapFlip is paused by the user, it forwards the original URL directly to a web browser.
 */
class RedirectActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = PreferencesRepository.getInstance(this)
        val isPaused = repository.isCurrentlyPaused()
        val dataUri = intent?.data

        if (dataUri != null) {
            val mapUrl = dataUri.toString()
            if (isPaused) {
                // When paused: forward original URL to non-MapFlip browser
                forwardOriginalUrl(dataUri)
            } else {
                val parsedLocation = UniversalMapParser.parse(mapUrl)
                val targetApp = repository.getTargetApp()
                val targetIntent = NavigationIntentBuilder.buildIntent(parsedLocation, targetApp)

                try {
                    startActivity(targetIntent)
                } catch (_: ActivityNotFoundException) {
                    // Fallback 1: Try generic geo intent (system picker)
                    try {
                        val genericIntent = NavigationIntentBuilder.buildGenericGeoIntent(parsedLocation)
                        startActivity(genericIntent)
                    } catch (_: Exception) {
                        // Fallback 2: Forward to web browser
                        forwardOriginalUrl(dataUri)
                    }
                } catch (_: Exception) {
                    forwardOriginalUrl(dataUri)
                }
            }
        }

        finish()
        suppressTransitionAnimation()
    }

    /**
     * Forwards original map URL to standard web browser when redirect is paused or no native map app is found.
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
