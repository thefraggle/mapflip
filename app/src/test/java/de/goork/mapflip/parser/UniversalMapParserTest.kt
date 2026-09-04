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
        assertEquals("other", UniversalMapParser.detectSourceService("https://example.com"))
        assertEquals("unknown", UniversalMapParser.detectSourceService(null))
        assertEquals("unknown", UniversalMapParser.detectSourceService(""))
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
}
