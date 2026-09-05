## 1.2.19
- Rechtliches & Transparenz: Impressum neben Datenschutzerklärung im Footer verlinkt
- Lokalisierung: Vollständige Übersetzung für Impressum in allen 19 Sprachen
- Benutzeroberfläche: Aufgeräumtes Footer-Layout mit klaren Direktlinks

## 1.2.18
- Quick Settings: Tile zeigt fehlende Link-Einrichtung an und öffnet direkt die Einstellungen
- Zwischenablage: Falscherkennung von Web-Links behoben, Prüfung auf Kartendienste & Fehler-Feedback
- Pause-Modus: Nachtpausen vor 06:00 Uhr stoppen jetzt verlässlich am selben Morgen
- Qualität: Test-Suite auf 81 Tests erweitert (Zeitzonen-, Datums- & Sonderzeichen-Prüfung)

## 1.2.17
- UX: Status-Badges („Installiert“ / „Nicht installiert“) im Ziel-App-Auswahldialog
- Barrierefreiheit: Touch-Target für „Einfügen“-Button im Link-Tester auf 48 dp vergrößert
- GPS-Präzision: Koordinatenübergabe auf 6 Nachkommastellen (~11 cm) optimiert
- Kartendienste: HERE WeGo und Yandex Maps als Ziel-Apps sowie Multi-Karten-Parsing vereinheitlicht
- Open Source: GitHub Issue-Templates für Bug-Reports und Feature-Requests ergänzt

## 1.2.16
- Sicherheit & Open-Source-Hygiene: Telemetrie-Zugangsdaten (Aptabase Ingest-Key & Host) vollständig aus dem öffentlichen Quellcode entfernt und in sichere Build-Konfigurationen ausgelagert
- Robustheit & Stabilität: Automatischer No-Op-Fallback für Telemetrie bei fehlenden Konfigurationsdaten in Test- und Fork-Builds

## 1.2.15
- Stabilität & Crash-Schutz: Absicherung sämtlicher Zwischenablage-Zugriffe im Link-Tester gegen leere Daten und Sicherheitsbeschränkungen
- GPS-Präzision: Koordinatenübergabe auf 6 Nachkommastellen (~11 cm Genauigkeit) für exakte Zielnavigation optimiert
- Vollständige Lokalisierung: Aktions-Banner, Screenreader-Texte und Hinweismeldungen über alle 19 Sprachen vervollständigt
- Unsichtbare Weiterleitung: Schließanimationen für nahtlose Weiterleitung unterdrückt
- Parser-Härtung & Web-Fallback: Robusteres Parsing bei Sonderzeichen in Karten-URLs

## 1.2.14
- Stabilitäts-Fix: Behebt einen kritischen Start-Absturz bei abgelaufenen Pausenzeiten in den Einstellungen
- Fehlerbehandlung & Robustheit: System- und Zwischenablage-Zugriffe beim Start noch widerstandsfähiger gestaltet

## 1.2.13
- Automatische Zwischenablage-Erkennung: Erkennt kopierte Karten-Links beim App-Start sofort und bietet ein 1-Tap-Aktions-Banner zum direkten Öffnen in deiner Lieblings-Navigations-App
- Optimierte Benutzeroberfläche: Aufgeräumte Statusanzeige und verbesserte Übersicht im Pausen-Modus
- Feinschliff & Zuverlässigkeit: Präzisere Link-Verifizierung für aktuelle Android-Versionen und optimierte App-Reaktionszeiten

## 1.2.12
- Stabilität & Performance: Zuverlässigere Hintergrund-Verarbeitung und optimierte App-Reaktionszeiten
- Verbesserte Kompatibilität: Verfeinerte Weiterleitung für aktuelle Android-Versionen und Drittanbieter-Karten-Apps
- Feinschliff: Allgemeine Qualitätsverbesserungen und Bereinigungen unter der Haube

## 1.2.11
- Stabilität & Performance: Zuverlässigere Hintergrund-Verarbeitung und optimierte App-Reaktionszeiten
- Verbesserte Kompatibilität: Verfeinerte Weiterleitung für aktuelle Android-Versionen und Drittanbieter-Karten-Apps
- Feinschliff: Allgemeine Qualitätsverbesserungen und Bereinigungen unter der Haube

