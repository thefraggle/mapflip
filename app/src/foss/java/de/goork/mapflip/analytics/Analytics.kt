package de.goork.mapflip.analytics

import android.content.Context

/**
 * FOSS Flavor Analytics stub: 100% No-Op.
 * Zero tracking, zero network requests, zero SDK dependencies.
 */
object Analytics {
    fun init(context: Context) {
        // No-op
    }

    fun trackEvent(eventName: String, properties: Map<String, Any> = emptyMap()) {
        // No-op
    }
}
