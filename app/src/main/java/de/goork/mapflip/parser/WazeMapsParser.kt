package de.goork.mapflip.parser

import java.net.URI
import java.net.URLDecoder
import java.util.regex.Pattern

object WazeMapsParser : MapUrlParser {

    override val serviceName: String = "Waze"
    override val supportedHosts: List<String> = listOf("waze.com", "www.waze.com", "ul.waze.com")

    private val URL_PATTERN = Pattern.compile(
        """https?://(?:(?:www|ul)\.)?waze\.com/(?:ul|live-map|location)[^\s<>"'()]*""",
        Pattern.CASE_INSENSITIVE
    )

    override fun canParse(url: String): Boolean {
        val lower = url.lowercase().trim()
        return lower.contains("waze.com/ul") || lower.contains("waze.com/live-map") || lower.contains("waze.com/location")
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
        val normalizedUrl = if (!extracted.startsWith("http://", ignoreCase = true) && !extracted.startsWith("https://", ignoreCase = true)) {
            "https://$extracted"
        } else extracted

        return try {
            val uri = URI(normalizedUrl.replace(" ", "%20"))
            val params = parseQueryParams(uri.rawQuery ?: "")

            // 1. Coordinates via ll parameter (ll=lat,lon or ll=lat%2Clon)
            val ll = params["ll"]
            if (!ll.isNullOrBlank()) {
                val parts = ll.split(",")
                if (parts.size == 2) {
                    val lat = parts[0].toDoubleOrNull()
                    val lon = parts[1].toDoubleOrNull()
                    if (lat != null && lon != null) {
                        val query = params["q"]
                        return ParsedLocation.Coordinates(lat, lon, label = query)
                    }
                }
            }

            // 2. live-map/directions?to=ll.lat,lon or ?to=place.XYZ
            val toParam = params["to"]
            if (!toParam.isNullOrBlank()) {
                if (toParam.startsWith("ll.", ignoreCase = true)) {
                    val coords = toParam.substring(3).split(",")
                    if (coords.size == 2) {
                        val lat = coords[0].toDoubleOrNull()
                        val lon = coords[1].toDoubleOrNull()
                        if (lat != null && lon != null) {
                            return ParsedLocation.Coordinates(lat, lon)
                        }
                    }
                } else if (toParam.startsWith("place.", ignoreCase = true)) {
                    return ParsedLocation.SearchQuery(toParam.substring(6).replace("_", " "))
                } else {
                    return ParsedLocation.SearchQuery(toParam)
                }
            }

            // 3. Search query q
            val query = params["q"]
            if (!query.isNullOrBlank()) {
                return ParsedLocation.SearchQuery(query)
            }

            // 4. Favorite home / work
            val favorite = params["favorite"]
            if (!favorite.isNullOrBlank()) {
                return ParsedLocation.SearchQuery(favorite)
            }

            ParsedLocation.WebFallback(normalizedUrl)
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
