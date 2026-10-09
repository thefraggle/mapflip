package de.goork.mapflip.parser

import de.goork.mapflip.navigation.NavigationIntentBuilder
import org.junit.Assert.assertEquals
import org.junit.Test

class AppleMapsParserTest {

    private fun convert(url: String?): String {
        val location = UniversalMapParser.parse(url)
        return NavigationIntentBuilder.buildGoogleMapsUriString(location)
    }

    private fun extractMapUrl(text: String?): String? = UniversalMapParser.extractMapUrl(text)

    @Test
    fun `converts search query`() {
        assertEquals("geo:0,0?q=Eiffelturm",
            convert("https://maps.apple.com/?q=Eiffelturm"))
    }

    @Test
    fun `converts coordinates`() {
        assertEquals("geo:48.858400,2.294500?q=48.858400,2.294500",
            convert("https://maps.apple.com/?ll=48.8584,2.2945"))
    }

    @Test
    fun `converts coordinates with query`() {
        assertEquals("geo:48.858400,2.294500?q=Eiffelturm",
            convert("https://maps.apple.com/?ll=48.8584,2.2945&q=Eiffelturm"))
    }

    @Test
    fun `converts address`() {
        assertEquals("geo:0,0?q=Berlin",
            convert("https://maps.apple.com/?address=Berlin"))
    }

    @Test
    fun `converts auid parameter as search query`() {
        assertEquals("geo:0,0?q=1234567890",
            convert("https://maps.apple.com/?auid=1234567890"))
    }

    @Test
    fun `converts directions`() {
        assertEquals("google.navigation:q=Munich",
            convert("https://maps.apple.com/?daddr=Munich"))
    }

    @Test
    fun `converts near parameter`() {
        assertEquals("geo:0,0?q=Hamburg",
            convert("https://maps.apple.com/?near=Hamburg"))
    }

    @Test
    fun `fallback for empty URL`() {
        assertEquals("https://www.google.com/maps",
            convert("https://maps.apple.com/"))
    }

    @Test
    fun `handles null or blank URL`() {
        assertEquals("https://www.google.com/maps", convert(null))
        assertEquals("https://www.google.com/maps", convert(""))
        assertEquals("https://www.google.com/maps", convert("   "))
    }

    @Test
    fun `handles URL without scheme`() {
        assertEquals("geo:0,0?q=Hamburg",
            convert("maps.apple.com/?q=Hamburg"))
    }

    @Test
    fun `converts directions with both origin and destination`() {
        assertEquals("https://www.google.com/maps/dir/?api=1&origin=Berlin&destination=Munich",
            convert("https://maps.apple.com/?saddr=Berlin&daddr=Munich"))
    }

    @Test
    fun `converts start address only`() {
        assertEquals("geo:0,0?q=Frankfurt",
            convert("https://maps.apple.com/?saddr=Frankfurt"))
    }

    @Test
    fun `converts pt coordinate parameter`() {
        assertEquals("geo:52.520000,13.405000?q=52.520000,13.405000",
            convert("https://maps.apple.com/?pt=52.5200,13.4050"))
    }

    @Test
    fun `converts pt coordinate parameter with query`() {
        assertEquals("geo:52.520000,13.405000?q=TV+Tower",
            convert("https://maps.apple.com/?pt=52.5200,13.4050&q=TV+Tower"))
    }

    @Test
    fun `handles case-insensitive query parameters`() {
        assertEquals("google.navigation:q=Hamburg",
            convert("https://maps.apple.com/?DADDR=Hamburg"))
        assertEquals("geo:0,0?q=Cologne",
            convert("https://maps.apple.com/?Q=Cologne"))
    }

    @Test
    fun `converts short link with place ID`() {
        val shortUrl = "https://maps.apple.com/p/dtcGHQZ--4bUSh"
        assertEquals("https://www.google.com/maps/search/?api=1&query=https%3A%2F%2Fmaps.apple.com%2Fp%2FdtcGHQZ--4bUSh",
            convert(shortUrl))
    }

    @Test
    fun `converts place path with name parameter`() {
        assertEquals("geo:0,0?q=Brandenburg+Gate",
            convert("https://maps.apple.com/place?name=Brandenburg+Gate"))
    }

    @Test
    fun `cleans spaces inside coordinates`() {
        assertEquals("geo:52.520000,13.405000?q=52.520000,13.405000",
            convert("https://maps.apple.com/?ll=%2052.5200,%2013.4050%20"))
    }

