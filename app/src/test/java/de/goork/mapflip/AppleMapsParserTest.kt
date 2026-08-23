package de.goork.mapflip

import org.junit.Assert.assertEquals
import org.junit.Test

class AppleMapsParserTest {
    @Test
    fun `converts search query`() {
        assertEquals("geo:0,0?q=Eiffelturm",
            AppleMapsParser.convert("https://maps.apple.com/?q=Eiffelturm"))
    }

    @Test
    fun `converts coordinates`() {
        assertEquals("geo:48.8584,2.2945?q=48.8584,2.2945",
            AppleMapsParser.convert("https://maps.apple.com/?ll=48.8584,2.2945"))
    }

    @Test
    fun `converts coordinates with query`() {
        assertEquals("geo:48.8584,2.2945?q=Eiffelturm",
            AppleMapsParser.convert("https://maps.apple.com/?ll=48.8584,2.2945&q=Eiffelturm"))
    }

    @Test
    fun `converts address`() {
        assertEquals("geo:0,0?q=Berlin",
            AppleMapsParser.convert("https://maps.apple.com/?address=Berlin"))
    }

    @Test
    fun `converts auid parameter as search query`() {
        assertEquals("geo:0,0?q=1234567890",
            AppleMapsParser.convert("https://maps.apple.com/?auid=1234567890"))
    }

    @Test
    fun `converts directions`() {
        assertEquals("google.navigation:q=Munich",
            AppleMapsParser.convert("https://maps.apple.com/?daddr=Munich"))
    }

    @Test
    fun `converts near parameter`() {
        assertEquals("geo:0,0?q=Hamburg",
            AppleMapsParser.convert("https://maps.apple.com/?near=Hamburg"))
    }

    @Test
    fun `fallback for empty URL`() {
        assertEquals("https://www.google.com/maps",
            AppleMapsParser.convert("https://maps.apple.com/"))
    }

    @Test
    fun `handles null or blank URL`() {
        assertEquals("https://www.google.com/maps", AppleMapsParser.convert(null))
        assertEquals("https://www.google.com/maps", AppleMapsParser.convert(""))
        assertEquals("https://www.google.com/maps", AppleMapsParser.convert("   "))
    }

    @Test
    fun `handles URL without scheme`() {
        assertEquals("geo:0,0?q=Hamburg",
            AppleMapsParser.convert("maps.apple.com/?q=Hamburg"))
    }

    @Test
    fun `converts directions with both origin and destination`() {
        assertEquals("https://www.google.com/maps/dir/?api=1&origin=Berlin&destination=Munich",
            AppleMapsParser.convert("https://maps.apple.com/?saddr=Berlin&daddr=Munich"))
    }

    @Test
    fun `converts start address only`() {
        assertEquals("geo:0,0?q=Frankfurt",
            AppleMapsParser.convert("https://maps.apple.com/?saddr=Frankfurt"))
    }

    @Test
    fun `converts pt coordinate parameter`() {
        assertEquals("geo:52.5200,13.4050?q=52.5200,13.4050",
            AppleMapsParser.convert("https://maps.apple.com/?pt=52.5200,13.4050"))
    }

    @Test
    fun `converts pt coordinate parameter with query`() {
        assertEquals("geo:52.5200,13.4050?q=TV+Tower",
            AppleMapsParser.convert("https://maps.apple.com/?pt=52.5200,13.4050&q=TV+Tower"))
    }

    @Test
    fun `handles case-insensitive query parameters`() {
        assertEquals("google.navigation:q=Hamburg",
            AppleMapsParser.convert("https://maps.apple.com/?DADDR=Hamburg"))
        assertEquals("geo:0,0?q=Cologne",
            AppleMapsParser.convert("https://maps.apple.com/?Q=Cologne"))
    }

    @Test
    fun `converts short link with place ID`() {
        val shortUrl = "https://maps.apple.com/p/dtcGHQZ--4bUSh"
        assertEquals("https://www.google.com/maps/search/?api=1&query=https%3A%2F%2Fmaps.apple.com%2Fp%2FdtcGHQZ--4bUSh",
            AppleMapsParser.convert(shortUrl))
    }

