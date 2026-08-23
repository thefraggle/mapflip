package de.goork.mapflip

import java.net.URI
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.regex.Pattern

/**
 * Converts Apple Maps URLs to Google Maps compatible URIs.
 *
 * Supported Apple Maps parameters & patterns:
 * - `saddr` + `daddr` (+ `dirflg`) → Google Maps Directions (`https://www.google.com/maps/dir/?api=1&origin=...&destination=...&travelmode=...`)
 * - `daddr` (+ `dirflg`) → Google Navigation (`google.navigation:q=...&mode=...`)
 * - `saddr` → Google Address search (`geo:0,0?q=...`)
 * - `ll` / `pt` / `coordinate` / `center` → Geo coordinates (`geo:lat,lng` or `geo:lat,lng?q=...`)
 * - `q`, `address`, `near`, `name`, `auid` → Search query (`geo:0,0?q=...`)
 * - `/p/slug` / `/place` short links → Google Maps Search
 * - `applemaps://` custom scheme support
 * - Text link extractor for shared messenger snippets
 *
 * Uses [java.net.URI] instead of `android.net.Uri` for JVM unit testability.
 */
object AppleMapsParser {

    private val URL_PATTERN = Pattern.compile(
        """(?:https?://|applemaps://|maps\.apple\.com)[^\s<>"'()]+""",
        Pattern.CASE_INSENSITIVE
    )

    /**
     * Extracts an Apple Maps URL from arbitrary text (e.g. shared messenger messages or clipboard snippets).
     * Returns null if no valid link pattern is found.
     */
    fun extractMapUrl(text: String?): String? {
        if (text.isNullOrBlank()) return null
        val matcher = URL_PATTERN.matcher(text)
        if (matcher.find()) {
            var url = matcher.group()
            // Clean trailing punctuation attached from surrounding sentence
            url = url.trimEnd('.', ',', ';', '!', '?', ')', ']', '>')
            return url
        }
        return null
    }

    /**
     * Converts an Apple Maps URL (or shared text containing an Apple Maps URL)
     * to a Google Maps compatible URI string.
     *
     * @param appleUrl full/partial Apple Maps URL or shared text snippet
     * @return a Google Maps compatible URI string (geo:, google.navigation:, or https URL)
     */
    fun convert(appleUrl: String?): String {
        if (appleUrl.isNullOrBlank()) return "https://www.google.com/maps"

        val extracted = extractMapUrl(appleUrl) ?: appleUrl
        val normalizedUrl = normalizeUrl(extracted)

        return try {
            val uri = URI(normalizedUrl)
            val params = parseQueryParams(uri.rawQuery ?: "")

            val saddr = params["saddr"]
            val daddr = params["daddr"]
            val dirflg = params["dirflg"]?.lowercase()

            val googleTravelMode = when (dirflg) {
                "w" -> "walking"
                "r" -> "transit"
                "b" -> "bicycling"
                "d" -> "driving"
                else -> null
            }

            val navMode = when (dirflg) {
                "w" -> "w"
                "b" -> "b"
                "r" -> "transit"
                "d" -> "d"
                else -> null
            }

            // 1. Directions with both origin and destination
            if (!saddr.isNullOrBlank() && !daddr.isNullOrBlank()) {
                val base = "https://www.google.com/maps/dir/?api=1&origin=${encode(saddr)}&destination=${encode(daddr)}"
                return if (googleTravelMode != null) "$base&travelmode=$googleTravelMode" else base
            }

            // 2. Navigation to destination only
            if (!daddr.isNullOrBlank()) {
                val base = "google.navigation:q=${encode(daddr)}"
                return if (navMode != null) "$base&mode=$navMode" else base
            }

            // 3. Start address only
            if (!saddr.isNullOrBlank()) {
                return "geo:0,0?q=${encode(saddr)}"
            }

            // 4. Coordinates (ll, pt, coordinate, center)
            val rawCoords = params["ll"] ?: params["pt"] ?: params["coordinate"] ?: params["center"]
            val coords = rawCoords?.replace(" ", "")
            if (!coords.isNullOrBlank()) {
                val searchQuery = params["q"] ?: params["address"] ?: params["near"] ?: params["name"]
                return if (!searchQuery.isNullOrBlank()) {
                    "geo:$coords?q=${encode(searchQuery)}"
                } else {
                    // Include coordinates as search query parameter so third-party map apps drop a pin marker reliably
                    "geo:$coords?q=$coords"
                }
            }

            // 5. Search, address, near, place name, auid or lsp queries
            val searchQuery = params["q"] ?: params["address"] ?: params["near"] ?: params["name"] ?: params["auid"]
            if (!searchQuery.isNullOrBlank()) {
                return "geo:0,0?q=${encode(searchQuery)}"
            }

            // 6. Short links or Place paths (e.g. /p/slug or /place)
            val path = uri.path ?: ""
            if (path.contains("/p/") || path.contains("/place") || path.contains("/directions")) {
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
        var trimmed = url.trim()
        if (trimmed.startsWith("applemaps://", ignoreCase = true)) {
            trimmed = "https://" + trimmed.substring("applemaps://".length)
        }
        if (!trimmed.startsWith("http://", ignoreCase = true) && !trimmed.startsWith("https://", ignoreCase = true)) {
            trimmed = "https://$trimmed"
        }
        // Replace unencoded spaces with %20 to avoid URISyntaxException in java.net.URI
        return trimmed.replace(" ", "%20")
    }

    private fun parseQueryParams(query: String): Map<String, String> {
        if (query.isBlank()) return emptyMap()
        return query.split("&").mapNotNull { param ->
            val parts = param.split("=", limit = 2)
            if (parts.size == 2) {
                try {
                    val key = URLDecoder.decode(parts[0], "UTF-8").lowercase().trim()
                    val value = URLDecoder.decode(parts[1], "UTF-8").trim()
                    key to value
                } catch (_: Exception) {
                    null
                }
            } else null
        }.toMap()
    }

    private fun encode(value: String): String = URLEncoder.encode(value, "UTF-8")
}

