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
        """(?:https?://(?:maps\.google\.[a-z.]+|www\.google\.[a-z.]+/maps|google\.[a-z.]+/maps|maps\.app\.goo\.gl|goo\.gl/maps))[^\s<>"']*""",
        Pattern.CASE_INSENSITIVE
    )

    // Path coordinates pattern e.g. /@52.520008,13.404954,15z
    private val PATH_COORD_PATTERN = Pattern.compile(
        """@([-+]?[0-9]+\.[0-9]+),([-+]?[0-9]+\.[0-9]+)"""
    )

    // Data-parameter pin coordinates pattern e.g. !3d52.520008!4d13.404954
    private val PIN_COORD_PATTERN = Pattern.compile(
        """!3d([-+]?[0-9]+\.[0-9]+)!4d([-+]?[0-9]+\.[0-9]+)"""
    )

    private val CAN_PARSE_PATTERN = Pattern.compile(
        """(?:maps\.google\.[a-z.]+|google\.[a-z.]+/maps|maps\.app\.goo\.gl|goo\.gl/maps)""",
        Pattern.CASE_INSENSITIVE
    )

    override fun canParse(url: String): Boolean {
        val lower = url.lowercase().trim()
        return CAN_PARSE_PATTERN.matcher(lower).find()
    }

    override fun extractUrl(text: String?): String? {
        if (text.isNullOrBlank()) return null
        val matcher = URL_PATTERN.matcher(text)
        if (matcher.find()) {
            val url = trimExtractedUrl(matcher.group())
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
            val rawPath = uri.rawPath ?: uri.path ?: ""
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
            if (rawPath.contains("/dir/")) {
                val rawSegments = rawPath.split("/").filter { it.isNotBlank() }
                val dirIndex = rawSegments.indexOfFirst { it.equals("dir", ignoreCase = true) }
                if (dirIndex != -1 && rawSegments.size >= dirIndex + 3) {
                    val origin = decodePathSegment(rawSegments[dirIndex + 1])
                    val destination = decodePathSegment(rawSegments[dirIndex + 2])
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

            // 4. Pin coordinates in path data e.g. !3d52.520008!4d13.404954 (precise POI location)
            val pinMatcher = PIN_COORD_PATTERN.matcher(rawPath)
            if (pinMatcher.find()) {
                val lat = pinMatcher.group(1)?.toDoubleOrNull()
                val lon = pinMatcher.group(2)?.toDoubleOrNull()
                if (lat != null && lon != null && isValidLatLon(lat, lon)) {
                    val label = extractPlaceLabel(rawPath) ?: qParam
                    return ParsedLocation.Coordinates(lat, lon, label = label, mode = travelMode)
                }
            }

            // 5. Path coordinates e.g. /@52.520008,13.404954,16z
            val coordMatcher = PATH_COORD_PATTERN.matcher(rawPath)
            if (coordMatcher.find()) {
                val lat = coordMatcher.group(1)?.toDoubleOrNull()
                val lon = coordMatcher.group(2)?.toDoubleOrNull()
                if (lat != null && lon != null && isValidLatLon(lat, lon)) {
                    val label = extractPlaceLabel(rawPath) ?: qParam
                    return ParsedLocation.Coordinates(lat, lon, label = label, mode = travelMode)
                }
            }

            // 6. Place name / search query in path: /maps/place/Name
            val placeLabel = extractPlaceLabel(rawPath)
            if (!placeLabel.isNullOrBlank()) {
                return ParsedLocation.SearchQuery(placeLabel)
            }

            // 7. Text query parameter (q=Brandenburger+Tor)
            if (!qParam.isNullOrBlank()) {
                return ParsedLocation.SearchQuery(qParam)
            }

            // 8. Short URLs / Web fallback
            ParsedLocation.WebFallback(normalizedUrl)
        } catch (_: Exception) {
            ParsedLocation.WebFallback(normalizedUrl)
        }
    }

    private fun decodePathSegment(segment: String): String {
        return try {
            URLDecoder.decode(segment, "UTF-8")
        } catch (_: Exception) {
            segment
        }
    }

    private fun extractPlaceLabel(path: String): String? {
        if (!path.contains("/place/")) return null
        val segments = path.split("/").filter { it.isNotBlank() }
        val placeIndex = segments.indexOfFirst { it.equals("place", ignoreCase = true) }
        if (placeIndex != -1 && segments.size > placeIndex + 1) {
            val placeSegment = segments[placeIndex + 1]
            if (placeSegment.startsWith("@")) return null
            return decodePathSegment(placeSegment)
        }
        return null
    }
}
