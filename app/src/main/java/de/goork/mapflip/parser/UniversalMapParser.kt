package de.goork.mapflip.parser

object UniversalMapParser {

    private val parsers: List<MapUrlParser> = listOf(
        MapyMapsParser,
        AppleMapsParser,
        BingMapsParser,
        OpenStreetMapParser,
        YandexMapsParser,
        HereMapsParser,
        WazeMapsParser,
        GoogleMapsParser,
        DuckDuckGoParser,
        PlusCodeParser,
        GeoCoordinateParser
    )

    fun canParse(url: String?): Boolean {
        if (url.isNullOrBlank()) return false
        val extracted = extractMapUrl(url) ?: url
        return parsers.any { it.canParse(extracted) }
    }

    fun extractMapUrl(text: String?): String? {
        if (text.isNullOrBlank()) return null
        for (parser in parsers) {
            val url = parser.extractUrl(text)
            if (url != null && parser.canParse(url)) return url
        }
        return null
    }

    fun parse(url: String?): ParsedLocation {
        if (url.isNullOrBlank()) return ParsedLocation.Home
        val extracted = extractMapUrl(url) ?: url
        for (parser in parsers) {
            if (parser.canParse(extracted)) {
                return parser.parse(extracted)
            }
        }
        // Fallback default: If valid web URL, return WebFallback; otherwise Home
        return try {
            val uri = java.net.URI(extracted.trim().replace(" ", "%20"))
            if (uri.scheme?.startsWith("http", ignoreCase = true) == true && uri.host != null) {
                ParsedLocation.WebFallback(extracted)
            } else {
                ParsedLocation.Home
            }
        } catch (_: Exception) {
            ParsedLocation.Home
        }
    }

    fun detectParser(url: String?): MapUrlParser? {
        if (url.isNullOrBlank()) return null
        val extracted = extractMapUrl(url) ?: url
        return parsers.firstOrNull { it.canParse(extracted) }
    }

    fun detectSourceService(url: String?): String {
        if (url.isNullOrBlank()) return "unknown"
        val lower = url.lowercase().trim()
        return when {
            MapyMapsParser.canParse(extractMapUrl(url) ?: url) -> "mapy"
            lower.contains("apple.com") || lower.startsWith("applemaps://") -> "apple"
            lower.contains("bing.com") -> "bing"
            lower.contains("openstreetmap.org") || lower.contains("osm.org") -> "osm"
            lower.contains("yandex.") -> "yandex"
            lower.contains("here.com") -> "here"
            lower.contains("waze.com") -> "waze"
            lower.contains("google.") || lower.contains("goo.gl") || GoogleMapsParser.canParse(url) -> "google"
            lower.contains("duckduckgo.com") || DuckDuckGoParser.canParse(url) -> "duckduckgo"
            lower.contains("plus.codes") || PlusCodeParser.canParse(url) -> "plus_code"
            lower.startsWith("geo:") || GeoCoordinateParser.canParse(url) -> "geo_coordinates"
            else -> "other"
        }
    }
}
