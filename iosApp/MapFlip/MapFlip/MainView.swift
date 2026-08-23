import SwiftUI
import UIKit

struct MainView: View {
    @EnvironmentObject var preferences: PreferencesManager
    @State private var clipboardUrl: String? = nil
    @State private var testInputUrl: String = "https://maps.apple.com/?q=Eiffelturm"
    @State private var convertedUrl: String = ""
    @State private var showingSettings: Bool = false
    @State private var showingHowTo: Bool = false

    var body: some View {
        NavigationView {
            ScrollView {
                VStack(spacing: 20) {
                    // Header Card
                    VStack(spacing: 8) {
                        Image(systemName: "arrow.triangle.2.circlepath.circle.fill")
                            .resizable()
                            .frame(width: 64, height: 64)
                            .foregroundColor(.blue)

                        Text("MapFlip")
                            .font(.largeTitle)
                            .bold()

                        Text("Apple Maps Links nahtlos in \(preferences.selectedTargetApp.displayName) öffnen.")
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                            .multilineTextAlignment(.center)
                            .padding(.horizontal)
                    }
                    .padding(.top, 20)

                    // Clipboard Banner
                    if let clip = clipboardUrl {
                        VStack(alignment: .leading, spacing: 10) {
                            HStack {
                                Image(systemName: "doc.on.clipboard.fill")
                                    .foregroundColor(.blue)
                                Text("Apple Maps Link in Zwischenablage")
                                    .font(.headline)
                            }

                            Text(clip)
                                .font(.caption)
                                .foregroundColor(.secondary)
                                .lineLimit(2)

                            Button(action: {
                                openLocation(urlStr: clip)
                            }) {
                                HStack {
                                    Image(systemName: preferences.selectedTargetApp.iconName)
                                    Text("In \(preferences.selectedTargetApp.displayName) öffnen")
                                }
                                .frame(maxWidth: .infinity)
                                .padding(.vertical, 12)
                                .background(Color.blue)
                                .foregroundColor(.white)
                                .cornerRadius(10)
                            }
                        }
                        .padding()
                        .background(Color(UIColor.secondarySystemBackground))
                        .cornerRadius(14)
                        .padding(.horizontal)
                    }

                    // How-To Card
                    VStack(alignment: .leading, spacing: 12) {
                        HStack {
                            Image(systemName: "square.and.arrow.up")
                                .foregroundColor(.orange)
                            Text("So funktioniert's auf iOS")
                                .font(.headline)
                        }

                        VStack(alignment: .leading, spacing: 8) {
                            StepRow(number: "1", text: "Öffne einen Apple Maps Link oder Ort.")
                            StepRow(number: "2", text: "Tippe auf das Teilen-Symbol (Share Sheet).")
                            StepRow(number: "3", text: "Wähle MapFlip – der Link öffnet direkt in \(preferences.selectedTargetApp.displayName).")
                        }
                    }
                    .padding()
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .background(Color(UIColor.secondarySystemBackground))
                    .cornerRadius(14)
                    .padding(.horizontal)

                    // Link Tester
                    VStack(alignment: .leading, spacing: 12) {
                        Text("Link Tester")
                            .font(.headline)

                        TextField("Apple Maps URL eingeben...", text: $testInputUrl)
                            .textFieldStyle(RoundedBorderTextFieldStyle())
                            .autocapitalization(.none)
                            .disableAutocorrection(true)

                        Button(action: {
                            openLocation(urlStr: testInputUrl)
                        }) {
                            HStack {
                                Image(systemName: "arrow.up.right.square")
                                Text("Konvertieren & Öffnen")
                            }
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 12)
                            .background(Color.accentColor)
                            .foregroundColor(.white)
                            .cornerRadius(10)
                        }
                    }
                    .padding()
                    .background(Color(UIColor.secondarySystemBackground))
                    .cornerRadius(14)
                    .padding(.horizontal)
                }
                .padding(.bottom, 30)
            }
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button(action: { showingSettings = true }) {
                        Image(systemName: "gearshape")
                    }
                }
            }
            .sheet(isPresented: $showingSettings) {
                SettingsView()
                    .environmentObject(preferences)
            }
            .onAppear {
                checkClipboard()
            }
        }
    }

    private func checkClipboard() {
        if UIPasteboard.general.hasStrings, let text = UIPasteboard.general.string {
            if let link = AppleMapsParser.extractMapUrl(from: text) {
                self.clipboardUrl = link
            }
        }
    }

    private func openLocation(urlStr: String) {
        let parsed = AppleMapsParser.parse(appleUrl: urlStr)
        let targetUrl = UrlSchemeBuilder.buildUrl(for: parsed, target: preferences.selectedTargetApp)
        if UIApplication.shared.canOpenURL(targetUrl) {
            UIApplication.shared.open(targetUrl)
        } else {
            if let fallbackWeb = URL(string: "https://www.google.com/maps/search/?api=1&query=\(urlStr)") {
                UIApplication.shared.open(fallbackWeb)
            }
        }
    }
}

struct StepRow: View {
    let number: String
    let text: String

    var body: some View {
        HStack(alignment: .top, spacing: 10) {
            Text(number)
                .font(.caption)
                .bold()
                .frame(width: 22, height: 22)
                .background(Color.blue.opacity(0.2))
                .foregroundColor(.blue)
                .clipShape(Circle())
            Text(text)
                .font(.subheadline)
                .foregroundColor(.primary)
        }
    }
}
