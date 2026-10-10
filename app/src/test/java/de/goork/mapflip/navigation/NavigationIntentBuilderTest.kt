package de.goork.mapflip.navigation

import de.goork.mapflip.parser.ParsedLocation
import de.goork.mapflip.parser.TravelMode
import de.goork.mapflip.ui.Strings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NavigationIntentBuilderTest {

    @Test
    fun `builds correct Google Maps URIs`() {
        val coords = ParsedLocation.Coordinates(52.5200, 13.4050, label = "Berlin")
        val uri = NavigationIntentBuilder.buildGoogleMapsUriString(coords)
        assertEquals("geo:52.520000,13.405000?q=Berlin", uri)

        val nav = ParsedLocation.Navigation("Munich", TravelMode.DRIVING)
        assertEquals("google.navigation:q=Munich&mode=d", NavigationIntentBuilder.buildGoogleMapsUriString(nav))
    }

    @Test
    fun `builds correct Waze URIs`() {
        val coords = ParsedLocation.Coordinates(52.5200, 13.4050)
        assertEquals("waze://?ll=52.52,13.405&navigate=yes", NavigationIntentBuilder.buildWazeUriString(coords))

        val search = ParsedLocation.SearchQuery("Brandenburg Gate")
        assertEquals("waze://?q=Brandenburg+Gate&navigate=yes", NavigationIntentBuilder.buildWazeUriString(search))

        val nav = ParsedLocation.Navigation("Alexanderplatz")
        assertEquals("waze://?q=Alexanderplatz&navigate=yes", NavigationIntentBuilder.buildWazeUriString(nav))

        val home = ParsedLocation.Home
        assertEquals("waze://", NavigationIntentBuilder.buildWazeUriString(home))
    }

    @Test
    fun `builds correct Organic Maps URIs`() {
        val coords = ParsedLocation.Coordinates(52.5200, 13.4050, label = "Checkpoint Charlie")
        assertEquals("om://map?v=1&ll=52.52,13.405&n=Checkpoint+Charlie", NavigationIntentBuilder.buildOrganicMapsUriString(coords))

        val search = ParsedLocation.SearchQuery("Berlin TV Tower")
        assertEquals("om://search?query=Berlin+TV+Tower", NavigationIntentBuilder.buildOrganicMapsUriString(search))
    }

    @Test
    fun `builds correct CoMaps URIs`() {
        val coords = ParsedLocation.Coordinates(52.5200, 13.4050, label = "Checkpoint Charlie")
        assertEquals("cm://map?v=1&ll=52.52,13.405&n=Checkpoint+Charlie", NavigationIntentBuilder.buildCoMapsUriString(coords))

        val search = ParsedLocation.SearchQuery("Berlin TV Tower")
        assertEquals("cm://search?query=Berlin+TV+Tower", NavigationIntentBuilder.buildCoMapsUriString(search))

        val nav = ParsedLocation.Navigation("Alexanderplatz")
        assertEquals("cm://search?query=Alexanderplatz", NavigationIntentBuilder.buildCoMapsUriString(nav))

        val dir = ParsedLocation.Directions(origin = "Berlin", destination = "Potsdam")
        assertEquals("cm://search?query=Potsdam", NavigationIntentBuilder.buildCoMapsUriString(dir))

        val home = ParsedLocation.Home
        assertEquals("cm://", NavigationIntentBuilder.buildCoMapsUriString(home))
    }

    @Test
    fun `builds correct Vela URIs`() {
        val coords = ParsedLocation.Coordinates(52.5200, 13.4050)
        assertEquals("https://maps.google.com/?q=52.52,13.405", NavigationIntentBuilder.buildVelaUriString(coords))

        val coordsWithMode = ParsedLocation.Coordinates(52.5200, 13.4050, mode = TravelMode.DRIVING)
        assertEquals("https://maps.google.com/maps/dir/?api=1&destination=52.52%2C13.405&travelmode=driving", NavigationIntentBuilder.buildVelaUriString(coordsWithMode))

        val search = ParsedLocation.SearchQuery("Berlin TV Tower")
        assertEquals("https://maps.google.com/?q=Berlin+TV+Tower", NavigationIntentBuilder.buildVelaUriString(search))

        val nav = ParsedLocation.Navigation("Alexanderplatz", TravelMode.WALKING)
        assertEquals("https://maps.google.com/maps/dir/?api=1&destination=Alexanderplatz&travelmode=walking", NavigationIntentBuilder.buildVelaUriString(nav))

        val dir = ParsedLocation.Directions(origin = "Berlin", destination = "Potsdam", mode = TravelMode.TRANSIT)
        assertEquals("https://maps.google.com/maps/dir/?api=1&destination=Potsdam&origin=Berlin&travelmode=transit", NavigationIntentBuilder.buildVelaUriString(dir))

        val home = ParsedLocation.Home
        assertEquals("https://maps.google.com", NavigationIntentBuilder.buildVelaUriString(home))

        val gmapsFallback = ParsedLocation.WebFallback("https://maps.google.com/maps?q=Berlin")
        assertEquals("https://maps.google.com/maps?q=Berlin", NavigationIntentBuilder.buildVelaUriString(gmapsFallback))

        val shortFallback = ParsedLocation.WebFallback("https://maps.app.goo.gl/xyz")
        assertEquals("https://maps.app.goo.gl/xyz", NavigationIntentBuilder.buildVelaUriString(shortFallback))

        val genericFallback = ParsedLocation.WebFallback("https://example.com/map")
        assertEquals("https://maps.google.com/?q=https%3A%2F%2Fexample.com%2Fmap", NavigationIntentBuilder.buildVelaUriString(genericFallback))
    }

    @Test
    fun `builds correct OsmAnd URIs`() {
        val coords = ParsedLocation.Coordinates(52.5200, 13.4050)
        assertEquals("osmandmaps://?lat=52.52&lon=13.405&z=16", NavigationIntentBuilder.buildOsmAndUriString(coords))

        val search = ParsedLocation.SearchQuery("Reichstag")
        assertEquals("osmandmaps://?q=Reichstag", NavigationIntentBuilder.buildOsmAndUriString(search))
    }

    @Test
    fun `builds correct Generic Geo URIs`() {
        val coords = ParsedLocation.Coordinates(52.5200, 13.4050, label = "Berlin")
        assertEquals("geo:52.520000,13.405000?q=Berlin", NavigationIntentBuilder.buildGenericGeoUriString(coords))

        val search = ParsedLocation.SearchQuery("Potsdam")
        assertEquals("geo:0,0?q=Potsdam", NavigationIntentBuilder.buildGenericGeoUriString(search))
    }

    @Test
    fun `builds correct Here WeGo URIs`() {
        val coords = ParsedLocation.Coordinates(52.5200, 13.4050, label = "Berlin")
        assertEquals("https://share.here.com/l/52.520000,13.405000?msg=Berlin", NavigationIntentBuilder.buildHereWeGoUriString(coords))

        val search = ParsedLocation.SearchQuery("Alexanderplatz")
        assertEquals("https://wego.here.com/search/Alexanderplatz", NavigationIntentBuilder.buildHereWeGoUriString(search))
    }

    @Test
    fun `builds correct Yandex Maps URIs`() {
        val coords = ParsedLocation.Coordinates(52.5200, 13.4050, label = "Red Square")
        assertEquals("yandexmaps://maps.yandex.ru/?ll=13.405,52.52&z=16&text=Red+Square", NavigationIntentBuilder.buildYandexMapsUriString(coords))

        val search = ParsedLocation.SearchQuery("Kremlin")
        assertEquals("yandexmaps://maps.yandex.ru/?text=Kremlin", NavigationIntentBuilder.buildYandexMapsUriString(search))
    }

    @Test
    fun `buildUriString dispatches to selected target app`() {
        val loc = ParsedLocation.Coordinates(48.8584, 2.2945)

        val googleUri = NavigationIntentBuilder.buildUriString(loc, TargetNavigationApp.GOOGLE_MAPS)
        assertTrue(googleUri.startsWith("geo:48.858400,2.294500"))

        val wazeUri = NavigationIntentBuilder.buildUriString(loc, TargetNavigationApp.WAZE)
        assertEquals("waze://?ll=48.8584,2.2945&navigate=yes", wazeUri)

        val omUri = NavigationIntentBuilder.buildUriString(loc, TargetNavigationApp.ORGANIC_MAPS)
        assertEquals("om://map?v=1&ll=48.8584,2.2945", omUri)

        val osmandUri = NavigationIntentBuilder.buildUriString(loc, TargetNavigationApp.OSMAND)
        assertEquals("osmandmaps://?lat=48.8584&lon=2.2945&z=16", osmandUri)

        val hereUri = NavigationIntentBuilder.buildUriString(loc, TargetNavigationApp.HERE_WEGO)
        assertEquals("https://share.here.com/l/48.858400,2.294500", hereUri)

        val yandexUri = NavigationIntentBuilder.buildUriString(loc, TargetNavigationApp.YANDEX_MAPS)
        assertEquals("yandexmaps://maps.yandex.ru/?ll=2.2945,48.8584&z=16", yandexUri)

        val comapsUri = NavigationIntentBuilder.buildUriString(loc, TargetNavigationApp.COMAPS)
        assertEquals("cm://map?v=1&ll=48.8584,2.2945", comapsUri)

        val velaUri = NavigationIntentBuilder.buildUriString(loc, TargetNavigationApp.VELA)
        assertEquals("https://maps.google.com/?q=48.8584,2.2945", velaUri)

        val sysUri = NavigationIntentBuilder.buildUriString(loc, TargetNavigationApp.SYSTEM_PICKER)
        assertTrue(sysUri.startsWith("geo:48.858400,2.294500"))
    }

    @Test
    fun `dynamic testButtonLabel formats correctly for all apps in German and English`() {
        val sDe = Strings.getStrings("de")
        assertEquals("In Google Maps testen", sDe.testButtonLabel(TargetNavigationApp.GOOGLE_MAPS))
        assertEquals("In Waze testen", sDe.testButtonLabel(TargetNavigationApp.WAZE))
        assertEquals("In Organic Maps testen", sDe.testButtonLabel(TargetNavigationApp.ORGANIC_MAPS))
        assertEquals("In CoMaps testen", sDe.testButtonLabel(TargetNavigationApp.COMAPS))
        assertEquals("In OsmAnd testen", sDe.testButtonLabel(TargetNavigationApp.OSMAND))
        assertEquals("In Vela testen", sDe.testButtonLabel(TargetNavigationApp.VELA))
        assertEquals("In HERE WeGo testen", sDe.testButtonLabel(TargetNavigationApp.HERE_WEGO))
        assertEquals("In Yandex Maps testen", sDe.testButtonLabel(TargetNavigationApp.YANDEX_MAPS))
        assertEquals("In Ziel-Navigations-App testen", sDe.testButtonLabel(TargetNavigationApp.SYSTEM_PICKER))

        val sEn = Strings.getStrings("en")
        assertEquals("Test in Google Maps", sEn.testButtonLabel(TargetNavigationApp.GOOGLE_MAPS))
        assertEquals("Test in Waze", sEn.testButtonLabel(TargetNavigationApp.WAZE))
        assertEquals("Test in Organic Maps", sEn.testButtonLabel(TargetNavigationApp.ORGANIC_MAPS))
        assertEquals("Test in CoMaps", sEn.testButtonLabel(TargetNavigationApp.COMAPS))
        assertEquals("Test in OsmAnd", sEn.testButtonLabel(TargetNavigationApp.OSMAND))
        assertEquals("Test in Vela", sEn.testButtonLabel(TargetNavigationApp.VELA))
        assertEquals("Test in HERE WeGo", sEn.testButtonLabel(TargetNavigationApp.HERE_WEGO))
        assertEquals("Test in Yandex Maps", sEn.testButtonLabel(TargetNavigationApp.YANDEX_MAPS))
        assertEquals("Test in Navigation App", sEn.testButtonLabel(TargetNavigationApp.SYSTEM_PICKER))
    }

    @Test
    fun `testButtonLabel works across all supported languages without exception`() {
        for (lang in Strings.SUPPORTED_LANGUAGES) {
            val s = Strings.getStrings(lang.code)
            for (app in TargetNavigationApp.entries) {
                val label = s.testButtonLabel(app)
                assertTrue("Label should not be blank for ${lang.code} with $app", label.isNotBlank())
            }
        }
    }

    @Test
    fun `builds encoded URIs for queries with umlauts and diacritics across apps`() {
        val loc = ParsedLocation.SearchQuery("München Marienplatz")

        // Google Maps
        assertEquals("geo:0,0?q=M%C3%BCnchen+Marienplatz", NavigationIntentBuilder.buildGoogleMapsUriString(loc))
        // Waze
        assertEquals("waze://?q=M%C3%BCnchen+Marienplatz&navigate=yes", NavigationIntentBuilder.buildWazeUriString(loc))
        // Organic Maps
        assertEquals("om://search?query=M%C3%BCnchen+Marienplatz", NavigationIntentBuilder.buildOrganicMapsUriString(loc))
        // CoMaps
        assertEquals("cm://search?query=M%C3%BCnchen+Marienplatz", NavigationIntentBuilder.buildCoMapsUriString(loc))
        // OsmAnd
        assertEquals("osmandmaps://?q=M%C3%BCnchen+Marienplatz", NavigationIntentBuilder.buildOsmAndUriString(loc))
        // Vela
        assertEquals("https://maps.google.com/?q=M%C3%BCnchen+Marienplatz", NavigationIntentBuilder.buildVelaUriString(loc))
        // HERE WeGo
        assertEquals("https://wego.here.com/search/M%C3%BCnchen+Marienplatz", NavigationIntentBuilder.buildHereWeGoUriString(loc))
        // Yandex Maps
        assertEquals("yandexmaps://maps.yandex.ru/?text=M%C3%BCnchen+Marienplatz", NavigationIntentBuilder.buildYandexMapsUriString(loc))
    }

    @Test
    fun `builds encoded URIs for queries with special characters ampersand plus and percent across apps`() {
        val queryWithAmpersand = ParsedLocation.SearchQuery("Café & Bar")
        assertEquals("geo:0,0?q=Caf%C3%A9+%26+Bar", NavigationIntentBuilder.buildGoogleMapsUriString(queryWithAmpersand))
        assertEquals("waze://?q=Caf%C3%A9+%26+Bar&navigate=yes", NavigationIntentBuilder.buildWazeUriString(queryWithAmpersand))

        val queryWithPlus = ParsedLocation.SearchQuery("C++ Innovation")
        assertEquals("geo:0,0?q=C%2B%2B+Innovation", NavigationIntentBuilder.buildGoogleMapsUriString(queryWithPlus))

        val queryWithPercent = ParsedLocation.SearchQuery("Top 10% Club")
        assertEquals("geo:0,0?q=Top+10%25+Club", NavigationIntentBuilder.buildGoogleMapsUriString(queryWithPercent))
    }

    @Test
    fun `builds properly encoded URIs for non-latin alphabets (Cyrillic, Arabic, Chinese)`() {
        // Cyrillic (Red Square)
        val cyrillicLoc = ParsedLocation.SearchQuery("Красная площадь")
        assertEquals("geo:0,0?q=%D0%9A%D1%80%D0%B0%D1%81%D0%BD%D0%B0%D1%8F+%D0%BF%D0%BB%D0%BE%D1%89%D0%B0%D0%B4%D1%8C",
            NavigationIntentBuilder.buildGoogleMapsUriString(cyrillicLoc))
        assertEquals("yandexmaps://maps.yandex.ru/?text=%D0%9A%D1%80%D0%B0%D1%81%D0%BD%D0%B0%D1%8F+%D0%BF%D0%BB%D0%BE%D1%89%D0%B0%D0%B4%D1%8C",
            NavigationIntentBuilder.buildYandexMapsUriString(cyrillicLoc))

        // Arabic (Burj Khalifa)
        val arabicLoc = ParsedLocation.SearchQuery("برج خليفة")
        assertEquals("geo:0,0?q=%D8%A8%D8%B1%D8%AC+%D8%AE%D9%84%D9%8A%D9%81%D8%A9",
            NavigationIntentBuilder.buildGoogleMapsUriString(arabicLoc))

        // Chinese (Forbidden City)
        val chineseLoc = ParsedLocation.SearchQuery("故宫博物院")
        assertEquals("geo:0,0?q=%E6%95%85%E5%AE%AB%E5%8D%9A%E7%89%A9%E9%99%A2",
            NavigationIntentBuilder.buildGoogleMapsUriString(chineseLoc))
    }

    @Test
    fun `builds encoded coordinates labels and navigation with umlauts and non-latin scripts`() {
        val coordsWithLabel = ParsedLocation.Coordinates(48.1371, 11.5754, label = "München Zentrum")
        assertEquals("geo:48.137100,11.575400?q=M%C3%BCnchen+Zentrum",
            NavigationIntentBuilder.buildGoogleMapsUriString(coordsWithLabel))
        assertEquals("om://map?v=1&ll=48.1371,11.5754&n=M%C3%BCnchen+Zentrum",
            NavigationIntentBuilder.buildOrganicMapsUriString(coordsWithLabel))
        assertEquals("https://share.here.com/l/48.137100,11.575400?msg=M%C3%BCnchen+Zentrum",
            NavigationIntentBuilder.buildHereWeGoUriString(coordsWithLabel))

        val navCyrillic = ParsedLocation.Navigation("Москва", TravelMode.DRIVING)
        assertEquals("google.navigation:q=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0&mode=d",
            NavigationIntentBuilder.buildGoogleMapsUriString(navCyrillic))
        assertEquals("yandexmaps://maps.yandex.ru/?rtext=~%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0&rtt=auto",
            NavigationIntentBuilder.buildYandexMapsUriString(navCyrillic))

        val directionsUmlaut = ParsedLocation.Directions("Köln", "München", TravelMode.TRANSIT)
        assertEquals("https://www.google.com/maps/dir/?api=1&origin=K%C3%B6ln&destination=M%C3%BCnchen&travelmode=transit",
            NavigationIntentBuilder.buildGoogleMapsUriString(directionsUmlaut))
    }

    @Test
    fun `builds correct URIs with travel modes for coordinates, here wego, and yandex`() {
        // Coordinates with walking mode
        val walkCoords = ParsedLocation.Coordinates(52.5200, 13.4050, mode = TravelMode.WALKING)
        assertEquals("google.navigation:q=52.520000,13.405000&mode=w",
            NavigationIntentBuilder.buildGoogleMapsUriString(walkCoords))
        assertEquals("https://wego.here.com/directions/walk//52.520000,13.405000",
            NavigationIntentBuilder.buildHereWeGoUriString(walkCoords))
        assertEquals("yandexmaps://maps.yandex.ru/?ll=13.405,52.52&z=16&rtt=pd",
            NavigationIntentBuilder.buildYandexMapsUriString(walkCoords))

        // Coordinates with bicycle mode
        val bikeCoords = ParsedLocation.Coordinates(52.5200, 13.4050, mode = TravelMode.BICYCLING)
        assertEquals("google.navigation:q=52.520000,13.405000&mode=b",
            NavigationIntentBuilder.buildGoogleMapsUriString(bikeCoords))
        assertEquals("https://wego.here.com/directions/bicycle//52.520000,13.405000",
            NavigationIntentBuilder.buildHereWeGoUriString(bikeCoords))
        assertEquals("yandexmaps://maps.yandex.ru/?ll=13.405,52.52&z=16&rtt=bc",
            NavigationIntentBuilder.buildYandexMapsUriString(bikeCoords))

        // Directions with transit mode
        val transitDirs = ParsedLocation.Directions("Alexanderplatz", "Potsdamer Platz", TravelMode.TRANSIT)
        assertEquals("https://wego.here.com/directions/public-transport/Alexanderplatz/Potsdamer+Platz",
            NavigationIntentBuilder.buildHereWeGoUriString(transitDirs))
        assertEquals("yandexmaps://maps.yandex.ru/?rtext=Alexanderplatz~Potsdamer+Platz&rtt=mt",
            NavigationIntentBuilder.buildYandexMapsUriString(transitDirs))
    }

    @Test
    fun `builds correct URIs for Magic Earth, Citymapper, Komoot, and TomTom AmiGO`() {
        val coords = ParsedLocation.Coordinates(52.5200, 13.4050, label = "Berlin")

        // Magic Earth
        assertEquals("magicearth://map?lat=52.52&lon=13.405&name=Berlin",
            NavigationIntentBuilder.buildMagicEarthUriString(coords))

        // Citymapper
        assertEquals("citymapper://directions?endcoord=52.52,13.405&endname=Berlin",
            NavigationIntentBuilder.buildCitymapperUriString(coords))

        // Komoot
        assertEquals("komoot://tour?coordinate=52.52,13.405",
            NavigationIntentBuilder.buildKomootUriString(coords))

        // TomTom AmiGO
        assertEquals("amigo://navigate?to=52.52,13.405",
            NavigationIntentBuilder.buildTomTomAmiGOUriString(coords))
    }

    @Test
    fun `builds correct URIs for Sygic and Locus Map`() {
        val coords = ParsedLocation.Coordinates(52.5200, 13.4050, label = "Berlin")
        val walkCoords = ParsedLocation.Coordinates(52.5200, 13.4050, mode = TravelMode.WALKING)
        val search = ParsedLocation.SearchQuery("Berlin TV Tower")
        val home = ParsedLocation.Home

        // Sygic
        assertEquals("com.sygic.aura://coordinate|13.405000|52.520000|drive",
            NavigationIntentBuilder.buildSygicUriString(coords))
        assertEquals("com.sygic.aura://coordinate|13.405000|52.520000|walk",
            NavigationIntentBuilder.buildSygicUriString(walkCoords))
        assertEquals("com.sygic.aura://search|Berlin+TV+Tower|drive",
            NavigationIntentBuilder.buildSygicUriString(search))
        assertEquals("com.sygic.aura://",
            NavigationIntentBuilder.buildSygicUriString(home))

        // Locus Map
        assertEquals("geo:52.520000,13.405000?q=52.520000,13.405000(Berlin)",
            NavigationIntentBuilder.buildLocusMapUriString(coords))
        assertEquals("geo:0,0?q=Berlin+TV+Tower",
            NavigationIntentBuilder.buildLocusMapUriString(search))
        assertEquals("geo:0,0",
            NavigationIntentBuilder.buildLocusMapUriString(home))

        // TargetNavigationApp button strings
        val s = Strings.getStrings("en")
        assertTrue(s.testButtonLabel(TargetNavigationApp.SYGIC).contains("Sygic"))
        assertTrue(s.testButtonLabel(TargetNavigationApp.LOCUS_MAP).contains("Locus Map"))
        assertTrue(s.openInButtonLabel(TargetNavigationApp.SYGIC).contains("Sygic"))
        assertTrue(s.openInButtonLabel(TargetNavigationApp.LOCUS_MAP).contains("Locus Map"))
        assertTrue(s.openInButtonLabel(TargetNavigationApp.COMAPS).contains("CoMaps"))
        assertTrue(s.openInButtonLabel(TargetNavigationApp.VELA).contains("Vela"))
    }

    @Test
    fun `here wego preserves label when travel mode is active`() {
        val coordsWithLabelAndMode = ParsedLocation.Coordinates(52.5200, 13.4050, label = "Berlin Center", mode = TravelMode.WALKING)
        val uri = NavigationIntentBuilder.buildHereWeGoUriString(coordsWithLabelAndMode)
        assertEquals("https://wego.here.com/directions/walk//52.520000,13.405000?msg=Berlin+Center", uri)
    }

    @Test
    fun `coordinate formatting avoids scientific notation across all navigation apps`() {
        // Very small coordinate that would turn into 1.0E-4 with standard Double.toString()
        val smallCoords = ParsedLocation.Coordinates(0.0001, 0.00005)

        val waze = NavigationIntentBuilder.buildWazeUriString(smallCoords)
        assertEquals("waze://?ll=0.0001,0.00005&navigate=yes", waze)

        val om = NavigationIntentBuilder.buildOrganicMapsUriString(smallCoords)
        assertEquals("om://map?v=1&ll=0.0001,0.00005", om)

        val comaps = NavigationIntentBuilder.buildCoMapsUriString(smallCoords)
        assertEquals("cm://map?v=1&ll=0.0001,0.00005", comaps)

        val osmand = NavigationIntentBuilder.buildOsmAndUriString(smallCoords)
        assertEquals("osmandmaps://?lat=0.0001&lon=0.00005&z=16", osmand)

        val vela = NavigationIntentBuilder.buildVelaUriString(smallCoords)
        assertEquals("https://maps.google.com/?q=0.0001,0.00005", vela)

        val yandex = NavigationIntentBuilder.buildYandexMapsUriString(smallCoords)
        assertEquals("yandexmaps://maps.yandex.ru/?ll=0.00005,0.0001&z=16", yandex)

        val magicEarth = NavigationIntentBuilder.buildMagicEarthUriString(smallCoords)
        assertEquals("magicearth://map?lat=0.0001&lon=0.00005", magicEarth)

        val citymapper = NavigationIntentBuilder.buildCitymapperUriString(smallCoords)
        assertEquals("citymapper://directions?endcoord=0.0001,0.00005", citymapper)

        val komoot = NavigationIntentBuilder.buildKomootUriString(smallCoords)
        assertEquals("komoot://tour?coordinate=0.0001,0.00005", komoot)

        val amigo = NavigationIntentBuilder.buildTomTomAmiGOUriString(smallCoords)
        assertEquals("amigo://navigate?to=0.0001,0.00005", amigo)

        val mapyCz = NavigationIntentBuilder.buildMapyCzUriString(smallCoords)
        assertEquals("geo:0.000100,0.000050?q=0.000100,0.000050", mapyCz)
    }

    @Test
    fun `builds correct Mapy Cz URIs`() {
        val coords = ParsedLocation.Coordinates(50.0878, 14.4205, label = "Prague Old Town")
        val uri = NavigationIntentBuilder.buildMapyCzUriString(coords)
        assertEquals("geo:50.087800,14.420500?q=Prague+Old+Town", uri)

        val search = ParsedLocation.SearchQuery("Charles Bridge")
        assertEquals("geo:0,0?q=Charles+Bridge", NavigationIntentBuilder.buildMapyCzUriString(search))

        val dirs = ParsedLocation.Directions(origin = "Prague", destination = "Brno")
        assertEquals("https://mapy.cz/route?start=Prague&end=Brno", NavigationIntentBuilder.buildMapyCzUriString(dirs))

        val home = ParsedLocation.Home
        assertEquals("https://mapy.cz", NavigationIntentBuilder.buildMapyCzUriString(home))
    }
}
