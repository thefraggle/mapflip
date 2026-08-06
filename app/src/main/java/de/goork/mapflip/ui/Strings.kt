package de.goork.mapflip.ui

import java.util.Locale

/**
 * App strings for all 14 supported Play Store languages.
 * Custom solution supporting runtime language switching without activity restarts.
 */
object Strings {

    data class LanguageItem(
        val code: String,
        val nativeName: String,
        val flag: String,
    )

    val SUPPORTED_LANGUAGES = listOf(
        LanguageItem("auto", "Systemstandard", "🌐"),
        LanguageItem("de", "Deutsch", "🇩🇪"),
        LanguageItem("en", "English", "🇬🇧"),
        LanguageItem("da", "Dansk", "🇩🇰"),
        LanguageItem("fr", "Français", "🇫🇷"),
        LanguageItem("it", "Italiano", "🇮🇹"),
        LanguageItem("ja", "日本語", "🇯🇵"),
        LanguageItem("nl", "Nederlands", "🇳🇱"),
        LanguageItem("no", "Norsk", "🇳🇴"),
        LanguageItem("pl", "Polski", "🇵🇱"),
        LanguageItem("pt", "Português", "🇵🇹"),
        LanguageItem("sv", "Svenska", "🇸🇪"),
        LanguageItem("es", "Español", "🇪🇸"),
        LanguageItem("tr", "Türkçe", "🇹🇷")
    )

    data class AppStrings(
        val headline: String,
        val subtitle: String,
        val tagline: String,
        val setupTitle: String,
        val step1: String,
        val step2: String,
        val step3: String,
        val btnSettings: String,
        val statusActive: String,
        val statusInactive: String,
        val statusHint: String,
        val famwakeTitle: String,
        val famwakePromo: String,
        val famwakeDesc: String,
        val famwakeButton: String,
        val copyright: String,
        val langToggle: String,
        val btnFeedback: String,
        val pauseTitle: String,
        val pauseDesc: String,
        val statusPaused: String,
        val selectLanguageTitle: String,
        val systemLanguageAuto: String,
        val btnRateApp: String = "Rate App",
        val privacyNote: String = "100% privacy-friendly: All redirects are processed locally. No data collection.",
        val testLinkTitle: String = "Link Tester & Clipboard",
        val testLinkHint: String = "Enter Apple Maps link here...",
        val btnPasteClipboard: String = "Paste",
        val btnTestLink: String = "Test in Google Maps",
        val testLinkConvertedLabel: String = "Converted Target URI:"
    )

    fun resolveLanguage(savedPref: String?): String {
        if (!savedPref.isNullOrBlank() && savedPref != "auto") {
            return savedPref
        }
        val sysLang = Locale.getDefault().language.lowercase()
        val supportedCodes = listOf("de", "da", "fr", "it", "ja", "nl", "no", "pl", "pt", "sv", "es", "tr", "en")
        return if (supportedCodes.contains(sysLang)) sysLang else "en"
    }

    fun getStrings(langCode: String): AppStrings {
        return when (langCode.lowercase()) {
            "de" -> DE
            "da" -> DA
            "fr" -> FR
            "it" -> IT
            "ja" -> JA
            "nl" -> NL
            "no" -> NO
            "pl" -> PL
            "pt" -> PT
            "sv" -> SV
            "es" -> ES
            "tr" -> TR
            else -> EN
        }
    }

    val DE = AppStrings(
        headline = "MapFlip",
        subtitle = "Apple Maps \u2192 Google Maps",
        tagline = "Automatisch. Unsichtbar.",
        setupTitle = "So geht\u2019s",
        step1 = "Tippe auf den Button unten",
        step2 = "Aktiviere \"Links \u00f6ffnen\" f\u00fcr maps.apple.com",
        step3 = "Fertig! Apple Maps Links \u00f6ffnen sich ab jetzt automatisch in Google Maps.",
        btnSettings = "Einstellungen \u00f6ffnen",
        statusActive = "Links sind aktiviert",
        statusInactive = "Links sind noch nicht aktiv",
        statusHint = "Auf \u00e4lteren Android-Versionen kann der Status nicht gepr\u00fcft werden.",
        famwakeTitle = "FamWake \u2013 Familienwecker",
        famwakePromo = "Vom gleichen Entwickler",
        famwakeDesc = "FamWake koordiniert den Morgen f\u00fcr die ganze Familie \u2013 Bad-Zeiten, Fr\u00fchst\u00fcck und Aufstehen.",
        famwakeButton = "Mehr erfahren",
        copyright = "\u00a9 2026 Daniel Notthoff \u2022 notthoff.org",
        langToggle = "DE",
        btnFeedback = "Support",
        pauseTitle = "Weiterleitung pausieren",
        pauseDesc = "Vorübergehend alle Umleitungen aussetzen",
        statusPaused = "Weiterleitung ist pausiert",
        selectLanguageTitle = "Sprache wählen",
        systemLanguageAuto = "Systemstandard (Automatisch)",
        btnRateApp = "App bewerten",
        privacyNote = "100% datenschutzfreundlich: Alle Konvertierungen finden lokal statt. Keine Datenerfassung.",
        testLinkTitle = "Link-Tester & Zwischenablage",
        testLinkHint = "Apple Maps Link hier eingeben...",
        btnPasteClipboard = "Einfügen",
        btnTestLink = "In Google Maps testen",
        testLinkConvertedLabel = "Umgewandelte Ziel-URI:"
    )