    @Test
    fun `graceful fallback on malformed URI syntax`() {
        assertEquals("https://www.google.com/maps",
            convert("http://invalid^url|test"))
    }

    @Test
    fun `extracts map url from text snippet`() {
        val snippet = "Hey! Let's meet at https://maps.apple.com/?q=Brandenburg+Gate."
        assertEquals("https://maps.apple.com/?q=Brandenburg+Gate", extractMapUrl(snippet))
        assertEquals("geo:0,0?q=Brandenburg+Gate", convert(snippet))
    }

    @Test
    fun `handles applemaps custom scheme`() {
        assertEquals("geo:0,0?q=Berlin",
            convert("applemaps://maps.apple.com/?q=Berlin"))
    }

    @Test
    fun `converts directions with travel mode`() {
        assertEquals("https://www.google.com/maps/dir/?api=1&origin=Berlin&destination=Potsdam&travelmode=transit",
            convert("https://maps.apple.com/?saddr=Berlin&daddr=Potsdam&dirflg=r"))
        assertEquals("https://www.google.com/maps/dir/?api=1&origin=Berlin&destination=Potsdam&travelmode=walking",
            convert("https://maps.apple.com/?saddr=Berlin&daddr=Potsdam&dirflg=w"))
        assertEquals("https://www.google.com/maps/dir/?api=1&origin=Berlin&destination=Potsdam&travelmode=bicycling",
            convert("https://maps.apple.com/?saddr=Berlin&daddr=Potsdam&dirflg=b"))
    }

    @Test
    fun `converts navigation with mode`() {
        assertEquals("google.navigation:q=Munich&mode=w",
            convert("https://maps.apple.com/?daddr=Munich&dirflg=w"))
        assertEquals("google.navigation:q=Munich&mode=b",
            convert("https://maps.apple.com/?daddr=Munich&dirflg=b"))
    }

    @Test
    fun `converts center and coordinate parameters`() {
        assertEquals("geo:40.712800,-74.006000?q=40.712800,-74.006000",
            convert("https://maps.apple.com/?coordinate=40.7128,-74.0060"))
        assertEquals("geo:48.856600,2.352200?q=Paris",
            convert("https://maps.apple.com/?center=48.8566,2.3522&q=Paris"))
    }

    @Test
    fun `converts search query with umlauts and diacritics`() {
        assertEquals("geo:0,0?q=M%C3%BCnchen+Marienplatz",
            convert("https://maps.apple.com/?q=M%C3%BCnchen%20Marienplatz"))
        assertEquals("geo:0,0?q=Z%C3%BCrich+HB",
            convert("https://maps.apple.com/?q=Z%C3%BCrich+HB"))
        assertEquals("geo:0,0?q=Champs-%C3%89lys%C3%A9es",
            convert("https://maps.apple.com/?q=Champs-%C3%89lys%C3%A9es"))
        assertEquals("geo:0,0?q=S%C3%A3o+Paulo",
            convert("https://maps.apple.com/?q=S%C3%A3o+Paulo"))
    }

    @Test
    fun `converts search query with special characters`() {
        // Ampersand (&) encoded as %26
        assertEquals("geo:0,0?q=Caf%C3%A9+%26+Bar",
            convert("https://maps.apple.com/?q=Caf%C3%A9%20%26%20Bar"))
        // Plus (+) encoded as %2B
        assertEquals("geo:0,0?q=C%2B%2B+Campus",
            convert("https://maps.apple.com/?q=C%2B%2B%20Campus"))
        // Percent (%) encoded as %25
        assertEquals("geo:0,0?q=Top+10%25+Club",
            convert("https://maps.apple.com/?q=Top%2010%25%20Club"))
    }

    @Test
    fun `converts search query with non-latin scripts`() {
        // Cyrillic (Moscow, Red Square)
        assertEquals("geo:0,0?q=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0%2C+%D0%9A%D1%80%D0%B0%D1%81%D0%BD%D0%B0%D1%8F+%D0%BF%D0%BB%D0%BE%D1%89%D0%B0%D0%B4%D1%8C",
            convert("https://maps.apple.com/?q=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0%2C%20%D0%9A%D1%80%D0%B0%D1%81%D0%BD%D0%B0%D1%8F%20%D0%BF%D0%BB%D0%BE%D1%89%D0%B0%D0%B4%D1%8C"))
        // Arabic (Burj Khalifa)
        assertEquals("geo:0,0?q=%D8%A8%D8%B1%D8%AC+%D8%AE%D9%84%D9%8A%D9%81%D8%A9",
            convert("https://maps.apple.com/?q=%D8%A8%D8%B1%D8%AC%20%D8%AE%D9%84%D9%8A%D9%81%D8%A9"))
        // Chinese (Forbidden City)
        assertEquals("geo:0,0?q=%E6%95%85%E5%AE%AB%E5%8D%9A%E7%89%A9%E9%99%A2",
            convert("https://maps.apple.com/?q=%E6%95%85%E5%AE%AB%E5%8D%9A%E7%89%A9%E9%99%A2"))
        // Japanese (Tokyo Tower)
        assertEquals("geo:0,0?q=%E6%9D%B1%E4%BA%AC%E3%82%BF%E3%83%AF%E3%83%BC",
            convert("https://maps.apple.com/?q=%E6%9D%B1%E4%BA%AC%E3%82%BF%E3%83%AF%E3%83%BC"))
    }

