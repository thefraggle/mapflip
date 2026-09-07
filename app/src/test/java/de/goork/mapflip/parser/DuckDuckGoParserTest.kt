package de.goork.mapflip.parser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DuckDuckGoParserTest {

    @Test
    fun `parses coordinates with iaxm=maps`() {
        val url = "https://duckduckgo.com/?q=52.520008%2C13.404954&iaxm=maps"
        assertTrue(DuckDuckGoParser.canParse(url))
        val parsed = DuckDuckGoParser.parse(url)
        assertTrue(parsed is ParsedLocation.Coordinates)
        val coords = parsed as ParsedLocation.Coordinates
        assertEquals(52.520008, coords.latitude, 0.000001)
        assertEquals(13.404954, coords.longitude, 0.000001)
    }

    @Test
    fun `parses place search with ia=maps`() {
        val url = "https://duckduckgo.com/?q=Brandenburg+Gate+Berlin&ia=maps"
        assertTrue(DuckDuckGoParser.canParse(url))
        val parsed = DuckDuckGoParser.parse(url)
        assertTrue(parsed is ParsedLocation.SearchQuery)
        val search = parsed as ParsedLocation.SearchQuery
        assertEquals("Brandenburg Gate Berlin", search.query)
    }

    @Test
    fun `parses directions with transport mode`() {
        val url = "https://duckduckgo.com/?q=Berlin+Hauptbahnhof&iaxm=directions&transport=walk"
        assertTrue(DuckDuckGoParser.canParse(url))
        val parsed = DuckDuckGoParser.parse(url)
        assertTrue(parsed is ParsedLocation.Navigation)
        val nav = parsed as ParsedLocation.Navigation
        assertEquals("Berlin Hauptbahnhof", nav.destination)
        assertEquals(TravelMode.WALKING, nav.mode)
    }

    @Test
    fun `parses directions with coordinates destination`() {
        val url = "https://duckduckgo.com/?q=48.137154,11.576124&iaxm=directions&transport=bike"
        assertTrue(DuckDuckGoParser.canParse(url))
        val parsed = DuckDuckGoParser.parse(url)
        assertTrue(parsed is ParsedLocation.Coordinates)
        val coords = parsed as ParsedLocation.Coordinates
        assertEquals(48.137154, coords.latitude, 0.000001)
        assertEquals(11.576124, coords.longitude, 0.000001)
        assertEquals(TravelMode.BICYCLING, coords.mode)
    }

    @Test
    fun `ignores regular web search without maps parameter`() {
        val url = "https://duckduckgo.com/?q=weather+berlin"
        assertFalse(DuckDuckGoParser.canParse(url))
        assertNull(DuckDuckGoParser.extractUrl("Check this out: $url"))
    }

    @Test
    fun `extracts map url from shared text`() {
        val text = "Meeting point: https://duckduckgo.com/?q=Alexanderplatz&iaxm=places see you there!"
        val extracted = DuckDuckGoParser.extractUrl(text)
        assertNotNull(extracted)
        assertEquals("https://duckduckgo.com/?q=Alexanderplatz&iaxm=places", extracted)
    }

    @Test
    fun `UniversalMapParser detects duckduckgo service and parses`() {
        val url = "https://duckduckgo.com/?q=Eiffel+Tower&iaxm=maps"
        assertEquals("duckduckgo", UniversalMapParser.detectSourceService(url))
        assertTrue(UniversalMapParser.canParse(url))
        val parsed = UniversalMapParser.parse(url)
        assertTrue(parsed is ParsedLocation.SearchQuery)
        assertEquals("Eiffel Tower", (parsed as ParsedLocation.SearchQuery).query)
    }

    @Test
    fun `empty query returns home`() {
        val url = "https://duckduckgo.com/?iaxm=maps"
        val parsed = DuckDuckGoParser.parse(url)
        assertTrue(parsed is ParsedLocation.Home)
    }
}
