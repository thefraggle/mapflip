package de.goork.mapflip.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import de.goork.mapflip.parser.ParsedLocation
import de.goork.mapflip.parser.TravelMode
import java.net.URI
import java.net.URLEncoder

object NavigationIntentBuilder {

    fun buildIntent(location: ParsedLocation, targetApp: TargetNavigationApp, context: Context? = null): Intent {
        return when (targetApp) {
            TargetNavigationApp.GOOGLE_MAPS -> buildGoogleMapsIntent(location)
            TargetNavigationApp.WAZE -> buildWazeIntent(location)
            TargetNavigationApp.ORGANIC_MAPS -> buildOrganicMapsIntent(location)
            TargetNavigationApp.COMAPS -> buildCoMapsIntent(location, context)
            TargetNavigationApp.OSMAND -> buildOsmAndIntent(location, context)
            TargetNavigationApp.VELA -> buildVelaIntent(location)
            TargetNavigationApp.HERE_WEGO -> buildHereWeGoIntent(location)
            TargetNavigationApp.YANDEX_MAPS -> buildYandexMapsIntent(location)
            TargetNavigationApp.MAGIC_EARTH -> buildMagicEarthIntent(location)
            TargetNavigationApp.CITYMAPPER -> buildCitymapperIntent(location)
            TargetNavigationApp.KOMOOT -> buildKomootIntent(location)
            TargetNavigationApp.TOMTOM_AMIGO -> buildTomTomAmiGOIntent(location)
            TargetNavigationApp.SYGIC -> buildSygicIntent(location)
            TargetNavigationApp.LOCUS_MAP -> buildLocusMapIntent(location, context)
            TargetNavigationApp.MAPY_CZ -> buildMapyCzIntent(location)
            TargetNavigationApp.SYSTEM_PICKER -> buildGenericGeoIntent(location, createChooser = true)
        }
    }

    fun buildUriString(location: ParsedLocation, targetApp: TargetNavigationApp): String {
        return when (targetApp) {
            TargetNavigationApp.GOOGLE_MAPS -> buildGoogleMapsUriString(location)
            TargetNavigationApp.WAZE -> buildWazeUriString(location)
            TargetNavigationApp.ORGANIC_MAPS -> buildOrganicMapsUriString(location)
            TargetNavigationApp.COMAPS -> buildCoMapsUriString(location)
            TargetNavigationApp.OSMAND -> buildOsmAndUriString(location)
            TargetNavigationApp.VELA -> buildVelaUriString(location)
            TargetNavigationApp.HERE_WEGO -> buildHereWeGoUriString(location)
            TargetNavigationApp.YANDEX_MAPS -> buildYandexMapsUriString(location)
            TargetNavigationApp.MAGIC_EARTH -> buildMagicEarthUriString(location)
            TargetNavigationApp.CITYMAPPER -> buildCitymapperUriString(location)
            TargetNavigationApp.KOMOOT -> buildKomootUriString(location)
            TargetNavigationApp.TOMTOM_AMIGO -> buildTomTomAmiGOUriString(location)
            TargetNavigationApp.SYGIC -> buildSygicUriString(location)
            TargetNavigationApp.LOCUS_MAP -> buildLocusMapUriString(location)
            TargetNavigationApp.MAPY_CZ -> buildMapyCzUriString(location)
            TargetNavigationApp.SYSTEM_PICKER -> buildGenericGeoUriString(location)
        }
    }

