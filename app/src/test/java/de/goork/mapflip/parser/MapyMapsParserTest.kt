package de.goork.mapflip.parser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class MapyMapsParserTest {

    @Test
    fun `coordinates use longitude first on both domains`() {
        for (host in listOf("mapy.com", "www.mapy.com", "mapy.cz", "www.mapy.cz")) {
            assertEquals(
                ParsedLocation.Coordinates(50.1, 14.4),
                UniversalMapParser.parse("https://$host/cs/zakladni?x=14.4&y=50.1")
            )
            assertEquals("mapy", UniversalMapParser.detectSourceService("https://$host/s/abcdef"))
        }
    }

    @Test
    fun `marker wins over map viewport`() {
        assertEquals(
            ParsedLocation.Coordinates(49.5, 16.6),
            MapyMapsParser.parse("https://mapy.com/turisticka?source=coor&id=16.6%2C49.5&x=14&y=50")
        )
        assertEquals(
            ParsedLocation.Coordinates(50.1, 14.4),
            MapyMapsParser.parse("https://mapy.com/fnc/v1/showmap?center=14.4%2C50.1&marker=true")
        )
    }

    @Test
    fun `search wins over viewport and decodes unicode`() {
        assertEquals(
            ParsedLocation.SearchQuery("Český Krumlov"),
            UniversalMapParser.parse("https://mapy.com/fnc/v1/search?query=%C4%8Cesk%C3%BD+Krumlov&center=14,50")
        )
    }

    @Test
    fun `documented routes preserve origin destination and mode`() {
        assertEquals(
            ParsedLocation.Directions("50.1,14.4", "49.5,16.6", TravelMode.WALKING),
            MapyMapsParser.parse("https://mapy.cz/fnc/v1/route?start=14.4,50.1&end=16.6,49.5&routeType=foot_hiking")
        )
        assertEquals(
            ParsedLocation.Navigation("49.5,16.6", TravelMode.BICYCLING),
            MapyMapsParser.parse("https://mapy.com/fnc/v1/route?end=16.6,49.5&routeType=bike_road")
        )
    }

    @Test
    fun `unsupported objects and routes never use viewport as destination`() {
        for (suffix in listOf(
            "?source=base&id=123&x=14&y=50",
            "?rc=encoded&x=14&y=50",
            "?vlastni-body&dim=694995a9243bd284f44cbdf9&x=16.0359366&y=49.7071642&z=19",
            "?planovani-trasy&x=14&y=50",
            "?vlastni-body&x=14&y=50",
            "/fnc/v1/route?end=14,50&waypoints=15,49",
            "/s/abcdef?x=14&y=50"
        )) {
            val url = "https://mapy.com$suffix"
            assertEquals(ParsedLocation.WebFallback(url), MapyMapsParser.parse(url))
        }
    }

    @Test
    fun `malformed and out of range coordinates fall back`() {
        for (query in listOf(
            "x=181&y=50",
            "x=14&y=91",
            "x=NaN&y=50",
            "x=14",
            "center=14,Infinity",
            "source=coor&id=bad&x=14&y=50",
            "query=%ZZ"
        )) {
            val url = "https://mapy.com/?$query"
            assertEquals(ParsedLocation.WebFallback(url), MapyMapsParser.parse(url))
        }
    }

    @Test
    fun `extract share text and reject unrelated hosts`() {
        assertEquals("https://mapy.cz/s/abcdef", UniversalMapParser.extractMapUrl("Visit https://mapy.cz/s/abcdef."))
        for (url in listOf(
            "https://mapy.com.evil.test/s/foo",
            "https://evil.test/?url=https://mapy.com",
            "https://mapy.com@evil.test/",
            "ftp://mapy.com/",
            "https://mapy.com:1234/"
        )) {
            assertFalse(MapyMapsParser.canParse(url))
        }
    }
}