    @Test
    fun `converts directions with special characters and umlauts`() {
        assertEquals("https://www.google.com/maps/dir/?api=1&origin=M%C3%BCnchen&destination=K%C3%B6ln",
            convert("https://maps.apple.com/?saddr=M%C3%BCnchen&daddr=K%C3%B6ln"))
        assertEquals("https://www.google.com/maps/dir/?api=1&origin=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0&destination=%D0%A1%D0%B0%D0%BD%D0%BA%D1%82-%D0%9F%D0%B5%D1%82%D0%B5%D1%80%D0%B1%D1%83%D1%80%D0%B3",
            convert("https://maps.apple.com/?saddr=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0&daddr=%D0%A1%D0%B0%D0%BD%D0%BA%D1%82-%D0%9F%D0%B5%D1%82%D0%B5%D1%80%D0%B1%D1%83%D1%80%D0%B3"))
    }

    @Test
    fun `supports all languages in Strings registry`() {
        val supportedCodes = de.goork.mapflip.ui.Strings.SUPPORTED_LANGUAGES.map { it.code }
        assertEquals(21, supportedCodes.size)
        val expected = listOf(
            "auto", "de", "en", "da", "fr", "it", "ja", "nl", "no", "pl",
            "pt", "pt-br", "sv", "es", "tr", "ko", "zh", "zh-tw", "ar", "ru", "id"
        )
        assertEquals(expected, supportedCodes)
        for (code in expected) {
            val strings = de.goork.mapflip.ui.Strings.getStrings(code)
            org.junit.Assert.assertFalse(strings.setupTitle.isBlank())
            org.junit.Assert.assertFalse(strings.tagline.isBlank())
            org.junit.Assert.assertFalse(strings.testLinkTitle.isBlank())
            org.junit.Assert.assertFalse(strings.privacyPolicyTitle.isBlank())
            org.junit.Assert.assertFalse(strings.impressumTitle.isBlank())
            org.junit.Assert.assertFalse(strings.sectionTargetApp.isBlank())
            org.junit.Assert.assertFalse(strings.targetAppAlwaysAsk.isBlank())
            org.junit.Assert.assertFalse(strings.targetAppFallbackOpened.isBlank())
            org.junit.Assert.assertFalse(strings.redirectingToApp.isBlank())
            org.junit.Assert.assertFalse(strings.step2.isBlank())
            org.junit.Assert.assertFalse(strings.step3.isBlank())
            org.junit.Assert.assertFalse(strings.testLinkHint.isBlank())
        }
    }

    @Test
    fun `resolveLanguage maps Bokmal and Nynorsk to no`() {
        assertEquals("no", de.goork.mapflip.ui.Strings.resolveLanguage("nb"))
        assertEquals("no", de.goork.mapflip.ui.Strings.resolveLanguage("nn"))
        assertEquals("no", de.goork.mapflip.ui.Strings.resolveLanguage("no"))
    }

    @Test
    fun `resolveLanguage and getStrings handle pt-BR and pt-PT distinctly`() {
        assertEquals("pt-br", de.goork.mapflip.ui.Strings.resolveLanguage("pt-br"))
        assertEquals("pt", de.goork.mapflip.ui.Strings.resolveLanguage("pt"))

        val ptBr = de.goork.mapflip.ui.Strings.getStrings("pt-br")
        val ptPt = de.goork.mapflip.ui.Strings.getStrings("pt")

        assertEquals("Redirecionando para %s...", ptBr.redirectingToApp)
        assertEquals("A redirecionar para %s...", ptPt.redirectingToApp)
        assertEquals("App de navegação", ptBr.sectionTargetApp)
        assertEquals("Aplicação de navegação", ptPt.sectionTargetApp)
    }
}