    fun buildGoogleMapsUriString(location: ParsedLocation): String {
        return when (location) {
            is ParsedLocation.Home -> "https://www.google.com/maps"
            is ParsedLocation.SearchQuery -> "geo:0,0?q=${encode(location.query)}"
            is ParsedLocation.Coordinates -> {
                val label = location.label
                val latStr = formatCoord(location.latitude)
                val lonStr = formatCoord(location.longitude)
                if (location.mode != null) {
                    val modeParam = when (location.mode) {
                        TravelMode.WALKING -> "&mode=w"
                        TravelMode.BICYCLING -> "&mode=b"
                        TravelMode.TRANSIT -> "&mode=transit"
                        TravelMode.DRIVING -> "&mode=d"
                    }
                    "google.navigation:q=$latStr,$lonStr$modeParam"
                } else if (!label.isNullOrBlank()) {
                    "geo:$latStr,$lonStr?q=${encode(label)}"
                } else {
                    "geo:$latStr,$lonStr?q=$latStr,$lonStr"
                }
            }
            is ParsedLocation.Navigation -> {
                val mode = when (location.mode) {
                    TravelMode.WALKING -> "&mode=w"
                    TravelMode.BICYCLING -> "&mode=b"
                    TravelMode.TRANSIT -> "&mode=transit"
                    TravelMode.DRIVING -> "&mode=d"
                    null -> ""
                }
                "google.navigation:q=${encode(location.destination)}$mode"
            }
            is ParsedLocation.Directions -> {
                val mode = when (location.mode) {
                    TravelMode.WALKING -> "&travelmode=walking"
                    TravelMode.BICYCLING -> "&travelmode=bicycling"
                    TravelMode.TRANSIT -> "&travelmode=transit"
                    TravelMode.DRIVING -> "&travelmode=driving"
                    null -> ""
                }
                "https://www.google.com/maps/dir/?api=1&origin=${encode(location.origin)}&destination=${encode(location.destination)}$mode"
            }
            is ParsedLocation.WebFallback -> "https://www.google.com/maps/search/?api=1&query=${encode(location.fallbackUrl)}"
        }
    }

    fun buildGoogleMapsIntent(location: ParsedLocation): Intent {
        return Intent(Intent.ACTION_VIEW, Uri.parse(buildGoogleMapsUriString(location))).apply {
            setPackage(TargetNavigationApp.GOOGLE_MAPS.packageName)
        }
    }

    fun buildWazeUriString(location: ParsedLocation): String {
        return when (location) {
            is ParsedLocation.Home -> "waze://"
            is ParsedLocation.Coordinates -> "waze://?ll=${formatCoordCompact(location.latitude)},${formatCoordCompact(location.longitude)}&navigate=yes"
            is ParsedLocation.SearchQuery -> "waze://?q=${encode(location.query)}&navigate=yes"
            is ParsedLocation.Navigation -> "waze://?q=${encode(location.destination)}&navigate=yes"
            is ParsedLocation.Directions -> "waze://?q=${encode(location.destination)}&navigate=yes"
            is ParsedLocation.WebFallback -> "https://www.waze.com/ul?q=${encode(location.fallbackUrl)}"
        }
    }

    fun buildWazeIntent(location: ParsedLocation): Intent {
        return Intent(Intent.ACTION_VIEW, Uri.parse(buildWazeUriString(location))).apply {
            setPackage(TargetNavigationApp.WAZE.packageName)
        }
    }

    fun buildOrganicMapsUriString(location: ParsedLocation): String {
        return buildOrganicMapsCompatibleUriString(location, "om")
    }

    private fun buildOrganicMapsCompatibleUriString(location: ParsedLocation, scheme: String): String {
        return when (location) {
            is ParsedLocation.Home -> "$scheme://"
            is ParsedLocation.Coordinates -> {
                val name = if (!location.label.isNullOrBlank()) "&n=${encode(location.label)}" else ""
                "$scheme://map?v=1&ll=${formatCoordCompact(location.latitude)},${formatCoordCompact(location.longitude)}$name"
            }
            is ParsedLocation.SearchQuery -> "$scheme://search?query=${encode(location.query)}"
            is ParsedLocation.Navigation -> "$scheme://search?query=${encode(location.destination)}"
            is ParsedLocation.Directions -> "$scheme://search?query=${encode(location.destination)}"
            is ParsedLocation.WebFallback -> "$scheme://search?query=${encode(location.fallbackUrl)}"
        }
    }

