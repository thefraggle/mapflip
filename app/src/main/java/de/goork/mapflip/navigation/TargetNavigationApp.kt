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
}
