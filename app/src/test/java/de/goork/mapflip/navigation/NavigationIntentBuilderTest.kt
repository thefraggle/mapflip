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
        assertEquals("geo:52.5200,13.4050?q=Berlin", uri)

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
    fun `builds correct OsmAnd URIs`() {
        val coords = ParsedLocation.Coordinates(52.5200, 13.4050)
        assertEquals("osmandmaps://?lat=52.52&lon=13.405&z=16", NavigationIntentBuilder.buildOsmAndUriString(coords))

        val search = ParsedLocation.SearchQuery("Reichstag")
        assertEquals("osmandmaps://?q=Reichstag", NavigationIntentBuilder.buildOsmAndUriString(search))
    }

    @Test
    fun `builds correct Generic Geo URIs`() {
        val coords = ParsedLocation.Coordinates(52.5200, 13.4050, label = "Berlin")
        assertEquals("geo:52.5200,13.4050?q=Berlin", NavigationIntentBuilder.buildGenericGeoUriString(coords))

        val search = ParsedLocation.SearchQuery("Potsdam")
        assertEquals("geo:0,0?q=Potsdam", NavigationIntentBuilder.buildGenericGeoUriString(search))
    }

    @Test
    fun `buildUriString dispatches to selected target app`() {
        val loc = ParsedLocation.Coordinates(48.8584, 2.2945)

        val googleUri = NavigationIntentBuilder.buildUriString(loc, TargetNavigationApp.GOOGLE_MAPS)
        assertTrue(googleUri.startsWith("geo:48.8584,2.2945"))

        val wazeUri = NavigationIntentBuilder.buildUriString(loc, TargetNavigationApp.WAZE)
        assertEquals("waze://?ll=48.8584,2.2945&navigate=yes", wazeUri)

        val omUri = NavigationIntentBuilder.buildUriString(loc, TargetNavigationApp.ORGANIC_MAPS)
        assertEquals("om://map?v=1&ll=48.8584,2.2945", omUri)

        val osmandUri = NavigationIntentBuilder.buildUriString(loc, TargetNavigationApp.OSMAND)
        assertEquals("osmandmaps://?lat=48.8584&lon=2.2945&z=16", osmandUri)

        val sysUri = NavigationIntentBuilder.buildUriString(loc, TargetNavigationApp.SYSTEM_PICKER)
        assertTrue(sysUri.startsWith("geo:48.8584,2.2945"))
    }

    @Test
    fun `dynamic testButtonLabel formats correctly for all apps in German and English`() {
        val sDe = Strings.getStrings("de")
        assertEquals("In Google Maps testen", sDe.testButtonLabel(TargetNavigationApp.GOOGLE_MAPS))
        assertEquals("In Waze testen", sDe.testButtonLabel(TargetNavigationApp.WAZE))
        assertEquals("In Organic Maps testen", sDe.testButtonLabel(TargetNavigationApp.ORGANIC_MAPS))
        assertEquals("In OsmAnd testen", sDe.testButtonLabel(TargetNavigationApp.OSMAND))
        assertEquals("In Ziel-Navigations-App testen", sDe.testButtonLabel(TargetNavigationApp.SYSTEM_PICKER))

        val sEn = Strings.getStrings("en")
        assertEquals("Test in Google Maps", sEn.testButtonLabel(TargetNavigationApp.GOOGLE_MAPS))
        assertEquals("Test in Waze", sEn.testButtonLabel(TargetNavigationApp.WAZE))
        assertEquals("Test in Organic Maps", sEn.testButtonLabel(TargetNavigationApp.ORGANIC_MAPS))
        assertEquals("Test in OsmAnd", sEn.testButtonLabel(TargetNavigationApp.OSMAND))
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
}
