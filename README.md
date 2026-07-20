# MapFlip

Automatically redirect Apple Maps links to Google Maps on Android. Set up once, then it works invisibly in the background.

🔗 **Website & Privacy Policy:** [goork.de/mapflip](https://goork.de/mapflip/)

## Features

- 🔄 Automatic redirect – no manual copying
- 👻 Invisible – the app doesn't visibly open
- 🗺️ Supports searches, coordinates, addresses & navigation
- 🔒 No data collection, no ads
- 🌍 English & German

## How it works

1. Install MapFlip
2. Open the app → tap "Open Settings"
3. Enable link forwarding for `maps.apple.com`
4. Done!

From now on, every Apple Maps link opens directly in Google Maps.

## Download

Download the latest APK from [Releases](https://github.com/thefraggle/mapflip/releases).

## Tech Stack

- Kotlin + Jetpack Compose (Material 3)
- Min SDK 26 (Android 8.0), Target SDK 35
- No external dependencies beyond AndroidX

## Build

```bash
./gradlew :app:assembleDebug
./gradlew :app:testDebugUnitTest
```

## License

[MIT](LICENSE) © 2026 Daniel Notthoff – [notthoff.org](https://notthoff.org)