    fun buildOrganicMapsIntent(location: ParsedLocation): Intent {
        return Intent(Intent.ACTION_VIEW, Uri.parse(buildOrganicMapsUriString(location))).apply {
            setPackage(TargetNavigationApp.ORGANIC_MAPS.packageName)
        }
    }

    fun buildCoMapsUriString(location: ParsedLocation): String {
        return buildOrganicMapsCompatibleUriString(location, "cm")
    }

    fun buildCoMapsIntent(location: ParsedLocation, context: Context? = null): Intent {
        val primaryPackage = TargetNavigationApp.COMAPS.packageName!!
        val pkg = if (context != null) {
            try {
                context.packageManager.getPackageInfo(primaryPackage, 0)
                primaryPackage
            } catch (_: Exception) {
                TargetNavigationApp.COMAPS_FDROID_PACKAGE
            }
        } else {
            primaryPackage
        }
        return Intent(Intent.ACTION_VIEW, Uri.parse(buildCoMapsUriString(location))).apply {
            setPackage(pkg)
        }
    }

    fun buildOsmAndUriString(location: ParsedLocation): String {
        return when (location) {
            is ParsedLocation.Home -> "osmandmaps://"
            is ParsedLocation.Coordinates -> "osmandmaps://?lat=${formatCoordCompact(location.latitude)}&lon=${formatCoordCompact(location.longitude)}&z=16"
            is ParsedLocation.SearchQuery -> "osmandmaps://?q=${encode(location.query)}"
            is ParsedLocation.Navigation -> "osmandmaps://?q=${encode(location.destination)}"
            is ParsedLocation.Directions -> "osmandmaps://?q=${encode(location.destination)}"
            is ParsedLocation.WebFallback -> "osmandmaps://?q=${encode(location.fallbackUrl)}"
        }
    }

    fun buildOsmAndIntent(location: ParsedLocation, context: Context? = null): Intent {
        val pkg = if (context != null) {
            val pm = context.packageManager
            val isPlusInstalled = try {
                pm.getPackageInfo("net.osmand.plus", 0)
                true
            } catch (_: Exception) {
                false
            }
            if (isPlusInstalled) "net.osmand.plus" else TargetNavigationApp.OSMAND.packageName
        } else {
            TargetNavigationApp.OSMAND.packageName
        }
        return Intent(Intent.ACTION_VIEW, Uri.parse(buildOsmAndUriString(location))).apply {
            if (pkg != null) setPackage(pkg)
        }
    }

    fun buildVelaUriString(location: ParsedLocation): String {
        val baseUrl = "https://maps.google.com"
        fun directions(destination: String, origin: String? = null, mode: TravelMode? = null): String {
            val originParam = origin?.let { "&origin=${encode(it)}" } ?: ""
            val modeParam = mode?.let { "&travelmode=${it.name.lowercase(java.util.Locale.ROOT)}" } ?: ""
            return "$baseUrl/maps/dir/?api=1&destination=${encode(destination)}$originParam$modeParam"
        }
        return when (location) {
            is ParsedLocation.Home -> baseUrl
            is ParsedLocation.SearchQuery -> "$baseUrl/?q=${encode(location.query)}"
            is ParsedLocation.Coordinates -> {
                val coordinates = "${formatCoordCompact(location.latitude)},${formatCoordCompact(location.longitude)}"
                if (location.mode != null) {
                    directions(coordinates, mode = location.mode)
                } else {
                    "$baseUrl/?q=$coordinates"
                }
            }
            is ParsedLocation.Navigation -> directions(location.destination, mode = location.mode)
            is ParsedLocation.Directions -> directions(location.destination, location.origin, location.mode)
            is ParsedLocation.WebFallback -> {
                // Vela can resolve these URLs itself, including opaque Google short links.
                val uri = runCatching { URI(location.fallbackUrl) }.getOrNull()
                if ((uri?.scheme.equals("https", ignoreCase = true) || uri?.scheme.equals("http", ignoreCase = true)) &&
                    (uri?.host.equals("maps.google.com", ignoreCase = true) || uri?.host.equals("maps.app.goo.gl", ignoreCase = true))) {
                    location.fallbackUrl
                } else {
                    "$baseUrl/?q=${encode(location.fallbackUrl)}"
                }
            }
        }
    }

