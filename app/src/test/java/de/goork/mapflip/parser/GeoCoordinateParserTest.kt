package de.goork.mapflip.parser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GeoCoordinateParserTest {

    @Test
    fun `parses standard geo URIs`() {
        val uri = "geo:52.520008,13.404954"
        assertTrue(GeoCoordinateParser.canParse(uri))
        val parsed = GeoCoordinateParser.parse(uri)
        assertTrue(parsed is ParsedLocation.Coordinates)
        val coords = parsed as ParsedLocation.Coordinates
        assertEquals(52.520008, coords.latitude, 0.000001)
        assertEquals(13.404954, coords.longitude, 0.000001)
        assertNull(coords.label)
    }

    @Test
    fun `parses geo URIs with query label`() {
        val uri = "geo:0,0?q=52.520008,13.404954(Berlin+Center)"
        val parsed = GeoCoordinateParser.parse(uri)
        assertTrue(parsed is ParsedLocation.Coordinates)
        val coords = parsed as ParsedLocation.Coordinates
        assertEquals(52.520008, coords.latitude, 0.000001)
        assertEquals(13.404954, coords.longitude, 0.000001)
        assertEquals("Berlin Center", coords.label)
    }

    @Test
    fun `parses geo URIs with text search query`() {
        val uri = "geo:0,0?q=Brandenburger+Tor"
        val parsed = GeoCoordinateParser.parse(uri)
        assertTrue(parsed is ParsedLocation.SearchQuery)
        val search = parsed as ParsedLocation.SearchQuery
        assertEquals("Brandenburger Tor", search.query)
    }

    @Test
    fun `parses raw decimal degree coordinates from text`() {
        val text = "Treffpunkt ist bei 52.520008, 13.404954 vor Ort"
        val extracted = GeoCoordinateParser.extractUrl(text)
        assertNotNull(extracted)
        assertEquals("geo:52.520008,13.404954", extracted)

        val parsed = GeoCoordinateParser.parse(extracted!!)
        assertTrue(parsed is ParsedLocation.Coordinates)
        val coords = parsed as ParsedLocation.Coordinates
        assertEquals(52.520008, coords.latitude, 0.000001)
        assertEquals(13.404954, coords.longitude, 0.000001)
    }

    @Test
    fun `parses negative coordinates and southern western hemispheres`() {
        val text = "-33.868820, 151.209296" // Sydney
        val extracted = GeoCoordinateParser.extractUrl(text)
        assertNotNull(extracted)

        val parsed = GeoCoordinateParser.parse(extracted!!)
        val coords = parsed as ParsedLocation.Coordinates
        assertEquals(-33.868820, coords.latitude, 0.000001)
        assertEquals(151.209296, coords.longitude, 0.000001)
    }

    @Test
    fun `parses directional coordinates`() {
        val text = "Standort: 52.5200° N, 13.4050° E"
        val extracted = GeoCoordinateParser.extractUrl(text)
        assertNotNull(extracted)

        val parsed = GeoCoordinateParser.parse(extracted!!)
        val coords = parsed as ParsedLocation.Coordinates
        assertEquals(52.5200, coords.latitude, 0.0001)
        assertEquals(13.4050, coords.longitude, 0.0001)
    }

    @Test
    fun `parses DMS coordinates`() {
        val text = """Koordinaten: 52°31'12.5"N, 13°24'18.2"E"""
        val extracted = GeoCoordinateParser.extractUrl(text)
        assertNotNull(extracted)

        val parsed = GeoCoordinateParser.parse(extracted!!)
        val coords = parsed as ParsedLocation.Coordinates
        // 52 + 31/60 + 12.5/3600 = 52.520138
        assertEquals(52.520138, coords.latitude, 0.0001)
        // 13 + 24/60 + 18.2/3600 = 13.405055
        assertEquals(13.405055, coords.longitude, 0.0001)
    }

    @Test
    fun `rejects non-coordinate texts and numbers to prevent false positives`() {
        assertNull(GeoCoordinateParser.extractUrl(null))
        assertNull(GeoCoordinateParser.extractUrl(""))
        assertNull(GeoCoordinateParser.extractUrl("Version 1.2.19"))
        assertNull(GeoCoordinateParser.extractUrl("Preis: 12.50, 14.50 Euro"))
        assertNull(GeoCoordinateParser.extractUrl("Datum 06.09.2026"))
        assertNull(GeoCoordinateParser.extractUrl("https://example.com/test"))
    }
}