## 1.2.10
- Direktes Teilen: Links aus WhatsApp oder Browsern jetzt einfach über das Android-Teilen-Menü an MapFlip senden und sofort in deiner Lieblings-Karten-App öffnen
- Komfortabler Link-Tester: Automatisches Einfügen aus der Zwischenablage beim Öffnen sowie neue Buttons zum Teilen und schnellen Leeren
- Mehr Kartendienste: Unterstützt jetzt auch Links von HERE WeGo und Waze im Web sowie HERE WeGo und Yandex Maps als Ziel-Apps

## 1.2.9
- Link-Tester Zwischenablage: Konvertierte Ziel-Links können jetzt per Fingertipp oder über den neuen Button „Link kopieren“ direkt in die Zwischenablage kopiert werden
- Optimierte Bedienung: Direktes Teilen und Weiterleiten umgewandelter Karten-Links erleichtert
- Vollständige Übersetzungen für alle 20 unterstützten Sprachen

## 1.2.8
- Dynamischer Link-Tester: Test-Button passt seine Beschriftung jetzt automatisch an die gewählte Navigations-App an (z. B. „In Waze testen“, „In Organic Maps testen“, „In OsmAnd testen“)
- Zuverlässiger Intent-Fallback: Behebt ein Problem, bei dem das Öffnen von Links zu Drittanbieter-Karten (wie Waze oder OsmAnd) fälschlicherweise auf Google Maps zurückfiel
- Verbesserte Kompatibilität: Automatische Erkennung von Varianten installierter Karten-Apps (z. B. OsmAnd+)

## 1.2.7
- Freie Wahl der Navigations-App: Wähle in den Einstellungen deine bevorzugte Karten-App (z. B. Google Maps, Waze, Organic Maps, OsmAnd) oder lass dich jedes Mal fragen
- Mehr Kartendienste: Unterstützt neben Apple Maps jetzt auch Links von Bing Maps, OpenStreetMap und Yandex Maps
- Einheitliches Design: Aufgeräumte Einstellungen und aktualisierte Kurzanleitung

## 1.2.6
- Höhere Zuverlässigkeit: Verbesserte Übergabe von Koordinaten und Apple-ID-Standorten an Navigations-Apps
- Barrierefreiheit & Bedienung: Optimierte Sprachausgabe (TalkBack) und vergrößerte Touch-Flächen
- Architektur-Modernisierung: Reaktives Einstellungs- und Statusmanagement im Hintergrund

## 1.2.5
- Individuelles Erscheinungsbild: Wahl zwischen Systemeinstellung, Hell- und Dunkelmodus in den Einstellungen
- Aufgeräumterer Startbildschirm: Link-Tester lässt sich bei Bedarf ein- und ausklappen
- Verbesserte Übersicht: Einstellungen und Kurzanleitung noch verständlicher gestaltet
- Vollständige Übersetzungen für alle 20 unterstützten Sprachen

## 1.2.4
- Übersichtlicherer Startbildschirm: Wichtigste Status-Infos und Pause-Funktion auf einen Blick
- Neues Einstellungs- und Info-Menü: Schneller Zugriff auf Sprache, Hilfe, Datenschutz und Feedback
- Verbessertes Design: Harmonischere Farben und Kontraste im Hell- und Dunkelmodus

## 1.2.3
- Zuverlässigere Link-Erkennung: Erkennt jetzt noch mehr geteilte Apple-Karten-Links, Routen und Messenger-Nachrichten direkt
- Link-Tester & Zwischenablage: Apple-Maps-Links können jetzt direkt in der App eingefügt und sofort in Google Maps getestet werden
- 6 neue Sprachen: MapFlip spricht jetzt zusätzlich Koreanisch, Chinesisch (Vereinfacht & Traditionell), Arabisch, Russisch und Indonesisch (insgesamt 20 Sprachen)
- Spürbares Feedback: Dezente Vibrationen beim Tippen auf Tasten und Schalter

## 1.2.2
- Modernisierte Benutzeroberfläche: Premium-Typografie und Glassmorphism-Kartenrahmen hinzugefügt
- Verbesserte Animationen: Neuer pulsierender Radar-Status-Glow zeigt die App-Aktivität noch lebendiger an
- Responsive Darstellung: Optimiertes, zentriertes Layout für Tablets, Foldables und Querformat
- Dialog-Ergonomie: Dialoge und Sprachwahl lassen sich per physischer ESC-Taste und Klick auf das Backdrop schließen

