package de.goork.mapflip.parser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WazeMapsParserTest {

    @Test
    fun `canParse detects waze domains and paths`() {
        assertTrue(WazeMapsParser.canParse("https://waze.com/ul?ll=48.8584,2.2945"))
        assertTrue(WazeMapsParser.canParse("https://www.waze.com/ul?q=Berlin"))
        assertTrue(WazeMapsParser.canParse("https://waze.com/live-map/directions?to=ll.52.5,13.4"))
        assertTrue(WazeMapsParser.canParse("https://waze.com/location?ll=52.5,13.4"))
        assertFalse(WazeMapsParser.canParse("https://google.com/maps"))
        assertFalse(WazeMapsParser.canParse("https://waze.com/about"))
    }

    @Test
    fun `extractUrl extracts waze link from text`() {
        val text = "Join me here: https://waze.com/ul?ll=48.8584,2.2945&navigate=yes."
        assertEquals("https://waze.com/ul?ll=48.8584,2.2945&navigate=yes", WazeMapsParser.extractUrl(text))
    }

    @Test
    fun `parse converts coordinates link with optional label`() {
        val parsedNoLabel = WazeMapsParser.parse("https://waze.com/ul?ll=48.8584,2.2945&navigate=yes")
        assertTrue(parsedNoLabel is ParsedLocation.Coordinates)
        val coordsNoLabel = parsedNoLabel as ParsedLocation.Coordinates
        assertEquals(48.8584, coordsNoLabel.latitude, 0.0001)
        assertEquals(2.2945, coordsNoLabel.longitude, 0.0001)

        val parsedWithLabel = WazeMapsParser.parse("https://waze.com/ul?ll=52.5200,13.4050&q=TV+Tower")
        assertTrue(parsedWithLabel is ParsedLocation.Coordinates)
        val coordsWithLabel = parsedWithLabel as ParsedLocation.Coordinates
        assertEquals(52.5200, coordsWithLabel.latitude, 0.0001)
        assertEquals(13.4050, coordsWithLabel.longitude, 0.0001)
        assertEquals("TV Tower", coordsWithLabel.label)
    }

    @Test
    fun `parse converts live-map directions with to param`() {
        val coordsTo = WazeMapsParser.parse("https://waze.com/live-map/directions?to=ll.52.5200,13.4050")
        assertTrue(coordsTo is ParsedLocation.Coordinates)
        val coords = coordsTo as ParsedLocation.Coordinates
        assertEquals(52.5200, coords.latitude, 0.0001)
        assertEquals(13.4050, coords.longitude, 0.0001)

        val placeTo = WazeMapsParser.parse("https://waze.com/live-map/directions?to=place.Brandenburg_Gate")
        assertEquals(ParsedLocation.SearchQuery("Brandenburg Gate"), placeTo)

        val simpleTo = WazeMapsParser.parse("https://waze.com/live-map/directions?to=Alexanderplatz")
        assertEquals(ParsedLocation.SearchQuery("Alexanderplatz"), simpleTo)
    }

    @Test
    fun `parse converts query and favorite`() {
        val parsedQuery = WazeMapsParser.parse("https://waze.com/ul?q=Eiffel+Tower&navigate=yes")
        assertEquals(ParsedLocation.SearchQuery("Eiffel Tower"), parsedQuery)

        val parsedFavorite = WazeMapsParser.parse("https://waze.com/ul?favorite=work")
        assertEquals(ParsedLocation.SearchQuery("work"), parsedFavorite)
    }

    @Test
    fun `parse handles unicode, umlauts and plus characters`() {
        val parsedPlus = WazeMapsParser.parse("https://waze.com/ul?q=C%2B%2B+Innovation+Lab&navigate=yes")
        assertEquals(ParsedLocation.SearchQuery("C++ Innovation Lab"), parsedPlus)

        val parsedUmlaut = WazeMapsParser.parse("https://waze.com/ul?q=D%C3%BCsseldorf&navigate=yes")
        assertEquals(ParsedLocation.SearchQuery("Düsseldorf"), parsedUmlaut)

        val parsedHebrew = WazeMapsParser.parse("https://waze.com/ul?q=%D7%AA%D7%9C+%D7%90%D7%91%D7%99%D7%91")
        assertEquals(ParsedLocation.SearchQuery("תל אביב"), parsedHebrew)
    }

    @Test
    fun `parse handles null island 0,0`() {
        val parsed = WazeMapsParser.parse("https://waze.com/ul?ll=0.0,0.0")
        assertTrue(parsed is ParsedLocation.Coordinates)
        val coords = parsed as ParsedLocation.Coordinates
        assertEquals(0.0, coords.latitude, 0.00001)
        assertEquals(0.0, coords.longitude, 0.00001)
    }

    @Test
    fun `parse rejects out-of-bounds coordinates`() {
        val outOfBoundsLat = WazeMapsParser.parse("https://waze.com/ul?ll=95.0,13.4")
        assertTrue(outOfBoundsLat is ParsedLocation.WebFallback)

        val outOfBoundsLon = WazeMapsParser.parse("https://waze.com/ul?ll=52.5,190.0")
        assertTrue(outOfBoundsLon is ParsedLocation.WebFallback)

        val outOfBoundsTo = WazeMapsParser.parse("https://waze.com/live-map/directions?to=ll.95.0,13.4")
        assertTrue(outOfBoundsTo is ParsedLocation.WebFallback)
    }

    @Test
    fun `parse handles malformed or empty URLs safely`() {
        assertEquals(ParsedLocation.Home, WazeMapsParser.parse(""))
        assertEquals(ParsedLocation.Home, WazeMapsParser.parse("   "))

        val malformed = WazeMapsParser.parse("https://waze.com/ul?q=%2")
        assertTrue(malformed is ParsedLocation.WebFallback)

        val noParams = WazeMapsParser.parse("https://waze.com/ul")
        assertTrue(noParams is ParsedLocation.WebFallback)
    }
}
