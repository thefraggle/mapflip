package de.goork.mapflip.parser

import java.net.URI
import java.net.URLDecoder
import java.util.regex.Pattern

object DuckDuckGoParser : MapUrlParser {

    override val serviceName: String = "DuckDuckGo Maps"
    override val supportedHosts: List<String> = listOf("duckduckgo.com", "www.duckduckgo.com")

    private val URL_PATTERN = Pattern.compile(
        """https?://(?:www\.)?duckduckgo\.com[^\s<>"'()]*""",
        Pattern.CASE_INSENSITIVE
    )

    private val COORD_PATTERN = Pattern.compile(
        """^([+-]?\d+(?:\.\d+)?)[,\s]+([+-]?\d+(?:\.\d+)?)$"""
    )

    override fun canParse(url: String): Boolean {
        val lower = url.lowercase().trim()
        val isDdg = lower.contains("duckduckgo.com")
        if (!isDdg) return false

        // Only intercept when it's explicitly a maps, places or directions query
        return lower.contains("iaxm=maps") ||
            lower.contains("ia=maps") ||
            lower.contains("iaxm=places") ||
            lower.contains("ia=places") ||
            lower.contains("iaxm=directions") ||
            lower.contains("ia=directions")
    }

    override fun extractUrl(text: String?): String? {
        if (text.isNullOrBlank()) return null
        val matcher = URL_PATTERN.matcher(text)
        while (matcher.find()) {
            var candidate = matcher.group()
            candidate = candidate.trimEnd('.', ',', ';', '!', '?', ')', ']', '>')
            if (canParse(candidate)) {
                return candidate
            }
        }
        return null
    }

    override fun parse(url: String): ParsedLocation {
        if (url.isBlank()) return ParsedLocation.Home

        val extracted = extractUrl(url) ?: url
        val normalizedUrl = if (!extracted.startsWith("http://", ignoreCase = true) && !extracted.startsWith("https://", ignoreCase = true)) {
            "https://$extracted"
        } else extracted

        return try {
            val uri = URI(normalizedUrl.replace(" ", "%20"))
            val params = parseQueryParams(uri.rawQuery ?: "")

            val query = params["q"]
            val isDirections = params["iaxm"]?.equals("directions", ignoreCase = true) == true ||
                params["ia"]?.equals("directions", ignoreCase = true) == true

            val transportParam = params["transport"]?.lowercase()
            val travelMode = when (transportParam) {
                "walk", "walking", "foot" -> TravelMode.WALKING
                "bike", "bicycle", "cycling" -> TravelMode.BICYCLING
                "transit", "bus", "train" -> TravelMode.TRANSIT
                "drive", "car", "driving" -> TravelMode.DRIVING
                else -> null
            }

            if (!query.isNullOrBlank()) {
                val coordMatcher = COORD_PATTERN.matcher(query.trim())
                if (coordMatcher.matches()) {
                    val lat = coordMatcher.group(1)?.toDoubleOrNull()
                    val lon = coordMatcher.group(2)?.toDoubleOrNull()
                    if (lat != null && lon != null && lat in -90.0..90.0 && lon in -180.0..180.0) {
                        return ParsedLocation.Coordinates(latitude = lat, longitude = lon, mode = travelMode)
                    }
                }

                return if (isDirections) {
                    ParsedLocation.Navigation(destination = query, mode = travelMode)
                } else {
                    ParsedLocation.SearchQuery(query = query)
                }
            }

            ParsedLocation.Home
        } catch (_: Exception) {
            ParsedLocation.Home
        }
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