    val EN = AppStrings(
        headline = "MapFlip",
        subtitle = "Apple Maps \u2192 Google Maps",
        tagline = "Automatic. Invisible.",
        setupTitle = "How it works",
        step1 = "Tap the button below",
        step2 = "Enable \"Open links\" for maps.apple.com",
        step3 = "Done! Apple Maps links will now open automatically in Google Maps.",
        btnSettings = "Open Settings",
        statusActive = "Links are enabled",
        statusInactive = "Links are not yet active",
        statusHint = "Status cannot be checked on older Android versions.",
        famwakeTitle = "FamWake \u2013 Family Alarm Clock",
        famwakePromo = "From the same developer",
        famwakeDesc = "FamWake coordinates the morning for the whole family \u2013 bathroom times, breakfast, and wake-up.",
        famwakeButton = "Learn more",
        copyright = "\u00a9 2026 Daniel Notthoff \u2022 notthoff.org",
        langToggle = "EN",
        btnFeedback = "Support",
        pauseTitle = "Pause Redirect",
        pauseDesc = "Temporarily suspend all link redirects",
        statusPaused = "Redirect is paused",
        selectLanguageTitle = "Select Language",
        systemLanguageAuto = "System Default (Auto)",
        btnRateApp = "Rate App",
        privacyNote = "100% privacy-friendly: All redirects are processed locally. No data collection.",
        testLinkTitle = "Link Tester & Clipboard",
        testLinkHint = "Enter Apple Maps link here...",
        btnPasteClipboard = "Paste",
        btnTestLink = "Test in Google Maps",
        testLinkConvertedLabel = "Converted Target URI:"
    )

    val DA = EN.copy(
        tagline = "Automatisk. Usynlig.",
        setupTitle = "Sådan virker det",
        step1 = "Tryk på knappen nedenfor",
        step2 = "Aktivér \"Åbn links\" for maps.apple.com",
        step3 = "Færdig! Apple Maps links åbnes nu automatisk i Google Maps.",
        btnSettings = "Åbn indstillinger",
        statusActive = "Links er aktiveret",
        statusInactive = "Links er endnu ikke aktive",
        statusHint = "Status kan ikke kontrolleres på ældre Android-versioner.",
        langToggle = "DA",
        btnFeedback = "Support",
        pauseTitle = "Pausér omdirigering",
        pauseDesc = "Midlertidigt afbryd alle linkomdirigeringer",
        statusPaused = "Omdirigering er sat på pause",
        selectLanguageTitle = "Vælg sprog",
        systemLanguageAuto = "Systemstandard (Automatisk)",
        btnRateApp = "Bedøm app",
        privacyNote = "100% privatlivsvenlig: Alle omdirigeringer sker lokalt. Ingen dataindsamling.",
        testLinkTitle = "Link-tester & udklipsholder",
        testLinkHint = "Indtast Apple Maps-link her...",
        btnPasteClipboard = "Indsæt",
        btnTestLink = "Test i Google Maps",
        testLinkConvertedLabel = "Konverteret mål-URI:"
    )

