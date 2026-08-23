package de.goork.mapflip.parser

import java.net.URI
import java.net.URLDecoder
import java.util.regex.Pattern

object YandexMapsParser : MapUrlParser {

    override val serviceName: String = "Yandex Maps"
    override val supportedHosts: List<String> = listOf("yandex.com", "www.yandex.com", "yandex.ru", "www.yandex.ru", "maps.yandex.com", "maps.yandex.ru")

    private val URL_PATTERN = Pattern.compile(
        """https?://(?:www\.)?(?:maps\.)?yandex\.(?:com|ru)/maps[^\s<>"'()]*""",
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

            // 1. Coordinates: ll=longitude,latitude (NOTE: Yandex puts LONGITUDE first!)
            val ll = params["ll"]
            if (!ll.isNullOrBlank()) {
                val parts = ll.replace(" ", "").split(",")
                if (parts.size == 2) {
                    val lon = parts[0].toDoubleOrNull()
                    val lat = parts[1].toDoubleOrNull()
                    if (lat != null && lon != null) {
                        val text = params["text"]
                        return ParsedLocation.Coordinates(lat, lon, label = text)
                    }
                }
            }

            // 2. Search query / text
            val text = params["text"] ?: params["what[where]"]
            if (!text.isNullOrBlank()) {
                return ParsedLocation.SearchQuery(text)
            }

            // 3. Route / rtext=lat,lon~lat,lon
            val rtext = params["rtext"]
            if (!rtext.isNullOrBlank()) {
                val legs = rtext.split("~")
                if (legs.size >= 2) {
                    return ParsedLocation.Directions(origin = legs.first(), destination = legs.last(), mode = null)
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
