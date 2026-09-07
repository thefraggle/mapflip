package de.goork.mapflip.navigation

enum class TargetNavigationApp(
    val id: String,
    val displayName: String,
    val packageName: String?,
    val isSystemPicker: Boolean = false
) {
    GOOGLE_MAPS(
        id = "google_maps",
        displayName = "Google Maps",
        packageName = "com.google.android.apps.maps"
    ),
    WAZE(
        id = "waze",
        displayName = "Waze",
        packageName = "com.waze"
    ),
    ORGANIC_MAPS(
        id = "organic_maps",
        displayName = "Organic Maps",
        packageName = "app.organicmaps"
    ),
    OSMAND(
        id = "osmand",
        displayName = "OsmAnd",
        packageName = "net.osmand"
    ),
    HERE_WEGO(
        id = "here_wego",
        displayName = "HERE WeGo",
        packageName = "com.here.app.maps"
    ),
    YANDEX_MAPS(
        id = "yandex_maps",
        displayName = "Yandex Maps",
        packageName = "ru.yandex.yandexmaps"
    ),
    MAGIC_EARTH(
        id = "magic_earth",
        displayName = "Magic Earth",
        packageName = "com.generalmagic.magicearth"
    ),
    CITYMAPPER(
        id = "citymapper",
        displayName = "Citymapper",
        packageName = "com.citymapper.app.release"
    ),
    KOMOOT(
        id = "komoot",
        displayName = "Komoot",
        packageName = "de.komoot.android"
    ),
    TOMTOM_AMIGO(
        id = "tomtom_amigo",
        displayName = "TomTom AmiGO",
        packageName = "com.tomtom.speedcams.android.map"
    ),
    SYGIC(
        id = "sygic",
        displayName = "Sygic",
        packageName = "com.sygic.aura"
    ),
    LOCUS_MAP(
        id = "locus_map",
        displayName = "Locus Map",
        packageName = "menion.android.locus"
    ),
    SYSTEM_PICKER(
        id = "system_picker",
        displayName = "Always ask (System Picker)",
        packageName = null,
        isSystemPicker = true
    );

    companion object {
        fun fromId(id: String?): TargetNavigationApp {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: GOOGLE_MAPS
        }
    }

    fun isInstalled(context: android.content.Context): Boolean {
        if (isSystemPicker) return true
        val pkg = packageName ?: return false
        val pm = context.packageManager
        return try {
            pm.getPackageInfo(pkg, 0)
            true
        } catch (_: Exception) {
            if (this == OSMAND) {
                try {
                    pm.getPackageInfo("net.osmand.plus", 0)
                    true
                } catch (_: Exception) {
                    false
                }
            } else if (this == LOCUS_MAP) {
                try {
                    pm.getPackageInfo("menion.android.locus.pro", 0)
                    true
                } catch (_: Exception) {
                    false
                }
            } else {
                false
            }
        }
    }
}
