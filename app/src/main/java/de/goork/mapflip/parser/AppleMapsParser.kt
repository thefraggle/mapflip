package de.goork.mapflip.parser

import java.net.URI
import java.net.URLDecoder
import java.util.regex.Pattern

object AppleMapsParser : MapUrlParser {

    override val serviceName: String = "Apple Maps"
    override val supportedHosts: List<String> = listOf("maps.apple.com")

    private val URL_PATTERN = Pattern.compile(
        """(?:https?://|applemaps://|maps\.apple\.com)[^\s<>"'()]+""",
        Pattern.CASE_INSENSITIVE
    )

    override fun canParse(url: String): Boolean {
        val lower = url.lowercase().trim()
        return lower.contains("maps.apple.com") || lower.startsWith("applemaps://")
    }

    override fun extractUrl(text: String?): String? {
        if (text.isNullOrBlank()) return null
        val matcher = URL_PATTERN.matcher(text)
        if (matcher.find()) {
            var url = matcher.group()
            url = url.trimEnd('.', ',', ';', '!', '?', ')', ']', '>')
            return url
        }
        return null
    }

    override fun parse(url: String): ParsedLocation {
        if (url.isBlank()) return ParsedLocation.Home

        val extracted = extractUrl(url) ?: url
        val normalizedUrl = normalizeUrl(extracted)

        return try {
            val uri = URI(normalizedUrl)
            val params = parseQueryParams(uri.rawQuery ?: "")

            val saddr = params["saddr"]
            val daddr = params["daddr"]
            val dirflg = params["dirflg"]?.lowercase()

            val travelMode = when (dirflg) {
                "w" -> TravelMode.WALKING
                "r" -> TravelMode.TRANSIT
                "b" -> TravelMode.BICYCLING
                "d" -> TravelMode.DRIVING
                else -> null
            }

            // 1. Directions with both origin and destination
            if (!saddr.isNullOrBlank() && !daddr.isNullOrBlank()) {
                return ParsedLocation.Directions(origin = saddr, destination = daddr, mode = travelMode)
            }

            // 2. Navigation to destination only
            if (!daddr.isNullOrBlank()) {
                return ParsedLocation.Navigation(destination = daddr, mode = travelMode)
            }

            // 3. Start address only
            if (!saddr.isNullOrBlank()) {
                return ParsedLocation.SearchQuery(saddr)
            }

            // 4. Coordinates (ll, pt, coordinate, center)
            val rawCoords = params["ll"] ?: params["pt"] ?: params["coordinate"] ?: params["center"]
            val coords = rawCoords?.replace(" ", "")
            if (!coords.isNullOrBlank()) {
                val parts = coords.split(",")
                if (parts.size == 2) {
                    val lat = parts[0].toDoubleOrNull()
                    val lon = parts[1].toDoubleOrNull()
                    if (lat != null && lon != null) {
                        val searchQuery = params["q"] ?: params["address"] ?: params["near"] ?: params["name"]
                        return ParsedLocation.Coordinates(lat, lon, label = searchQuery)
                    }
                }
            }

            // 5. Search, address, near, place name, auid
            val searchQuery = params["q"] ?: params["address"] ?: params["near"] ?: params["name"] ?: params["auid"]
            if (!searchQuery.isNullOrBlank()) {
                return ParsedLocation.SearchQuery(searchQuery)
            }

            // 6. Short links or Place paths (e.g. /p/slug or /place)
            val path = uri.path ?: ""
            if (path.contains("/p/") || path.contains("/place") || path.contains("/directions")) {
                return ParsedLocation.WebFallback(normalizedUrl)
            }

            // 7. Fallback: raw query
            if (!uri.rawQuery.isNullOrBlank()) {
                ParsedLocation.WebFallback(normalizedUrl)
            } else {
                ParsedLocation.Home
            }
        } catch (_: Exception) {
            ParsedLocation.Home
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
}
