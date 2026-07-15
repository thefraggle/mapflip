# MapFlip

Apple Maps → Google Maps Redirector für Android.

## Was macht MapFlip?

MapFlip fängt Apple Maps Links (`maps.apple.com`) automatisch ab und öffnet sie in Google Maps. Nach einmaliger Einrichtung arbeitet die App komplett unsichtbar im Hintergrund.

## Features

- 🔄 Automatische Umleitung – kein manuelles Kopieren
- 👻 Unsichtbar – die App öffnet sich nicht sichtbar
- 🗺️ Unterstützt Suchen, Koordinaten, Adressen & Navigation
- 🔒 Keine Datensammlung, keine Werbung
- 🌍 Deutsch & Englisch

## Einrichtung

1. App installieren
2. App öffnen → „Einstellungen öffnen" tippen
3. Link-Weiterleitung für `maps.apple.com` aktivieren
4. Fertig!

## Tech Stack

- Kotlin + Jetpack Compose (Material 3)
- Min SDK 26 (Android 8.0), Target SDK 35
- Keine externen Dependencies außer AndroidX

## Build

```bash
./gradlew :app:assembleDebug
./gradlew :app:testDebugUnitTest
```

## Lizenz

© 2025 Daniel Notthoff – [notthoff.org](https://notthoff.org)
