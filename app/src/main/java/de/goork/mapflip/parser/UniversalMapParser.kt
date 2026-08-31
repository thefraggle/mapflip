package de.goork.mapflip.parser

object UniversalMapParser {

    private val parsers: List<MapUrlParser> = listOf(
        AppleMapsParser,
        BingMapsParser,
        OpenStreetMapParser,
        YandexMapsParser,
        HereMapsParser,
        WazeMapsParser
    )

    fun extractMapUrl(text: String?): String? {
        if (text.isNullOrBlank()) return null
        for (parser in parsers) {
            val url = parser.extractUrl(text)
            if (url != null) return url
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
        // Fallback default: Try AppleMapsParser or search query
        return AppleMapsParser.parse(extracted)
    }

    fun detectSourceService(url: String?): String {
        if (url.isNullOrBlank()) return "unknown"
        val lower = url.lowercase()
        return when {
            lower.contains("apple.com") -> "apple"
            lower.contains("bing.com") -> "bing"
            lower.contains("openstreetmap.org") || lower.contains("osm.org") -> "osm"
            lower.contains("yandex.") -> "yandex"
            lower.contains("here.com") -> "here"
            lower.contains("waze.com") -> "waze"
            else -> "other"
        }
    }
}
