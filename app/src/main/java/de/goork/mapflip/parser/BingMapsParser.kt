package de.goork.mapflip.parser

import java.net.URI
import java.net.URLDecoder
import java.util.regex.Pattern

object BingMapsParser : MapUrlParser {

    override val serviceName: String = "Bing Maps"
    override val supportedHosts: List<String> = listOf("bing.com", "www.bing.com", "maps.bing.com")

    private val URL_PATTERN = Pattern.compile(
        """https?://(?:www\.)?bing\.com/maps[^\s<>"'()]*""",
        Pattern.CASE_INSENSITIVE
    )

    override fun canParse(url: String): Boolean {
        val lower = url.lowercase().trim()
        return (lower.contains("bing.com/maps") || lower.contains("maps.bing.com"))
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

            // 1. Directions (rtp=pos.lat_lon_Name~pos.lat_lon_Name or rtp=adr.Berlin~adr.Munich)
            val rtp = params["rtp"]
            if (!rtp.isNullOrBlank()) {
                val legs = rtp.split("~")
                if (legs.size >= 2) {
                    val origin = cleanRtpPoint(legs.first())
                    val destination = cleanRtpPoint(legs.last())
                    val modeParam = params["mode"]?.lowercase()
                    val travelMode = when (modeParam) {
                        "w", "walking" -> TravelMode.WALKING
                        "t", "transit" -> TravelMode.TRANSIT
                        "d", "driving" -> TravelMode.DRIVING
                        else -> null
                    }
                    return ParsedLocation.Directions(origin = origin, destination = destination, mode = travelMode)
                }
            }

            // 2. Center point coordinates (cp=lat~lon or cp=lat_lon)
            val cp = params["cp"]
            if (!cp.isNullOrBlank()) {
                val coords = cp.replace("~", ",").replace("_", ",").replace(" ", "")
                val parts = coords.split(",")
                if (parts.size == 2) {
                    val lat = parts[0].toDoubleOrNull()
                    val lon = parts[1].toDoubleOrNull()
                    if (lat != null && lon != null) {
                        val searchQuery = params["q"] ?: params["where1"]
                        return ParsedLocation.Coordinates(lat, lon, label = searchQuery)
                    }
                }
            }

            // 3. Search query / where1
            val query = params["q"] ?: params["where1"] ?: params["q1"]
            if (!query.isNullOrBlank()) {
                return ParsedLocation.SearchQuery(query)
            }

            ParsedLocation.WebFallback(normalizedUrl)
        } catch (_: Exception) {
            ParsedLocation.Home
        }
    }

    private fun cleanRtpPoint(point: String): String {
        var clean = point
        if (clean.startsWith("pos.", ignoreCase = true) || clean.startsWith("adr.", ignoreCase = true)) {
            clean = clean.substring(4)
        }
        return clean.replace("_", ", ")
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
