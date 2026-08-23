package de.goork.mapflip.parser

object UniversalMapParser {

    private val parsers: List<MapUrlParser> = listOf(
        AppleMapsParser,
        BingMapsParser,
        OpenStreetMapParser,
        YandexMapsParser
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
}
