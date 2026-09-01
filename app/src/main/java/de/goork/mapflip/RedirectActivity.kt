package de.goork.mapflip

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import de.goork.mapflip.analytics.Analytics
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
        val incomingUri = intent?.data
        val isShareSheet = intent?.action == Intent.ACTION_SEND
        val sharedText = if (isShareSheet) {
            intent?.getStringExtra(Intent.EXTRA_TEXT) ?: intent?.getStringExtra(Intent.EXTRA_SUBJECT)
        } else null

        val mapUrl = when {
            incomingUri != null -> incomingUri.toString()
            !sharedText.isNullOrBlank() -> UniversalMapParser.extractMapUrl(sharedText) ?: sharedText.trim()
            else -> null
        }

        if (mapUrl != null) {
            val sourceService = UniversalMapParser.detectSourceService(mapUrl)
            val dataUri = incomingUri ?: Uri.parse(mapUrl)
            if (isPaused) {
                // When paused: forward original URL to non-MapFlip browser
                Analytics.trackEvent("redirect_paused", mapOf("source_service" to sourceService))
                forwardOriginalUrl(dataUri)
            } else {
                val parsedLocation = UniversalMapParser.parse(mapUrl)
                val targetApp = repository.getTargetApp()
                val targetIntent = NavigationIntentBuilder.buildIntent(parsedLocation, targetApp, this).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }

                val locationType = when (parsedLocation) {
                    is de.goork.mapflip.parser.ParsedLocation.Coordinates -> "coordinates"
                    is de.goork.mapflip.parser.ParsedLocation.SearchQuery -> "search_query"
                    is de.goork.mapflip.parser.ParsedLocation.Navigation -> "navigation"
                    is de.goork.mapflip.parser.ParsedLocation.Directions -> "directions"
                    is de.goork.mapflip.parser.ParsedLocation.WebFallback -> "web_fallback"
                    is de.goork.mapflip.parser.ParsedLocation.Home -> "home"
                }

                Analytics.trackEvent("redirect_performed", mapOf(
                    "target_app" to targetApp.name.lowercase(),
                    "source_service" to sourceService,
                    "location_type" to locationType,
                    "is_share_sheet" to isShareSheet
                ))

                try {
                    startActivity(targetIntent)
                } catch (_: ActivityNotFoundException) {
                    Analytics.trackEvent("redirect_fallback", mapOf(
                        "target_app" to targetApp.name.lowercase(),
                        "source_service" to sourceService,
                        "reason" to "app_not_installed"
                    ))
                    handleTargetNotFoundFallback(targetApp, dataUri, parsedLocation)
                } catch (e: Exception) {
                    Analytics.trackEvent("redirect_fallback", mapOf(
                        "target_app" to targetApp.name.lowercase(),
                        "source_service" to sourceService,
                        "reason" to e.javaClass.simpleName
                    ))
                    forwardOriginalUrl(dataUri)
                }
            }
        } else {
            Analytics.trackEvent("redirect_failed", mapOf(
                "reason" to "no_url_found",
                "is_share_sheet" to isShareSheet
            ))
        }

        finish()
        suppressTransitionAnimation()
    }

    private fun handleTargetNotFoundFallback(
        targetApp: de.goork.mapflip.navigation.TargetNavigationApp,
        dataUri: Uri,
        parsedLocation: de.goork.mapflip.parser.ParsedLocation
    ) {
        when (targetApp) {
            de.goork.mapflip.navigation.TargetNavigationApp.WAZE -> {
                // Try Waze web fallback or browser
                try {
                    val wazeWebUri = Uri.parse("https://waze.com/ul?${dataUri.query ?: ""}")
                    val fallbackIntent = Intent(Intent.ACTION_VIEW, wazeWebUri).apply {
                        addCategory(Intent.CATEGORY_BROWSABLE)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    startActivity(fallbackIntent)
                } catch (_: Exception) {
                    forwardOriginalUrl(dataUri)
                }
            }
            de.goork.mapflip.navigation.TargetNavigationApp.OSMAND -> {
                // Try OsmAnd Plus if standard OsmAnd wasn't found
                try {
                    val plusIntent = Intent(Intent.ACTION_VIEW, Uri.parse(NavigationIntentBuilder.buildOsmAndUriString(parsedLocation))).apply {
                        setPackage("net.osmand.plus")
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    startActivity(plusIntent)
                } catch (_: Exception) {
                    forwardOriginalUrl(dataUri)
                }
            }
            else -> {
                forwardOriginalUrl(dataUri)
            }
        }
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