    fun buildVelaIntent(location: ParsedLocation): Intent {
        return Intent(Intent.ACTION_VIEW, Uri.parse(buildVelaUriString(location))).apply {
            setPackage(TargetNavigationApp.VELA.packageName)
        }
    }

    fun buildHereWeGoUriString(location: ParsedLocation): String {
        return when (location) {
            is ParsedLocation.Home -> "https://wego.here.com"
            is ParsedLocation.Coordinates -> {
                val latStr = formatCoord(location.latitude)
                val lonStr = formatCoord(location.longitude)
                val labelParam = if (!location.label.isNullOrBlank()) "?msg=${encode(location.label)}" else ""
                if (location.mode != null) {
                    val modePart = when (location.mode) {
                        TravelMode.WALKING -> "walk"
                        TravelMode.BICYCLING -> "bicycle"
                        TravelMode.TRANSIT -> "public-transport"
                        TravelMode.DRIVING -> "drive"
                    }
                    "https://wego.here.com/directions/$modePart//$latStr,$lonStr$labelParam"
                } else {
                    "https://share.here.com/l/$latStr,$lonStr$labelParam"
                }
            }
            is ParsedLocation.SearchQuery -> "https://wego.here.com/search/${encode(location.query)}"
            is ParsedLocation.Navigation -> {
                val modePart = when (location.mode) {
                    TravelMode.WALKING -> "walk"
                    TravelMode.BICYCLING -> "bicycle"
                    TravelMode.TRANSIT -> "public-transport"
                    TravelMode.DRIVING, null -> "drive"
                }
                "https://wego.here.com/directions/$modePart//${encode(location.destination)}"
            }
            is ParsedLocation.Directions -> {
                val modePart = when (location.mode) {
                    TravelMode.WALKING -> "walk"
                    TravelMode.BICYCLING -> "bicycle"
                    TravelMode.TRANSIT -> "public-transport"
                    TravelMode.DRIVING, null -> "drive"
                }
                "https://wego.here.com/directions/$modePart/${encode(location.origin)}/${encode(location.destination)}"
            }
            is ParsedLocation.WebFallback -> "https://wego.here.com/search/${encode(location.fallbackUrl)}"
        }
    }

    fun buildHereWeGoIntent(location: ParsedLocation): Intent {
        return Intent(Intent.ACTION_VIEW, Uri.parse(buildHereWeGoUriString(location))).apply {
            setPackage(TargetNavigationApp.HERE_WEGO.packageName)
        }
    }

    fun buildYandexMapsUriString(location: ParsedLocation): String {
        return when (location) {
            is ParsedLocation.Home -> "yandexmaps://maps.yandex.ru"
            is ParsedLocation.Coordinates -> {
                val labelParam = if (!location.label.isNullOrBlank()) "&text=${encode(location.label)}" else ""
                val rttParam = when (location.mode) {
                    TravelMode.WALKING -> "&rtt=pd"
                    TravelMode.BICYCLING -> "&rtt=bc"
                    TravelMode.TRANSIT -> "&rtt=mt"
                    TravelMode.DRIVING -> "&rtt=auto"
                    null -> ""
                }
                "yandexmaps://maps.yandex.ru/?ll=${formatCoordCompact(location.longitude)},${formatCoordCompact(location.latitude)}&z=16$labelParam$rttParam"
            }
            is ParsedLocation.SearchQuery -> "yandexmaps://maps.yandex.ru/?text=${encode(location.query)}"
            is ParsedLocation.Navigation -> {
                val rtt = when (location.mode) {
                    TravelMode.WALKING -> "pd"
                    TravelMode.BICYCLING -> "bc"
                    TravelMode.TRANSIT -> "mt"
                    TravelMode.DRIVING, null -> "auto"
                }
                "yandexmaps://maps.yandex.ru/?rtext=~${encode(location.destination)}&rtt=$rtt"
            }
            is ParsedLocation.Directions -> {
                val rtt = when (location.mode) {
                    TravelMode.WALKING -> "pd"
                    TravelMode.BICYCLING -> "bc"
                    TravelMode.TRANSIT -> "mt"
                    TravelMode.DRIVING, null -> "auto"
                }
                "yandexmaps://maps.yandex.ru/?rtext=${encode(location.origin)}~${encode(location.destination)}&rtt=$rtt"
            }
            is ParsedLocation.WebFallback -> "https://yandex.com/maps/?text=${encode(location.fallbackUrl)}"
        }
    }

