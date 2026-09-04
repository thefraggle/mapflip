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

    // HERE WeGo Tests
    @Test
    fun `here maps converts coordinates from share link`() {
        val parsed = HereMapsParser.parse("https://share.here.com/l/52.5200,13.4050,16,Berlin")
        assertTrue(parsed is ParsedLocation.Coordinates)
        val coords = parsed as ParsedLocation.Coordinates
        assertEquals(52.5200, coords.latitude, 0.0001)
        assertEquals(13.4050, coords.longitude, 0.0001)
    }

    @Test
    fun `here maps converts search query`() {
        val parsed = HereMapsParser.parse("https://wego.here.com/search/Brandenburg+Gate")
        assertEquals(ParsedLocation.SearchQuery("Brandenburg Gate"), parsed)
    }

    // Waze Tests
    @Test
    fun `waze converts coordinates link`() {
        val parsed = WazeMapsParser.parse("https://waze.com/ul?ll=48.8584,2.2945&navigate=yes")
        assertTrue(parsed is ParsedLocation.Coordinates)
        val coords = parsed as ParsedLocation.Coordinates
        assertEquals(48.8584, coords.latitude, 0.0001)
        assertEquals(2.2945, coords.longitude, 0.0001)
    }

    @Test
    fun `waze converts query link`() {
        val parsed = WazeMapsParser.parse("https://waze.com/ul?q=Eiffel+Tower&navigate=yes")
        assertEquals(ParsedLocation.SearchQuery("Eiffel Tower"), parsed)
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

        val here = UniversalMapParser.parse("https://share.here.com/l/52.52,13.40")
        assertTrue(here is ParsedLocation.Coordinates)

        val waze = UniversalMapParser.parse("https://waze.com/ul?q=Hamburg")
        assertEquals(ParsedLocation.SearchQuery("Hamburg"), waze)
    }

    // Encoding, Umlauts & Special Characters Tests across services
    @Test
    fun `bing maps handles umlauts and special characters`() {
        val parsedUmlaut = BingMapsParser.parse("https://www.bing.com/maps?q=M%C3%BCnchen+Hauptbahnhof")
        assertEquals(ParsedLocation.SearchQuery("München Hauptbahnhof"), parsedUmlaut)

        val parsedSpecial = BingMapsParser.parse("https://www.bing.com/maps?q=Bed+%26+Breakfast")
        assertEquals(ParsedLocation.SearchQuery("Bed & Breakfast"), parsedSpecial)
    }

    @Test
    fun `osm handles umlauts and cyrillic`() {
        val parsedUmlaut = OpenStreetMapParser.parse("https://www.openstreetmap.org/search?query=K%C3%B6lner+Dom")
        assertEquals(ParsedLocation.SearchQuery("Kölner Dom"), parsedUmlaut)

        val parsedCyrillic = OpenStreetMapParser.parse("https://www.openstreetmap.org/search?query=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0")
        assertEquals(ParsedLocation.SearchQuery("Москва"), parsedCyrillic)
    }

    @Test
    fun `yandex maps handles cyrillic, arabic and chinese`() {
        // Cyrillic (Red Square)
        val cyrillic = YandexMapsParser.parse("https://yandex.com/maps/?text=%D0%9A%D1%80%D0%B0%D1%81%D0%BD%D0%B0%D1%8F+%D0%BF%D0%BB%D0%BE%D1%89%D0%B0%D0%B4%D1%8C")
        assertEquals(ParsedLocation.SearchQuery("Красная площадь"), cyrillic)

        // Arabic (Burj Khalifa)
        val arabic = YandexMapsParser.parse("https://yandex.com/maps/?text=%D8%A8%D8%B1%D8%AC+%D8%AE%D9%84%D9%8A%D9%81%D8%A9")
        assertEquals(ParsedLocation.SearchQuery("برج خليفة"), arabic)

        // Chinese (Forbidden City)
        val chinese = YandexMapsParser.parse("https://yandex.com/maps/?text=%E6%95%85%E5%AE%AB%E5%8D%9A%E7%89%A9%E9%99%A2")
        assertEquals(ParsedLocation.SearchQuery("故宫博物院"), chinese)
    }

    @Test
    fun `here maps handles umlauts and special characters in label and search`() {
        val parsedSearch = HereMapsParser.parse("https://wego.here.com/search/Z%C3%BCrich+HB")
        assertEquals(ParsedLocation.SearchQuery("Zürich HB"), parsedSearch)

        val parsedCoords = HereMapsParser.parse("https://share.here.com/l/47.3769,8.5417?msg=Caf%C3%A9+%26+Bar")
        assertTrue(parsedCoords is ParsedLocation.Coordinates)
        val coords = parsedCoords as ParsedLocation.Coordinates
        assertEquals("Café & Bar", coords.label)
    }

    @Test
    fun `waze handles umlauts and plus characters`() {
        val parsed = WazeMapsParser.parse("https://waze.com/ul?q=C%2B%2B+Innovation+Lab&navigate=yes")
        assertEquals(ParsedLocation.SearchQuery("C++ Innovation Lab"), parsed)

        val parsedUmlaut = WazeMapsParser.parse("https://waze.com/ul?q=D%C3%BCsseldorf&navigate=yes")
        assertEquals(ParsedLocation.SearchQuery("Düsseldorf"), parsedUmlaut)
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
}
