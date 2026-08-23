package de.goork.mapflip

import de.goork.mapflip.navigation.NavigationIntentBuilder
import de.goork.mapflip.parser.AppleMapsParser
import de.goork.mapflip.parser.UniversalMapParser

/**
 * Backward compatibility facade for [de.goork.mapflip.parser.AppleMapsParser].
 */
object AppleMapsParser {

    fun extractMapUrl(text: String?): String? = UniversalMapParser.extractMapUrl(text)

    fun convert(appleUrl: String?): String {
        val location = UniversalMapParser.parse(appleUrl)
        return NavigationIntentBuilder.buildGoogleMapsUriString(location)
    }
}