    fun buildYandexMapsIntent(location: ParsedLocation): Intent {
        return Intent(Intent.ACTION_VIEW, Uri.parse(buildYandexMapsUriString(location))).apply {
            setPackage(TargetNavigationApp.YANDEX_MAPS.packageName)
        }
    }

    fun buildMagicEarthUriString(location: ParsedLocation): String {
        return when (location) {
            is ParsedLocation.Home -> "magicearth://"
            is ParsedLocation.Coordinates -> {
                val labelParam = if (!location.label.isNullOrBlank()) "&name=${encode(location.label)}" else ""
                "magicearth://map?lat=${formatCoordCompact(location.latitude)}&lon=${formatCoordCompact(location.longitude)}$labelParam"
            }
            is ParsedLocation.SearchQuery -> "magicearth://q=${encode(location.query)}"
            is ParsedLocation.Navigation -> "magicearth://navigate?destination=${encode(location.destination)}"
            is ParsedLocation.Directions -> "magicearth://navigate?destination=${encode(location.destination)}"
            is ParsedLocation.WebFallback -> "magicearth://q=${encode(location.fallbackUrl)}"
        }
    }

    fun buildMagicEarthIntent(location: ParsedLocation): Intent {
        return Intent(Intent.ACTION_VIEW, Uri.parse(buildMagicEarthUriString(location))).apply {
            setPackage(TargetNavigationApp.MAGIC_EARTH.packageName)
        }
    }

    fun buildCitymapperUriString(location: ParsedLocation): String {
        return when (location) {
            is ParsedLocation.Home -> "citymapper://"
            is ParsedLocation.Coordinates -> {
                val nameParam = if (!location.label.isNullOrBlank()) "&endname=${encode(location.label)}" else ""
                "citymapper://directions?endcoord=${formatCoordCompact(location.latitude)},${formatCoordCompact(location.longitude)}$nameParam"
            }
            is ParsedLocation.SearchQuery -> "citymapper://directions?endaddress=${encode(location.query)}"
            is ParsedLocation.Navigation -> "citymapper://directions?endaddress=${encode(location.destination)}"
            is ParsedLocation.Directions -> "citymapper://directions?startaddress=${encode(location.origin)}&endaddress=${encode(location.destination)}"
            is ParsedLocation.WebFallback -> "citymapper://directions?endaddress=${encode(location.fallbackUrl)}"
        }
    }

    fun buildCitymapperIntent(location: ParsedLocation): Intent {
        return Intent(Intent.ACTION_VIEW, Uri.parse(buildCitymapperUriString(location))).apply {
            setPackage(TargetNavigationApp.CITYMAPPER.packageName)
        }
    }

    fun buildKomootUriString(location: ParsedLocation): String {
        return when (location) {
            is ParsedLocation.Home -> "komoot://"
            is ParsedLocation.Coordinates -> "komoot://tour?coordinate=${formatCoordCompact(location.latitude)},${formatCoordCompact(location.longitude)}"
            is ParsedLocation.SearchQuery -> "https://www.komoot.com/search/${encode(location.query)}"
            is ParsedLocation.Navigation -> "komoot://tour?coordinate=${encode(location.destination)}"
            is ParsedLocation.Directions -> "komoot://tour?coordinate=${encode(location.destination)}"
            is ParsedLocation.WebFallback -> "https://www.komoot.com/search/${encode(location.fallbackUrl)}"
        }
    }

