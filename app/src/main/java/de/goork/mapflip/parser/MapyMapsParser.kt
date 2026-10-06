package de.goork.mapflip.parser

import java.net.URI
import java.net.URLDecoder

/**
 * Parser for Mapy.com / Mapy.cz URLs.
 *
 * Important: In Mapy URLs, coordinate pairs use longitude first (x=lon, y=lat or lon,lat).
 * Written by Martin Malec (brozkeff) and integrated into upstream MapFlip.
 */
object MapyMapsParser : MapUrlParser {
    override val serviceName = "Mapy.com"
    override val supportedHosts = listOf("mapy.com", "www.mapy.com", "mapy.cz", "www.mapy.cz")
    private val urlPattern = Regex("""https?://(?:www\.)?mapy\.(?:com|cz)(?=[/\s?#]|$)[^\s<>"']*""", RegexOption.IGNORE_CASE)

    override fun canParse(url: String): Boolean = try {
        val uri = URI(url.trim().replace(" ", "%20"))
        uri.scheme?.lowercase() in listOf("http", "https") &&
            uri.host?.lowercase() in supportedHosts && uri.rawUserInfo == null &&
            (uri.port == -1 || uri.port == 443 || uri.port == 80)
    } catch (_: Exception) { false }

    override fun extractUrl(text: String?): String? = text?.let {
        urlPattern.find(it)?.value?.trimEnd('.', ',', ';', '!', ')', ']', '>')
    }

    override fun parse(url: String): ParsedLocation {
        val extracted = extractUrl(url) ?: url.trim()
        val fallback = ParsedLocation.WebFallback(extracted)
        if (!canParse(extracted)) return fallback
        return try {
            val uri = URI(extracted.replace(" ", "%20"))
            val params = (uri.rawQuery ?: "").split('&').filter { it.isNotBlank() }.associate {
                val parts = it.split('=', limit = 2)
                URLDecoder.decode(parts[0], "UTF-8") to URLDecoder.decode(parts.getOrElse(1) { "" }, "UTF-8").trim()
            }
            // Short links cannot be resolved offline: preserve the original URL for web fallback.
            if (uri.path.startsWith("/s/")) return fallback
            if (uri.path.endsWith("/route")) {
                // ParsedLocation cannot represent intermediate stops: preserve the original route.
                if (!params["waypoints"].isNullOrBlank()) return fallback
                val end = coordinates(params["end"]) ?: return fallback
                val mode = when {
                    params["routeType"]?.startsWith("foot_") == true -> TravelMode.WALKING
                    params["routeType"]?.startsWith("bike_") == true -> TravelMode.BICYCLING
                    else -> TravelMode.DRIVING
                }
                val start = coordinates(params["start"])
                if (!params["start"].isNullOrBlank() && start == null) return fallback
                return if (start == null) ParsedLocation.Navigation(end.latLonString, mode)
                else ParsedLocation.Directions(start.latLonString, end.latLonString, mode)
            }
            // Legacy encoded routes and object IDs are not map-center destinations.
            if (params.keys.any { it in listOf("rc", "planovani-trasy", "dim", "vlastni-body", "pano", "pid", "gallery") }) return fallback
            if (params["source"] == "coor") return coordinates(params["id"]) ?: fallback
            if (!params["source"].isNullOrBlank() || !params["id"].isNullOrBlank()) return fallback
            val query = params["query"] ?: params["q"]
            if (!query.isNullOrBlank()) return ParsedLocation.SearchQuery(query)
            if (params.containsKey("center")) return coordinates(params["center"]) ?: fallback
            if (params.containsKey("x") || params.containsKey("y")) {
                return coordinates("${params["x"]},${params["y"]}") ?: fallback
            }
            fallback
        } catch (_: Exception) { fallback }
    }

    private fun coordinates(value: String?): ParsedLocation.Coordinates? {
        val parts = value?.split(',') ?: return null
        if (parts.size != 2) return null
        val lon = parts[0].trim().toDoubleOrNull() ?: return null
        val lat = parts[1].trim().toDoubleOrNull() ?: return null
        return if (isValidLatLon(lat, lon)) ParsedLocation.Coordinates(lat, lon) else null
    }
}
