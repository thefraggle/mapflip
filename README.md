# MapFlip

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Google Play](https://img.shields.io/badge/Google_Play-Available-green.svg)](https://play.google.com/store/apps/details?id=de.goork.mapflip)
[![F-Droid](https://img.shields.io/badge/F--Droid-In_Review-orange.svg)](https://gitlab.com/fdroid/fdroiddata/-/merge_requests/45011)
[![Permissions](https://img.shields.io/badge/Permissions-0%20Internet-blue.svg)](app/src/main/AndroidManifest.xml)

> **No more clunky browser previews or broken workflows on Android.**  
> MapFlip is a lightweight, 100% offline Android utility that intercepts Apple Maps web links (`maps.apple.com`) and seamlessly redirects them directly to Google Maps.

Set it up once, and it works invisibly in the background.

🔗 **Website & Privacy Policy:** [goork.de/mapflip](https://goork.de/mapflip/)

---

## ⚡ Why MapFlip?

| ❌ Without MapFlip | ✅ With MapFlip |
|---|---|
| Clunky web view in mobile browser | Direct launch in native Google Maps |
| No turn-by-turn navigation or Android Auto | Full turn-by-turn routing & Android Auto support |
| Manual copying & pasting of coordinates | Seamless 1-tap redirect from any app |

---

## ✨ Features

- 🔄 **Automatic & Instant** – Intercepts `maps.apple.com` links on-device; no manual copying or browser detours.
- 🚗 **Navigation & Android Auto Ready** – Opens destinations directly with native routing, live traffic, and Android Auto support.
- 💬 **Universal App Compatibility** – Intercepts links shared in WhatsApp, Telegram, Signal, Slack, SMS, Gmail, and notes.
- 🗺️ **Full Query & Coordinate Support** – Handles search queries, GPS coordinates, place IDs, and addresses.
- 🔒 **100% Offline & Private** – Zero internet permission in manifest (`android.permission.INTERNET` is not declared), zero analytics, zero ads, zero battery drain.
- ⏸️ **Smart Pause Mode** – Pause redirection for 1h, 8h, until tomorrow, or indefinitely via the in-app toggle or **Quick Settings Tile**.
- 🌍 **14 Languages** – Localized in English, German, Spanish, French, Italian, Japanese, Dutch, Danish, Norwegian, Polish, Portuguese (BR & PT), Swedish, and Turkish.

---

## 🔧 How it works

1. Install MapFlip from Google Play or GitHub Releases.
2. Open the app and tap **"Open Settings"**.
3. Under *Open by default* (Standardmäßig öffnen), enable link handling for `maps.apple.com`.
4. Done! Every Apple Maps link will now open directly in Google Maps.

---

## 📥 Download

- **Google Play:** [Get it on Google Play](https://play.google.com/store/apps/details?id=de.goork.mapflip)
- **F-Droid:** Currently in review ([Merge Request !45011](https://gitlab.com/fdroid/fdroiddata/-/merge_requests/45011))
- **GitHub:** Direct APK download from [Releases](https://github.com/thefraggle/mapflip/releases)

---

## 🛠️ Build

The project is split into two build flavors:
- `play` (for Google Play Store, includes rate button and promo banner).
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
- Min SDK 26 (Android 8.0), Target SDK 36 (Android 16)
- Pure local link parser (zero network requests)
- Zero external dependencies beyond AndroidX

---

## 📄 License

Distributed under the [MIT License](LICENSE).  
© 2026 Daniel Notthoff – [notthoff.org](https://notthoff.org)