    val FR = EN.copy(
        tagline = "Automatique. Invisible.",
        setupTitle = "Comment ça marche",
        step1 = "Appuyez sur le bouton ci-dessous",
        step2 = "Activez \"Ouvrir les liens\" pour maps.apple.com",
        step3 = "Terminé! Les liens Apple Maps s'ouvriront désormais automatiquement dans Google Maps.",
        btnSettings = "Ouvrir les paramètres",
        statusActive = "Les liens sont activés",
        statusInactive = "Les liens ne sont pas encore actifs",
        statusHint = "Le statut ne peut pas être vérifié sur les anciennes versions d'Android.",
        langToggle = "FR",
        btnFeedback = "Support",
        pauseTitle = "Mettre en pause la redirection",
        pauseDesc = "Suspendre temporairement toutes les redirections",
        statusPaused = "La redirection est en pause",
        selectLanguageTitle = "Choisir la langue",
        systemLanguageAuto = "Par défaut du système (Auto)",
        btnRateApp = "Évaluer l'application",
        privacyNote = "100% respectueux de la vie privée : redirection locale, aucune collecte de données.",
        testLinkTitle = "Testeur de liens & presse-papiers",
        testLinkHint = "Entrez le lien Apple Maps ici...",
        btnPasteClipboard = "Coller",
        btnTestLink = "Tester dans Google Maps",
        testLinkConvertedLabel = "URI cible convertie :"
    )

    val IT = EN.copy(
        tagline = "Automatico. Invisibile.",
        setupTitle = "Come funziona",
        step1 = "Tocca il pulsante qui sotto",
        step2 = "Attiva \"Apri link\" per maps.apple.com",
        step3 = "Fatto! I link di Apple Maps si apriranno ora automaticamente in Google Maps.",
        btnSettings = "Apri Impostazioni",
        statusActive = "I link sono attivi",
        statusInactive = "I link non sono ancora attivi",
        statusHint = "Lo stato non può essere verificato sulle versioni Android meno recenti.",
        langToggle = "IT",
        btnFeedback = "Supporto",
        pauseTitle = "Pausa reindirizzamento",
        pauseDesc = "Sospendi temporaneamente tutti i reindirizzamenti",
        statusPaused = "Il reindirizzamento è in pausa",
        selectLanguageTitle = "Seleziona lingua",
        systemLanguageAuto = "Predefinita di sistema (Auto)",
        btnRateApp = "Valuta l'app",
        privacyNote = "100% rispetto della privacy: il reindirizzamento avviene localmente. Nessuna raccolta dati.",
        testLinkTitle = "Tester di link e appunti",
        testLinkHint = "Inserisci qui il link di Apple Maps...",
        btnPasteClipboard = "Incolla",
        btnTestLink = "Testa in Google Maps",
        testLinkConvertedLabel = "URI di destinazione convertito:"
    )

    val JA = EN.copy(
        tagline = "自動。バックグラウンド。",
        setupTitle = "使い方",
        step1 = "下のボタンをタップ",
        step2 = "maps.apple.com の「リンクを開く」を有効化",
        step3 = "完了！Apple Maps のリンクが Google Maps で自動的に開きます。",
        btnSettings = "設定を開く",
        statusActive = "リンク機能は有効です",
        statusInactive = "リンク機能はまだ有効ではありません",
        statusHint = "古い Android バージョンではステータスを確認できません。",
        langToggle = "JA",
        btnFeedback = "サポート",
        pauseTitle = "転送を一時停止",
        pauseDesc = "すべてのリンク転送を一時的に停止します",
        statusPaused = "転送は一時停止中です",
        selectLanguageTitle = "言語を選択",
        systemLanguageAuto = "システムデフォルト (自動)",
        btnRateApp = "アプリを評価する",
        privacyNote = "100% プライバシー保護：転送処理はすべてローカルで行われ、データ収集は一切行いません。",
        testLinkTitle = "リンクテスター＆クリップボード",
        testLinkHint = "Apple Mapsリンクをここに入力...",
        btnPasteClipboard = "貼り付け",
        btnTestLink = "Google Mapsでテスト",
        testLinkConvertedLabel = "変換されたターゲットURI:"
    )

    val NL = EN.copy(
        tagline = "Automatisch. Onzichtbaar.",
        setupTitle = "Hoe het werkt",
        step1 = "Tik op de knop hieronder",
        step2 = "Schakel \"Links openen\" in voor maps.apple.com",
        step3 = "Klaar! Apple Maps-links openen vanaf nu automatisch in Google Maps.",
        btnSettings = "Instellingen openen",
        statusActive = "Links zijn ingeschakeld",
        statusInactive = "Links zijn nog niet actief",
        statusHint = "Status kan niet worden gecontroleerd op oudere Android-versies.",
        langToggle = "NL",
        btnFeedback = "Support",
        pauseTitle = "Omleiding pauzeren",
        pauseDesc = "Tijdelijk alle link-omleidingen onderbreken",
        statusPaused = "Omleiding is gepauzeerd",
        selectLanguageTitle = "Taal selecteren",
        systemLanguageAuto = "Systeemstandaard (Automatisk)",
        btnRateApp = "Beoordeel app",
        privacyNote = "100% privacyvriendelijk: alle omleidingen vinden lokaal plaats. Geen gegevensverzameling.",
        testLinkTitle = "Link-tester & klembord",
        testLinkHint = "Voer hier Apple Maps-link in...",
        btnPasteClipboard = "Plakken",
        btnTestLink = "Testen in Google Maps",
        testLinkConvertedLabel = "Geconverteerde doel-URI:"
    )

