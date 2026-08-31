package de.goork.mapflip.analytics

import android.content.Context
import android.util.Log
import com.aptabase.Aptabase
import com.aptabase.InitOptions

/**
 * Play Store Flavor Analytics implementation powered by self-hosted Aptabase.
 * Collects strictly anonymous, privacy-friendly telemetry (no PII, no URLs, no coordinates).
 */
object Analytics {
    private const val TAG = "MapFlipAnalytics"
    private const val APP_KEY = "A-SH-1812872922"
    private const val CUSTOM_HOST = "https://telemetry-apps.goork.de"
    private var isInitialized = false

    fun init(context: Context) {
        if (isInitialized) return
        try {
            Aptabase.instance.initialize(
                context = context.applicationContext,
                appKey = APP_KEY,
                opts = InitOptions(host = CUSTOM_HOST)
            )
            isInitialized = true
        } catch (e: Exception) {
            Log.w(TAG, "Failed to initialize Aptabase", e)
        }
    }

    fun trackEvent(eventName: String, properties: Map<String, Any> = emptyMap()) {
        if (!isInitialized) return
        try {
            Aptabase.instance.trackEvent(eventName, properties)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to track event $eventName", e)
        }
    }
}
