import SwiftUI

struct SettingsView: View {
    @EnvironmentObject var preferences: PreferencesManager
    @Environment(\.presentationMode) var presentationMode

    var body: some View {
        NavigationView {
            Form {
                Section(header: Text("Bevorzugte Navigations-App")) {
                    ForEach(TargetApp.allCases) { app in
                        HStack {
                            Image(systemName: app.iconName)
                                .foregroundColor(.blue)
                                .frame(width: 28)
                            Text(app.displayName)
                            Spacer()
                            if preferences.selectedTargetApp == app {
                                Image(systemName: "checkmark")
                                    .foregroundColor(.blue)
                            }
                        }
                        .contentShape(Rectangle())
                        .onTapGesture {
                            preferences.selectedTargetApp = app
                        }
                    }
                }

                Section(header: Text("Zwischenablage")) {
                    Toggle("Apple Maps Links automatisch erkennen", isOn: $preferences.autoOpenClipboard)
                }

                Section(header: Text("Über MapFlip")) {
                    HStack {
                        Text("Version")
                        Spacer()
                        Text("1.2.6 (iOS)")
                            .foregroundColor(.secondary)
                    }
                    HStack {
                        Text("Datenschutz")
                        Spacer()
                        Text("100% Offline & Privat")
                            .foregroundColor(.secondary)
                    }
                    Link("Datenschutzerklärung", destination: URL(string: "https://goork.de/mapflip/#privacy")!)
                    Link("Impressum", destination: URL(string: "https://goork.de/mapflip/#impressum")!)
                    Link("Website & Quellcode", destination: URL(string: "https://goork.de/mapflip/")!)
                }
            }
            .navigationTitle("Einstellungen")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Fertig") {
                        presentationMode.wrappedValue.dismiss()
                    }
                }
            }
        }
    }
}
