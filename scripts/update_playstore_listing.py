import os
import json
import sys

LISTINGS = {
    'en-US': {
        'title': 'MapFlip - Apple to Google',
        'shortDescription': 'Open Apple Maps links & URLs in Google Maps. Fast Apple Maps converter.',
        'fullDescription': """Your friends send you Apple Maps links – but you use Google Maps?

MapFlip is the ultimate Apple Maps converter and link redirect tool for Android. Set it up once, and everything works automatically as a seamless Google Maps redirect: Every Apple Maps link, URL, or location opens directly in Google Maps. No copying, no pasting, no browser detours.

Whether you need an Apple Maps for Android solution or an automatic Apple to Google Maps converter, MapFlip handles all Apple Maps URLs in the background.

🔧 How it works:
1. Open MapFlip
2. Tap "Open Settings"
3. Enable link forwarding for maps.apple.com
4. Done! Apple Maps links now open directly in Google Maps.

✨ Features & Privacy:
• 🚗 Turn-by-Turn Navigation & Android Auto – Instantly launches native routing in Google Maps
• 🔄 Automatic Apple to Google redirect – Instant on-device link converter
• 🔒 100% Offline & Private – Zero internet permissions (no android.permission.INTERNET), zero tracking, zero ads
• ⏸️ Pause Mode & Quick Settings Tile – Suspend redirect anytime directly from your notification shade
• 💬 Universal App Compatibility – Works seamlessly with WhatsApp, Telegram, Signal, SMS, Slack, and email
• 👻 Invisible Background Operation – No extra screens or battery drain

🗺️ Supported link formats:
• Apple Maps URLs & search queries (e.g. "Eiffel Tower")
• GPS coordinates & shared locations
• Addresses & place markers
• Navigation directions

MapFlip was built for Android users who regularly receive Apple Maps links from iPhone users."""
    },
    'de-DE': {
        'title': 'MapFlip - Apple zu Google',
        'shortDescription': 'Apple Maps Links & URLs in Google Maps öffnen. Schneller Karten-Konverter.',
        'fullDescription': """Deine Freunde schicken dir Apple Maps Links – aber du nutzt Google Maps auf Android?

MapFlip ist der zuverlässige Apple Maps Konverter und die smarte Kartenweiterleitung für Android. Einmal einrichten, danach läuft alles automatisch als Google Maps Weiterleitung: Jeder Apple Maps Link und jede Apple Karten URL öffnet sich direkt in Google Maps. Kein Kopieren, kein Einfügen, kein Umweg über den Browser.

Egal ob Apple Maps zu Google Maps, Apple Karten öffnen oder Apple Maps für Android: MapFlip leitet alle Karten-Links blitzschnell um.

🔧 So funktioniert's:
1. Öffne MapFlip
2. Tippe auf „Einstellungen öffnen“
3. Aktiviere die Link-Weiterleitung für maps.apple.com
4. Fertig! Ab jetzt werden Apple Maps Links automatisch umgeleitet.

✨ Features & Datenschutz:
• 🚗 Turn-by-Turn Navigation & Android Auto – Startet sofort die native Routenführung in Google Maps
• 🔄 Automatische Kartenweiterleitung – Apple Maps Link-Konverter ohne manuelles Kopieren
• 🔒 100% Offline & Datenschutz – Keine Internet-Berechtigung (0 Permissions), kein Tracking, keine Werbung
• ⏸️ Pausen-Modus & Schnelleinstellungs-Kachel – Umleitung jederzeit im Kontrollzentrum pausieren
• 💬 Universelle App-Kompatibilität – Funktioniert mit WhatsApp, Telegram, Signal, SMS, Slack und E-Mails
• 👻 Unsichtbar im Hintergrund – Keine störenden Zusatz-Bildschirme, null Akkuverbrauch

🗺️ Unterstützte Link-Formate:
• Apple Maps URLs & Suchanfragen (z. B. „Eiffelturm“)
• GPS-Koordinaten & geteilte Standorte
• Adressen & Ortsmarkierungen
• Navigationsanweisungen

MapFlip wurde für Android-Nutzer entwickelt, die regelmäßig Apple Maps Links oder Apple Karten von iPhone-Nutzern erhalten."""
    },
    'da-DK': {
        'title': 'MapFlip - Apple til Google',
        'shortDescription': 'Åbn Apple Maps-links automatisk i Google Maps. Med pause-knap.',
        'fullDescription': """Dine venner sender dig Apple Maps-links – men du bruger Google Maps?

MapFlip er den ultimative Apple Maps-løsning og link-konverter til Android. Sæt det op én gang, og alt sker automatisk som en omdirigering til Google Maps: Hvert Apple Maps-link åbnes direkte i Google Maps. Ingen kopiering, ingen indsættelse, ingen omveje via browseren.

🔧 Sådan fungerer det:
1. Åbn MapFlip
2. Tryk på "Åbn indstillinger"
3. Aktiver link-omdirigering for maps.apple.com
4. Færdig! Apple Maps-links omdirigeres nu automatisk.

✨ Funktioner & Privatliv:
• 🚗 Turn-by-turn navigation & Android Auto – Starter øjeblikkeligt rutevejledning i Google Maps
• 🔄 Automatisk omdirigering – Apple Maps til Google Maps link-konverter
• 🔒 100% offline & privat – Nul internettilladelser, ingen sporing, ingen reklamer
• ⏸️ Pause-tilstand & Hurtigindstillinger-knap – Sæt omdirigering på pause i meddelelsespanelet
• 💬 Universel app-kompatibilitet – Fungerer med WhatsApp, Telegram, Signal, SMS og e-mail
• 👻 Usynlig baggrundsdrift – Ingen ekstra skærme, intet batteriforbrug

🗺️ Understøttede linkformater:
• Søgninger (f.eks. "Eiffeltårnet")
• GPS-koordinater & delte placeringer
• Adresser & stedmarkører
• Navigationsanvisninger

MapFlip er bygget til Android-brugere, der regelmæssigt modtager Apple Maps-links fra iPhone-brugere."""
    },
    'fr-FR': {
        'title': 'MapFlip - Apple à Google',
        'shortDescription': 'Ouvrir les liens Apple Maps dans Google Maps. Avec mode pause.',
        'fullDescription': """Vos amis vous envoient des liens Apple Maps – mais vous utilisez Google Maps ?

MapFlip est la solution Apple Maps ultime et le convertisseur de liens pour Android. Configurez-le une fois, et tout fonctionne automatiquement comme une redirection Google Maps : chaque lien Apple Maps s'ouvre directement dans Google Maps. Pas de copier-coller, pas de détour par le navigateur.

🔧 Comment ça marche :
1. Ouvrez MapFlip
2. Appuyez sur "Ouvrir les paramètres"
3. Activez la redirection des liens pour maps.apple.com
4. Terminé ! Les liens Apple Maps sont désormais redirigés automatiquement.

✨ Fonctionnalités & Confidentialité :
• 🚗 Navigation étape par étape & Android Auto – Lance instantanément l'itinéraire dans Google Maps
• 🔄 Redirection automatique – Convertisseur de liens Apple Maps vers Google Maps
• 🔒 100% hors ligne & privé – Aucune permission Internet, aucun suivi, aucune publicité
• ⏸️ Mode pause & Tuile Paramètres rapides – Interrompez la redirection depuis le panneau de notification
• 💬 Compatibilité universelle – Fonctionne avec WhatsApp, Telegram, Signal, SMS et e-mails
• 👻 Fonctionnement invisible en arrière-plan – Aucun écran supplémentaire, aucune consommation de batterie

🗺️ Formats de liens pris en charge :
• Requêtes de recherche (ex. "Tour Eiffel")
• Coordonnées GPS & lieux partagés
• Adresses & repères de lieux
• Instructions de navigation

MapFlip a été conçu pour les utilisateurs d'Android qui reçoivent régulièrement des liens Apple Maps de la part d'utilisateurs d'iPhone."""
    },
    'it-IT': {
        'title': 'MapFlip - Apple a Google',
        'shortDescription': 'Apri i link Apple Maps in Google Maps. Con tessera di pausa.',
        'fullDescription': """I tuoi amici ti inviano link di Apple Maps – ma tu usi Google Maps?

MapFlip è la soluzione Apple Maps definitiva e il convertitore di link per Android. Configuralo una volta e tutto funzionerà automaticamente come un reindirizzamento a Google Maps: ogni link di Apple Maps si aprirà direttamente in Google Maps. Nessun copia-incolla, nessun passaggio dal browser.

🔧 Come funziona:
1. Apri MapFlip
2. Tocca "Apri impostazioni"
3. Abilita il reindirizzamento dei link per maps.apple.com
4. Fatto! I link di Apple Maps verranno reindirizzati automaticamente.

✨ Caratteristiche & Privacy:
• 🚗 Navigazione passo-passo & Android Auto – Avvia istantaneamente l'itinerario in Google Maps
• 🔄 Reindirizzamento automatico – Convertitore di link da Apple Maps a Google Maps
• 🔒 100% offline e privato – Zero permessi Internet, nessun tracciamento, nessuna pubblicità
• ⏸️ Modalità pausa e tessera Impostazioni rapide – Sospendi il reindirizzamento dal pannello notifiche
• 💬 Compatibilità universale – Funziona con WhatsApp, Telegram, Signal, SMS ed e-mail
• 👻 Funzionamento invisibile in background – Nessuna schermata aggiuntiva, zero consumo di batteria

🗺️ Formati di link supportati:
• Ricerche (es. "Torre Eiffel")
• Coordinate GPS & posizioni condivise
• Indirizzi & segnaposto di luoghi
• Indicazioni stradali

MapFlip è stato creato per gli utenti Android che ricevono regolarmente link di Apple Maps da utenti iPhone."""
    },
    'ja-JP': {
        'title': 'MapFlip – Apple Maps変換',
        'shortDescription': 'Apple MapsのリンクをGoogle Mapsで自動的に開きます。',
        'fullDescription': """友達からApple Mapsのリンクが送られてくるけれど、普段使っているのはGoogle Mapsですか？

MapFlipは、Android向けの究極のApple Mapsソリューションおよびリンクコンバーターです。一度設定するだけで、Google Mapsへの転送としてすべてが自動的に機能します。Apple Mapsのリンクをタップするだけで、直接Google Mapsで開きます。コピー＆ペーストやブラウザ経由の手間は一切不要です。

🔧 使い方：
1. MapFlipを開く
2. 「設定を開く」をタップ
3. maps.apple.com のリンク転送を有効化
4. 完了！これ以降、Apple Mapsのリンクは自動的に転送されます。

✨ 特徴＆プライバシー：
• 🚗 音声ナビ＆Android Auto対応 – Google Mapsで即座にルート案内を開始
• 🔄 完全自動転送 – Apple MapsリンクをGoogle Mapsへ自動変換
• 🔒 100%オフライン＆プライバシー保護 – インターネット権限なし、追跡なし、広告なし
• ⏸️ 一時停止モード＆クイック設定タイル – 通知シェードからいつでも転送を一時停止可能
• 💬 主要アプリに対応 – LINE、WhatsApp、Telegram、SMS、メールなど
• 👻 バックグラウンドで透明動作 – 余計な画面なし、バッテリー消費ゼロ

🗺️ 対応リンクフォーマット：
• 検索クエリ（例：「東京タワー」）
• GPS座標＆共有された位置情報
• 住所＆スポットピン
• ルート案内

MapFlipは、iPhoneユーザーからApple Mapsのリンクをよく受け取るAndroidユーザーのために開発されました。"""
    },
    'nl-NL': {
        'title': 'MapFlip - Apple naar Google',
        'shortDescription': 'Open Apple Maps-links automatisch in Google Maps. Met pauze-tegel.',
        'fullDescription': """Je vrienden sturen je Apple Maps-links – maar jij gebruikt Google Maps?

MapFlip is de ultieme Apple Maps-oplossing en link-converter voor Android. Stel het één keer in en alles werkt automatisch als een Google Maps-omleiding: elke Apple Maps-link opent direct in Google Maps. Geen kopiëren, geen plakken, geen omwegen via de browser.

🔧 Hoe het werkt:
1. Open MapFlip
2. Tik op "Instellingen openen"
3. Schakel link-omleiding in voor maps.apple.com
4. Klaar! Apple Maps-links worden nu automatisch omgeleid.

✨ Functies & Privacy:
• 🚗 Turn-by-turn navigatie & Android Auto – Start direct routebeschrijving in Google Maps
• 🔄 Automatische omleiding – Apple Maps naar Google Maps link-converter
• 🔒 100% offline & privacyvriendelijk – Geen internetrechten, geen tracking, geen advertenties
• ⏸️ Pauzemodus & Snelle instellingen-tegel – Omleiding pauzeren vanuit de meldingenbalk
• 💬 Universele app-compatibiliteit – Werkt met WhatsApp, Telegram, Signal, sms en e-mail
• 👻 Onzichtbare achtergrondwerking – Geen extra schermen, geen batterijverbruik

🗺️ Ondersteunde linkformaten:
• Zoekopdrachten (bijv. "Eiffeltoren")
• GPS-coördinaten & gedeelde locaties
• Adressen & locatiemarkeringen
• Navigatie-aanwijzingen

MapFlip is gebouwd voor Android-gebruikers die regelmatig Apple Maps-links ontvangen van iPhone-gebruikers."""
    },
    'no-NO': {
        'title': 'MapFlip - Apple til Google',
        'shortDescription': 'Åpne Apple Maps-lenker automatisk i Google Maps. Med pause-knapp.',
        'fullDescription': """Vennene dine sender deg Apple Maps-lenker – men du bruker Google Maps?

MapFlip er den ultimate Apple Maps-løsningen og lenkekonverteren for Android. Sett det opp én gang, og alt fungerer automatisk som en viderekobling til Google Maps: Hver Apple Maps-lenke åpnes direkte i Google Maps. Ingen kopiering, ingen liming, ingen omveier via nettleseren.

🔧 Slik fungerer det:
1. Åpne MapFlip
2. Trykk på "Åpne innstillinger"
3. Aktiver lenke-viderekobling for maps.apple.com
4. Ferdig! Apple Maps-lenker viderekobles nå automatisk.

✨ Funksjoner & Personvern:
• 🚗 Turn-by-turn navigasjon & Android Auto – Starter umiddelbart ruteveiledning i Google Maps
• 🔄 Automatisk viderekobling – Apple Maps til Google Maps lenkekonverter
• 🔒 100% frakoblet & privat – Null internetttillatelser, ingen sporing, ingen reklame
• ⏸️ Pausemodus & Hurtiginnstillinger-knapp – Sett viderekobling på pause i varslingspanelet
• 💬 Universell app-kompatibilitet – Fungerer med WhatsApp, Telegram, Signal, SMS og e-post
• 👻 Usynlig bakgrunnsdrift – Ingen ekstra skjermer, null batteribruk

🗺️ Støttede lenkeformater:
• Søk (f.eks. "Eiffeltårnet")
• GPS-koordinater & delte posisjoner
• Adresser & stedmarkører
• Navigasjonsinstruksjoner

MapFlip er bygget for Android-brukere som regelmessig mottar Apple Maps-lenker fra iPhone-brukere."""
    },
    'pl-PL': {
        'title': 'MapFlip - Apple na Google',
        'shortDescription': 'Otwieraj linki Apple Maps w Google Maps. Z kafelkiem pauzy.',
        'fullDescription': """Znajomi przysyłają Ci linki z Apple Maps – ale Ty używasz Google Maps?

MapFlip to najlepsze rozwiązanie dla Apple Maps i konwerter linków dla Androida. Skonfiguruj raz, a wszystko zadziała automatycznie jako przekierowanie do Google Maps: każdy link Apple Maps otworzy się bezpośrednio w Google Maps. Bez kopiowania, bez wklejania, bez przechodzenia przez przeglądarkę.

🔧 Jak to działa:
1. Otwórz MapFlip
2. Stuknij "Otwórz ustawienia"
3. Włącz przekierowanie linków dla maps.apple.com
4. Gotowe! Linki Apple Maps będą teraz przekierowywane automatycznie.

✨ Funkcje & Prywatność:
• 🚗 Nawigacja zakręt po zakręcie & Android Auto – Błyskawicznie uruchamia trasę w Google Maps
• 🔄 Automatyczne przekierowanie – Konwerter linków Apple Maps na Google Maps
• 🔒 100% offline i prywatność – Zero uprawnień internetowych, zero śledzenia, brak reklam
• ⏸️ Tryb pauzy i kafelek szybkich ustawień – Wstrzymuj przekierowanie z panelu powiadomień
• 💬 Uniwersalna kompatybilność – Działa z WhatsApp, Telegram, Signal, SMS i e-mail
• 👻 Niewidoczna praca w tle – Brak zbędnych ekranów, zerowe zużycie baterii

🗺️ Obsługiwane formaty linków:
• Wyszukiwania (np. "Wieża Eiffla")
• Współrzędne GPS & udostępnione lokalizacje
• Adresy & znaczniki miejsc
• Wskazówki nawigacyjne

MapFlip powstał z myślą o użytkownikach Androida, którzy regularnie otrzymują linki Apple Maps od użytkowników iPhone'ów."""
    },
    'pt-BR': {
        'title': 'MapFlip - Apple para Google',
        'shortDescription': 'Abra links do Apple Maps diretamente no Google Maps.',
        'fullDescription': """Seus amigos enviam links do Apple Maps – mas você usa o Google Maps?

O MapFlip é a solução definitiva para o Apple Maps e o conversor de links para Android. Configure uma vez e tudo funcionará automaticamente como um redirecionamento para o Google Maps: cada link do Apple Maps abre diretamente no Google Maps. Sem copiar, sem colar, sem atalhos pelo navegador.

🔧 Como funciona:
1. Abra o MapFlip
2. Toque em "Abrir configurações"
3. Ative o redirecionamento de links para maps.apple.com
4. Pronto! Os links do Apple Maps agora são redirecionados automaticamente.

✨ Recursos & Privacidade:
• 🚗 Navegação curva a curva & Android Auto – Inicia instantaneamente a rota no Google Maps
• 🔄 Redirecionamento automático – Conversor de links Apple Maps para Google Maps
• 🔒 100% offline e seguro – Zero permissões de internet, sem rastreamento, sem anúncios
• ⏸️ Modo pausa e atalho de Configurações Rápidas – Pause o redirecionamento no painel de notificações
• 💬 Compatibilidade universal – Funciona com WhatsApp, Telegram, Signal, SMS e e-mail
• 👻 Operação invisível em segundo plano – Sem telas extras, sem consumo de bateria

🗺️ Formatos de links suportados:
• Buscas (ex: "Torre Eiffel")
• Coordenadas GPS & localizações compartilhadas
• Endereços & marcadores de locais
• Instruções de navegação

O MapFlip foi feito para usuários de Android que recebem frequentemente links do Apple Maps de usuários de iPhone."""
    },
    'pt-PT': {
        'title': 'MapFlip - Apple para Google',
        'shortDescription': 'Abra links do Apple Maps diretamente no Google Maps.',
        'fullDescription': """Os seus amigos enviam-lhe links do Apple Maps – mas você usa o Google Maps?

O MapFlip é a solução definitiva para o Apple Maps e o conversor de links para Android. Configure uma vez e tudo funcionará automaticamente como um redirecionamento para o Google Maps: cada link do Apple Maps abre diretamente no Google Maps. Sem copiar, sem colar, sem atalhos pelo navegador.

🔧 Como funciona:
1. Abra o MapFlip
2. Toque em "Abrir definições"
3. Ative o redirecionamento de links para maps.apple.com
4. Concluído! Os links do Apple Maps são agora redirecionados automaticamente.

✨ Funcionalidades & Privacidade:
• 🚗 Navegação passo a passo & Android Auto – Inicia instantaneamente o trajeto no Google Maps
• 🔄 Redirecionamento automático – Conversor de links Apple Maps para Google Maps
• 🔒 100% offline e privado – Zero permissões de internet, sem rastreio, sem anúncios
• ⏸️ Modo pausa e atalho de Definições Rápidas – Pause o redirecionamento no painel de notificações
• 💬 Compatibilidade universal – Funciona com WhatsApp, Telegram, Signal, SMS e e-mail
• 👻 Funcionamento invisível em segundo plano – Sem ecrãs adicionais, zero consumo de bateria

🗺️ Formatos de links suportados:
• Pesquisas (ex: "Torre Eiffel")
• Coordenadas GPS & localizações partilhadas
• Endereços & marcadores de locais
• Instruções de navegação

O MapFlip foi desenvolvido para utilizadores de Android que recebem frequentemente links do Apple Maps de utilizadores de iPhone."""
    },
    'sv-SE': {
        'title': 'MapFlip - Apple till Google',
        'shortDescription': 'Öppna Apple Maps-länkar i Google Maps. Med paus-knapp.',
        'fullDescription': """Dina vänner skickar Apple Maps-länkar – men du använder Google Maps?

MapFlip är den ultimata Apple Maps-lösningen och länkkonverteraren för Android. Ställ in det en gång så fungerar allt automatiskt som en omdirigering till Google Maps: Varje Apple Maps-länk öppnas direkt i Google Maps. Ingen kopiering, ingen klistra in, inga omvägar via webbläsaren.

🔧 Så fungerar det:
1. Öppna MapFlip
2. Tryck på "Öppna inställningar"
3. Aktivera länk-omdirigering för maps.apple.com
4. Klart! Apple Maps-länkar omdirigeras nu automatiskt.

✨ Funktioner & Integritet:
• 🚗 Sväng-för-sväng-navigering & Android Auto – Startar omedelbart rutt i Google Maps
• 🔄 Automatisk omdirigering – Apple Maps till Google Maps länkkonverterare
• 🔒 100% offline & integritetsvänlig – Noll internetbehörigheter, ingen spårning, inga annonser
• ⏸️ Pausläge & Snabbinställnings-knapp – Pausa omdirigeringen från aviseringspanelen
• 💬 Universell app-kompatibilitet – Fungerar med WhatsApp, Telegram, Signal, SMS och e-post
• 👻 Osynlig bakgrundsdrift – Inga extra skärmar, noll batteriförbrukning

🗺️ Länkformat som stöds:
• Sökningar (t.ex. "Eiffeltornet")
• GPS-koordinater & delade platser
• Adresser & platsmarkörer
• Navigeringsanvisningar

MapFlip är skapat för Android-användare som regelbundet tar emot Apple Maps-länkar från iPhone-användare."""
    },
    'es-ES': {
        'title': 'MapFlip - Apple a Google',
        'shortDescription': 'Abre enlaces de Apple Maps directamente en Google Maps.',
        'fullDescription': """¿Tus amigos te envían enlaces de Apple Maps, pero tú usas Google Maps?

MapFlip es la solución definitiva de Apple Maps y el convertidor de enlaces para Android. Configúralo una vez y todo funcionará automáticamente como una redirección a Google Maps: cada enlace de Apple Maps se abre directamente en Google Maps. Sin copiar, sin pegar, sin desvíos en el navegador.

🔧 Cómo funciona:
1. Abre MapFlip
2. Toca "Abrir ajustes"
3. Activa la redirección de enlaces para maps.apple.com
4. ¡Listo! Los enlaces de Apple Maps ahora se redirigen automáticamente.

✨ Características & Privacidad:
• 🚗 Navegación paso a paso & Android Auto – Inicia al instante la ruta en Google Maps
• 🔄 Redirección automática – Convertidor de enlaces Apple Maps a Google Maps
• 🔒 100% offline y privado – Cero permisos de internet, sin rastreo, sin anuncios
• ⏸️ Modo pausa y atajo de Ajustes Rápidos – Pausa la redirección desde el panel de notificaciones
• 💬 Compatibilidad universal – Funciona con WhatsApp, Telegram, Signal, SMS y correo electrónico
• 👻 Funcionamiento invisible en segundo plano – Sin pantallas adicionales, sin consumo de batería

🗺️ Formatos de enlace compatibles:
• Búsquedas (p. ej., "Torre Eiffel")
• Coordenadas GPS & ubicaciones compartidas
• Direcciones & marcadores de lugares
• Indicaciones de navegación

MapFlip fue creado para usuarios de Android que reciben con frecuencia enlaces de Apple Maps de usuarios de iPhone."""
    },
    'tr-TR': {
        'title': 'MapFlip - Apple\'dan Google',
        'shortDescription': 'Apple Maps bağlantılarını otomatik olarak Google Maps\'te açın.',
        'fullDescription': """Arkadaşlarınız size Apple Maps bağlantıları gönderiyor ama siz Google Maps mi kullanıyorsunuz?

MapFlip, Android için nihai Apple Maps çözümü ve bağlantı dönüştürücüsüdür. Bir kez kurun, Google Maps yönlendirmesi olarak her şey otomatik çalışsın: Her Apple Maps bağlantısı doğrudan Google Maps'te açılır. Kopyalama yok, yapıştırma yok, tarayıcıyla uğraşmak yok.

🔧 Nasıl çalışır:
1. MapFlip'i açın
2. "Ayarları Aç"a dokunun
3. maps.apple.com için bağlantı yönlendirmesini etkinleştirin
4. Tamam! Apple Maps bağlantıları artık otomatik olarak yönlendirilir.

✨ Özellikler & Gizlilik:
• 🚗 Adım adım navigasyon ve Android Auto – Google Maps'te anında rota başlatır
• 🔄 Otomatik yönlendirme – Apple Maps'ten Google Maps'e bağlantı dönüştürücü
• 🔒 %100 çevrimdışı ve gizli – Sıfır internet izni, takip yok, reklam yok
• ⏸️ Duraklatma modu ve Hızlı Ayarlar kutusu – Yönlendirmeyi bildirim panelinden duraklatın
• 💬 Evrensel uygulama uyumluluğu – WhatsApp, Telegram, Signal, SMS ve e-posta ile çalışır
• 👻 Arka planda görünmez çalışma – Ekstra ekran yok, sıfır pil tüketimi

🗺️ Desteklenen bağlantı formatları:
• Arama sorguları (ör. "Eyfel Kulesi")
• GPS koordinatları & paylaşılan konumlar
• Adresler & yer işaretleri
• Navigasyon tarifleri

MapFlip, iPhone kullanıcılarından düzenli olarak Apple Maps bağlantısı alan Android kullanıcıları için tasarlanmıştır."""
    }
}

