package de.goork.mapflip.ui

/**
 * App strings for DE and EN. Custom solution instead of Android resources
 * to support runtime language switching without restarting the activity.
 */
object Strings {

    data class AppStrings(
        val headline: String,
        val subtitle: String,
        val tagline: String,
        val setupTitle: String,
        val step1: String,
        val step2: String,
        val step3: String,
        val btnSettings: String,
        val statusActive: String,
        val statusInactive: String,
        val statusHint: String,
        val famwakeTitle: String,
        val famwakePromo: String,
        val famwakeDesc: String,
        val famwakeButton: String,
        val copyright: String,
        val langToggle: String,
        val btnFeedback: String,
        val pauseTitle: String,
        val pauseDesc: String,
        val statusPaused: String,
    )

    val DE = AppStrings(
        headline = "MapFlip",
        subtitle = "Apple Maps \u2192 Google Maps",
        tagline = "Automatisch. Unsichtbar.",
        setupTitle = "So geht\u2019s",
        step1 = "Tippe auf den Button unten",
        step2 = "Aktiviere \"Links \u00f6ffnen\" f\u00fcr maps.apple.com",
        step3 = "Fertig! Apple Maps Links \u00f6ffnen sich ab jetzt automatisch in Google Maps.",
        btnSettings = "Einstellungen \u00f6ffnen",
        statusActive = "Links sind aktiviert",
        statusInactive = "Links sind noch nicht aktiv",
        statusHint = "Auf \u00e4lteren Android-Versionen kann der Status nicht gepr\u00fcft werden.",
        famwakeTitle = "FamWake \u2013 Familienwecker",
        famwakePromo = "Vom gleichen Entwickler",
        famwakeDesc = "FamWake koordiniert den Morgen f\u00fcr die ganze Familie \u2013 Bad-Zeiten, Fr\u00fchst\u00fcck und Aufstehen.",
        famwakeButton = "Mehr erfahren",
        copyright = "\u00a9 2026 Daniel Notthoff \u2022 notthoff.org",
        langToggle = "EN",
        btnFeedback = "Feedback & Bugs melden",
        pauseTitle = "Weiterleitung pausieren",
        pauseDesc = "Vorübergehend alle Umleitungen aussetzen",
        statusPaused = "Weiterleitung ist pausiert",
    )

    val EN = AppStrings(
        headline = "MapFlip",
        subtitle = "Apple Maps \u2192 Google Maps",
        tagline = "Automatic. Invisible.",
        setupTitle = "How it works",
        step1 = "Tap the button below",
        step2 = "Enable \"Open links\" for maps.apple.com",
        step3 = "Done! Apple Maps links will now open automatically in Google Maps.",
        btnSettings = "Open Settings",
        statusActive = "Links are enabled",
        statusInactive = "Links are not yet active",
        statusHint = "Status cannot be checked on older Android versions.",
        famwakeTitle = "FamWake \u2013 Family Alarm Clock",
        famwakePromo = "From the same developer",
        famwakeDesc = "FamWake coordinates the morning for the whole family \u2013 bathroom times, breakfast, and wake-up.",
        famwakeButton = "Learn more",
        copyright = "\u00a9 2026 Daniel Notthoff \u2022 notthoff.org",
        langToggle = "DE",
        btnFeedback = "Report Feedback & Bugs",
        pauseTitle = "Pause Redirect",
        pauseDesc = "Temporarily suspend all link redirects",
        statusPaused = "Redirect is paused",
    )
}