    fun buildKomootIntent(location: ParsedLocation): Intent {
        return Intent(Intent.ACTION_VIEW, Uri.parse(buildKomootUriString(location))).apply {
            setPackage(TargetNavigationApp.KOMOOT.packageName)
        }
    }

    fun buildTomTomAmiGOUriString(location: ParsedLocation): String {
        return when (location) {
            is ParsedLocation.Home -> "amigo://"
            is ParsedLocation.Coordinates -> "amigo://navigate?to=${formatCoordCompact(location.latitude)},${formatCoordCompact(location.longitude)}"
            is ParsedLocation.SearchQuery -> "amigo://search?q=${encode(location.query)}"
            is ParsedLocation.Navigation -> "amigo://navigate?to=${encode(location.destination)}"
            is ParsedLocation.Directions -> "amigo://navigate?to=${encode(location.destination)}"
            is ParsedLocation.WebFallback -> "amigo://search?q=${encode(location.fallbackUrl)}"
        }
    }

    fun buildTomTomAmiGOIntent(location: ParsedLocation): Intent {
        return Intent(Intent.ACTION_VIEW, Uri.parse(buildTomTomAmiGOUriString(location))).apply {
            setPackage(TargetNavigationApp.TOMTOM_AMIGO.packageName)
        }
    }

    fun buildSygicUriString(location: ParsedLocation): String {
        return when (location) {
            is ParsedLocation.Home -> "com.sygic.aura://"
            is ParsedLocation.Coordinates -> {
                val lonStr = formatCoord(location.longitude)
                val latStr = formatCoord(location.latitude)
                val mode = if (location.mode == TravelMode.WALKING) "walk" else "drive"
                "com.sygic.aura://coordinate|$lonStr|$latStr|$mode"
            }
            is ParsedLocation.SearchQuery -> "com.sygic.aura://search|${encode(location.query)}|drive"
            is ParsedLocation.Navigation -> {
                val dest = location.destination
                val mode = if (location.mode == TravelMode.WALKING) "walk" else "drive"
                "com.sygic.aura://search|${encode(dest)}|$mode"
            }
            is ParsedLocation.Directions -> {
                val dest = location.destination
                val mode = if (location.mode == TravelMode.WALKING) "walk" else "drive"
                "com.sygic.aura://search|${encode(dest)}|$mode"
            }
            is ParsedLocation.WebFallback -> "com.sygic.aura://search|${encode(location.fallbackUrl)}|drive"
        }
    }

    fun buildSygicIntent(location: ParsedLocation): Intent {
        return Intent(Intent.ACTION_VIEW, Uri.parse(buildSygicUriString(location))).apply {
            setPackage(TargetNavigationApp.SYGIC.packageName)
        }
    }

    fun buildLocusMapUriString(location: ParsedLocation): String {
        return when (location) {
            is ParsedLocation.Home -> "geo:0,0"
            is ParsedLocation.SearchQuery -> "geo:0,0?q=${encode(location.query)}"
            is ParsedLocation.Coordinates -> {
                val latStr = formatCoord(location.latitude)
                val lonStr = formatCoord(location.longitude)
                val label = location.label
                if (!label.isNullOrBlank()) {
                    "geo:$latStr,$lonStr?q=$latStr,$lonStr(${encode(label)})"
                } else {
                    "geo:$latStr,$lonStr?q=$latStr,$lonStr"
                }
            }
            is ParsedLocation.Navigation -> "geo:0,0?q=${encode(location.destination)}"
            is ParsedLocation.Directions -> "geo:0,0?q=${encode(location.destination)}"
            is ParsedLocation.WebFallback -> "geo:0,0?q=${encode(location.fallbackUrl)}"
        }
    }

