package de.goork.mapflip.parser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OsmMapsParserTest {

    @Test
    fun `canParse detects openstreetmap and osm dot org`() {
        assertTrue(OpenStreetMapParser.canParse("https://www.openstreetmap.org/?mlat=52.5&mlon=13.4"))
        assertTrue(OpenStreetMapParser.canParse("https://osm.org/#map=16/48.8584/2.2945"))
        assertTrue(OpenStreetMapParser.canParse("http://openstreetmap.org/search?query=Berlin"))
        assertFalse(OpenStreetMapParser.canParse("https://google.com/maps"))
        assertFalse(OpenStreetMapParser.canParse("https://osm.com"))
    }

    @Test
    fun `extractUrl extracts osm links from text`() {
        val text = "Map link: https://www.openstreetmap.org/#map=16/48.8584/2.2945!"
        assertEquals("https://www.openstreetmap.org/#map=16/48.8584/2.2945", OpenStreetMapParser.extractUrl(text))
    }

    @Test
    fun `parse converts marker coordinates with mlat and mlon`() {
        val parsed = OpenStreetMapParser.parse("https://www.openstreetmap.org/?mlat=52.5200&mlon=13.4050")
        assertTrue(parsed is ParsedLocation.Coordinates)
        val coords = parsed as ParsedLocation.Coordinates
        assertEquals(52.5200, coords.latitude, 0.0001)
        assertEquals(13.4050, coords.longitude, 0.0001)
    }

    @Test
    fun `parse converts map hash fragment`() {
        val parsed = OpenStreetMapParser.parse("https://www.openstreetmap.org/#map=16/48.8584/2.2945")
        assertTrue(parsed is ParsedLocation.Coordinates)
        val coords = parsed as ParsedLocation.Coordinates
        assertEquals(48.8584, coords.latitude, 0.0001)
        assertEquals(2.2945, coords.longitude, 0.0001)
    }

    @Test
    fun `parse converts map query parameter`() {
        val parsed = OpenStreetMapParser.parse("https://www.openstreetmap.org/?map=14/53.5511/9.9937")
        assertTrue(parsed is ParsedLocation.Coordinates)
        val coords = parsed as ParsedLocation.Coordinates
        assertEquals(53.5511, coords.latitude, 0.0001)
        assertEquals(9.9937, coords.longitude, 0.0001)
    }

    @Test
    fun `parse converts route with engines`() {
        val foot = OpenStreetMapParser.parse("https://www.openstreetmap.org/directions?engine=fossgis_osrm_foot&route=52.52,13.40;52.51,13.41")
        assertEquals(ParsedLocation.Directions(origin = "52.52,13.40", destination = "52.51,13.41", mode = TravelMode.WALKING), foot)

        val bike = OpenStreetMapParser.parse("https://www.openstreetmap.org/directions?engine=graphhopper_bicycle&route=52.52,13.40;52.51,13.41")
        assertEquals(ParsedLocation.Directions(origin = "52.52,13.40", destination = "52.51,13.41", mode = TravelMode.BICYCLING), bike)

        val car = OpenStreetMapParser.parse("https://www.openstreetmap.org/directions?engine=fossgis_osrm_car&route=52.52,13.40;52.51,13.41")
        assertEquals(ParsedLocation.Directions(origin = "52.52,13.40", destination = "52.51,13.41", mode = TravelMode.DRIVING), car)
    }

    @Test
    fun `parse converts query parameter`() {
        val parsedQuery = OpenStreetMapParser.parse("https://www.openstreetmap.org/search?query=K%C3%B6lner+Dom")
        assertEquals(ParsedLocation.SearchQuery("Kölner Dom"), parsedQuery)

        val parsedQ = OpenStreetMapParser.parse("https://www.openstreetmap.org/?q=Hamburg+Rathaus")
        assertEquals(ParsedLocation.SearchQuery("Hamburg Rathaus"), parsedQ)
    }

    @Test
    fun `parse handles unicode and cyrillic`() {
        val parsedCyrillic = OpenStreetMapParser.parse("https://www.openstreetmap.org/search?query=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0")
        assertEquals(ParsedLocation.SearchQuery("Москва"), parsedCyrillic)

        val parsedArabic = OpenStreetMapParser.parse("https://www.openstreetmap.org/search?query=%D8%A7%D9%84%D9%82%D8%A7%D9%87%D8%B1%D8%A9")
        assertEquals(ParsedLocation.SearchQuery("القاهرة"), parsedArabic)
    }

    @Test
    fun `parse handles null island 0,0`() {
        val parsed = OpenStreetMapParser.parse("https://www.openstreetmap.org/?mlat=0.0&mlon=0.0")
        assertTrue(parsed is ParsedLocation.Coordinates)
        val coords = parsed as ParsedLocation.Coordinates
        assertEquals(0.0, coords.latitude, 0.00001)
        assertEquals(0.0, coords.longitude, 0.00001)
    }

    @Test
    fun `parse rejects out-of-bounds coordinates`() {
        val outOfBoundsLat = OpenStreetMapParser.parse("https://www.openstreetmap.org/?mlat=95.0&mlon=13.4")
        assertTrue(outOfBoundsLat is ParsedLocation.WebFallback)

        val outOfBoundsLon = OpenStreetMapParser.parse("https://www.openstreetmap.org/?mlat=52.5&mlon=190.0")
        assertTrue(outOfBoundsLon is ParsedLocation.WebFallback)

        val hashOutOfBounds = OpenStreetMapParser.parse("https://www.openstreetmap.org/#map=16/95.0/13.4")
        assertTrue(hashOutOfBounds is ParsedLocation.WebFallback)
    }

    @Test
    fun `parse handles malformed URLs safely`() {
        assertEquals(ParsedLocation.Home, OpenStreetMapParser.parse(""))
        assertEquals(ParsedLocation.Home, OpenStreetMapParser.parse("  "))

        val malformed = OpenStreetMapParser.parse("https://www.openstreetmap.org/?query=%2")
        assertTrue(malformed is ParsedLocation.WebFallback)

        val incomplete = OpenStreetMapParser.parse("https://www.openstreetmap.org/")
        assertTrue(incomplete is ParsedLocation.WebFallback)
    }
}
