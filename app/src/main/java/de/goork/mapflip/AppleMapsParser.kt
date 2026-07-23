package de.goork.mapflip

import java.net.URI
import java.net.URLDecoder
import java.net.URLEncoder

/**
 * Converts Apple Maps URLs to Google Maps compatible URIs.
 *
 * Supported Apple Maps parameters & patterns:
 * - `saddr` + `daddr` → Google Maps Directions (`https://www.google.com/maps/dir/?api=1&origin=...&destination=...`)
 * - `daddr` → Google Navigation (`google.navigation:q=...`)
 * - `saddr` → Google Address search (`geo:0,0?q=...`)
 * - `ll` / `pt` → Geo coordinates (`geo:lat,lng` or `geo:lat,lng?q=...`)
 * - `q`, `address`, `near`, `name` → Search query (`geo:0,0?q=...`)
 * - `/p/slug` / `/place` short links → Google Maps Search
 *
 * Uses [java.net.URI] instead of `android.net.Uri` for JVM unit testability.
 *
 * @see <a href="https://developer.apple.com/library/archive/featuredarticles/iPhoneURLScheme_Reference/MapLinks/MapLinks.html">Apple Maps URL Scheme</a>
 */
object AppleMapsParser {

    /**
     * Converts an Apple Maps URL to a Google Maps compatible URI string.
     *
     * @param appleUrl full or partial Apple Maps URL (e.g. `https://maps.apple.com/?q=Berlin`)
     * @return a Google Maps compatible URI string (geo:, google.navigation:, or https URL)
     */
    fun convert(appleUrl: String?): String {
        if (appleUrl.isNullOrBlank()) return "https://www.google.com/maps"

        val normalizedUrl = normalizeUrl(appleUrl)

        return try {
            val uri = URI(normalizedUrl)
            val params = parseQueryParams(uri.rawQuery ?: "")

            val saddr = params["saddr"]
            val daddr = params["daddr"]

            // 1. Directions with both origin and destination
            if (!saddr.isNullOrBlank() && !daddr.isNullOrBlank()) {
                return "https://www.google.com/maps/dir/?api=1&origin=${encode(saddr)}&destination=${encode(daddr)}"
            }

            // 2. Navigation to destination only
            if (!daddr.isNullOrBlank()) {
                return "google.navigation:q=${encode(daddr)}"
            }

            // 3. Start address only
            if (!saddr.isNullOrBlank()) {
                return "geo:0,0?q=${encode(saddr)}"
            }

            // 4. Coordinates (ll or pt)
            val coords = (params["ll"] ?: params["pt"])?.replace(" ", "")
            if (!coords.isNullOrBlank()) {
                val searchQuery = params["q"] ?: params["address"] ?: params["near"] ?: params["name"]
                return if (!searchQuery.isNullOrBlank()) {
                    "geo:$coords?q=${encode(searchQuery)}"
                } else {
                    "geo:$coords"
                }
            }

            // 5. Search, address, near, or place name queries
            val searchQuery = params["q"] ?: params["address"] ?: params["near"] ?: params["name"]
            if (!searchQuery.isNullOrBlank()) {
                return "geo:0,0?q=${encode(searchQuery)}"
            }

            // 6. Short links or Place paths (e.g. /p/slug or /place)
            val path = uri.path ?: ""
            if (path.contains("/p/") || path.contains("/place")) {
                return "https://www.google.com/maps/search/?api=1&query=${encode(normalizedUrl)}"
            }

            // 7. Fallback: forward raw query to Google Maps web, or open Maps home
            if (!uri.rawQuery.isNullOrBlank()) {
                "https://www.google.com/maps/search/?api=1&query=${encode(uri.rawQuery)}"
            } else {
                "https://www.google.com/maps"
            }
        } catch (e: Exception) {
            // Graceful fallback on malformed URI syntax
            "https://www.google.com/maps"
        }
    }

    private fun normalizeUrl(url: String): String {
        val trimmed = url.trim()
        return if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            "https://$trimmed"
        } else {
            trimmed
        }
    }

    private fun parseQueryParams(query: String): Map<String, String> {
        if (query.isBlank()) return emptyMap()
        return query.split("&").mapNotNull { param ->
            val parts = param.split("=", limit = 2)
            if (parts.size == 2) {
                val key = URLDecoder.decode(parts[0], "UTF-8").lowercase().trim()
                val value = URLDecoder.decode(parts[1], "UTF-8").trim()
                key to value
            } else null
        }.toMap()
    }

    private fun encode(value: String): String = URLEncoder.encode(value, "UTF-8")
}
