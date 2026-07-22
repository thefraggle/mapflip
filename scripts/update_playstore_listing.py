import os
import json
import sys

LISTINGS = {
    'en-US': {
        'title': 'MapFlip – Apple to Google Maps',
        'shortDescription': 'Automatically open Apple Maps links directly in Google Maps.',
        'fullDescription': """Your friends send you Apple Maps links – but you use Google Maps?

MapFlip solves this problem. Set it up once, and everything happens automatically: Every Apple Maps link opens directly in Google Maps. No copying, no pasting, no browser detours.

🔧 How it works:
1. Open MapFlip
2. Tap "Open Settings"
3. Enable link forwarding for maps.apple.com
4. Done! Apple Maps links are now automatically redirected.

✨ Features:
• Automatic redirect – no manual copying needed
• Invisible background operation – no extra screens
• Supports search queries, coordinates, addresses, and navigation
• Privacy-friendly – no data collected or sent
• Completely free, no ads

🗺️ Supported link formats:
• Search queries (e.g. "Eiffel Tower")
• GPS coordinates
• Addresses
• Navigation directions
• Place markers

MapFlip was built for Android users who regularly receive Apple Maps links from iPhone users – whether via WhatsApp, email, SMS, or social media."""
    },
    'de-DE': {
        'title': 'MapFlip – Apple zu Google Maps',
        'shortDescription': 'Apple Maps Links automatisch in Google Maps öffnen.',
        'fullDescription': """Deine Freunde schicken dir Apple Maps Links – aber du nutzt Google Maps?

MapFlip löst dieses Problem. Einmal einrichten, danach passiert alles automatisch: Jeder Apple Maps Link öffnet sich direkt in Google Maps. Kein Kopieren, kein Einfügen, kein Umweg über den Browser.

🔧 So funktioniert's:
1. Öffne MapFlip
2. Tippe auf „Einstellungen öffnen"
3. Aktiviere die Link-Weiterleitung für maps.apple.com
4. Fertig! Ab jetzt werden Apple Maps Links automatisch umgeleitet.

✨ Features:
• Automatische Umleitung – kein manuelles Kopieren nötig
• Unsichtbar im Hintergrund – keine störenden Zusatz-Bildschirme
• Unterstützt Suchanfragen, Koordinaten, Adressen und Navigation
• Datenschutzfreundlich – keine Daten werden gesammelt oder gesendet
• Komplett kostenlos, keine Werbung

🗺️ Unterstützte Link-Formate:
• Suchanfragen (z.B. „Eiffelturm")
• GPS-Koordinaten
• Adressen
• Navigationsanweisungen
• Ortsmarkierungen

MapFlip wurde für Android-Nutzer entwickelt, die regelmäßig Apple Maps Links von iPhone-Nutzern erhalten – ob per WhatsApp, E-Mail, SMS oder Social Media."""
    },
    'da-DK': {
        'title': 'MapFlip – Apple til Google Maps',
        'shortDescription': 'Åbn Apple Maps-links automatisk i Google Maps.',
        'fullDescription': """Dine venner sender dig Apple Maps-links – men du bruger Google Maps?

MapFlip løser dette problem. Sæt det op én gang, og alt sker automatisk: Hvert Apple Maps-link åbnes direkte i Google Maps. Ingen kopiering, ingen indsættelse, ingen omveje via browseren.

🔧 Sådan fungerer det:
1. Åbn MapFlip
2. Tryk på "Åbn indstillinger"
3. Aktiver link-omdirigering for maps.apple.com
4. Færdig! Apple Maps-links omdirigeres nu automatisk.

✨ Funktioner:
• Automatisk omdirigering – ingen manuel kopiering
• Usynlig baggrundsdrift – ingen ekstra skærme
• Understøtter søgninger, koordinater, adresser og navigation
• Privatlivsvenlig – ingen data indsamles eller sendes
• Helt gratis, ingen reklamer

🗺️ Understøttede linkformater:
• Søgninger (f.eks. "Eiffeltårnet")
• GPS-koordinater
• Adresser
• Navigationsanvisninger
• Stedmarkører

MapFlip er bygget til Android-brugere, der regelmæssigt modtager Apple Maps-links fra iPhone-brugere – uanset om det er via WhatsApp, e-mail, SMS eller sociale medier."""
    },
    'fr-FR': {
        'title': 'MapFlip - Apple à Google Maps',
        'shortDescription': 'Ouvrez automatiquement les liens Apple Maps dans Google Maps.',
        'fullDescription': """Vos amis vous envoient des liens Apple Maps – mais vous utilisez Google Maps ?

MapFlip résout ce problème. Configurez-le une fois, et tout se fait automatiquement : chaque lien Apple Maps s'ouvre directement dans Google Maps. Pas de copier-coller, pas de détour par le navigateur.

🔧 Comment ça marche :
1. Ouvrez MapFlip
2. Appuyez sur "Ouvrir les paramètres"
3. Activez la redirection des liens pour maps.apple.com
4. Terminé ! Les liens Apple Maps sont désormais redirigés automatiquement.

✨ Fonctionnalités :
• Redirection automatique – pas besoin de copier manuellement
• Fonctionnement invisible en arrière-plan – pas d'écrans supplémentaires
• Prise en charge des recherches, coordonnées, adresses et navigation
• Respect de la vie privée – aucune donnée collectée ni envoyée
• Entièrement gratuit, sans publicité

🗺️ Formats de liens pris en charge :
• Requêtes de recherche (ex. "Tour Eiffel")
• Coordonnées GPS
• Adresses
• Instructions de navigation
• Repères de lieux

MapFlip a été conçu pour les utilisateurs d'Android qui reçoivent régulièrement des liens Apple Maps de la part d'utilisateurs d'iPhone – que ce soit par WhatsApp, e-mail, SMS ou réseaux sociaux."""
    },
    'it-IT': {
        'title': 'MapFlip: Apple a Google Maps',
        'shortDescription': 'Apri automaticamente i link di Apple Maps in Google Maps.',
        'fullDescription': """I tuoi amici ti inviano link di Apple Maps – ma tu usi Google Maps?

MapFlip risolve questo problema. Configuralo una volta e tutto avverrà automaticamente: ogni link di Apple Maps si aprirà direttamente in Google Maps. Nessun copia-incolla, nessun passaggio dal browser.

🔧 Come funziona:
1. Apri MapFlip
2. Tocca "Apri impostazioni"
3. Abilita il reindirizzamento dei link per maps.apple.com
4. Fatto! I link di Apple Maps verranno reindirizzati automaticamente.

✨ Caratteristiche:
• Reindirizzamento automatico – nessuna copia manuale
• Funzionamento invisibile in background – nessuna schermata aggiuntiva
• Supporta ricerche, coordinate, indirizzi e navigazione
• Rispetto della privacy – nessun dato raccolto o inviato
• Completamente gratuito, senza pubblicità

🗺️ Formati di link supportati:
• Ricerche (es. "Torre Eiffel")
• Coordinate GPS
• Indirizzi
• Indicazioni stradali
• Segnaposto di luoghi

MapFlip è stato creato per gli utenti Android che ricevono regolarmente link di Apple Maps da utenti iPhone – tramite WhatsApp, e-mail, SMS o social media."""
    },
    'ja-JP': {
        'title': 'MapFlip – Apple Maps変換',
        'shortDescription': 'Apple MapsのリンクをGoogle Mapsで自動的に開きます。',
        'fullDescription': """友達からApple Mapsのリンクが送られてくるけれど、普段使っているのはGoogle Mapsですか？

MapFlipがその問題を解決します。一度設定するだけで、すべての動作が自動化されます。Apple Mapsのリンクをタップするだけで、直接Google Mapsで開きます。コピー＆ペーストやブラウザ経由の手間は一切不要です。

🔧 使い方：
1. MapFlipを開く
2. 「設定を開く」をタップ
3. maps.apple.com のリンク転送を有効化
4. 完了！これ以降、Apple Mapsのリンクは自動的に転送されます。

✨ 特徴：
• 完全自動転送 – 手動でのコピー不要
• バックグラウンドで透明動作 – 余計な画面が出ません
• 検索ワード、GPS座標、住所、ルート案内をサポート
• プライバシー重視 – データの収集や送信は一切行いません
• 完全無料、広告なし

🗺️ 対応リンクフォーマット：
• 検索クエリ（例：「東京タワー」）
• GPS座標
• 住所
• ルート案内
• スポットピン

MapFlipは、iPhoneユーザーからApple Mapsのリンクをよく受け取るAndroidユーザーのために開発されました（LINE、WhatsApp、メール、SMS、SNSなど）。"""
    },
    'nl-NL': {
        'title': 'MapFlip: Apple naar Google Maps',
        'shortDescription': 'Open Apple Maps-links automatisch in Google Maps.',
        'fullDescription': """Je vrienden sturen je Apple Maps-links – maar jij gebruikt Google Maps?

MapFlip lost dit probleem op. Stel het één keer in en alles gebeurt automatisch: elke Apple Maps-link opent direct in Google Maps. Geen kopiëren, geen plakken, geen omwegen via de browser.

🔧 Hoe het werkt:
1. Open MapFlip
2. Tik op "Instellingen openen"
3. Schakel link-omleiding in voor maps.apple.com
4. Klaar! Apple Maps-links worden nu automatisch omgeleid.

✨ Functies:
• Automatische omleiding – niet handmatig kopiëren
• Onzichtbare achtergrondwerking – geen extra schermen
• Ondersteunt zoekopdrachten, coördinaten, adressen en navigatie
• Privacyvriendelijk – geen gegevens verzameld of verzonden
• Volledig gratis, geen advertenties

🗺️ Ondersteunde linkformaten:
• Zoekopdrachten (bijv. "Eiffeltoren")
• GPS-coördinaten
• Adressen
• Navigatie-aanwijzingen
• Locatiemarkeringen

MapFlip is gebouwd voor Android-gebruikers die regelmatig Apple Maps-links ontvangen van iPhone-gebruikers – via WhatsApp, e-mail, sms of sociale media."""
    },
    'no-NO': {
        'title': 'MapFlip – Apple til Google Maps',
        'shortDescription': 'Åpne Apple Maps-lenker automatisk i Google Maps.',
        'fullDescription': """Vennene dine sender deg Apple Maps-lenker – men du bruker Google Maps?

MapFlip løser dette problemet. Sett det opp én gang, og alt skjer automatisk: Hver Apple Maps-lenke åpnes direkte i Google Maps. Ingen kopiering, ingen liming, ingen omveier via nettleseren.

🔧 Slik fungerer det:
1. Åpne MapFlip
2. Trykk på "Åpne innstillinger"
3. Aktiver lenke-viderekobling for maps.apple.com
4. Ferdig! Apple Maps-lenker viderekobles nå automatisk.

✨ Funksjoner:
• Automatisk viderekobling – ingen manuell kopiering
• Usynlig bakgrunnsdrift – ingen ekstra skjermer
• Støtter søk, koordinater, adresser og navigasjon
• Personvernvennlig – ingen data samles inn eller sendes
• Helt gratis, ingen reklame

🗺️ Støttede lenkeformater:
• Søk (f.eks. "Eiffeltårnet")
• GPS-koordinater
• Adresser
• Navigasjonsinstruksjoner
• Stedmarkører

MapFlip er bygget for Android-brukere som regelmessig mottar Apple Maps-lenker fra iPhone-brukere – enten via WhatsApp, e-post, SMS eller sosiale medier."""
    },
    'pl-PL': {
        'title': 'MapFlip – Apple na Google Maps',
        'shortDescription': 'Automatycznie otwieraj linki Apple Maps w Google Maps.',
        'fullDescription': """Znajomi przysyłają Ci linki z Apple Maps – ale Ty używasz Google Maps?

MapFlip rozwiązuje ten problem. Skonfiguruj raz, a wszystko stanie się automatycznie: każdy link Apple Maps otworzy się bezpośrednio w Google Maps. Bez kopiowania, bez wklejania, bez przechodzenia przez przeglądarkę.

🔧 Jak to działa:
1. Otwórz MapFlip
2. Stuknij "Otwórz ustawienia"
3. Włącz przekierowanie linków dla maps.apple.com
4. Gotowe! Linki Apple Maps będą teraz przekierowywane automatycznie.

✨ Funkcje:
• Automatyczne przekierowanie – bez ręcznego kopiowania
• Niewidoczna praca w tle – bez dodatkowych ekranów
• Obsługuje wyszukiwania, współrzędne, adresy i nawigację
• Chroni prywatność – żadne dane nie są zbierane ani wysyłane
• Całkowicie za darmo, bez reklam

🗺️ Obsługiwane formaty linków:
• Wyszukiwania (np. "Wieża Eiffla")
• Współrzędne GPS
• Adresy
• Wskazówki nawigacyjne
• Znaczniki miejsc

MapFlip powstał z myślą o użytkownikach Androida, którzy regularnie otrzymują linki Apple Maps od użytkowników iPhone'ów – przez WhatsApp, e-mail, SMS czy media społecznościowe."""
    },
    'pt-BR': {
        'title': 'MapFlip: Apple p/ Google Maps',
        'shortDescription': 'Abra links do Apple Maps diretamente no Google Maps.',
        'fullDescription': """Seus amigos enviam links do Apple Maps – mas você usa o Google Maps?

O MapFlip resolve esse problema. Configure uma vez e tudo acontece automaticamente: cada link do Apple Maps abre diretamente no Google Maps. Sem copiar, sem colar, sem atalhos pelo navegador.

🔧 Como funciona:
1. Abra o MapFlip
2. Toque em "Abrir configurações"
3. Ative o redirecionamento de links para maps.apple.com
4. Pronto! Os links do Apple Maps agora são redirecionados automaticamente.

✨ Recursos:
• Redirecionamento automático – sem cópia manual
• Operação invisível em segundo plano – sem telas extras
• Suporta buscas, coordenadas, endereços e navegação
• Respeito à privacidade – nenhum dado é coletado ou enviado
• Totalmente gratuito, sem anúncios

🗺️ Formatos de links suportados:
• Buscas (ex: "Torre Eiffel")
• Coordenadas GPS
• Endereços
• Instruções de navegação
• Marcadores de locais

O MapFlip foi feito para usuários de Android que recebem frequentemente links do Apple Maps de usuários de iPhone – seja pelo WhatsApp, e-mail, SMS ou redes sociais."""
    },
    'pt-PT': {
        'title': 'MapFlip: Apple p/ Google Maps',
        'shortDescription': 'Abra links do Apple Maps diretamente no Google Maps.',
        'fullDescription': """Os seus amigos enviam-lhe links do Apple Maps – mas você usa o Google Maps?

O MapFlip resolve este problema. Configure uma vez e tudo acontece automaticamente: cada link do Apple Maps abre diretamente no Google Maps. Sem copiar, sem colar, sem atalhos pelo navegador.

🔧 Como funciona:
1. Abra o MapFlip
2. Toque em "Abrir definições"
3. Ative o redirecionamento de links para maps.apple.com
4. Concluído! Os links do Apple Maps são agora redirecionados automaticamente.

✨ Funcionalidades:
• Redirecionamento automático – sem cópia manual
• Funcionamento invisível em segundo plano – sem ecrãs adicionais
• Suporta pesquisas, coordenadas, endereços e navegação
• Respeito pela privacidade – nenhum dado é recolhido ou enviado
• Totalmente gratuito, sem anúncios

🗺️ Formatos de links suportados:
• Pesquisas (ex: "Torre Eiffel")
• Coordenadas GPS
• Endereços
• Instruções de navegação
• Marcadores de locais

O MapFlip foi desenvolvido para utilizadores de Android que recebem frequentemente links do Apple Maps de utilizadores de iPhone – seja por WhatsApp, e-mail, SMS ou redes sociais."""
    },
    'sv-SE': {
        'title': 'MapFlip – Apple til Google Maps',
        'shortDescription': 'Öppna Apple Maps-länkar automatiskt i Google Maps.',
        'fullDescription': """Dina vänner skickar Apple Maps-länkar – men du använder Google Maps?

MapFlip löser detta problem. Ställ in det en gång så sker allt automatiskt: Varje Apple Maps-länk öppnas direkt i Google Maps. Ingen kopiering, ingen klistra in, inga omvägar via webbläsaren.

🔧 Så fungerar det:
1. Öppna MapFlip
2. Tryck på "Öppna inställningar"
3. Aktivera länk-omdirigering för maps.apple.com
4. Klart! Apple Maps-länkar omdirigeras nu automatiskt.

✨ Funktioner:
• Automatisk omdirigering – ingen manuell kopiering
• Osynlig bakgrundsdrift – inga extra skärmar
• Stöder sökningar, koordinater, adresser och navigering
• Integritetsvänlig – ingen data samlas in eller skickas
• Helt gratis, inga annonser

🗺️ Länkformat som stöds:
• Sökningar (t.ex. "Eiffeltornet")
• GPS-koordinater
• Adresser
• Navigeringsanvisningar
• Platsmarkörer

MapFlip är skapat för Android-användare som regelbundet tar emot Apple Maps-länkar från iPhone-användare – via WhatsApp, e-post, SMS eller sociala medier."""
    },
    'es-ES': {
        'title': 'MapFlip – Apple a Google Maps',
        'shortDescription': 'Abre enlaces de Apple Maps directamente en Google Maps.',
        'fullDescription': """¿Tus amigos te envían enlaces de Apple Maps, pero tú usas Google Maps?

MapFlip soluciona este problema. Configúralo una vez y todo sucederá automáticamente: cada enlace de Apple Maps se abre directamente en Google Maps. Sin copiar, sin pegar, sin desvíos en el navegador.

🔧 Cómo funciona:
1. Abre MapFlip
2. Toca "Abrir ajustes"
3. Activa la redirección de enlaces para maps.apple.com
4. ¡Listo! Los enlaces de Apple Maps ahora se redirigen automáticamente.

✨ Características:
• Redirección automática – sin copiado manual
• Funcionamiento invisible en segundo plano – sin pantallas adicionales
• Soporta búsquedas, coordenadas, direcciones y navegación
• Respeta la privacidad – no se recopila ni envía ningún dato
• Completamente gratis, sin anuncios

🗺️ Formatos de enlace compatibles:
• Búsquedas (p. ej., "Torre Eiffel")
• Coordenadas GPS
• Direcciones
• Indicaciones de navegación
• Marcadores de lugares

MapFlip fue creado para usuarios de Android que reciben con frecuencia enlaces de Apple Maps de usuarios de iPhone, ya sea por WhatsApp, correo electrónico, SMS o redes sociales."""
    },
    'tr-TR': {
        'title': 'MapFlip: Apple\'dan Google Maps',
        'shortDescription': 'Apple Maps bağlantılarını otomatik olarak Google Maps\'te açın.',
        'fullDescription': """Arkadaşlarınız size Apple Maps bağlantıları gönderiyor ama siz Google Maps mi kullanıyorsunuz?

MapFlip bu sorunu çözer. Bir kez kurun, her şey otomatik gerçekleşsin: Her Apple Maps bağlantısı doğrudan Google Maps'te açılır. Kopyalama yok, yapıştırma yok, tarayıcıyla uğraşmak yok.

🔧 Nasıl çalışır:
1. MapFlip'i açın
2. "Ayarları Aç"a dokunun
3. maps.apple.com için bağlantı yönlendirmesini etkinleştirin
4. Tamam! Apple Maps bağlantıları artık otomatik olarak yönlendirilir.

✨ Özellikler:
• Otomatik yönlendirme – manuel kopyalamaya gerek yok
• Arka planda görünmez çalışma – ekstra ekran yok
• Aramaları, koordinatları, adresleri ve navigasyonu destekler
• Gizlilik dostu – hiçbir veri toplanmaz veya gönderilmez
• Tamamen ücretsiz, reklam yok

🗺️ Desteklenen bağlantı formatları:
• Arama sorguları (ör. "Eyfel Kulesi")
• GPS koordinatları
• Adresler
• Navigasyon tarifleri
• Yer işaretleri

MapFlip, iPhone kullanıcılarından (WhatsApp, e-posta, SMS veya sosyal medya üzerinden) düzenli olarak Apple Maps bağlantısı alan Android kullanıcıları için tasarlanmıştır."""
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
