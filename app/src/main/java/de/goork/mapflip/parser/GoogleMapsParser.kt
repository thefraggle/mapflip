package de.goork.mapflip.parser

import java.net.URI
import java.net.URLDecoder
import java.util.regex.Pattern

/**
 * Parser for Google Maps URLs (maps.google.com, google.com/maps, maps.app.goo.gl, goo.gl/maps).
 * Enables privacy-conscious "De-Google" redirection to FOSS map apps like Organic Maps or OsmAnd.
 */
object GoogleMapsParser : MapUrlParser {

    override val serviceName: String = "Google Maps"
    override val supportedHosts: List<String> = listOf(
        "maps.google.com",
        "google.com",
        "www.google.com",
        "maps.app.goo.gl",
        "goo.gl"
    )

    // Regex for matching Google Maps URLs in text
    private val URL_PATTERN = Pattern.compile(
        """(?:https?://(?:maps\.google\.[a-z.]+|www\.google\.[a-z.]+/maps|google\.[a-z.]+/maps|maps\.app\.goo\.gl|goo\.gl/maps))[^\s<>"'()]*""",
        Pattern.CASE_INSENSITIVE
    )

    // Path coordinates pattern e.g. /@52.520008,13.404954,15z
    private val PATH_COORD_PATTERN = Pattern.compile(
        """@([-+]?[0-9]+\.[0-9]+),([-+]?[0-9]+\.[0-9]+)"""
    )

    override fun canParse(url: String): Boolean {
        val lower = url.lowercase().trim()
        return lower.contains("maps.google.") ||
                lower.contains("google.com/maps") ||
                lower.contains("google.de/maps") ||
                lower.contains("maps.app.goo.gl") ||
                lower.contains("goo.gl/maps")
    }

    override fun extractUrl(text: String?): String? {
        if (text.isNullOrBlank()) return null
        val matcher = URL_PATTERN.matcher(text)
        if (matcher.find()) {
            val url = matcher.group().trimEnd('.', ',', ';', '!', '?', ')')
            if (canParse(url)) return url
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

            // Extract travel mode if present
            val travelModeParam = params["travelmode"]?.lowercase() ?: params["mode"]?.lowercase()
            val travelMode = when (travelModeParam) {
                "walking", "walk", "w" -> TravelMode.WALKING
                "bicycling", "bike", "b" -> TravelMode.BICYCLING
                "transit", "t" -> TravelMode.TRANSIT
                "driving", "drive", "d" -> TravelMode.DRIVING
                else -> null
            }

            // 1. Directions via query parameters (api=1&origin=...&destination=...)
            val originParam = params["origin"]
            val destParam = params["destination"]
            if (!destParam.isNullOrBlank()) {
                val origin = if (!originParam.isNullOrBlank()) originParam else ""
                return if (origin.isNotBlank()) {
                    ParsedLocation.Directions(origin = origin, destination = destParam, mode = travelMode)
                } else {
                    ParsedLocation.Navigation(destination = destParam, mode = travelMode)
                }
            }

            // 2. Directions via path: /maps/dir/Origin/Destination/...
            if (path.contains("/dir/")) {
                val segments = path.split("/").filter { it.isNotBlank() }
                val dirIndex = segments.indexOfFirst { it.equals("dir", ignoreCase = true) }
                if (dirIndex != -1 && segments.size >= dirIndex + 3) {
                    val origin = URLDecoder.decode(segments[dirIndex + 1], "UTF-8")
                    val destination = URLDecoder.decode(segments[dirIndex + 2], "UTF-8")
                    if (!origin.startsWith("@") && !destination.startsWith("@")) {
                        return ParsedLocation.Directions(origin = origin, destination = destination, mode = travelMode)
                    }
                }
            }

            // 3. Query coordinates: q=lat,lon or ll=lat,lon or loc:lat,lon
            val qParam = params["q"] ?: params["query"] ?: params["ll"] ?: params["center"]
            if (!qParam.isNullOrBlank()) {
                val cleanedQ = qParam.replace("loc:", "").replace(" ", "")
                val parts = cleanedQ.split(",")
                if (parts.size >= 2) {
                    val lat = parts[0].toDoubleOrNull()
                    val lon = parts[1].toDoubleOrNull()
                    if (lat != null && lon != null && isValidLatLon(lat, lon)) {
                        return ParsedLocation.Coordinates(lat, lon, mode = travelMode)
                    }
                }
            }

            // 4. Path coordinates e.g. /@52.520008,13.404954,16z
            val coordMatcher = PATH_COORD_PATTERN.matcher(path)
            if (coordMatcher.find()) {
                val lat = coordMatcher.group(1)?.toDoubleOrNull()
                val lon = coordMatcher.group(2)?.toDoubleOrNull()
                if (lat != null && lon != null && isValidLatLon(lat, lon)) {
                    val label = extractPlaceLabel(path) ?: qParam
                    return ParsedLocation.Coordinates(lat, lon, label = label, mode = travelMode)
                }
            }

            // 5. Place name / search query in path: /maps/place/Name
            val placeLabel = extractPlaceLabel(path)
            if (!placeLabel.isNullOrBlank()) {
                return ParsedLocation.SearchQuery(placeLabel)
            }

            // 6. Text query parameter (q=Brandenburger+Tor)
            if (!qParam.isNullOrBlank()) {
                return ParsedLocation.SearchQuery(qParam)
            }

            // 7. Short URLs / Web fallback
            ParsedLocation.WebFallback(normalizedUrl)
        } catch (_: Exception) {
            ParsedLocation.Home
        }
    }

    private fun extractPlaceLabel(path: String): String? {
        if (!path.contains("/place/")) return null
        val segments = path.split("/").filter { it.isNotBlank() }
        val placeIndex = segments.indexOfFirst { it.equals("place", ignoreCase = true) }
        if (placeIndex != -1 && segments.size > placeIndex + 1) {
            val placeSegment = segments[placeIndex + 1]
            return try {
                URLDecoder.decode(placeSegment.replace("+", " "), "UTF-8")
            } catch (_: Exception) {
                placeSegment
            }
        }
        return null
    }

    private fun isValidLatLon(lat: Double, lon: Double): Boolean {
        return lat in -90.0..90.0 && lon in -180.0..180.0
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
