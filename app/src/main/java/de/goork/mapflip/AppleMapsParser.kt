package de.goork.mapflip

import java.net.URI
import java.net.URLDecoder
import java.net.URLEncoder

object AppleMapsParser {
    fun convert(appleUrl: String): String {
        val uri = URI(appleUrl)
        val params = parseQueryParams(uri.rawQuery ?: "")

        params["daddr"]?.let { return "google.navigation:q=${encode(it)}" }

        params["ll"]?.let { ll ->
            val q = params["q"]
            return if (q != null) "geo:$ll?q=${encode(q)}" else "geo:$ll"
        }

        params["q"]?.let { return "geo:0,0?q=${encode(it)}" }
        params["address"]?.let { return "geo:0,0?q=${encode(it)}" }
        params["near"]?.let { return "geo:0,0?q=${encode(it)}" }

        if (uri.path?.contains("place") == true) {
            params["name"]?.let { return "geo:0,0?q=${encode(it)}" }
        }

        return if (!uri.rawQuery.isNullOrBlank()) {
            "https://www.google.com/maps/search/?api=1&query=${encode(uri.rawQuery)}"
        } else {
            "https://www.google.com/maps"
        }
    }

    private fun parseQueryParams(query: String): Map<String, String> {
        if (query.isBlank()) return emptyMap()
        return query.split("&").mapNotNull { param ->
            val parts = param.split("=", limit = 2)
            if (parts.size == 2) {
                URLDecoder.decode(parts[0], "UTF-8") to URLDecoder.decode(parts[1], "UTF-8")
            } else null
        }.toMap()
    }

    private fun encode(value: String): String = URLEncoder.encode(value, "UTF-8")
}
