package de.goork.mapflip.parser

import java.util.Locale
import java.util.regex.Pattern

/**
 * Pure offline decoder and parser for Open Location Codes (Google Plus Codes).
 * Complies with the Open Location Code (OLC) specification.
 */
object PlusCodeParser : MapUrlParser {

    override val serviceName: String = "Plus Code"
    override val supportedHosts: List<String> = listOf("plus.codes")

    private const val CODE_ALPHABET = "23456789CFGHJMPQRVWX"
    private const val SEPARATOR = '+'
    private const val SEPARATOR_POSITION = 8

    // URL pattern (e.g. https://plus.codes/8FW4V75V+8G or plus.codes/9F42C2M2+X7)
    private val URL_PATTERN = Pattern.compile(
        """(?:https?://(?:www\.)?plus\.codes/|plus\.codes/)([23456789CFGHJMPQRVWX]{4,8}\+[23456789CFGHJMPQRVWX]{2,6})""",
        Pattern.CASE_INSENSITIVE
    )

    // Standalone full Plus Code pattern (e.g. 8FW4V75V+8G or 9F42C2M2+X7)
    private val STANDALONE_PATTERN = Pattern.compile(
        """\b([23456789CFGHJMPQRVWX]{8}\+[23456789CFGHJMPQRVWX]{2,6})\b""",
        Pattern.CASE_INSENSITIVE
    )

    override fun canParse(url: String): Boolean {
        val trimmed = url.trim()
        if (trimmed.contains("plus.codes", ignoreCase = true)) return true
        val upper = trimmed.uppercase(Locale.US)
        return isFullCode(upper)
    }

    override fun extractUrl(text: String?): String? {
        if (text.isNullOrBlank()) return null

        // 1. Check for plus.codes URL
        val urlMatcher = URL_PATTERN.matcher(text)
        if (urlMatcher.find()) {
            val code = urlMatcher.group(1)?.uppercase(Locale.US)
            if (code != null && isFullCode(code)) {
                return "https://plus.codes/$code"
            }
        }

        // 2. Check for standalone full Plus Code
        val standaloneMatcher = STANDALONE_PATTERN.matcher(text)
        if (standaloneMatcher.find()) {
            val code = standaloneMatcher.group(1)?.uppercase(Locale.US)
            if (code != null && isFullCode(code)) {
                return "https://plus.codes/$code"
            }
        }

        return null
    }

    override fun parse(url: String): ParsedLocation {
        val rawCode = extractCode(url) ?: return ParsedLocation.Home
        val coords = decode(rawCode) ?: return ParsedLocation.Home
        return ParsedLocation.Coordinates(
            latitude = coords.first,
            longitude = coords.second,
            label = rawCode
        )
    }

    /**
     * Decodes a full Plus Code string into latitude and longitude coordinates.
     * Returns null if the code is invalid or not a full global code.
     */
    fun decode(code: String): Pair<Double, Double>? {
        val upper = code.trim().uppercase(Locale.US)
        if (!isFullCode(upper)) return null

        val cleanCode = upper.replace("+", "")
        if (cleanCode.length < 8) return null

        var lat = 0.0
        var lon = 0.0
        var latRes = 20.0
        var lonRes = 20.0

        // Pair 1 (chars 0, 1): resolution 20.0°
        val v0 = charValue(cleanCode[0]) ?: return null
        val v1 = charValue(cleanCode[1]) ?: return null
        lat += v0 * 20.0
        lon += v1 * 20.0

        // Pair 2 (chars 2, 3): resolution 1.0°
        val v2 = charValue(cleanCode[2]) ?: return null
        val v3 = charValue(cleanCode[3]) ?: return null
        lat += v2 * 1.0
        lon += v3 * 1.0
        latRes = 1.0
        lonRes = 1.0

        // Pair 3 (chars 4, 5): resolution 0.05°
        val v4 = charValue(cleanCode[4]) ?: return null
        val v5 = charValue(cleanCode[5]) ?: return null
        lat += v4 * 0.05
        lon += v5 * 0.05
        latRes = 0.05
        lonRes = 0.05

        // Pair 4 (chars 6, 7): resolution 0.0025°
        val v6 = charValue(cleanCode[6]) ?: return null
        val v7 = charValue(cleanCode[7]) ?: return null
        lat += v6 * 0.0025
        lon += v7 * 0.0025
        latRes = 0.0025
        lonRes = 0.0025

        // Pair 5 (chars 8, 9) if present: resolution 0.000125°
        if (cleanCode.length >= 10) {
            val v8 = charValue(cleanCode[8]) ?: return null
            val v9 = charValue(cleanCode[9]) ?: return null
            lat += v8 * 0.000125
            lon += v9 * 0.000125
            latRes = 0.000125
            lonRes = 0.000125
        }

        // Sub-grid refinement for chars 10+ (each char refines 5 rows x 4 cols)
        if (cleanCode.length > 10) {
            for (i in 10 until cleanCode.length) {
                val cv = charValue(cleanCode[i]) ?: return null
                val row = cv / 4
                val col = cv % 4
                latRes /= 5.0
                lonRes /= 4.0
                lat += row * latRes
                lon += col * lonRes
            }
        }

        // Convert south-west corner to center of bounding box
        val centerLat = lat - 90.0 + (latRes / 2.0)
        val centerLon = lon - 180.0 + (lonRes / 2.0)

        return Pair(centerLat, centerLon)
    }

    private fun extractCode(input: String): String? {
        val trimmed = input.trim()
        val urlMatcher = URL_PATTERN.matcher(trimmed)
        if (urlMatcher.find()) {
            return urlMatcher.group(1)?.uppercase(Locale.US)
        }
        val standaloneMatcher = STANDALONE_PATTERN.matcher(trimmed)
        if (standaloneMatcher.find()) {
            return standaloneMatcher.group(1)?.uppercase(Locale.US)
        }
        val upper = trimmed.uppercase(Locale.US)
        return if (isFullCode(upper)) upper else null
    }

    private fun isFullCode(code: String): Boolean {
        val sepIndex = code.indexOf(SEPARATOR)
        if (sepIndex != SEPARATOR_POSITION) return false
        val clean = code.replace("+", "")
        if (clean.length < 10) return false
        return clean.all { it in CODE_ALPHABET }
    }

    private fun charValue(c: Char): Int? {
        val idx = CODE_ALPHABET.indexOf(c)
        return if (idx >= 0) idx else null
    }
}
