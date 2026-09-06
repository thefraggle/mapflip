package de.goork.mapflip.parser

import java.net.URLDecoder
import java.util.Locale
import java.util.regex.Pattern

/**
 * Parser for geo: URIs (RFC 5870) and raw coordinates (Decimal Degrees & DMS)
 * found in text, messenger chats, or the clipboard.
 */
object GeoCoordinateParser : MapUrlParser {

    override val serviceName: String = "Geo / Coordinates"
    override val supportedHosts: List<String> = emptyList()

    // 1. geo: URI Pattern (e.g. geo:52.52,13.40?q=Berlin or geo:0,0?q=52.52,13.40)
    private val GEO_URI_PATTERN = Pattern.compile(
        """\bgeo:[-+]?[0-9]+(?:\.[0-9]+)?(?:,[-+]?[0-9]+(?:\.[0-9]+)?)?(?:[^\s<>"'()]*[^\s<>"'().,;:!])?""",
        Pattern.CASE_INSENSITIVE
    )

    // 2. DMS Pattern (e.g. 52°31'12.5"N, 13°24'18.2"E or 52° 31' 12" N 13° 24' 18" E)
    // Supports °, d, ', m, ′, ", s, ″
    private val DMS_PATTERN = Pattern.compile(
        """([0-9]{1,3})\s*[°ddeg\s]\s*([0-9]{1,2})\s*['′m\s]\s*([0-9]{1,2}(?:\.[0-9]+)?)\s*["″s]?\s*([NSEWnsew])\s*[,;\s]\s*([0-9]{1,3})\s*[°ddeg\s]\s*([0-9]{1,2})\s*['′m\s]\s*([0-9]{1,2}(?:\.[0-9]+)?)\s*["″s]?\s*([NSEWnsew])"""
    )

    // 3. Decimal Degrees with directional indicators (e.g. 52.5200° N, 13.4050° E or 52.5200N, 13.4050E)
    private val DD_DIRECTIONAL_PATTERN = Pattern.compile(
        """([0-9]{1,2}(?:\.[0-9]+)?)\s*°?\s*([NSEWnsew])\s*[,;\s]\s*([0-9]{1,3}(?:\.[0-9]+)?)\s*°?\s*([NSEWnsew])"""
    )

    // 4. Raw Decimal Degrees with comma or semicolon separator (require at least 3 decimal places to avoid false positives like prices 12.50, 14.50)
    private val DD_PAIR_PATTERN = Pattern.compile(
        """(?<![0-9.])([-+]?[0-9]{1,2}\.[0-9]{3,10})\s*[,;]\s*([-+]?[0-9]{1,3}\.[0-9]{3,10})(?![0-9.])"""
    )

    override fun canParse(url: String): Boolean {
        val trimmed = url.trim()
        if (trimmed.startsWith("geo:", ignoreCase = true)) return true
        return tryParseCoordinates(trimmed) != null
    }

