package de.goork.mapflip.parser

import java.net.URLDecoder

interface MapUrlParser {
    val serviceName: String
    val supportedHosts: List<String>

    fun canParse(url: String): Boolean
    fun parse(url: String): ParsedLocation
    fun extractUrl(text: String?): String?
}

fun isValidLatLon(lat: Double, lon: Double): Boolean {
    return !lat.isNaN() && !lon.isNaN() && lat in -90.0..90.0 && lon in -180.0..180.0
}

/**
 * Shared query string parser for map URL parsers.
 * Safely URL-decodes keys (lowercased) and values using UTF-8.
 */
fun parseQueryParams(query: String): Map<String, String> {
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

/**
 * Trims trailing punctuation from extracted URLs/URIs while preserving balanced closing parentheses
 * (e.g., `geo:0,0?q=52.52,13.40(Berlin)` keeps its closing `)` whereas `(geo:52.52,13.40)` strips it).
 */
fun trimExtractedUrl(raw: String): String {
    var result = raw.trim()
    while (result.isNotEmpty()) {
        val last = result.last()
        if (last in charArrayOf('.', ',', ';', '!', '?', ']', '>')) {
            result = result.dropLast(1)
        } else if (last == ')') {
            val openCount = result.count { it == '(' }
            val closeCount = result.count { it == ')' }
            if (closeCount > openCount) {
                result = result.dropLast(1)
            } else {
                break
            }
        } else {
            break
        }
    }
    return result
}
