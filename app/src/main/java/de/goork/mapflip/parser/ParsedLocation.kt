package de.goork.mapflip.parser

enum class TravelMode {
    DRIVING,
    WALKING,
    BICYCLING,
    TRANSIT
}

sealed class ParsedLocation {
    object Home : ParsedLocation()

    data class SearchQuery(
        val query: String
    ) : ParsedLocation()

    data class Coordinates(
        val latitude: Double,
        val longitude: Double,
        val label: String? = null
    ) : ParsedLocation() {
        val latLonString: String get() = "$latitude,$longitude"
    }

    data class Navigation(
        val destination: String,
        val mode: TravelMode? = null
    ) : ParsedLocation()

    data class Directions(
        val origin: String,
        val destination: String,
        val mode: TravelMode? = null
    ) : ParsedLocation()

    data class WebFallback(
        val fallbackUrl: String
    ) : ParsedLocation()
}
