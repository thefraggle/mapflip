package de.goork.mapflip.navigation

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import de.goork.mapflip.RedirectActivity
import de.goork.mapflip.analytics.Analytics
import de.goork.mapflip.data.PreferencesRepository
import de.goork.mapflip.parser.ParsedLocation
import de.goork.mapflip.parser.UniversalMapParser
import de.goork.mapflip.ui.Strings

/**
 * Seam for Android platform operations (package checks, activity launching, browser resolution, toasts).
 * Enables testing [NavigationLauncher] orchestration directly with an in-memory adapter.
 */
interface SystemLauncherPort {
    fun isAppInstalled(app: TargetNavigationApp): Boolean
    fun buildTargetIntent(location: ParsedLocation, targetApp: TargetNavigationApp): Intent
    fun buildChooserIntent(location: ParsedLocation): Intent
    fun startActivity(intent: Intent)
    fun hasExternalGeoHandlers(geoUriString: String): Boolean
    fun resolveDefaultBrowserPackage(): String?
    fun showToast(message: String, longDuration: Boolean)
}

internal class AndroidSystemLauncherPort(private val context: Context) : SystemLauncherPort {

    override fun isAppInstalled(app: TargetNavigationApp): Boolean = app.isInstalled(context)

    override fun buildTargetIntent(location: ParsedLocation, targetApp: TargetNavigationApp): Intent {
        if (targetApp == TargetNavigationApp.SYSTEM_PICKER) {
            return buildChooserIntent(location)
        }
        return NavigationIntentBuilder.buildIntent(location, targetApp, context).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    override fun buildChooserIntent(location: ParsedLocation): Intent {
        return NavigationIntentBuilder.buildGenericGeoIntent(location, createChooser = true).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                putExtra(
                    Intent.EXTRA_EXCLUDE_COMPONENTS,
                    arrayOf(ComponentName(context, RedirectActivity::class.java))
                )
            }
        }
    }

    override fun startActivity(intent: Intent) {
        context.startActivity(intent)
    }

    override fun hasExternalGeoHandlers(geoUriString: String): Boolean {
        val geoIntent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUriString))
        val handlers = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.queryIntentActivities(
                geoIntent,
                PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong())
            )
        } else {
            @Suppress("DEPRECATION")
            context.packageManager.queryIntentActivities(geoIntent, PackageManager.MATCH_DEFAULT_ONLY)
        }
        return handlers.any { it.activityInfo.packageName != context.packageName }
    }

    override fun resolveDefaultBrowserPackage(): String? {
        val genericWebIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com")).apply {
            addCategory(Intent.CATEGORY_BROWSABLE)
        }
        val pm = context.packageManager
        return try {
            val defaultBrowser = pm.resolveActivity(genericWebIntent, PackageManager.MATCH_DEFAULT_ONLY)
                ?.activityInfo?.packageName
                ?.takeIf { it != context.packageName && it != "android" }
            defaultBrowser
                ?: pm.queryIntentActivities(genericWebIntent, PackageManager.MATCH_DEFAULT_ONLY)
                    .map { it.activityInfo.packageName }
                    .firstOrNull { it != context.packageName && it != "android" }
                ?: pm.queryIntentActivities(genericWebIntent, 0)
                    .map { it.activityInfo.packageName }
                    .firstOrNull { it != context.packageName && it != "android" }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to query browser activities", e)
            null
        }
    }

    override fun showToast(message: String, longDuration: Boolean) {
        try {
            val duration = if (longDuration) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
            Toast.makeText(context.applicationContext, message, duration).show()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to show toast", e)
        }
    }

    private companion object {
        const val TAG = "MapFlip"
    }
}

/**
 * Deep coordinator for map URL parsing, pause/unsupported forwarding, target resolution,
 * fallback handling, toast feedback, and telemetry across RedirectActivity, ClipboardBanner, and LinkTesterCard.
 */