    val NO = EN.copy(
        tagline = "Automatisk. Usynlig.",
        setupTitle = "Slik fungerer det",
        step1 = "Trykk på knappen nedenfor",
        step2 = "Aktiver \"Åpne lenker\" for maps.apple.com",
        step3 = "Ferdig! Apple Maps-lenker åpnes nå automatisk i Google Maps.",
        btnSettings = "Åpne innstillinger",
        statusActive = "Lenker er aktivert",
        statusInactive = "Lenker er ikke aktive ennå",
        statusHint = "Status kan ikke sjekkes på eldre Android-versjoner.",
        langToggle = "NO",
        btnFeedback = "Støtte",
        pauseTitle = "Sett viderekobling på pause",
        pauseDesc = "Avbryt midlertidig alle lenke-viderekoblinger",
        statusPaused = "Viderekobling er satt på pause",
        selectLanguageTitle = "Velg språk",
        systemLanguageAuto = "Systemstandard (Automatisk)",
        btnRateApp = "Vurder appen",
        privacyNote = "100% personvernvennlig: Alle viderekoblinger skjer lokalt. Ingen datainnsamling.",
        testLinkTitle = "Lenketester & utklippstavle",
        testLinkHint = "Skriv inn Apple Maps-lenke her...",
        btnPasteClipboard = "Lim inn",
        btnTestLink = "Test i Google Maps",
        testLinkConvertedLabel = "Konvertert mål-URI:"
    )

    val PL = EN.copy(
        tagline = "Automatycznie. Niewidocznie.",
        setupTitle = "Jak to działa",
        step1 = "Dotknij przycisku poniżej",
        step2 = "Włącz \"Otwieraj linki\" dla maps.apple.com",
        step3 = "Gotowe! Linki Apple Maps będą teraz automatycznie otwierać się w Google Maps.",
        btnSettings = "Otwórz ustawienia",
        statusActive = "Linki są włączone",
        statusInactive = "Linki nie są jeszcze aktywne",
        statusHint = "Nie można sprawdzić stanu na starszych wersjach systemu Android.",
        langToggle = "PL",
        btnFeedback = "Pomoc",
        pauseTitle = "Wstrzymaj przekierowanie",
        pauseDesc = "Tymczasowo zawieś wszystkie przekierowania",
        statusPaused = "Przekierowanie jest wstrzymane",
        selectLanguageTitle = "Wybierz język",
        systemLanguageAuto = "Domyślny systemu (Auto)",
        btnRateApp = "Oceń aplikację",
        privacyNote = "100% prywatności: wszystkie przekierowania odbywają się lokalnie. Brak zbierania danych.",
        testLinkTitle = "Tester linków i schowek",
        testLinkHint = "Wpisz tutaj link Apple Maps...",
        btnPasteClipboard = "Wklej",
        btnTestLink = "Testuj w Google Maps",
        testLinkConvertedLabel = "Przekonwertowany URI docelowy:"
    )

    val PT = EN.copy(
        tagline = "Automático. Invisível.",
        setupTitle = "Como funciona",
        step1 = "Toque no botão abaixo",
        step2 = "Ative \"Abrir links\" para maps.apple.com",
        step3 = "Concluído! Os links do Apple Maps serão abertos automaticamente no Google Maps.",
        btnSettings = "Abrir Configurações",
        statusActive = "Links estão ativados",
        statusInactive = "Links ainda não estão ativos",
        statusHint = "O status não pode ser verificado em versões anteriores do Android.",
        langToggle = "PT",
        btnFeedback = "Suporte",
        pauseTitle = "Pausar redirecionamento",
        pauseDesc = "Suspender temporariamente todos os redirecionamentos",
        statusPaused = "O redirecionamento está pausado",
        selectLanguageTitle = "Selecionar idioma",
        systemLanguageAuto = "Padrão do sistema (Auto)",
        btnRateApp = "Avaliar app",
        privacyNote = "100% privacidade: todos os redirecionamentos ocorrem localmente. Sem coleta de dados.",
        testLinkTitle = "Testador de links e área de transferência",
        testLinkHint = "Digite o link do Apple Maps aqui...",
        btnPasteClipboard = "Colar",
        btnTestLink = "Testar no Google Maps",
        testLinkConvertedLabel = "URI de destino convertido:"
    )

