package de.goork.mapflip.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import de.goork.mapflip.parser.ParsedLocation
import de.goork.mapflip.parser.TravelMode
import java.net.URLEncoder

object NavigationIntentBuilder {

    fun buildIntent(location: ParsedLocation, targetApp: TargetNavigationApp, context: Context? = null): Intent {
        return when (targetApp) {
            TargetNavigationApp.GOOGLE_MAPS -> buildGoogleMapsIntent(location)
            TargetNavigationApp.WAZE -> buildWazeIntent(location)
            TargetNavigationApp.ORGANIC_MAPS -> buildOrganicMapsIntent(location)
            TargetNavigationApp.OSMAND -> buildOsmAndIntent(location, context)
            TargetNavigationApp.SYSTEM_PICKER -> buildGenericGeoIntent(location, createChooser = true)
        }
    }

    fun buildUriString(location: ParsedLocation, targetApp: TargetNavigationApp): String {
        return when (targetApp) {
            TargetNavigationApp.GOOGLE_MAPS -> buildGoogleMapsUriString(location)
            TargetNavigationApp.WAZE -> buildWazeUriString(location)
            TargetNavigationApp.ORGANIC_MAPS -> buildOrganicMapsUriString(location)
            TargetNavigationApp.OSMAND -> buildOsmAndUriString(location)
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
                if (!label.isNullOrBlank()) {
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
            is ParsedLocation.Coordinates -> "waze://?ll=${location.latitude},${location.longitude}&navigate=yes"
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
        return when (location) {
            is ParsedLocation.Home -> "om://"
            is ParsedLocation.Coordinates -> {
                val name = if (!location.label.isNullOrBlank()) "&n=${encode(location.label)}" else ""
                "om://map?v=1&ll=${location.latitude},${location.longitude}$name"
            }
            is ParsedLocation.SearchQuery -> "om://search?query=${encode(location.query)}"
            is ParsedLocation.Navigation -> "om://search?query=${encode(location.destination)}"
            is ParsedLocation.Directions -> "om://search?query=${encode(location.destination)}"
            is ParsedLocation.WebFallback -> "om://search?query=${encode(location.fallbackUrl)}"
        }
    }

    fun buildOrganicMapsIntent(location: ParsedLocation): Intent {
        return Intent(Intent.ACTION_VIEW, Uri.parse(buildOrganicMapsUriString(location))).apply {
            setPackage(TargetNavigationApp.ORGANIC_MAPS.packageName)
        }
    }

    fun buildOsmAndUriString(location: ParsedLocation): String {
        return when (location) {
            is ParsedLocation.Home -> "osmandmaps://"
            is ParsedLocation.Coordinates -> "osmandmaps://?lat=${location.latitude}&lon=${location.longitude}&z=16"
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
        return "%.4f".format(java.util.Locale.US, value)
    }

    private fun encode(value: String): String = URLEncoder.encode(value, "UTF-8")
}