def main():
    package_name = 'de.goork.mapflip'
    service_account_env = os.environ.get('SERVICE_ACCOUNT_JSON')
    
    if not service_account_env:
        print("Error: SERVICE_ACCOUNT_JSON environment variable is not set.")
        sys.exit(1)
        
    try:
        from google.oauth2 import service_account
        from googleapiclient.discovery import build
    except ImportError:
        print("Installing required google-api-python-client and google-auth...")
        import subprocess
        subprocess.check_call([sys.executable, "-m", "pip", "install", "google-api-python-client", "google-auth"])
        from google.oauth2 import service_account
        from googleapiclient.discovery import build

    try:
        service_account_info = json.loads(service_account_env)
        credentials = service_account.Credentials.from_service_account_info(
            service_account_info,
            scopes=['https://www.googleapis.com/auth/androidpublisher']
        )

        service = build('androidpublisher', 'v3', credentials=credentials)

        print(f"Creating new edit session for package: {package_name}...")
        edit = service.edits().insert(packageName=package_name, body={}).execute()
        edit_id = edit['id']
        print(f"Edit session created: {edit_id}")

        for locale, listing in LISTINGS.items():
            print(f"Updating Store Listing for locale: '{locale}'...")
            service.edits().listings().update(
                packageName=package_name,
                editId=edit_id,
                language=locale,
                body={
                    'language': locale,
                    'title': listing['title'],
                    'shortDescription': listing['shortDescription'],
                    'fullDescription': listing['fullDescription']
                }
            ).execute()
            print(f"✅ Store Listing updated for {locale}")

        print("Committing edit session to Google Play...")
        service.edits().commit(packageName=package_name, editId=edit_id).execute()
        print("🚀 All 14 Play Store listings updated successfully!")

    except Exception as e:
        print(f"❌ Error updating Play Store listings: {e}")
        sys.exit(1)

if __name__ == '__main__':
    main()
