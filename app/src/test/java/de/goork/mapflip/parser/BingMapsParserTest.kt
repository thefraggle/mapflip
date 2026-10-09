package de.goork.mapflip.parser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BingMapsParserTest {

    @Test
    fun `canParse detects bing maps domains`() {
        assertTrue(BingMapsParser.canParse("https://www.bing.com/maps?q=Berlin"))
        assertTrue(BingMapsParser.canParse("https://bing.com/maps?cp=52.5~13.4"))
        assertTrue(BingMapsParser.canParse("http://maps.bing.com/?q=Paris"))
        assertFalse(BingMapsParser.canParse("https://google.com/maps"))
        assertFalse(BingMapsParser.canParse("https://bing.com/search?q=maps"))
    }

    @Test
    fun `extractUrl extracts bing maps from text`() {
        val text = "Check out this place: https://www.bing.com/maps?q=Eiffel+Tower!"
        assertEquals("https://www.bing.com/maps?q=Eiffel+Tower", BingMapsParser.extractUrl(text))
    }

    @Test
    fun `parse converts search query and where1`() {
        val parsedQ = BingMapsParser.parse("https://www.bing.com/maps?q=Eiffel+Tower")
        assertEquals(ParsedLocation.SearchQuery("Eiffel Tower"), parsedQ)

        val parsedWhere = BingMapsParser.parse("https://www.bing.com/maps?where1=Brandenburger+Tor")
        assertEquals(ParsedLocation.SearchQuery("Brandenburger Tor"), parsedWhere)
    }

    @Test
    fun `parse converts coordinates with tilde or underscore separator`() {
        val parsedTilde = BingMapsParser.parse("https://www.bing.com/maps?cp=48.8584~2.2945")
        assertTrue(parsedTilde is ParsedLocation.Coordinates)
        val coordsTilde = parsedTilde as ParsedLocation.Coordinates
        assertEquals(48.8584, coordsTilde.latitude, 0.0001)
        assertEquals(2.2945, coordsTilde.longitude, 0.0001)

        val parsedUnderscore = BingMapsParser.parse("https://www.bing.com/maps?cp=48.8584_2.2945")
        assertTrue(parsedUnderscore is ParsedLocation.Coordinates)
        val coordsUnderscore = parsedUnderscore as ParsedLocation.Coordinates
        assertEquals(48.8584, coordsUnderscore.latitude, 0.0001)
        assertEquals(2.2945, coordsUnderscore.longitude, 0.0001)
    }

    @Test
    fun `parse handles coordinates with query label`() {
        val parsed = BingMapsParser.parse("https://www.bing.com/maps?cp=52.5200~13.4050&q=Alexanderplatz")
        assertTrue(parsed is ParsedLocation.Coordinates)
        val coords = parsed as ParsedLocation.Coordinates
        assertEquals(52.5200, coords.latitude, 0.0001)
        assertEquals(13.4050, coords.longitude, 0.0001)
        assertEquals("Alexanderplatz", coords.label)
    }

    @Test
    fun `parse converts directions and travel modes`() {
        val driving = BingMapsParser.parse("https://www.bing.com/maps?rtp=adr.Berlin~adr.Munich&mode=d")
        assertEquals(ParsedLocation.Directions(origin = "Berlin", destination = "Munich", mode = TravelMode.DRIVING), driving)

        val walking = BingMapsParser.parse("https://www.bing.com/maps?rtp=adr.Köln~adr.Bonn&mode=w")
        assertEquals(ParsedLocation.Directions(origin = "Köln", destination = "Bonn", mode = TravelMode.WALKING), walking)

        val transit = BingMapsParser.parse("https://www.bing.com/maps?rtp=pos.52.5_13.4~pos.48.1_11.5&mode=t")
        assertEquals(ParsedLocation.Directions(origin = "52.5, 13.4", destination = "48.1, 11.5", mode = TravelMode.TRANSIT), transit)
    }

    @Test
    fun `parse handles unicode, umlauts and special characters`() {
        val parsedUmlaut = BingMapsParser.parse("https://www.bing.com/maps?q=M%C3%BCnchen+Hauptbahnhof")
        assertEquals(ParsedLocation.SearchQuery("München Hauptbahnhof"), parsedUmlaut)

        val parsedSpecial = BingMapsParser.parse("https://www.bing.com/maps?q=Bed+%26+Breakfast")
        assertEquals(ParsedLocation.SearchQuery("Bed & Breakfast"), parsedSpecial)

        val parsedJapanese = BingMapsParser.parse("https://www.bing.com/maps?q=%E6%9D%B1%E4%BA%AC%E9%A7%85")
        assertEquals(ParsedLocation.SearchQuery("東京駅"), parsedJapanese)
    }

    @Test
    fun `parse handles null island 0,0`() {
        val parsed = BingMapsParser.parse("https://www.bing.com/maps?cp=0.0~0.0")
        assertTrue(parsed is ParsedLocation.Coordinates)
        val coords = parsed as ParsedLocation.Coordinates
        assertEquals(0.0, coords.latitude, 0.00001)
        assertEquals(0.0, coords.longitude, 0.00001)
    }

    @Test
    fun `parse rejects out-of-bounds coordinates and falls back to WebFallback`() {
        val outOfBoundsLat = BingMapsParser.parse("https://www.bing.com/maps?cp=95.0~13.4")
        assertTrue(outOfBoundsLat is ParsedLocation.WebFallback)

        val outOfBoundsLon = BingMapsParser.parse("https://www.bing.com/maps?cp=52.5~190.0")
        assertTrue(outOfBoundsLon is ParsedLocation.WebFallback)

        val negativeOutOfBounds = BingMapsParser.parse("https://www.bing.com/maps?cp=-95.0~-190.0")
        assertTrue(negativeOutOfBounds is ParsedLocation.WebFallback)
    }

    @Test
    fun `parse handles malformed or empty URLs safely`() {
        assertEquals(ParsedLocation.Home, BingMapsParser.parse(""))
        assertEquals(ParsedLocation.Home, BingMapsParser.parse("   "))

        val malformedQuery = BingMapsParser.parse("https://www.bing.com/maps?q=%2")
        assertTrue(malformedQuery is ParsedLocation.WebFallback)

        val missingParams = BingMapsParser.parse("https://www.bing.com/maps")
        assertTrue(missingParams is ParsedLocation.WebFallback)
    }
}
