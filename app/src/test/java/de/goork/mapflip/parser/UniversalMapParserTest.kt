package de.goork.mapflip.parser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UniversalMapParserTest {

    @Test
    fun `extractMapUrl extracts Apple Maps URL from surrounding message text`() {
        val message = "Hier ist der Treffpunkt: https://maps.apple.com/?ll=48.137154,11.576124 bis gleich!"
        val extracted = UniversalMapParser.extractMapUrl(message)
        assertNotNull(extracted)
        assertTrue(extracted!!.startsWith("https://maps.apple.com/?ll=48.137154,11.576124"))
    }

    @Test
    fun `extractMapUrl extracts Bing Maps URL from text`() {
        val text = "Schau mal hier https://www.bing.com/maps?cp=52.5200~13.4050 im Browser"
        val extracted = UniversalMapParser.extractMapUrl(text)
        assertNotNull(extracted)
        assertTrue(extracted!!.startsWith("https://www.bing.com/maps?cp=52.5200~13.4050"))
    }

    @Test
    fun `extractMapUrl extracts OpenStreetMap URL from text`() {
        val text = "OSM Link: https://www.openstreetmap.org/#map=17/52.5200/13.4050 - Open Source!"
        val extracted = UniversalMapParser.extractMapUrl(text)
        assertNotNull(extracted)
        assertTrue(extracted!!.startsWith("https://www.openstreetmap.org/#map=17/52.5200/13.4050"))
    }

    @Test
    fun `extractMapUrl extracts Yandex Maps URL from text`() {
        val text = "Adresse: https://yandex.com/maps/?ll=37.620393,55.753960&z=15 danke"
        val extracted = UniversalMapParser.extractMapUrl(text)
        assertNotNull(extracted)
        assertTrue(extracted!!.startsWith("https://yandex.com/maps/?ll=37.620393,55.753960&z=15"))
    }

    @Test
    fun `extractMapUrl extracts HERE WeGo URL from text`() {
        val text = "Route: https://wego.here.com/directions/mix/Berlin/Munich Gute Fahrt!"
        val extracted = UniversalMapParser.extractMapUrl(text)
        assertNotNull(extracted)
        assertTrue(extracted!!.startsWith("https://wego.here.com/directions/mix/Berlin/Munich"))
    }

    @Test
    fun `extractMapUrl extracts Waze URL from text`() {
        val text = "Navigiere via https://waze.com/ul?ll=48.137,11.576&navigate=yes um Stau zu umfahren"
        val extracted = UniversalMapParser.extractMapUrl(text)
        assertNotNull(extracted)
        assertTrue(extracted!!.startsWith("https://waze.com/ul?ll=48.137,11.576&navigate=yes"))
    }

    @Test
    fun `extractMapUrl returns null for blank or non-map strings`() {
        assertNull(UniversalMapParser.extractMapUrl(null))
        assertNull(UniversalMapParser.extractMapUrl(""))
        assertNull(UniversalMapParser.extractMapUrl("   \n\t  "))
        assertNull(UniversalMapParser.extractMapUrl("Hallo wie geht es dir?"))
        assertNull(UniversalMapParser.extractMapUrl("https://www.wikipedia.org"))
        assertNull(UniversalMapParser.extractMapUrl("https://github.com/thefraggle/mapflip"))
    }

    @Test
    fun `detectSourceService identifies all map services correctly`() {
        assertEquals("apple", UniversalMapParser.detectSourceService("https://maps.apple.com/?q=Berlin"))
        assertEquals("bing", UniversalMapParser.detectSourceService("https://www.bing.com/maps?q=Berlin"))
        assertEquals("osm", UniversalMapParser.detectSourceService("https://www.openstreetmap.org/#map=17/52.52/13.40"))
        assertEquals("osm", UniversalMapParser.detectSourceService("https://osm.org/go/0EEQjE?m="))
        assertEquals("yandex", UniversalMapParser.detectSourceService("https://yandex.ru/maps/213/moscow/"))
        assertEquals("here", UniversalMapParser.detectSourceService("https://wego.here.com/location?map=52.52,13.40"))
        assertEquals("waze", UniversalMapParser.detectSourceService("https://waze.com/ul?q=Munich"))
        assertEquals("plus_code", UniversalMapParser.detectSourceService("https://plus.codes/8FW4V75V+8G"))
        assertEquals("plus_code", UniversalMapParser.detectSourceService("8FW4V75V+8G"))
        assertEquals("geo_coordinates", UniversalMapParser.detectSourceService("geo:52.5200,13.4050"))
        assertEquals("geo_coordinates", UniversalMapParser.detectSourceService("52.520008, 13.404954"))
        assertEquals("other", UniversalMapParser.detectSourceService("https://example.com"))
        assertEquals("unknown", UniversalMapParser.detectSourceService(null))
        assertEquals("unknown", UniversalMapParser.detectSourceService(""))
    }

    @Test
    fun `extractMapUrl extracts raw coordinates from text`() {
        val text = "Komm zu 52.520008, 13.404954 heute"
        val extracted = UniversalMapParser.extractMapUrl(text)
        assertNotNull(extracted)
        assertEquals("geo:52.520008,13.404954", extracted)
    }

    @Test
    fun `extractMapUrl extracts Plus Code from text`() {
        val text = "Standort: 8FW4V75V+8G in Paris"
        val extracted = UniversalMapParser.extractMapUrl(text)
        assertNotNull(extracted)
        assertEquals("https://plus.codes/8FW4V75V+8G", extracted)
    }

    @Test
    fun `parse extracts location directly from raw message containing map URL`() {
        val chatMessage = "Treffpunkt hier: https://maps.apple.com/?ll=48.137154,11.576124 Bis später!"
        val parsed = UniversalMapParser.parse(chatMessage)
        assertTrue(parsed is ParsedLocation.Coordinates)
        val coords = parsed as ParsedLocation.Coordinates
        assertEquals(48.137154, coords.latitude, 0.000001)
        assertEquals(11.576124, coords.longitude, 0.000001)
    }

    @Test
    fun `parse returns WebFallback for unknown valid web URLs`() {
        val unknownUrl = "https://custom-maps-service.example.org/place?id=12345"
        val parsed = UniversalMapParser.parse(unknownUrl)
        assertTrue(parsed is ParsedLocation.WebFallback)
        assertEquals(unknownUrl, (parsed as ParsedLocation.WebFallback).fallbackUrl)
    }

    @Test
    fun `parse returns Home for malformed URI syntax`() {
        val malformed = "http://invalid^url|test"
        val parsed = UniversalMapParser.parse(malformed)
        assertTrue(parsed is ParsedLocation.Home)
    }

    @Test
    fun `universal parser dispatches across all providers correctly`() {
        val apple = UniversalMapParser.parse("https://maps.apple.com/?q=Berlin")
        assertEquals(ParsedLocation.SearchQuery("Berlin"), apple)

        val bing = UniversalMapParser.parse("https://www.bing.com/maps?q=Munich")
        assertEquals(ParsedLocation.SearchQuery("Munich"), bing)

        val osm = UniversalMapParser.parse("https://www.openstreetmap.org/?mlat=52.5&mlon=13.4")
        assertTrue(osm is ParsedLocation.Coordinates)

        val here = UniversalMapParser.parse("https://share.here.com/l/52.52,13.40")
        assertTrue(here is ParsedLocation.Coordinates)

        val waze = UniversalMapParser.parse("https://waze.com/ul?q=Hamburg")
        assertEquals(ParsedLocation.SearchQuery("Hamburg"), waze)

        val yandex = UniversalMapParser.parse("https://yandex.com/maps/?ll=13.4,52.5&text=Berlin")
        assertTrue(yandex is ParsedLocation.Coordinates)
    }

    @Test
    fun `universal parser handles non-latin scripts and umlauts across all providers`() {
        val arabicApple = UniversalMapParser.parse("https://maps.apple.com/?q=%D8%A8%D8%B1%D8%AC+%D8%AE%D9%84%D9%8A%D9%81%D8%A9")
        assertEquals(ParsedLocation.SearchQuery("برج خليفة"), arabicApple)

        val cyrillicYandex = UniversalMapParser.parse("https://yandex.ru/maps/?text=%D0%AD%D1%80%D0%BC%D0%B8%D1%82%D0%B0%D0%B6")
        assertEquals(ParsedLocation.SearchQuery("Эрмитаж"), cyrillicYandex)

        val chineseOsm = UniversalMapParser.parse("https://www.openstreetmap.org/search?query=%E5%8C%97%E4%BA%AC")
        assertEquals(ParsedLocation.SearchQuery("北京"), chineseOsm)

        val umlautHere = UniversalMapParser.parse("https://wego.here.com/search/N%C3%BCrnberg")
        assertEquals(ParsedLocation.SearchQuery("Nürnberg"), umlautHere)
    }

    @Test
    fun `parsers reject out of bounds coordinates and fallback to web or search`() {
        val appleOutOfBounds = AppleMapsParser.parse("https://maps.apple.com/?ll=95.0,13.4")
        assertTrue(appleOutOfBounds is ParsedLocation.WebFallback)

        val osmOutOfBounds = OpenStreetMapParser.parse("https://www.openstreetmap.org/?mlat=95.0&mlon=13.4")
        assertTrue(osmOutOfBounds is ParsedLocation.WebFallback)

        val yandexOutOfBounds = YandexMapsParser.parse("https://yandex.com/maps/?ll=13.4,95.0")
        assertTrue(yandexOutOfBounds is ParsedLocation.WebFallback)

        val wazeOutOfBounds = WazeMapsParser.parse("https://waze.com/ul?ll=95.0,13.4")
        assertTrue(wazeOutOfBounds is ParsedLocation.WebFallback)

        val hereOutOfBounds = HereMapsParser.parse("https://share.here.com/l/95.0,13.4")
        assertTrue(hereOutOfBounds is ParsedLocation.WebFallback)

        val bingOutOfBounds = BingMapsParser.parse("https://www.bing.com/maps?cp=95.0~13.4")
        assertTrue(bingOutOfBounds is ParsedLocation.WebFallback)
    }
}