    override fun extractUrl(text: String?): String? {
        if (text.isNullOrBlank()) return null

        // 1. Check for explicit geo: URI
        val geoMatcher = GEO_URI_PATTERN.matcher(text)
        if (geoMatcher.find()) {
            val uri = geoMatcher.group().trimEnd('.', ',', ';', '!', '?', ')')
            if (canParse(uri)) return uri
        }

        // 2. Check for DMS coordinates
        val dmsMatcher = DMS_PATTERN.matcher(text)
        if (dmsMatcher.find()) {
            val lat = parseDmsComponent(
                dmsMatcher.group(1), dmsMatcher.group(2), dmsMatcher.group(3), dmsMatcher.group(4)
            )
            val lon = parseDmsComponent(
                dmsMatcher.group(5), dmsMatcher.group(6), dmsMatcher.group(7), dmsMatcher.group(8)
            )
            if (isValidLatLon(lat, lon)) {
                return formatGeoUri(lat, lon)
            }
        }

        // 3. Check for directional decimal degrees (e.g. 52.52N, 13.40E)
        val ddDirMatcher = DD_DIRECTIONAL_PATTERN.matcher(text)
        if (ddDirMatcher.find()) {
            val val1 = ddDirMatcher.group(1)?.toDoubleOrNull()
            val dir1 = ddDirMatcher.group(2)
            val val2 = ddDirMatcher.group(3)?.toDoubleOrNull()
            val dir2 = ddDirMatcher.group(4)
            if (val1 != null && val2 != null && dir1 != null && dir2 != null) {
                val (lat, lon) = resolveDirectional(val1, dir1, val2, dir2)
                if (isValidLatLon(lat, lon)) {
                    return formatGeoUri(lat, lon)
                }
            }
        }

        // 4. Check for standard decimal degree pair (e.g. 52.520008, 13.404954)
        val ddMatcher = DD_PAIR_PATTERN.matcher(text)
        if (ddMatcher.find()) {
            val lat = ddMatcher.group(1)?.toDoubleOrNull()
            val lon = ddMatcher.group(2)?.toDoubleOrNull()
            if (lat != null && lon != null && isValidLatLon(lat, lon)) {
                return formatGeoUri(lat, lon)
            }
        }

        return null
    }

    override fun parse(url: String): ParsedLocation {
        val trimmed = url.trim()
        if (trimmed.startsWith("geo:", ignoreCase = true)) {
            return parseGeoUri(trimmed)
        }

        val coords = tryParseCoordinates(trimmed)
        if (coords != null) {
            return ParsedLocation.Coordinates(coords.first, coords.second)
        }

        return ParsedLocation.Home
    }

    private fun parseGeoUri(geoUri: String): ParsedLocation {
        // Format: geo:lat,lon?q=... or geo:0,0?q=lat,lon(label) or geo:0,0?q=query
        val afterScheme = geoUri.substring(4).trim()
        val queryIndex = afterScheme.indexOf('?')

        val pathPart = if (queryIndex != -1) afterScheme.substring(0, queryIndex) else afterScheme
        val queryPart = if (queryIndex != -1) afterScheme.substring(queryIndex + 1) else ""

        var queryParam: String? = null
        if (queryPart.isNotBlank()) {
            for (param in queryPart.split("&")) {
                val kv = param.split("=", limit = 2)
                if (kv.isNotEmpty() && kv[0].equals("q", ignoreCase = true)) {
                    queryParam = if (kv.size == 2) {
                        try { URLDecoder.decode(kv[1], "UTF-8") } catch (_: Exception) { kv[1] }
                    } else ""
                }
            }
        }

        // If q param exists, check if it contains coordinates or label
        if (!queryParam.isNullOrBlank()) {
            // Check for format: lat,lon(label) or lat,lon
            val labelPattern = Pattern.compile("""^([-+]?[0-9]+(?:\.[0-9]+)?)\s*,\s*([-+]?[0-9]+(?:\.[0-9]+)?)(?:\((.*)\))?$""")
            val labelMatcher = labelPattern.matcher(queryParam.trim())
            if (labelMatcher.matches()) {
                val qLat = labelMatcher.group(1)?.toDoubleOrNull()
                val qLon = labelMatcher.group(2)?.toDoubleOrNull()
                val label = labelMatcher.group(3)?.trim()
                if (qLat != null && qLon != null && isValidLatLon(qLat, qLon)) {
                    return ParsedLocation.Coordinates(qLat, qLon, label = if (label.isNullOrBlank()) null else label)
                }
            }

            // If coordinates were in pathPart, use them and attach queryParam as label
            val pathCoords = parsePathCoords(pathPart)
            if (pathCoords != null && (pathCoords.first != 0.0 || pathCoords.second != 0.0)) {
                return ParsedLocation.Coordinates(pathCoords.first, pathCoords.second, label = queryParam)
            }

            // Pure search query
            return ParsedLocation.SearchQuery(queryParam)
        }

        // No query param: use path coordinates
        val pathCoords = parsePathCoords(pathPart)
        if (pathCoords != null && isValidLatLon(pathCoords.first, pathCoords.second)) {
            return ParsedLocation.Coordinates(pathCoords.first, pathCoords.second)
        }

        return ParsedLocation.Home
    }