    val SV = EN.copy(
        tagline = "Automatiskt. Osynligt.",
        setupTitle = "Hur det fungerar",
        step1 = "Tryck på knappen nedan",
        step2 = "Aktivera \"Öppna länkar\" för maps.apple.com",
        step3 = "Klart! Apple Maps-länkar öppnas nu automatiskt i Google Maps.",
        btnSettings = "Öppna inställningar",
        statusActive = "Länkar är aktiverade",
        statusInactive = "Länkar är inte aktiva än",
        statusHint = "Status kan inte kontrolleras på äldre Android-versioner.",
        langToggle = "SV",
        btnFeedback = "Support",
        pauseTitle = "Pausa omdirigering",
        pauseDesc = "Avbryt tillfälligt alla länkomdirigeringar",
        statusPaused = "Omdirigering är pausad",
        selectLanguageTitle = "Välj språk",
        systemLanguageAuto = "Systemstandard (Automatisk)",
        btnRateApp = "Betygsätt appen",
        privacyNote = "100% integritetsvänlig: alla omdirigeringar sker lokalt. Ingen datainsamling.",
        testLinkTitle = "Länktester & urklipp",
        testLinkHint = "Ange Apple Maps-länk här...",
        btnPasteClipboard = "Klistra in",
        btnTestLink = "Testa i Google Maps",
        testLinkConvertedLabel = "Konverterad mål-URI:"
    )

    val ES = EN.copy(
        tagline = "Automático. Invisible.",
        setupTitle = "Cómo funciona",
        step1 = "Toca el botón de abajo",
        step2 = "Activa \"Abrir enlaces\" para maps.apple.com",
        step3 = "¡Listo! Los enlaces de Apple Maps se abrirán automáticamente en Google Maps.",
        btnSettings = "Abrir Ajustes",
        statusActive = "Los enlaces están activados",
        statusInactive = "Los enlaces aún no están activos",
        statusHint = "El estado no se puede verificar en versiones anteriores de Android.",
        langToggle = "ES",
        btnFeedback = "Soporte",
        pauseTitle = "Pausar redirección",
        pauseDesc = "Suspender temporalmente todas las redirecciones de enlaces",
        statusPaused = "La redirección está pausada",
        selectLanguageTitle = "Seleccionar idioma",
        systemLanguageAuto = "Predeterminado del sistema (Auto)",
        btnRateApp = "Calificar app",
        privacyNote = "100% privado: todas las redirecciones se procesan localmente. Sin recopilación de datos.",
        testLinkTitle = "Probador de enlaces y portapapeles",
        testLinkHint = "Introduce aquí el enlace de Apple Maps...",
        btnPasteClipboard = "Pegar",
        btnTestLink = "Probar en Google Maps",
        testLinkConvertedLabel = "URI de destino convertida:"
    )

    val TR = EN.copy(
        tagline = "Otomatik. Görünmez.",
        setupTitle = "Nasıl çalışır",
        step1 = "Aşağıdaki düğmeye dokunun",
        step2 = "maps.apple.com için \"Bağlantıları aç\"ı etkinleştirin",
        step3 = "Bitti! Apple Maps bağlantıları artık Google Maps'te otomatik açılacak.",
        btnSettings = "Ayarları Aç",
        statusActive = "Bağlantılar etkinleştirildi",
        statusInactive = "Bağlantılar henüz etkin değil",
        statusHint = "Eski Android sürümlerinde durum kontrol edilemez.",
        langToggle = "TR",
        btnFeedback = "Destek",
        pauseTitle = "Yönlendirmeyi Duraklat",
        pauseDesc = "Tüm bağlantı yönlendirmelerini geçici olarak durdurun",
        statusPaused = "Yönlendirme duraklatıldı",
        selectLanguageTitle = "Dil Seçin",
        systemLanguageAuto = "Sistem Varsayılanı (Otomatik)",
        btnRateApp = "Uygulamayı değerlendir",
        privacyNote = "100% gizlilik dostu: Tüm yönlendirmeler yerel olarak yapılır. Veri toplama yoktur.",
        testLinkTitle = "Bağlantı Test Edici ve Pano",
        testLinkHint = "Buraya Apple Maps bağlantısını girin...",
        btnPasteClipboard = "Yapıştır",
        btnTestLink = "Google Maps'te Test Et",
        testLinkConvertedLabel = "Dönüştürülen Hedef URI:"
    )
}
