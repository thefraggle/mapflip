import Foundation
import Combine

/// Shared user preferences across MapFlip App and Share Extension using AppGroup UserDefaults.
public class PreferencesManager: ObservableObject {
    public static let shared = PreferencesManager()

    public static let appGroupId = "group.de.goork.mapflip"
    private let userDefaults: UserDefaults

    private enum Keys {
        static let selectedTargetApp = "selected_target_app"
        static let autoOpenClipboard = "auto_open_clipboard"
        static let appTheme = "app_theme"
        static let appLanguage = "app_language"
    }

    public init(defaults: UserDefaults = UserDefaults(suiteName: appGroupId) ?? .standard) {
        self.userDefaults = defaults
        self.selectedTargetApp = TargetApp(rawValue: defaults.string(forKey: Keys.selectedTargetApp) ?? "") ?? .googleMaps
        self.autoOpenClipboard = defaults.object(forKey: Keys.autoOpenClipboard) as? Bool ?? true
        self.appTheme = defaults.string(forKey: Keys.appTheme) ?? "system"
        self.appLanguage = defaults.string(forKey: Keys.appLanguage) ?? "auto"
    }

    @Published public var selectedTargetApp: TargetApp {
        didSet {
            userDefaults.set(selectedTargetApp.rawValue, forKey: Keys.selectedTargetApp)
        }
    }

    @Published public var autoOpenClipboard: Bool {
        didSet {
            userDefaults.set(autoOpenClipboard, forKey: Keys.autoOpenClipboard)
        }
    }

    @Published public var appTheme: String {
        didSet {
            userDefaults.set(appTheme, forKey: Keys.appTheme)
        }
    }

    @Published public var appLanguage: String {
        didSet {
            userDefaults.set(appLanguage, forKey: Keys.appLanguage)
        }
    }
}
