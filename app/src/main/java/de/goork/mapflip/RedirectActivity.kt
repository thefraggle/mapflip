package de.goork.mapflip

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import de.goork.mapflip.analytics.Analytics
import de.goork.mapflip.data.PreferencesRepository
import de.goork.mapflip.navigation.NavigationLauncher
import de.goork.mapflip.parser.ParsedLocation
import de.goork.mapflip.parser.UniversalMapParser

/**
 * Transparent activity that silently intercepts and redirects supported map links,
 * coordinates, and Plus Codes to the user's preferred navigation app.
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
            if (!UniversalMapParser.canParse(mapUrl)) {
                if (incomingUri != null) {
                    // Not a supported map URL (e.g. regular web search from intercepted search engine host)
                    NavigationLauncher.forwardOriginalUrl(this, dataUri)
                } else {
                    Analytics.trackEvent("redirect_failed", mapOf(
                        "reason" to "unsupported_shared_text",
                        "is_share_sheet" to isShareSheet
                    ))
                }
            } else if (isPaused) {
                // When paused: forward original URL to non-MapFlip browser
                Analytics.trackEvent("redirect_paused", mapOf("source_service" to sourceService))
                NavigationLauncher.forwardOriginalUrl(this, dataUri)
            } else {
                val parsedLocation = UniversalMapParser.parse(mapUrl)
                val targetApp = repository.getTargetApp()
                val effectiveTargetApp = NavigationLauncher.resolveEffectiveTargetApp(
                    context = this,
                    configuredApp = targetApp,
                    sourceService = sourceService,
                    showToastOnFallback = true
                )

                val locationType = when (parsedLocation) {
                    is ParsedLocation.Coordinates -> "coordinates"
                    is ParsedLocation.SearchQuery -> "search_query"
                    is ParsedLocation.Navigation -> "navigation"
                    is ParsedLocation.Directions -> "directions"
                    is ParsedLocation.WebFallback -> "web_fallback"
                    is ParsedLocation.Home -> "home"
                }

                Analytics.trackEvent("redirect_performed", mapOf(
                    "target_app" to effectiveTargetApp.name.lowercase(),
                    "source_service" to sourceService,
                    "location_type" to locationType,
                    "is_share_sheet" to isShareSheet
                ))

                val result = NavigationLauncher.launchWithFallback(
                    context = this,
                    parsedLocation = parsedLocation,
                    effectiveTargetApp = effectiveTargetApp,
                    sourceUri = dataUri,
                    sourceService = sourceService,
                    showSuccessToast = true
                )
                if (result.launched) {
                    repository.incrementSuccessfulFlips()
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