class NavigationLauncher internal constructor(
    private val repository: PreferencesRepository,
    private val system: SystemLauncherPort,
    private val trackEvent: (String, Map<String, Any>) -> Unit = Analytics::trackEvent
) {

    data class LaunchResult(
        val launched: Boolean,
        val effectiveTargetApp: TargetNavigationApp
    )

    fun redirect(
        rawInput: String?,
        incomingUri: Uri?,
        isShareSheet: Boolean
    ): LaunchResult {
        val mapUrl = when {
            incomingUri != null -> incomingUri.toString()
            !rawInput.isNullOrBlank() -> UniversalMapParser.extractMapUrl(rawInput) ?: rawInput.trim()
            else -> null
        }

        if (mapUrl == null) {
            trackEvent("redirect_failed", mapOf(
                "reason" to "no_url_found",
                "is_share_sheet" to isShareSheet
            ))
            return LaunchResult(launched = false, effectiveTargetApp = TargetNavigationApp.SYSTEM_PICKER)
        }

        val sourceService = UniversalMapParser.detectSourceService(mapUrl)
        val dataUri = incomingUri ?: Uri.parse(mapUrl)

        if (!UniversalMapParser.canParse(mapUrl)) {
            return if (incomingUri != null) {
                LaunchResult(launched = forwardOriginalUrl(dataUri), effectiveTargetApp = TargetNavigationApp.SYSTEM_PICKER)
            } else {
                trackEvent("redirect_failed", mapOf(
                    "reason" to "unsupported_shared_text",
                    "is_share_sheet" to isShareSheet
                ))
                LaunchResult(launched = false, effectiveTargetApp = TargetNavigationApp.SYSTEM_PICKER)
            }
        }

        if (repository.isCurrentlyPaused()) {
            trackEvent("redirect_paused", mapOf("source_service" to sourceService))
            return LaunchResult(launched = forwardOriginalUrl(dataUri), effectiveTargetApp = TargetNavigationApp.SYSTEM_PICKER)
        }

        val parsedLocation = UniversalMapParser.parse(mapUrl)
        val configuredApp = repository.getTargetApp()
        val effectiveTargetApp = resolveEffectiveTargetApp(
            configuredApp = configuredApp,
            sourceService = sourceService,
            showToastOnFallback = true
        )

        trackEvent("redirect_performed", mapOf(
            "target_app" to effectiveTargetApp.name.lowercase(),
            "source_service" to sourceService,
            "location_type" to locationTypeName(parsedLocation),
            "is_share_sheet" to isShareSheet
        ))

        val result = launchWithFallback(
            parsedLocation = parsedLocation,
            effectiveTargetApp = effectiveTargetApp,
            sourceUri = dataUri,
            sourceService = sourceService,
            showSuccessToast = true
        )
        if (result.launched) {
            repository.incrementSuccessfulFlips()
        }
        return result
    }

    fun launchFromUi(
        url: String,
        configuredApp: TargetNavigationApp,
        eventName: String,
        showErrorToast: Boolean = true
    ): LaunchResult {
        val sourceService = UniversalMapParser.detectSourceService(url)
        val parsed = UniversalMapParser.parse(url)
        val effectiveTarget = resolveEffectiveTargetApp(
            configuredApp = configuredApp,
            sourceService = sourceService,
            showToastOnFallback = true
        )

        trackEvent(eventName, mapOf(
            "source_service" to sourceService,
            "target_app" to effectiveTarget.name.lowercase()
        ))

        val sourceUri = runCatching { Uri.parse(url.trim()) }.getOrNull()
        val result = launchWithFallback(
            parsedLocation = parsed,
            effectiveTargetApp = effectiveTarget,
            sourceUri = sourceUri,
            sourceService = sourceService,
            showSuccessToast = false
        )

        if (!result.launched && showErrorToast) {
            system.showToast(currentStrings().targetAppOpenError, longDuration = false)
        }
        return result
    }

    private fun resolveEffectiveTargetApp(
        configuredApp: TargetNavigationApp,
        sourceService: String?,
        showToastOnFallback: Boolean
    ): TargetNavigationApp {
        if (system.isAppInstalled(configuredApp)) {
            return configuredApp
        }

        val fallback = if (system.isAppInstalled(TargetNavigationApp.GOOGLE_MAPS)) {
            TargetNavigationApp.GOOGLE_MAPS
        } else {
            TargetNavigationApp.SYSTEM_PICKER
        }

        if (showToastOnFallback) {
            showFallbackToast(configuredApp, fallback)
        }

        if (sourceService != null) {
            trackEvent("redirect_fallback", mapOf(
                "configured_app" to configuredApp.name.lowercase(),
                "fallback_app" to fallback.name.lowercase(),
                "source_service" to sourceService,
                "reason" to "configured_app_not_installed"
            ))
        }
        return fallback
    }

    private fun launchWithFallback(
        parsedLocation: ParsedLocation,
        effectiveTargetApp: TargetNavigationApp,
        sourceUri: Uri?,
        sourceService: String?,
        showSuccessToast: Boolean
    ): LaunchResult {
        val targetIntent = system.buildTargetIntent(parsedLocation, effectiveTargetApp)

        return try {
            system.startActivity(targetIntent)
            if (showSuccessToast) {
                showRedirectToast(effectiveTargetApp)
            }
            LaunchResult(launched = true, effectiveTargetApp = effectiveTargetApp)
        } catch (e: ActivityNotFoundException) {
            Log.w(TAG, "Target app activity not found for ${effectiveTargetApp.name}", e)
            if (sourceService != null) {
                trackEvent("redirect_fallback", mapOf(
                    "target_app" to effectiveTargetApp.name.lowercase(),
                    "source_service" to sourceService,
                    "reason" to "app_not_installed"
                ))
            }
            val fallbackLaunched = handleTargetNotFoundFallback(effectiveTargetApp, sourceUri, parsedLocation)
            LaunchResult(launched = fallbackLaunched, effectiveTargetApp = effectiveTargetApp)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to launch target intent for ${effectiveTargetApp.name}", e)
            if (sourceService != null) {
                trackEvent("redirect_fallback", mapOf(
                    "target_app" to effectiveTargetApp.name.lowercase(),
                    "source_service" to sourceService,
                    "reason" to e.javaClass.simpleName
                ))
            }
            val forwarded = if (sourceUri != null) forwardOriginalUrl(sourceUri) else false
            LaunchResult(launched = forwarded, effectiveTargetApp = effectiveTargetApp)
        }
    }

    private fun handleTargetNotFoundFallback(
        targetApp: TargetNavigationApp,
        dataUri: Uri?,
        parsedLocation: ParsedLocation
    ): Boolean {
        try {
            val geoUriString = NavigationIntentBuilder.buildGenericGeoUriString(parsedLocation)
            if (system.hasExternalGeoHandlers(geoUriString)) {
                system.startActivity(system.buildChooserIntent(parsedLocation))
                return true
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to launch generic chooser intent", e)
        }

        if (targetApp == TargetNavigationApp.WAZE) {
            try {
                val wazeWebUri = Uri.parse(NavigationIntentBuilder.buildWazeWebFallbackUriString(parsedLocation))
                if (forwardOriginalUrl(wazeWebUri)) {
                    return true
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to launch Waze web fallback", e)
            }
        }

        return if (dataUri != null) forwardOriginalUrl(dataUri) else false
    }

    private fun forwardOriginalUrl(uri: Uri): Boolean {
        val scheme = uri.scheme?.lowercase()
        if (scheme != "http" && scheme != "https") {
            Log.w(TAG, "Refusing to forward non-web URI to browser: $uri")
            return false
        }

        val browserPackage = system.resolveDefaultBrowserPackage()
        if (browserPackage != null) {
            val targetIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                addCategory(Intent.CATEGORY_BROWSABLE)
                setPackage(browserPackage)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                system.startActivity(targetIntent)
                return true
            } catch (e: Exception) {
                Log.w(TAG, "Failed to launch browser with package $browserPackage", e)
            }
        }

        return try {
            val selectorIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_APP_BROWSER)
            }
            val browserIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                selector = selectorIntent
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            system.startActivity(browserIntent)
            true
        } catch (e: Exception) {
            Log.w(TAG, "Failed to launch browser selector intent", e)
            false
        }
    }

    private fun currentStrings(): Strings.AppStrings {
        val langCode = Strings.resolveLanguage(repository.preferences.value.language)
        return Strings.getStrings(langCode)
    }

    private fun showFallbackToast(
        configuredApp: TargetNavigationApp,
        fallbackApp: TargetNavigationApp
    ) {
        val s = currentStrings()
        val fallbackName = if (fallbackApp.isSystemPicker) s.targetAppAlwaysAsk else fallbackApp.displayName
        val msg = s.targetAppFallbackOpened.format(configuredApp.displayName, fallbackName)
        system.showToast(msg, longDuration = true)
    }

    private fun showRedirectToast(targetApp: TargetNavigationApp) {
        val s = currentStrings()
        val appName = if (targetApp.isSystemPicker) s.targetAppAlwaysAsk else targetApp.displayName
        val msg = s.redirectingToApp.format(appName)
        system.showToast(msg, longDuration = false)
    }

    private fun locationTypeName(location: ParsedLocation): String = when (location) {
        is ParsedLocation.Coordinates -> "coordinates"
        is ParsedLocation.SearchQuery -> "search_query"
        is ParsedLocation.Navigation -> "navigation"
        is ParsedLocation.Directions -> "directions"
        is ParsedLocation.WebFallback -> "web_fallback"
        is ParsedLocation.Home -> "home"
    }

    companion object {
        private const val TAG = "MapFlip"

        fun create(context: Context): NavigationLauncher {
            return NavigationLauncher(
                repository = PreferencesRepository.getInstance(context),
                system = AndroidSystemLauncherPort(context)
            )
        }
    }
}
