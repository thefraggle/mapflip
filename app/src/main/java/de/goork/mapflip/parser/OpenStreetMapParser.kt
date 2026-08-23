package de.goork.mapflip.parser

import java.net.URI
import java.net.URLDecoder
import java.util.regex.Pattern

object OpenStreetMapParser : MapUrlParser {

    override val serviceName: String = "OpenStreetMap"
    override val supportedHosts: List<String> = listOf("openstreetmap.org", "www.openstreetmap.org", "osm.org")

    private val URL_PATTERN = Pattern.compile(
        """https?://(?:www\.)?(?:openstreetmap\.org|osm\.org)[^\s<>"'()]*""",
        Pattern.CASE_INSENSITIVE
    )

    override fun canParse(url: String): Boolean {
        val lower = url.lowercase().trim()
        return lower.contains("openstreetmap.org") || lower.contains("osm.org")
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

            // 1. Direct Marker coordinates (mlat=lat&mlon=lon)
            val mlat = params["mlat"]?.toDoubleOrNull()
            val mlon = params["mlon"]?.toDoubleOrNull()
            if (mlat != null && mlon != null) {
                val query = params["query"]
                return ParsedLocation.Coordinates(mlat, mlon, label = query)
            }

            // 2. Query parameter
            val query = params["query"] ?: params["q"]
            if (!query.isNullOrBlank()) {
                return ParsedLocation.SearchQuery(query)
            }

            // 3. Map fragment or parameter: #map=zoom/lat/lon or ?map=zoom/lat/lon
            val fragment = uri.fragment ?: ""
            val mapStr = if (fragment.startsWith("map=")) fragment.substring(4) else params["map"]
            if (!mapStr.isNullOrBlank()) {
                val parts = mapStr.split("/")
                if (parts.size >= 3) {
                    val lat = parts[1].toDoubleOrNull()
                    val lon = parts[2].toDoubleOrNull()
                    if (lat != null && lon != null) {
                        return ParsedLocation.Coordinates(lat, lon, label = null)
                    }
                }
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