    fun buildLocusMapIntent(location: ParsedLocation, context: Context? = null): Intent {
        val pkg = if (context != null) {
            val pm = context.packageManager
            val isProInstalled = try {
                pm.getPackageInfo("menion.android.locus.pro", 0)
                true
            } catch (_: Exception) {
                false
            }
            if (isProInstalled) "menion.android.locus.pro" else TargetNavigationApp.LOCUS_MAP.packageName
        } else {
            TargetNavigationApp.LOCUS_MAP.packageName
        }
        return Intent(Intent.ACTION_VIEW, Uri.parse(buildLocusMapUriString(location))).apply {
            if (pkg != null) setPackage(pkg)
        }
    }

    fun buildMapyCzUriString(location: ParsedLocation): String {
        return when (location) {
            is ParsedLocation.Home -> "https://mapy.cz"
            is ParsedLocation.Coordinates -> {
                val latStr = formatCoord(location.latitude)
                val lonStr = formatCoord(location.longitude)
                val label = location.label
                if (!label.isNullOrBlank()) {
                    "geo:$latStr,$lonStr?q=${encode(label)}"
                } else {
                    "geo:$latStr,$lonStr?q=$latStr,$lonStr"
                }
            }
            is ParsedLocation.SearchQuery -> "geo:0,0?q=${encode(location.query)}"
            is ParsedLocation.Navigation -> "geo:0,0?q=${encode(location.destination)}"
            is ParsedLocation.Directions -> {
                val origin = location.origin
                val dest = location.destination
                if (origin.isNotBlank()) {
                    "https://mapy.cz/route?start=${encode(origin)}&end=${encode(dest)}"
                } else {
                    "geo:0,0?q=${encode(dest)}"
                }
            }
            is ParsedLocation.WebFallback -> location.fallbackUrl
        }
    }

    fun buildMapyCzIntent(location: ParsedLocation): Intent {
        return Intent(Intent.ACTION_VIEW, Uri.parse(buildMapyCzUriString(location))).apply {
            setPackage(TargetNavigationApp.MAPY_CZ.packageName)
        }
    }

    fun buildGenericGeoUriString(location: ParsedLocation): String {
        return when (location) {
            is ParsedLocation.Home -> "geo:0,0"
            is ParsedLocation.SearchQuery -> "geo:0,0?q=${encode(location.query)}"
            is ParsedLocation.Coordinates -> {
                val label = location.label
                val latStr = formatCoord(location.latitude)
                val lonStr = formatCoord(location.longitude)
                if (!label.isNullOrBlank()) {
                    "geo:$latStr,$lonStr?q=${encode(label)}"
                } else {
                    "geo:$latStr,$lonStr?q=$latStr,$lonStr"
                }
            }
            is ParsedLocation.Navigation -> "geo:0,0?q=${encode(location.destination)}"
            is ParsedLocation.Directions -> "geo:0,0?q=${encode(location.destination)}"
            is ParsedLocation.WebFallback -> "https://www.google.com/maps/search/?api=1&query=${encode(location.fallbackUrl)}"
        }
    }

    fun buildGenericGeoIntent(location: ParsedLocation, createChooser: Boolean = false): Intent {
        val baseIntent = Intent(Intent.ACTION_VIEW, Uri.parse(buildGenericGeoUriString(location)))
        return if (createChooser) {
            Intent.createChooser(baseIntent, null)
        } else {
            baseIntent
        }
    }

    private fun formatCoord(value: Double): String {
        return "%.6f".format(java.util.Locale.US, value)
    }

    private fun formatCoordCompact(value: Double): String {
        return java.text.DecimalFormat("0.######", java.text.DecimalFormatSymbols(java.util.Locale.US)).format(value)
    }

    private fun encode(value: String): String = URLEncoder.encode(value, "UTF-8")
}
