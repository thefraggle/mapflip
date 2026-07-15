package de.goork.mapflip

import java.net.URI
import java.net.URLDecoder
import java.net.URLEncoder

/**
 * Converts Apple Maps URLs to Google Maps compatible URIs.
 *
 * Supported Apple Maps parameters:
 * - `daddr` → Google Navigation (`google.navigation:q=...`)
 * - `ll` → Geo coordinates (`geo:lat,lng`)
 * - `q` → Search query (`geo:0,0?q=...`)
 * - `address` → Address search
 * - `near` → Nearby search
 * - `place?name=` → Place name search
 *
 * Uses [java.net.URI] instead of `android.net.Uri` for unit testability.
 *
 * @see <a href="https://developer.apple.com/library/archive/featuredarticles/iPhoneURLScheme_Reference/MapLinks/MapLinks.html">Apple Maps URL Scheme</a>
 */
object AppleMapsParser {

    /**
     * Converts an Apple Maps URL to a Google Maps URI string.
     *
     * @param appleUrl full Apple Maps URL (e.g. `https://maps.apple.com/?q=Berlin`)
     * @return a Google Maps compatible URI string (geo:, google.navigation:, or https URL)
     */
    fun convert(appleUrl: String): String {
        val uri = URI(appleUrl)
        val params = parseQueryParams(uri.rawQuery ?: "")

        // Navigation/directions
        params["daddr"]?.let { return "google.navigation:q=${encode(it)}" }

        // Coordinates with optional search query
        params["ll"]?.let { ll ->
            val q = params["q"]
            return if (q != null) "geo:$ll?q=${encode(q)}" else "geo:$ll"
        }

        // Search, address, or nearby queries
        params["q"]?.let { return "geo:0,0?q=${encode(it)}" }
        params["address"]?.let { return "geo:0,0?q=${encode(it)}" }
        params["near"]?.let { return "geo:0,0?q=${encode(it)}" }

        // Place page with name
        if (uri.path?.contains("place") == true) {
            params["name"]?.let { return "geo:0,0?q=${encode(it)}" }
        }

        // Fallback: forward raw query to Google Maps web, or open Maps home
        return if (!uri.rawQuery.isNullOrBlank()) {
            "https://www.google.com/maps/search/?api=1&query=${encode(uri.rawQuery)}"
        } else {
            "https://www.google.com/maps"
        }
    }

    private fun parseQueryParams(query: String): Map<String, String> {
        if (query.isBlank()) return emptyMap()
        return query.split("&").mapNotNull { param ->
            val parts = param.split("=", limit = 2)
            if (parts.size == 2) {
                URLDecoder.decode(parts[0], "UTF-8") to URLDecoder.decode(parts[1], "UTF-8")
            } else null
        }.toMap()
    }

    private fun encode(value: String): String = URLEncoder.encode(value, "UTF-8")
}
