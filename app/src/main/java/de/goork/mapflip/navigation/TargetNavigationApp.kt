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
    COMAPS(
        id = "comaps",
        displayName = "CoMaps",
        packageName = "app.comaps"
    ),
    OSMAND(
        id = "osmand",
        displayName = "OsmAnd",
        packageName = "net.osmand"
    ),
    VELA(
        id = "vela",
        displayName = "Vela",
        packageName = "app.vela"
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
    MAPY_CZ(
        id = "mapy_cz",
        displayName = "Mapy.cz",
        packageName = "cz.seznam.mapy"
    ),
    SYSTEM_PICKER(
        id = "system_picker",
        displayName = "Always ask (System Picker)",
        packageName = null,
        isSystemPicker = true
    );

    companion object {
        const val COMAPS_FDROID_PACKAGE = "app.comaps.fdroid"
        const val OSMAND_PLUS_PACKAGE = "net.osmand.plus"
        const val LOCUS_PRO_PACKAGE = "menion.android.locus.pro"

        fun fromId(id: String?): TargetNavigationApp {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: GOOGLE_MAPS
        }
    }

    /**
     * Resolves the actually installed package name for this navigation app,
     * supporting multi-package apps (OsmAnd/OsmAnd+, CoMaps/CoMaps F-Droid, Locus Map/Locus Pro).
     */
    fun resolveInstalledPackage(context: android.content.Context?): String? {
        if (isSystemPicker) return null
        val primary = packageName ?: return null
        if (context == null) return primary
        val pm = context.packageManager
        val candidates = when (this) {
            COMAPS -> listOf(primary, COMAPS_FDROID_PACKAGE)
            OSMAND -> listOf(OSMAND_PLUS_PACKAGE, primary)
            LOCUS_MAP -> listOf(LOCUS_PRO_PACKAGE, primary)
            else -> listOf(primary)
        }
        return candidates.firstOrNull { pkg ->
            try {
                pm.getPackageInfo(pkg, 0)
                true
            } catch (_: Exception) {
                false
            }
        }
    }

    fun isInstalled(context: android.content.Context): Boolean {
        if (isSystemPicker) return true
        return resolveInstalledPackage(context) != null
    }
}
