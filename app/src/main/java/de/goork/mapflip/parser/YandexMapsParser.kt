package de.goork.mapflip.parser

import java.net.URI
import java.net.URLDecoder
import java.util.regex.Pattern

object YandexMapsParser : MapUrlParser {

    override val serviceName: String = "Yandex Maps"
    override val supportedHosts: List<String> = listOf("yandex.com", "www.yandex.com", "yandex.ru", "www.yandex.ru", "maps.yandex.com", "maps.yandex.ru")

    private val URL_PATTERN = Pattern.compile(
        """https?://(?:www\.)?(?:maps\.)?yandex\.(?:com|ru)/maps[^\s<>"']*""",
        Pattern.CASE_INSENSITIVE
    )

    override fun canParse(url: String): Boolean {
        val lower = url.lowercase().trim()
        return lower.contains("yandex.com/maps") || lower.contains("yandex.ru/maps") || lower.contains("maps.yandex.com") || lower.contains("maps.yandex.ru")
    }

    override fun extractUrl(text: String?): String? {
        if (text.isNullOrBlank()) return null
        val matcher = URL_PATTERN.matcher(text)
        if (matcher.find()) {
            var url = trimExtractedUrl(matcher.group())
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

            val rtt = params["rtt"]?.lowercase()
            val travelMode = when (rtt) {
                "pd" -> TravelMode.WALKING
                "bc" -> TravelMode.BICYCLING
                "mt" -> TravelMode.TRANSIT
                "auto" -> TravelMode.DRIVING
                else -> null
            }

            // 1. Route / rtext=lat,lon~lat,lon (check BEFORE ll to avoid taking viewport center)
            val rtext = params["rtext"]
            if (!rtext.isNullOrBlank()) {
                val legs = rtext.split("~")
                if (legs.size >= 2) {
                    val origin = legs.first().trim()
                    val destination = legs.last().trim()
                    if (origin.isEmpty() && destination.isNotEmpty()) {
                        return ParsedLocation.Navigation(destination = destination, mode = travelMode)
                    } else if (origin.isNotEmpty() && destination.isNotEmpty()) {
                        return ParsedLocation.Directions(origin = origin, destination = destination, mode = travelMode)
                    }
                } else if (legs.size == 1 && legs[0].isNotBlank()) {
                    val target = legs[0].trim()
                    return ParsedLocation.Navigation(destination = target, mode = travelMode)
                }
            }

            // 2. Coordinates: ll=longitude,latitude (NOTE: Yandex puts LONGITUDE first!)
            val ll = params["ll"]
            if (!ll.isNullOrBlank()) {
                val parts = ll.replace(" ", "").split(",")
                if (parts.size == 2) {
                    val lon = parts[0].toDoubleOrNull()
                    val lat = parts[1].toDoubleOrNull()
                    if (lat != null && lon != null && isValidLatLon(lat, lon)) {
                        val text = params["text"]
                        return ParsedLocation.Coordinates(lat, lon, label = text, mode = travelMode)
                    }
                }
            }

            // 3. Search query / text
            val text = params["text"] ?: params["what[where]"]
            if (!text.isNullOrBlank()) {
                return ParsedLocation.SearchQuery(text)
            }

            ParsedLocation.WebFallback(normalizedUrl)
        } catch (_: Exception) {
            ParsedLocation.WebFallback(normalizedUrl)
        }
    }
}
