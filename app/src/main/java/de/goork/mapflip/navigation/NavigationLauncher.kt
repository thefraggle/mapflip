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
import de.goork.mapflip.ui.Strings

/**
 * Centralized navigation intent launcher with installation checks, fallback resolution,
 * and browser forwarding to ensure consistent behavior across RedirectActivity, ClipboardBanner,
 * and LinkTesterCard.
 */
object NavigationLauncher {

    private const val TAG = "MapFlip"

    data class LaunchResult(
        val launched: Boolean,
        val effectiveTargetApp: TargetNavigationApp
    )

    fun resolveEffectiveTargetApp(
        context: Context,
        configuredApp: TargetNavigationApp,
        sourceService: String? = null,
        showToastOnFallback: Boolean = true
    ): TargetNavigationApp {
        if (configuredApp.isInstalled(context)) {
            return configuredApp
        }

        val fallback = if (TargetNavigationApp.GOOGLE_MAPS.isInstalled(context)) {
            TargetNavigationApp.GOOGLE_MAPS
        } else {
            TargetNavigationApp.SYSTEM_PICKER
        }

        if (showToastOnFallback) {
            showFallbackToast(context, configuredApp, fallback)
        }

        if (sourceService != null) {
            Analytics.trackEvent("redirect_fallback", mapOf(
                "configured_app" to configuredApp.name.lowercase(),
                "fallback_app" to fallback.name.lowercase(),
                "source_service" to sourceService,
                "reason" to "configured_app_not_installed"
            ))
        }
        return fallback
    }

    fun launchWithFallback(
        context: Context,
        parsedLocation: ParsedLocation,
        effectiveTargetApp: TargetNavigationApp,
        sourceUri: Uri? = null,
        sourceService: String? = null,
        showSuccessToast: Boolean = false
    ): LaunchResult {
        val targetIntent = NavigationIntentBuilder.buildIntent(parsedLocation, effectiveTargetApp, context).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        return try {
            context.startActivity(targetIntent)
            if (showSuccessToast) {
                showRedirectToast(context, effectiveTargetApp)
            }
            LaunchResult(launched = true, effectiveTargetApp = effectiveTargetApp)
        } catch (e: ActivityNotFoundException) {
            Log.w(TAG, "Target app activity not found for ${effectiveTargetApp.name}", e)
            if (sourceService != null) {
                Analytics.trackEvent("redirect_fallback", mapOf(
                    "target_app" to effectiveTargetApp.name.lowercase(),
                    "source_service" to sourceService,
                    "reason" to "app_not_installed"
                ))
            }
            val fallbackLaunched = handleTargetNotFoundFallback(context, effectiveTargetApp, sourceUri, parsedLocation)
            LaunchResult(launched = fallbackLaunched, effectiveTargetApp = effectiveTargetApp)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to launch target intent for ${effectiveTargetApp.name}", e)
            if (sourceService != null) {
                Analytics.trackEvent("redirect_fallback", mapOf(
                    "target_app" to effectiveTargetApp.name.lowercase(),
                    "source_service" to sourceService,
                    "reason" to e.javaClass.simpleName
                ))
            }
            val forwarded = if (sourceUri != null) forwardOriginalUrl(context, sourceUri) else false
            LaunchResult(launched = forwarded, effectiveTargetApp = effectiveTargetApp)
        }
    }

    fun handleTargetNotFoundFallback(
        context: Context,
        targetApp: TargetNavigationApp,
        dataUri: Uri?,
        parsedLocation: ParsedLocation
    ): Boolean {
        // 1. Try generic System Picker chooser (only if external handlers exist to prevent self-looping)
        try {
            val geoIntent = Intent(Intent.ACTION_VIEW, Uri.parse(NavigationIntentBuilder.buildGenericGeoUriString(parsedLocation)))
            val handlers = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.queryIntentActivities(
                    geoIntent,
                    PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong())
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.queryIntentActivities(geoIntent, PackageManager.MATCH_DEFAULT_ONLY)
            }.filter { it.activityInfo.packageName != context.packageName }

            if (handlers.isNotEmpty()) {
                val chooserIntent = NavigationIntentBuilder.buildGenericGeoIntent(parsedLocation, createChooser = true).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        putExtra(
                            Intent.EXTRA_EXCLUDE_COMPONENTS,
                            arrayOf(ComponentName(context, RedirectActivity::class.java))
                        )
                    }
                }
                context.startActivity(chooserIntent)
                return true
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to launch generic chooser intent", e)
        }

        // 2. Specific Waze web fallback if applicable
        if (targetApp == TargetNavigationApp.WAZE) {
            try {
                val wazeWebUri = Uri.parse(NavigationIntentBuilder.buildWazeWebFallbackUriString(parsedLocation))
                val fallbackIntent = Intent(Intent.ACTION_VIEW, wazeWebUri).apply {
                    addCategory(Intent.CATEGORY_BROWSABLE)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallbackIntent)
                return true
            } catch (e: Exception) {
                Log.w(TAG, "Failed to launch Waze web fallback", e)
            }
        }

        // 3. Fallback: browser
        return if (dataUri != null) forwardOriginalUrl(context, dataUri) else false
    }

    /**
     * Forwards original map URL to standard web browser when redirect is paused or no native map app is found.
     */
    fun forwardOriginalUrl(context: Context, uri: Uri): Boolean {
        val scheme = uri.scheme?.lowercase()
        if (scheme != "http" && scheme != "https") {
            Log.w(TAG, "Refusing to forward non-web URI to browser: $uri")
            return false
        }

        val genericWebIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com")).apply {
            addCategory(Intent.CATEGORY_BROWSABLE)
        }

        val pm = context.packageManager
        val browserPackage = try {
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

        if (browserPackage != null) {
            val targetIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                addCategory(Intent.CATEGORY_BROWSABLE)
                setPackage(browserPackage)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                context.startActivity(targetIntent)
                return true
            } catch (e: Exception) {
                Log.w(TAG, "Failed to launch browser with package $browserPackage", e)
            }
        }

        // Fallback: browser selector intent
        return try {
            val selectorIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_APP_BROWSER)
            }
            val browserIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                selector = selectorIntent
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(browserIntent)
            true
        } catch (e: Exception) {
            Log.w(TAG, "Failed to launch browser selector intent", e)
            false
        }
    }

    private fun showFallbackToast(
        context: Context,
        configuredApp: TargetNavigationApp,
        fallbackApp: TargetNavigationApp
    ) {
        try {
            val repo = PreferencesRepository.getInstance(context)
            val langCode = Strings.resolveLanguage(repo.preferences.value.language)
            val s = Strings.getStrings(langCode)
            val fallbackName = if (fallbackApp.isSystemPicker) s.targetAppAlwaysAsk else fallbackApp.displayName
            val msg = s.targetAppFallbackOpened.format(configuredApp.displayName, fallbackName)
            Toast.makeText(context.applicationContext, msg, Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to show fallback toast", e)
        }
    }

    private fun showRedirectToast(context: Context, targetApp: TargetNavigationApp) {
        try {
            val repo = PreferencesRepository.getInstance(context)
            val langCode = Strings.resolveLanguage(repo.preferences.value.language)
            val s = Strings.getStrings(langCode)
            val appName = if (targetApp.isSystemPicker) s.targetAppAlwaysAsk else targetApp.displayName
            val msg = s.redirectingToApp.format(appName)
            Toast.makeText(context.applicationContext, msg, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to show redirect toast", e)
        }
    }
}
