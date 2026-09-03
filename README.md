# MapFlip

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Google Play](https://img.shields.io/badge/Google_Play-Available-green.svg)](https://play.google.com/store/apps/details?id=de.goork.mapflip)
[![F-Droid](https://img.shields.io/badge/F--Droid-In_Review-orange.svg)](https://gitlab.com/fdroid/fdroiddata/-/merge_requests/45011)
[![Build Status](https://github.com/thefraggle/mapflip/actions/workflows/android-release.yml/badge.svg)](https://github.com/thefraggle/mapflip/actions/workflows/android-release.yml)

> **No more clunky web previews or broken map links on Android.**  
> MapFlip is a lightweight Android utility that intercepts map links and seamlessly redirects them directly into your favorite navigation app.

Set it up once, and it works invisibly in the background.

🔗 **Website & Privacy Policy:** [goork.de/mapflip](https://goork.de/mapflip/)

---

## ⚡ Why MapFlip?

| ❌ Without MapFlip | ✅ With MapFlip |
|---|---|
| Clunky web previews in mobile browser | Direct launch in native navigation apps |
| No turn-by-turn routing or Android Auto | Full turn-by-turn routing & Android Auto support |
| Manual copying & pasting of coordinates | Seamless 1-tap redirect or clipboard detection |
| Locked into single proprietary providers | Full flexibility: Google Maps, OsmAnd, Organic Maps, Waze & more |

---

## ✨ Features

- 🔄 **Multi-Service Interception** – Intercepts map links from:
  - **Apple Maps** (`maps.apple.com`, `applemaps://`)
  - **Bing Maps** (`bing.com/maps`, `maps.bing.com`)
  - **OpenStreetMap** (`openstreetmap.org`, `osm.org`)
  - **Yandex Maps** (`yandex.com/maps`, `maps.yandex.ru`, etc.)
  - **HERE WeGo** (`wego.here.com`, `share.here.com`, `here.com`)
  - **Waze** (`waze.com/ul`, `waze.com/live-map`)
- 🎯 **Configurable Target Apps** – Route destinations directly to:
  - **Google Maps**
  - **Waze**
  - **Organic Maps** (100% FOSS)
  - **OsmAnd / OsmAnd+** (100% FOSS)
  - **HERE WeGo**
  - **Yandex Maps**
  - **System App Picker** (Always ask)
- 📋 **Smart Clipboard Banner** – Detects map links copied to your clipboard on app open for quick 1-tap launching.
- 🧪 **Interactive Link Tester** – Test map URLs, convert target URIs, copy, or share without leaving the app.
- ⏸️ **Smart Pause Mode** – Temporarily pause redirection (1h, 8h, until tomorrow morning, or indefinitely) in-app or via the **Quick Settings Tile**.
- 🔒 **Privacy-First Architecture**:
  - **FOSS Flavor**: 100% offline, zero internet permission in manifest (`android.permission.INTERNET` not declared), zero tracking, zero ads.
  - **Play Flavor**: Minimal anonymous telemetry via self-hosted Aptabase (app version, OS, event names; zero personal data or location coordinates).
- 🌍 **19 Languages + Auto (RTL supported)** – English, German, Spanish, French, Italian, Japanese, Dutch, Danish, Norwegian, Polish, Portuguese, Swedish, Turkish, Korean, Simplified Chinese, Traditional Chinese, Arabic (RTL), Russian, and Indonesian.

---

## 🔧 How It Works

1. Install MapFlip from Google Play, F-Droid, or GitHub Releases.
2. Open the app and tap **"Open Settings"**.
3. Under *Open by default* (Standardmäßig öffnen), enable link handling for the supported map domains.
4. Select your preferred navigation app in MapFlip settings (or keep Google Maps as default).
5. Done! Map links will now open directly in your chosen navigation app.

---

## 📥 Download

- **Google Play:** [Get it on Google Play](https://play.google.com/store/apps/details?id=de.goork.mapflip)
- **F-Droid:** Currently in review ([Merge Request !45011](https://gitlab.com/fdroid/fdroiddata/-/merge_requests/45011))
- **GitHub:** Direct APK download from [Releases](https://github.com/thefraggle/mapflip/releases)

---

## 🛠️ Build & Development

The project is split into two flavors:
- `foss` – 100% open source, zero permissions, independent of Google services.
- `play` – Google Play release flavor with rating prompts and self-hosted privacy-focused telemetry.

Build the FOSS release APK locally:
```bash
./gradlew :app:assembleFossRelease
```

Run unit tests:
```bash
./gradlew test
```

---

## ⚙️ Tech Stack

- Kotlin & Jetpack Compose (Material 3)
- Min SDK 26 (Android 8.0), Target SDK 36 (Android 16)
- High-precision local coordinate & query parsing
- Zero external SDK dependencies in FOSS flavor

---

## 📄 License

Distributed under the [MIT License](LICENSE).  
© 2026 Daniel Notthoff – [notthoff.org](https://notthoff.org)