    private fun parsePathCoords(path: String): Pair<Double, Double>? {
        val parts = path.split(",")
        if (parts.size >= 2) {
            val lat = parts[0].trim().toDoubleOrNull()
            // Longitude may contain ;u=uncertainty or ;crs=
            val lonPart = parts[1].trim().split(";", limit = 2)[0].trim()
            val lon = lonPart.toDoubleOrNull()
            if (lat != null && lon != null) {
                return Pair(lat, lon)
            }
        }
        return null
    }

    private fun tryParseCoordinates(text: String): Pair<Double, Double>? {
        // Check DMS
        val dms = DMS_PATTERN.matcher(text)
        if (dms.matches()) {
            val lat = parseDmsComponent(dms.group(1), dms.group(2), dms.group(3), dms.group(4))
            val lon = parseDmsComponent(dms.group(5), dms.group(6), dms.group(7), dms.group(8))
            if (isValidLatLon(lat, lon)) return Pair(lat, lon)
        }

        // Check Directional DD
        val ddDir = DD_DIRECTIONAL_PATTERN.matcher(text)
        if (ddDir.matches()) {
            val v1 = ddDir.group(1)?.toDoubleOrNull()
            val d1 = ddDir.group(2)
            val v2 = ddDir.group(3)?.toDoubleOrNull()
            val d2 = ddDir.group(4)
            if (v1 != null && v2 != null && d1 != null && d2 != null) {
                val (lat, lon) = resolveDirectional(v1, d1, v2, d2)
                if (isValidLatLon(lat, lon)) return Pair(lat, lon)
            }
        }

        // Check DD Pair
        val dd = DD_PAIR_PATTERN.matcher(text)
        if (dd.matches()) {
            val lat = dd.group(1)?.toDoubleOrNull()
            val lon = dd.group(2)?.toDoubleOrNull()
            if (lat != null && lon != null && isValidLatLon(lat, lon)) return Pair(lat, lon)
        }

        return null
    }

    private fun parseDmsComponent(degStr: String?, minStr: String?, secStr: String?, hemiStr: String?): Double {
        val deg = degStr?.toDoubleOrNull() ?: 0.0
        val min = minStr?.toDoubleOrNull() ?: 0.0
        val sec = secStr?.toDoubleOrNull() ?: 0.0
        val decimal = deg + (min / 60.0) + (sec / 3600.0)
        val hemi = hemiStr?.trim()?.uppercase(Locale.US) ?: "N"
        return if (hemi == "S" || hemi == "W") -decimal else decimal
    }

    private fun resolveDirectional(val1: Double, dir1: String, val2: Double, dir2: String): Pair<Double, Double> {
        val d1 = dir1.trim().uppercase(Locale.US)
        val d2 = dir2.trim().uppercase(Locale.US)
        var lat = 0.0
        var lon = 0.0

        if (d1 == "N" || d1 == "S") {
            lat = if (d1 == "S") -val1 else val1
            lon = if (d2 == "W") -val2 else val2
        } else {
            lon = if (d1 == "W") -val1 else val1
            lat = if (d2 == "S") -val2 else val2
        }
        return Pair(lat, lon)
    }

    private fun isValidLatLon(lat: Double, lon: Double): Boolean {
        return lat in -90.0..90.0 && lon in -180.0..180.0
    }

    private fun formatGeoUri(lat: Double, lon: Double): String {
        return "geo:%.6f,%.6f".format(Locale.US, lat, lon)
    }
}
