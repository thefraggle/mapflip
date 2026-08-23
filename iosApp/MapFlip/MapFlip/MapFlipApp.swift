import SwiftUI

@main
struct MapFlipApp: App {
    @StateObject private var preferences = PreferencesManager.shared

    var body: some Scene {
        WindowGroup {
            MainView()
                .environmentObject(preferences)
        }
    }
}
