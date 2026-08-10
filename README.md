# MapFlip

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Google Play](https://img.shields.io/badge/Google_Play-Available-green.svg)](https://play.google.com/store/apps/details?id=de.goork.mapflip)
[![F-Droid](https://img.shields.io/badge/F--Droid-In_Review-orange.svg)](https://gitlab.com/fdroid/fdroiddata/-/merge_requests/45011)

A lightweight, open-source Android background utility that intercepts Apple Maps web links (`maps.apple.com`) and automatically redirects them to Google Maps. 

Set it up once, and it works invisibly in the background.

🔗 **Website & Privacy Policy:** [goork.de/mapflip](https://goork.de/mapflip/)

---

## ✨ Features

- 🔄 **Automatic redirect** – no manual copying or pasting.
- 👻 **Invisible operation** – intercepts links at the system level without showing a map-viewer screen of its own.
- 🗺️ **Full query support** – handles search queries, GPS coordinates, addresses, and navigation directions.
- ⏸️ **Pause mode** – easily pause the redirect for 1 hour, 8 hours, until tomorrow, or indefinitely.
- 🎛️ **Quick Settings Tile** – pause and resume directly from your system notification shade.
- 🔒 **100% Privacy Friendly** – no internet permission required, no trackers, no ads, no data collection.
- 🌍 **14 Supported Languages** – English, German, Danish, French, Italian, Japanese, Dutch, Norwegian, Polish, Portuguese, Swedish, Spanish, Turkish, and system default.

---

## 🔧 How it works

1. Install MapFlip.
2. Open the app and tap **"Open Settings"**.
3. Enable link forwarding (under *Open by default / Standardmäßig öffnen*) for `maps.apple.com`.
4. Done! Every Apple Maps link will now open directly in Google Maps.

---

## 📥 Download

- **Google Play:** [Get it on Google Play](https://play.google.com/store/apps/details?id=de.goork.mapflip)
- **F-Droid:** Currently in review ([Merge Request !45011](https://gitlab.com/fdroid/fdroiddata/-/merge_requests/45011))
- **GitHub:** Direct APK download from [Releases](https://github.com/thefraggle/mapflip/releases)

---

## 🛠️ Build

The project is split into two build flavors:
- `play` (for the Google Play Store, includes a rate button and a promo-card).
- `foss` (for F-Droid and GitHub Releases, fully independent of Google services).

Build the FOSS release APK locally:
```bash
./gradlew :app:assembleFossRelease
```

Run unit tests:
```bash
./gradlew :app:testDebugUnitTest
```

---

## ⚙️ Tech Stack

- Kotlin & Jetpack Compose (Material 3)
- Min SDK 26 (Android 8.0), Target SDK 36
- Zero external dependencies beyond AndroidX

---

## 📄 License

Distributed under the [MIT License](LICENSE).  
© 2026 Daniel Notthoff – [notthoff.org](https://notthoff.org)