    @Test
    fun `converts place path with name parameter`() {
        assertEquals("geo:0,0?q=Brandenburg+Gate",
            AppleMapsParser.convert("https://maps.apple.com/place?name=Brandenburg+Gate"))
    }

    @Test
    fun `cleans spaces inside coordinates`() {
        assertEquals("geo:52.5200,13.4050?q=52.5200,13.4050",
            AppleMapsParser.convert("https://maps.apple.com/?ll=%2052.5200,%2013.4050%20"))
    }

    @Test
    fun `graceful fallback on malformed URI syntax`() {
        assertEquals("https://www.google.com/maps",
            AppleMapsParser.convert("http://invalid^url|test"))
    }

    @Test
    fun `extracts map url from text snippet`() {
        val snippet = "Hey! Let's meet at https://maps.apple.com/?q=Brandenburg+Gate."
        assertEquals("https://maps.apple.com/?q=Brandenburg+Gate", AppleMapsParser.extractMapUrl(snippet))
        assertEquals("geo:0,0?q=Brandenburg+Gate", AppleMapsParser.convert(snippet))
    }

    @Test
    fun `handles applemaps custom scheme`() {
        assertEquals("geo:0,0?q=Berlin",
            AppleMapsParser.convert("applemaps://maps.apple.com/?q=Berlin"))
    }

    @Test
    fun `converts directions with travel mode`() {
        assertEquals("https://www.google.com/maps/dir/?api=1&origin=Berlin&destination=Potsdam&travelmode=transit",
            AppleMapsParser.convert("https://maps.apple.com/?saddr=Berlin&daddr=Potsdam&dirflg=r"))
        assertEquals("https://www.google.com/maps/dir/?api=1&origin=Berlin&destination=Potsdam&travelmode=walking",
            AppleMapsParser.convert("https://maps.apple.com/?saddr=Berlin&daddr=Potsdam&dirflg=w"))
        assertEquals("https://www.google.com/maps/dir/?api=1&origin=Berlin&destination=Potsdam&travelmode=bicycling",
            AppleMapsParser.convert("https://maps.apple.com/?saddr=Berlin&daddr=Potsdam&dirflg=b"))
    }

    @Test
    fun `converts navigation with mode`() {
        assertEquals("google.navigation:q=Munich&mode=w",
            AppleMapsParser.convert("https://maps.apple.com/?daddr=Munich&dirflg=w"))
        assertEquals("google.navigation:q=Munich&mode=b",
            AppleMapsParser.convert("https://maps.apple.com/?daddr=Munich&dirflg=b"))
    }

    @Test
    fun `converts center and coordinate parameters`() {
        assertEquals("geo:40.7128,-74.0060?q=40.7128,-74.0060",
            AppleMapsParser.convert("https://maps.apple.com/?coordinate=40.7128,-74.0060"))
        assertEquals("geo:48.8566,2.3522?q=Paris",
            AppleMapsParser.convert("https://maps.apple.com/?center=48.8566,2.3522&q=Paris"))
    }

    @Test
    fun `supports all 20 languages in Strings registry`() {
        val supportedCodes = de.goork.mapflip.ui.Strings.SUPPORTED_LANGUAGES.map { it.code }
        org.junit.Assert.assertEquals(20, supportedCodes.size)
        val expected = listOf(
            "auto", "de", "en", "da", "fr", "it", "ja", "nl", "no", "pl",
            "pt", "sv", "es", "tr", "ko", "zh", "zh-tw", "ar", "ru", "id"
        )
        org.junit.Assert.assertEquals(expected, supportedCodes)
        for (code in expected) {
            val strings = de.goork.mapflip.ui.Strings.getStrings(code)
            org.junit.Assert.assertFalse(strings.setupTitle.isBlank())
            org.junit.Assert.assertFalse(strings.tagline.isBlank())
            org.junit.Assert.assertFalse(strings.testLinkTitle.isBlank())
        }
    }
}

