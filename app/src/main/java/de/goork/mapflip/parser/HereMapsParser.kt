package de.goork.mapflip.parser

import java.net.URI
import java.net.URLDecoder
import java.util.regex.Pattern

object HereMapsParser : MapUrlParser {

    override val serviceName: String = "HERE WeGo"
    override val supportedHosts: List<String> = listOf("wego.here.com", "share.here.com", "maps.here.com", "here.com")

    private val URL_PATTERN = Pattern.compile(
        """https?://(?:(?:wego|share|maps)\.here\.com|here\.com/[^\s<>"'()]+)[^\s<>"'()]*""",
        Pattern.CASE_INSENSITIVE
    )

    private val SHARE_COORD_PATTERN = Pattern.compile(
        """/(?:l|r|location)/(-?\d+(?:\.\d+)?),(-?\d+(?:\.\d+)?)""",
        Pattern.CASE_INSENSITIVE
    )

    override fun canParse(url: String): Boolean {
        val lower = url.lowercase().trim()
        return lower.contains("wego.here.com") || lower.contains("share.here.com") || lower.contains("maps.here.com") || lower.contains("here.com/")
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
            val path = uri.path ?: ""
            val params = parseQueryParams(uri.rawQuery ?: "")

            // 1. Path-based coordinates e.g. /l/52.5200,13.4050,16,Berlin or /r/52.52,13.40
            val pathCoordMatcher = SHARE_COORD_PATTERN.matcher(path)
            if (pathCoordMatcher.find()) {
                val lat = pathCoordMatcher.group(1)?.toDoubleOrNull()
                val lon = pathCoordMatcher.group(2)?.toDoubleOrNull()
                if (lat != null && lon != null) {
                    val label = params["msg"] ?: params["q"]
                    return ParsedLocation.Coordinates(lat, lon, label = label)
                }
            }

            // 2. Query/Param map=lat,lon or map=lat,lon,zoom,type
            val mapParam = params["map"]
            if (!mapParam.isNullOrBlank()) {
                val parts = mapParam.split(",")
                if (parts.size >= 2) {
                    val lat = parts[0].toDoubleOrNull()
                    val lon = parts[1].toDoubleOrNull()
                    if (lat != null && lon != null) {
                        val label = params["msg"] ?: params["q"]
                        return ParsedLocation.Coordinates(lat, lon, label = label)
                    }
                }
            }

            // 3. Directions in path e.g. /directions/drive/Origin/Destination or /directions/mix/Origin/Destination
            if (path.contains("/directions/")) {
                val segments = path.split("/").filter { it.isNotBlank() }
                val dirIndex = segments.indexOfFirst { it.equals("directions", ignoreCase = true) }
                if (dirIndex != -1 && segments.size >= dirIndex + 3) {
                    val modePart = segments.getOrNull(dirIndex + 1)?.lowercase()
                    val origin = URLDecoder.decode(segments[dirIndex + 2], "UTF-8")
                    val destination = if (segments.size >= dirIndex + 4) URLDecoder.decode(segments[dirIndex + 3], "UTF-8") else null
                    val travelMode = when (modePart) {
                        "walk", "pedestrian" -> TravelMode.WALKING
                        "bicycle", "bike" -> TravelMode.BICYCLING
                        "publicTransport", "transit" -> TravelMode.TRANSIT
                        "drive", "car" -> TravelMode.DRIVING
                        else -> null
                    }
                    if (destination != null) {
                        return ParsedLocation.Directions(origin = origin, destination = destination, mode = travelMode)
                    } else {
                        return ParsedLocation.Navigation(destination = origin, mode = travelMode)
                    }
                }
            }

            // 4. Search query in path e.g. /search/Berlin
            if (path.contains("/search/")) {
                val queryPart = path.substringAfter("/search/").substringBefore("/")
                if (queryPart.isNotBlank()) {
                    return ParsedLocation.SearchQuery(URLDecoder.decode(queryPart, "UTF-8"))
                }
            }

            // 5. Query parameter q
            val query = params["q"] ?: params["search"]
            if (!query.isNullOrBlank()) {
                return ParsedLocation.SearchQuery(query)
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