## 1.2.1
- Kachel-Verhalten verbessert: Wenn die Umleitung aktiv ist, öffnet ein Klick auf die Schnelleinstellungs-Kachel nun direkt die App mit dem Pausen-Auswahldialog
- Kachel-Direkt-Fortsetzen: Wenn MapFlip pausiert ist, reaktiviert ein Klick auf die Kachel die App sofort weiterhin per Ein-Klick-Aktion
- Interne gradle Bereinigungen

## 1.2.0
- Neue Funktion: Umleitung vorübergehend pausieren (für 1 Stunde, 8 Stunden oder bis zum nächsten Morgen) mit automatischer Reaktivierung
- Pausenzustand wird jetzt in der System-Kachel detailliert angezeigt
- Interne Verbesserungen und Bereinigungen

## 1.1.6
- Neue Funktion: Umleitung vorübergehend pausieren (für 1 Stunde, 8 Stunden oder bis zum nächsten Morgen) mit automatischer Reaktivierung
- Pausenzustand wird jetzt auch in der System-Kachel detailliert angezeigt

## 1.1.5
- Statischen VersionCode für zuverlässige F-Droid Reproducible Builds eingeführt
- Interne Stabilitäts-Updates für den Build-Prozess

## 1.1.4
- Build-Konfiguration für F-Droid Reproducible Builds optimiert (JDK 21, dependenciesInfo)
- Interne Anpassungen

## 1.1.3
- Grafische Assets für Fastlane und F-Droid-Store hinzugefügt
- Interne Bereinigung der Ressourcen

## 1.1.2
- Interne Sicherheits-Updates für Build-Prozesse
- Metadaten-Updates zur Vorbereitung des F-Droid-Releases

## 1.1.1
- Einstellungen aufgeräumt: Menü vereinfacht und Lesbarkeit verbessert
- Build-Flavors hinzugefügt zur Vorbereitung des F-Droid-Releases

## 1.1.0
- Neuer Bewertungs-Button zum schnellen Bewerten der App im Google Play Store
- Dezenten Datenschutzhinweis in der App-UI hinzugefügt (100% lokal, kein Datentracking)
- App-Beschreibung für bessere Auffindbarkeit im App Store optimiert

## 1.0.9
- Link-Tester zeigt jetzt in der richtigen Sprache an – auch bei Englisch, Japanisch und allen anderen
- Kachel-Beschriftung passt sich nicht mehr ab, egal wie lang die Übersetzung ist

## 1.0.8
- In-App Link-Tester & Zwischenablage-Button zum schnellen Ausprobieren von Links hinzugefügt
- Verbesserte Stabilität und Zuverlässigkeit bei der Link-Umwandlung

## 1.0.7
- Schnelleinstellungs-Kachel für die Android-Benachrichtigungsleiste hinzugefügt
- Pausieren und Fortsetzen der Umleitung direkt über das Kontrollzentrum möglich

## 1.0.6
- In-App Mehrsprachigkeit für alle 14 Store-Sprachen hinzugefügt
- Automatische Erkennung der Systemsprache mit einfachem Sprachwahl-Menü

## 1.0.5
- Browser-Weiterleitung im pausierten Zustand für neuere Android-Versionen korrigiert
- Zuverlässiges Öffnen der originalen Karten-Links beim Pausieren

## 1.0.4
- Fehler beim Öffnen von Links im pausierten Zustand behoben
- Nahtloses Öffnen von Apple-Maps-Links im Browser bei pausierter Weiterleitung

## 1.0.3
- Neuer Pause-Schalter in der App zum vorübergehenden Deaktivieren der Weiterleitung
- Statusanzeige signalisiert jetzt auch den pausierten Zustand

## 1.0.2
- Erkennung von Karten-Links aus Chats und Nachrichten verbessert
- Unterstützung für Routen mit Start- und Zielort hinzugefügt
- Noch schnellere und zuverlässigere Weiterleitung zu Google Maps

## 1.0.1
- Feedback-Button zum schnellen Melden von Wünschen und Fehlern hinzugefügt
- Neue pulsierende Anzeige signalisiert die aktive Hintergrundfunktion
- Performance und Stabilität verbessert

## 1.0.0
- Erstveröffentlichung von MapFlip
- Automatische Weiterleitung von Apple Maps Links zu Google Maps
- Läuft komplett unsichtbar im Hintergrund
- Datenschutzfreundlich ohne Datenerfassung
