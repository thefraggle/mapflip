package de.goork.mapflip.parser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HereMapsParserTest {

    @Test
    fun `canParse detects here maps hosts`() {
        assertTrue(HereMapsParser.canParse("https://wego.here.com/search/Berlin"))
        assertTrue(HereMapsParser.canParse("https://share.here.com/l/52.5200,13.4050,16,Berlin"))
        assertTrue(HereMapsParser.canParse("https://maps.here.com/?map=52.5,13.4"))
        assertTrue(HereMapsParser.canParse("https://here.com/directions/drive/Berlin/Munich"))
        assertFalse(HereMapsParser.canParse("https://google.com/maps"))
        assertFalse(HereMapsParser.canParse("https://here.com"))
    }

    @Test
    fun `extractUrl extracts here links from text`() {
        val text = "Meeting point: https://share.here.com/l/52.5200,13.4050,16,Berlin."
        assertEquals("https://share.here.com/l/52.5200,13.4050,16,Berlin", HereMapsParser.extractUrl(text))
    }

    @Test
    fun `parse converts coordinates from share link`() {
        val parsed = HereMapsParser.parse("https://share.here.com/l/52.5200,13.4050,16,Berlin")
        assertTrue(parsed is ParsedLocation.Coordinates)
        val coords = parsed as ParsedLocation.Coordinates
        assertEquals(52.5200, coords.latitude, 0.0001)
        assertEquals(13.4050, coords.longitude, 0.0001)
    }

    @Test
    fun `parse converts coordinates with msg query parameter as label`() {
        val parsed = HereMapsParser.parse("https://share.here.com/l/47.3769,8.5417?msg=Caf%C3%A9+%26+Bar")
        assertTrue(parsed is ParsedLocation.Coordinates)
        val coords = parsed as ParsedLocation.Coordinates
        assertEquals(47.3769, coords.latitude, 0.0001)
        assertEquals(8.5417, coords.longitude, 0.0001)
        assertEquals("Café & Bar", coords.label)
    }

    @Test
    fun `parse converts map parameter coordinates`() {
        val parsed = HereMapsParser.parse("https://wego.here.com/?map=53.5511,9.9937,14,normal")
        assertTrue(parsed is ParsedLocation.Coordinates)
        val coords = parsed as ParsedLocation.Coordinates
        assertEquals(53.5511, coords.latitude, 0.0001)
        assertEquals(9.9937, coords.longitude, 0.0001)
    }

    @Test
    fun `parse converts search in path or query`() {
        val pathSearch = HereMapsParser.parse("https://wego.here.com/search/Brandenburg+Gate")
        assertEquals(ParsedLocation.SearchQuery("Brandenburg Gate"), pathSearch)

        val querySearch = HereMapsParser.parse("https://wego.here.com/?q=Fernsehturm")
        assertEquals(ParsedLocation.SearchQuery("Fernsehturm"), querySearch)
    }

    @Test
    fun `parse converts directions and navigation in path`() {
        val driving = HereMapsParser.parse("https://wego.here.com/directions/drive/Berlin/Munich")
        assertEquals(ParsedLocation.Directions(origin = "Berlin", destination = "Munich", mode = TravelMode.DRIVING), driving)

        val walking = HereMapsParser.parse("https://wego.here.com/directions/walk/Alexanderplatz/Potsdamer+Platz")
        assertEquals(ParsedLocation.Directions(origin = "Alexanderplatz", destination = "Potsdamer Platz", mode = TravelMode.WALKING), walking)

        val singleNav = HereMapsParser.parse("https://wego.here.com/directions/drive/Berlin")
        assertEquals(ParsedLocation.Navigation(destination = "Berlin", mode = TravelMode.DRIVING), singleNav)
    }

    @Test
    fun `parse handles unicode, umlauts and special characters`() {
        val parsedSearch = HereMapsParser.parse("https://wego.here.com/search/Z%C3%BCrich+HB")
        assertEquals(ParsedLocation.SearchQuery("Zürich HB"), parsedSearch)

        val parsedGreek = HereMapsParser.parse("https://wego.here.com/search/%CE%91%CE%B8%CE%AE%CE%BD%CE%B1")
        assertEquals(ParsedLocation.SearchQuery("Αθήνα"), parsedGreek)
    }

    @Test
    fun `parse handles null island 0,0`() {
        val parsed = HereMapsParser.parse("https://share.here.com/l/0.0,0.0")
        assertTrue(parsed is ParsedLocation.Coordinates)
        val coords = parsed as ParsedLocation.Coordinates
        assertEquals(0.0, coords.latitude, 0.00001)
        assertEquals(0.0, coords.longitude, 0.00001)
    }

    @Test
    fun `parse rejects out-of-bounds coordinates`() {
        val outOfBounds = HereMapsParser.parse("https://share.here.com/l/95.0,13.4")
        assertTrue(outOfBounds is ParsedLocation.WebFallback)

        val outOfBoundsMap = HereMapsParser.parse("https://wego.here.com/?map=52.5,200.0")
        assertTrue(outOfBoundsMap is ParsedLocation.WebFallback)
    }

    @Test
    fun `parse handles malformed or empty URLs safely`() {
        assertEquals(ParsedLocation.Home, HereMapsParser.parse(""))
        assertEquals(ParsedLocation.Home, HereMapsParser.parse("  "))

        val malformed = HereMapsParser.parse("https://share.here.com/l/%2")
        assertTrue(malformed is ParsedLocation.WebFallback)

        val incomplete = HereMapsParser.parse("https://wego.here.com/")
        assertTrue(incomplete is ParsedLocation.WebFallback)
    }
}
