## 1.2.13
- Automatic Clipboard Detection: Instantly detects copied map links upon opening the app and provides a 1-tap banner to launch them in your preferred navigation app
- Refined User Interface: Cleaned up status layout and improved readability in pause mode
- Polish & Reliability: Enhanced link handling verification for modern Android versions and overall performance tuning

## 1.2.12
- Stability & Performance: More reliable background processing and improved app responsiveness
- Enhanced Compatibility: Refined link redirection for modern Android versions and third-party map apps
- Polish: General quality improvements and optimizations under the hood

## 1.2.11
- Stability & Performance: More reliable background processing and improved app responsiveness
- Enhanced Compatibility: Refined link redirection for modern Android versions and third-party map apps
- Polish: General quality improvements and optimizations under the hood

## 1.2.10
- Direct Share Support: Share map links directly from WhatsApp or browsers to MapFlip via the Android share menu to open them instantly in your preferred navigation app
- Smarter Link Tester: Automatic clipboard detection on expand plus new buttons to share and quickly clear inputs
- More Map Services: Added support for HERE WeGo and Waze web links, as well as HERE WeGo and Yandex Maps as target navigation apps

## 1.2.9
- Link Tester Clipboard Support: Converted destination links can now be copied directly to the clipboard by tapping the link box or using the dedicated "Copy link" button
- Enhanced Workflow: Easily share and export converted navigation URLs
- Complete localization for all 20 supported languages

## 1.2.8
- Dynamic Link Tester: The test button label now automatically adapts to your selected navigation app (e.g. "Test in Waze", "Test in Organic Maps", "Test in OsmAnd")
- Reliable Intent Fallback: Fixed an issue where redirecting to third-party navigation apps (such as Waze or OsmAnd) could incorrectly fall back to Google Maps
- Enhanced Compatibility: Automatic detection of app variants (e.g. OsmAnd+)

## 1.2.7
- Choose Your Navigation App: Select your preferred maps app in Settings (e.g. Google Maps, Waze, Organic Maps, OsmAnd) or choose to be asked every time
- More Map Services: Now also seamlessly handles links from Bing Maps, OpenStreetMap, and Yandex Maps
- Refined Look: Streamlined settings layout and updated setup guide

## 1.2.6
- Improved Reliability: Enhanced coordinate pin dropping and Apple ID location parsing for navigation apps
- Accessibility & Ergonomics: Optimized TalkBack screen reader support and larger touch targets
- Architecture Modernization: Centralized reactive preferences and state management

## 1.2.5
- Appearance Theme Option: Choose between System default, Light, and Dark mode in Settings
- Cleaner Home Screen: In-app Link Tester is now collapsible to keep the layout tidy
- Streamlined Experience: Refined setup guidance and settings overview
- Complete localization for all 20 supported languages

## 1.2.4
- Cleaner Home Screen: Key status information and pause control right at a glance
- New Settings & Info Menu: Quick access to language options, help, privacy info, and support
- Enhanced Look & Feel: Refined colors, typography, and contrast for both light and dark themes

## 1.2.3
- Smarter Link Detection: Automatically detects even more shared Apple Maps links, navigation routes, and chat messages
- In-App Link Tester: Paste and test any Apple Maps link directly inside the app with a single tap
- 6 New Languages: Added support for Korean, Simplified Chinese, Traditional Chinese, Arabic, Russian, and Indonesian (20 languages total)
- Haptic Feedback: Subtle touch vibrations when interacting with buttons and switches

## 1.2.2
- Modernized user interface: Added premium typography styling and subtle glassmorphic borders to cards
- Improved animations: New animated pulsing radar status indicator for active redirects
- Responsive layouts: Centered and scaled column presentation for tablets, foldables, and landscape mode
- Dialog ergonomics: Modal sheets and dialogs can now be dismissed using physical ESC key and backdrop clicks

## 1.2.1
- Improved Quick Settings Tile: Tapping the active tile now collapses the shade and opens the app directly showing the pause duration options
- Quick One-Tap Resume: Tapping a paused tile continues to instantly reactivate the app with a single click
- Internal gradle cleanups

## 1.2.0
- New feature: Temporarily pause redirect (for 1 hour, 8 hours, or until next morning) with automatic reactivation
- Pause state details are now shown in the Quick Settings Tile
- Internal improvements and cleanups

## 1.1.6
- New feature: Temporarily pause redirect (for 1 hour, 8 hours, or until next morning) with automatic reactivation
- Pause state details are now shown in the Quick Settings Tile

## 1.1.5
- Introduced static VersionCode for reliable F-Droid Reproducible Builds
- Internal stability updates for the build process

## 1.1.4
- Optimized build configuration for F-Droid Reproducible Builds (JDK 21, dependenciesInfo)
- Internal adjustments

## 1.1.3
- Added graphic assets for Fastlane and F-Droid store
- Internal cleanup of resources

## 1.1.2
- Internal security updates for build processes
- Metadata updates to prepare for the F-Droid release

## 1.1.1
- Cleaned up settings: simplified the menu and improved readability
- Added build flavors to prepare for the F-Droid store release

## 1.1.0
- Added direct rating button to easily rate the app on the Google Play Store
- Added a clear privacy note in the app (100% local, no data tracking)
- Optimized app store description for better visibility

## 1.0.9
- Link Tester now displays in the correct language – including English, Japanese, and all others
- Paste button label no longer gets squashed, regardless of translation length

## 1.0.8
- Added in-app Link Tester & Clipboard button for quick link testing
- Improved stability and robustness for link conversions

## 1.0.7
- Added Quick Settings Tile for the Android notification shade
- Quickly pause and resume redirects directly from control center

## 1.0.6
- Added in-app multi-language support for all 14 store languages
- Automatic system language detection with easy language picker menu

## 1.0.5
- Fixed browser forwarding for paused links on modern Android versions
- Reliable opening of original map links when paused

## 1.0.4
- Fixed an issue when opening links while redirect is paused
- Seamlessly opens Apple Maps links in web browser when paused

## 1.0.3
- Added in-app pause toggle switch to temporarily suspend redirects
- Status indicator now highlights paused state

## 1.0.2
- Improved recognition of map links shared in chat apps and messages
- Added support for route links with start and destination locations
- Faster and even more reliable redirect to Google Maps

## 1.0.1
- Added direct feedback button to report issues and suggestions
- New pulsing indicator visualizes active background service status
- Improved overall performance and stability

## 1.0.0
- Initial release of MapFlip
- Automatically redirect Apple Maps links to Google Maps
- Runs completely invisibly in the background
- Privacy-friendly with no data collection
