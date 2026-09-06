package de.goork.mapflip.parser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GoogleMapsParserTest {

    @Test
    fun `parses query coordinates`() {
        val url = "https://maps.google.com/?q=52.520008,13.404954"
        assertTrue(GoogleMapsParser.canParse(url))
        val parsed = GoogleMapsParser.parse(url)
        assertTrue(parsed is ParsedLocation.Coordinates)
        val coords = parsed as ParsedLocation.Coordinates
        assertEquals(52.520008, coords.latitude, 0.000001)
        assertEquals(13.404954, coords.longitude, 0.000001)
    }

    @Test
    fun `parses path coordinates with zoom`() {
        val url = "https://www.google.com/maps/@52.520008,13.404954,16z"
        val parsed = GoogleMapsParser.parse(url)
        assertTrue(parsed is ParsedLocation.Coordinates)
        val coords = parsed as ParsedLocation.Coordinates
        assertEquals(52.520008, coords.latitude, 0.000001)
        assertEquals(13.404954, coords.longitude, 0.000001)
    }

    @Test
    fun `parses place path with coordinates and name`() {
        val url = "https://www.google.com/maps/place/Brandenburg+Gate/@52.516275,13.377704,17z"
        val parsed = GoogleMapsParser.parse(url)
        assertTrue(parsed is ParsedLocation.Coordinates)
        val coords = parsed as ParsedLocation.Coordinates
        assertEquals(52.516275, coords.latitude, 0.000001)
        assertEquals(13.377704, coords.longitude, 0.000001)
        assertEquals("Brandenburg Gate", coords.label)
    }

    @Test
    fun `parses directions with query params and mode`() {
        val url = "https://www.google.com/maps/dir/?api=1&origin=Berlin&destination=Munich&travelmode=transit"
        val parsed = GoogleMapsParser.parse(url)
        assertTrue(parsed is ParsedLocation.Directions)
        val dirs = parsed as ParsedLocation.Directions
        assertEquals("Berlin", dirs.origin)
        assertEquals("Munich", dirs.destination)
        assertEquals(TravelMode.TRANSIT, dirs.mode)
    }

    @Test
    fun `parses path-based directions`() {
        val url = "https://www.google.com/maps/dir/Hamburg/Frankfurt"
        val parsed = GoogleMapsParser.parse(url)
        assertTrue(parsed is ParsedLocation.Directions)
        val dirs = parsed as ParsedLocation.Directions
        assertEquals("Hamburg", dirs.origin)
        assertEquals("Frankfurt", dirs.destination)
    }

    @Test
    fun `parses search query`() {
        val url = "https://maps.google.com/?q=Reichstag+Building+Berlin"
        val parsed = GoogleMapsParser.parse(url)
        assertTrue(parsed is ParsedLocation.SearchQuery)
        val search = parsed as ParsedLocation.SearchQuery
        assertEquals("Reichstag Building Berlin", search.query)
    }

    @Test
    fun `extracts Google Maps URL from surrounding text`() {
        val message = "Schau mal hier: https://www.google.com/maps/@52.5200,13.4050,15z bis später!"
        val extracted = GoogleMapsParser.extractUrl(message)
        assertNotNull(extracted)
        assertTrue(extracted!!.startsWith("https://www.google.com/maps/@52.5200,13.4050,15z"))
    }

    @Test
    fun `extracts short goo gl link`() {
        val message = "Treffpunkt https://maps.app.goo.gl/xyz123 danke"
        val extracted = GoogleMapsParser.extractUrl(message)
        assertNotNull(extracted)
        assertEquals("https://maps.app.goo.gl/xyz123", extracted)
    }

    @Test
    fun `rejects non Google URLs`() {
        assertNull(GoogleMapsParser.extractUrl("https://maps.apple.com/?q=Berlin"))
        assertNull(GoogleMapsParser.extractUrl("https://example.com"))
    }
}
