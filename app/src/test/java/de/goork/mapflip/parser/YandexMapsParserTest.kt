package de.goork.mapflip.parser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class YandexMapsParserTest {

    @Test
    fun `canParse detects yandex domains and maps paths`() {
        assertTrue(YandexMapsParser.canParse("https://yandex.com/maps/?ll=13.4,52.5"))
        assertTrue(YandexMapsParser.canParse("https://yandex.ru/maps/?text=Moscow"))
        assertTrue(YandexMapsParser.canParse("https://maps.yandex.ru/maps/?ll=37.6,55.7"))
        assertFalse(YandexMapsParser.canParse("https://google.com/maps"))
        assertFalse(YandexMapsParser.canParse("https://yandex.com/search"))
    }

    @Test
    fun `extractUrl extracts yandex links from text`() {
        val text = "Map link: https://yandex.com/maps/?ll=13.4050,52.5200&text=Berlin!"
        assertEquals("https://yandex.com/maps/?ll=13.4050,52.5200&text=Berlin", YandexMapsParser.extractUrl(text))
    }

    @Test
    fun `parse converts longitude and latitude correctly with yandex ordering`() {
        // Yandex uses ll=lon,lat
        val parsed = YandexMapsParser.parse("https://yandex.com/maps/?ll=13.4050,52.5200&text=Berlin")
        assertTrue(parsed is ParsedLocation.Coordinates)
        val coords = parsed as ParsedLocation.Coordinates
        assertEquals(52.5200, coords.latitude, 0.0001)
        assertEquals(13.4050, coords.longitude, 0.0001)
        assertEquals("Berlin", coords.label)
    }

    @Test
    fun `parse converts search query or what where`() {
        val parsedText = YandexMapsParser.parse("https://yandex.ru/maps/?text=Saint+Petersburg")
        assertEquals(ParsedLocation.SearchQuery("Saint Petersburg"), parsedText)

        val parsedWhatWhere = YandexMapsParser.parse("https://yandex.com/maps/?what[where]=Novosibirsk")
        assertEquals(ParsedLocation.SearchQuery("Novosibirsk"), parsedWhatWhere)
    }

    @Test
    fun `parse converts route and travel modes`() {
        val driving = YandexMapsParser.parse("https://yandex.ru/maps/?rtext=55.75,37.61~59.93,30.31&rtt=auto")
        assertEquals(ParsedLocation.Directions(origin = "55.75,37.61", destination = "59.93,30.31", mode = TravelMode.DRIVING), driving)

        val walking = YandexMapsParser.parse("https://yandex.ru/maps/?rtext=Moscow~Tver&rtt=pd")
        assertEquals(ParsedLocation.Directions(origin = "Moscow", destination = "Tver", mode = TravelMode.WALKING), walking)

        val transit = YandexMapsParser.parse("https://yandex.ru/maps/?rtext=Moscow~Sochi&rtt=mt")
        assertEquals(ParsedLocation.Directions(origin = "Moscow", destination = "Sochi", mode = TravelMode.TRANSIT), transit)

        val bicycling = YandexMapsParser.parse("https://yandex.ru/maps/?rtext=PointA~PointB&rtt=bc")
        assertEquals(ParsedLocation.Directions(origin = "PointA", destination = "PointB", mode = TravelMode.BICYCLING), bicycling)
    }

    @Test
    fun `parse handles cyrillic, arabic, chinese and umlauts`() {
        val cyrillic = YandexMapsParser.parse("https://yandex.com/maps/?text=%D0%9A%D1%80%D0%B0%D1%81%D0%BD%D0%B0%D1%8F+%D0%BF%D0%BB%D0%BE%D1%89%D0%B0%D0%B4%D1%8C")
        assertEquals(ParsedLocation.SearchQuery("Красная площадь"), cyrillic)

        val arabic = YandexMapsParser.parse("https://yandex.com/maps/?text=%D8%A8%D8%B1%D8%AC+%D8%AE%D9%84%D9%8A%D9%81%D8%A9")
        assertEquals(ParsedLocation.SearchQuery("برج خليفة"), arabic)

        val chinese = YandexMapsParser.parse("https://yandex.com/maps/?text=%E6%95%85%E5%AE%AB%E5%8D%9A%E7%89%A9%E9%99%A2")
        assertEquals(ParsedLocation.SearchQuery("故宫博物院"), chinese)
    }

    @Test
    fun `parse handles null island 0,0`() {
        val parsed = YandexMapsParser.parse("https://yandex.com/maps/?ll=0.0,0.0")
        assertTrue(parsed is ParsedLocation.Coordinates)
        val coords = parsed as ParsedLocation.Coordinates
        assertEquals(0.0, coords.latitude, 0.00001)
        assertEquals(0.0, coords.longitude, 0.00001)
    }

    @Test
    fun `parse rejects out-of-bounds coordinates`() {
        // Remember ll=lon,lat -> 13.4 is lon, 95.0 is lat (out of bounds)
        val outOfBoundsLat = YandexMapsParser.parse("https://yandex.com/maps/?ll=13.4,95.0")
        assertTrue(outOfBoundsLat is ParsedLocation.WebFallback)

        // 195.0 is lon (out of bounds), 52.5 is lat
        val outOfBoundsLon = YandexMapsParser.parse("https://yandex.com/maps/?ll=195.0,52.5")
        assertTrue(outOfBoundsLon is ParsedLocation.WebFallback)
    }

    @Test
    fun `parse handles malformed or empty URLs safely`() {
        assertEquals(ParsedLocation.Home, YandexMapsParser.parse(""))
        assertEquals(ParsedLocation.Home, YandexMapsParser.parse("   "))

        val malformed = YandexMapsParser.parse("https://yandex.com/maps/?text=%2")
        assertTrue(malformed is ParsedLocation.WebFallback)

        val incomplete = YandexMapsParser.parse("https://yandex.com/maps/")
        assertTrue(incomplete is ParsedLocation.WebFallback)
    }
}
