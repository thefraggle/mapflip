package de.goork.mapflip.parser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AdditionalMapParsersTest {

    // Bing Maps Tests
    @Test
    fun `bing maps converts search query`() {
        val parsed = BingMapsParser.parse("https://www.bing.com/maps?q=Eiffel+Tower")
        assertEquals(ParsedLocation.SearchQuery("Eiffel Tower"), parsed)
    }

    @Test
    fun `bing maps converts coordinates`() {
        val parsed = BingMapsParser.parse("https://www.bing.com/maps?cp=48.8584~2.2945")
        assertTrue(parsed is ParsedLocation.Coordinates)
        val coords = parsed as ParsedLocation.Coordinates
        assertEquals(48.8584, coords.latitude, 0.0001)
        assertEquals(2.2945, coords.longitude, 0.0001)
    }

    @Test
    fun `bing maps converts directions`() {
        val parsed = BingMapsParser.parse("https://www.bing.com/maps?rtp=adr.Berlin~adr.Munich&mode=d")
        assertEquals(ParsedLocation.Directions(origin = "Berlin", destination = "Munich", mode = TravelMode.DRIVING), parsed)
    }

    // OpenStreetMap Tests
    @Test
    fun `osm converts marker coordinates`() {
        val parsed = OpenStreetMapParser.parse("https://www.openstreetmap.org/?mlat=52.5200&mlon=13.4050")
        assertTrue(parsed is ParsedLocation.Coordinates)
        val coords = parsed as ParsedLocation.Coordinates
        assertEquals(52.5200, coords.latitude, 0.0001)
        assertEquals(13.4050, coords.longitude, 0.0001)
    }

    @Test
    fun `osm converts map hash coordinates`() {
        val parsed = OpenStreetMapParser.parse("https://www.openstreetmap.org/#map=16/48.8584/2.2945")
        assertTrue(parsed is ParsedLocation.Coordinates)
        val coords = parsed as ParsedLocation.Coordinates
        assertEquals(48.8584, coords.latitude, 0.0001)
        assertEquals(2.2945, coords.longitude, 0.0001)
    }

    // Yandex Maps Tests
    @Test
    fun `yandex maps converts lon lat coordinates`() {
        // In Yandex Maps: ll=longitude,latitude
        val parsed = YandexMapsParser.parse("https://yandex.com/maps/?ll=13.4050,52.5200&text=Berlin")
        assertTrue(parsed is ParsedLocation.Coordinates)
        val coords = parsed as ParsedLocation.Coordinates
        assertEquals(52.5200, coords.latitude, 0.0001)
        assertEquals(13.4050, coords.longitude, 0.0001)
        assertEquals("Berlin", coords.label)
    }

    // Universal Dispatcher Tests
    @Test
    fun `universal parser dispatches correctly`() {
        val apple = UniversalMapParser.parse("https://maps.apple.com/?q=Berlin")
        assertEquals(ParsedLocation.SearchQuery("Berlin"), apple)

        val bing = UniversalMapParser.parse("https://www.bing.com/maps?q=Munich")
        assertEquals(ParsedLocation.SearchQuery("Munich"), bing)

        val osm = UniversalMapParser.parse("https://www.openstreetmap.org/?mlat=52.5&mlon=13.4")
        assertTrue(osm is ParsedLocation.Coordinates)
    }
}
