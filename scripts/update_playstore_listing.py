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
        'shortDescription': "Åbn Apple Maps-links & URL'er i Google Maps. Hurtig Apple Maps-konverter.",
        'fullDescription': """Dine venner sender dig Apple Maps-links – men du bruger Google Maps?

MapFlip er den ultimative Apple Maps-konverter og link-omdirigering til Android. Sæt det op én gang, og alt sker automatisk som en Google Maps-omdirigering: Hvert Apple Maps-link, URL eller placering åbnes direkte i Google Maps. Ingen kopiering, ingen indsættelse, ingen omveje via browseren.

Uanset om du har brug for Apple Maps til Android eller en automatisk Apple til Google Maps-konverter, håndterer MapFlip alle links lynhurtigt i baggrunden.

🔧 Sådan fungerer det:
1. Åbn MapFlip
2. Tryk på "Åbn indstillinger"
3. Aktiver link-omdirigering for maps.apple.com
4. Færdig! Apple Maps-links åbner nu direkte i Google Maps.

✨ Funktioner & Privatliv:
• 🚗 Turn-by-turn navigation & Android Auto – Starter øjeblikkeligt rutevejledning i Google Maps
• 🔄 Automatisk omdirigering – Apple Maps til Google Maps link-konverter
• 🔒 100% offline & privat – Nul internettilladelser (no android.permission.INTERNET), ingen sporing, ingen reklamer
• ⏸️ Pause-tilstand & Hurtigindstillinger-knap – Sæt omdirigering på pause i meddelelsespanelet
• 💬 Universel app-kompatibilitet – Fungerer problemfrit med WhatsApp, Telegram, Signal, SMS, Slack og e-mail
• 👻 Usynlig baggrundsdrift – Ingen ekstra skærme, intet batteriforbrug

🗺️ Understøttede linkformater:
• Apple Maps URL'er & søgninger (f.eks. "Eiffeltårnet")
• GPS-koordinater & delte placeringer
• Adresser & stedmarkører
• Navigationsanvisninger

MapFlip er bygget til Android-brugere, der regelmæssigt modtager Apple Maps-links fra iPhone-brugere."""
    },
    'fr-FR': {
        'title': 'MapFlip - Apple à Google',
        'shortDescription': 'Ouvrez liens & URL Apple Maps dans Google Maps. Convertisseur rapide.',
        'fullDescription': """Vos amis vous envoient des liens Apple Maps – mais vous utilisez Google Maps ?

MapFlip est le convertisseur Apple Maps ultime et l'outil de redirection de liens pour Android. Configurez-le une fois, et tout fonctionne automatiquement comme une redirection Google Maps : chaque lien Apple Maps, URL ou lieu s'ouvre directement dans Google Maps. Pas de copier-coller, pas de détour par le navigateur.

Que vous cherchiez une solution Apple Maps pour Android ou un convertisseur Apple vers Google Maps automatique, MapFlip traite tous les liens en arrière-plan.

🔧 Comment ça marche :
1. Ouvrez MapFlip
2. Appuyez sur "Ouvrir les paramètres"
3. Activez la redirection des liens pour maps.apple.com
4. Terminé ! Les liens Apple Maps s'ouvrent maintenant directement dans Google Maps.

✨ Fonctionnalités & Confidentialité :
• 🚗 Navigation étape par étape & Android Auto – Lance instantanément l'itinéraire dans Google Maps
• 🔄 Redirection automatique – Convertisseur de liens Apple Maps vers Google Maps
• 🔒 100% hors ligne & privé – Aucune permission Internet (sans android.permission.INTERNET), aucun suivi, aucune publicité
• ⏸️ Mode pause & Tuile Paramètres rapides – Interrompez la redirection depuis le panneau de notification
• 💬 Compatibilité universelle – Fonctionne parfaitement avec WhatsApp, Telegram, Signal, SMS, Slack et e-mails
• 👻 Fonctionnement invisible en arrière-plan – Aucun écran supplémentaire, aucune consommation de batterie

🗺️ Formats de liens pris en charge :
• URL Apple Maps & requêtes de recherche (ex. "Tour Eiffel")
• Coordonnées GPS & lieux partagés
• Adresses & repères de lieux
• Instructions de navigation

MapFlip a été conçu pour les utilisateurs d'Android qui reçoivent régulièrement des liens Apple Maps de la part d'utilisateurs d'iPhone."""
    },
    'it-IT': {
        'title': 'MapFlip - Apple a Google',
        'shortDescription': 'Apri link & URL Apple Maps in Google Maps. Convertitore di mappe veloce.',
        'fullDescription': """I tuoi amici ti inviano link di Apple Maps – ma tu usi Google Maps?

MapFlip è il convertitore definitivo per Apple Maps e lo strumento di reindirizzamento link per Android. Configuralo una volta e tutto funzionerà automaticamente come un reindirizzamento a Google Maps: ogni link Apple Maps, URL o posizione si aprirà direttamente in Google Maps. Nessun copia-incolla, nessun passaggio dal browser.

Che tu abbia bisogno di Apple Maps per Android o di un convertitore da Apple a Google Maps, MapFlip gestisce tutti i link in background.

🔧 Come funziona:
1. Apri MapFlip
2. Tocca "Apri impostazioni"
3. Abilita il reindirizzamento dei link per maps.apple.com
4. Fatto! I link di Apple Maps ora si aprono direttamente in Google Maps.

✨ Caratteristiche & Privacy:
• 🚗 Navigazione passo-passo & Android Auto – Avvia istantaneamente l'itinerario in Google Maps
• 🔄 Reindirizzamento automatico – Convertitore di link da Apple Maps a Google Maps
• 🔒 100% offline e privato – Zero permessi Internet (no android.permission.INTERNET), nessun tracciamento, nessuna pubblicità
• ⏸️ Modalità pausa e tessera Impostazioni rapide – Sospendi il reindirizzamento dal pannello notifiche
• 💬 Compatibilità universale – Funziona perfettamente con WhatsApp, Telegram, Signal, SMS, Slack ed e-mail
• 👻 Funzionamento invisibile in background – Nessuna schermata aggiuntiva, zero consumo di batteria

🗺️ Formati di link supportati:
• URL Apple Maps & ricerche (es. "Torre Eiffel")
• Coordinate GPS & posizioni condivise
• Indirizzi & segnaposto di luoghi
• Indicazioni stradali

MapFlip è stato creato per gli utenti Android che ricevono regolarmente link di Apple Maps da utenti iPhone."""
    },
    'ja-JP': {
        'title': 'MapFlip – Apple Maps変換',
        'shortDescription': 'Apple MapsのリンクやURLをGoogle Mapsで開く。高速マップ変換ツール。',
        'fullDescription': """友達からApple Mapsのリンクが送られてくるけれど、普段使っているのはGoogle Mapsですか？

MapFlipは、Android向けの究極のApple Mapsリンク変換・自動転送ツールです。一度設定するだけで、すべての動作が自動化されます。Apple Mapsのリンク、URL、位置情報をタップするだけで、直接Google Mapsで開きます。コピー＆ペーストやブラウザ経由の手間は一切不要です。

AndroidでApple Mapsリンクを開きたい方や、AppleからGoogle Mapsへの自動コンバーターをお探しの方に最適です。

🔧 使い方：
1. MapFlipを開く
2. 「設定を開く」をタップ
3. maps.apple.com のリンク転送を有効化
4. 完了！これ以降、Apple MapsのリンクはGoogle Mapsで直接開きます。

✨ 特徴＆プライバシー：
• 🚗 音声ナビ＆Android Auto対応 – Google Mapsで即座にルート案内を開始
• 🔄 完全自動転送 – Apple MapsリンクをGoogle Mapsへ自動変換
• 🔒 100%オフライン＆プライバシー保護 – インターネット権限なし（0 Permissions）、追跡なし、広告なし
• ⏸️ 一時停止モード＆クイック設定タイル – 通知シェードからいつでも転送を一時停止可能
• 💬 主要アプリに対応 – LINE、WhatsApp、Telegram、SMS、Slack、メールなど
• 👻 バックグラウンドで透明動作 – 余計な画面なし、バッテリー消費ゼロ

🗺️ 対応リンクフォーマット：
• Apple Maps URL＆検索クエリ（例：「東京タワー」）
• GPS座標＆共有された位置情報
• 住所＆スポットピン
• ルート案内

MapFlipは、iPhoneユーザーからApple Mapsのリンクをよく受け取るAndroidユーザーのために開発されました。"""
    },
    'nl-NL': {
        'title': 'MapFlip - Apple naar Google',
        'shortDescription': "Open Apple Maps-links & URL's in Google Maps. Snelle kaartconverter.",
        'fullDescription': """Je vrienden sturen je Apple Maps-links – maar jij gebruikt Google Maps?

MapFlip is de ultieme Apple Maps-converter en link-omleiding voor Android. Stel het één keer in en alles werkt automatisch als een Google Maps-omleiding: elke Apple Maps-link, URL of locatie opent direct in Google Maps. Geen kopiëren, geen plakken, geen omwegen via de browser.

Of je nu een Apple Maps voor Android-oplossing zoekt of een automatische Apple naar Google Maps converter, MapFlip verwerkt alle links naadloos op de achtergrond.

🔧 Hoe het werkt:
1. Open MapFlip
2. Tik op "Instellingen openen"
3. Schakel link-omleiding in voor maps.apple.com
4. Klaar! Apple Maps-links openen nu direct in Google Maps.

✨ Functies & Privacy:
• 🚗 Turn-by-turn navigatie & Android Auto – Start direct routebeschrijving in Google Maps
• 🔄 Automatische omleiding – Apple Maps naar Google Maps link-converter
• 🔒 100% offline & privacyvriendelijk – Geen internetrechten (geen android.permission.INTERNET), geen tracking, geen advertenties
• ⏸️ Pauzemodus & Snelle instellingen-tegel – Omleiding pauzeren vanuit de meldingenbalk
• 💬 Universele app-compatibiliteit – Werkt naadloos met WhatsApp, Telegram, Signal, SMS, Slack en e-mail
• 👻 Onzichtbare achtergrondwerking – Geen extra schermen, geen batterijverbruik

🗺️ Ondersteunde linkformaten:
• Apple Maps URL's & zoekopdrachten (bijv. "Eiffeltoren")
• GPS-coördinaten & gedeelde locaties
• Adressen & locatiemarkeringen
• Navigatie-aanwijzingen

MapFlip is gebouwd voor Android-gebruikers die regelmatig Apple Maps-links ontvangen van iPhone-gebruikers."""
    },
    'no-NO': {
        'title': 'MapFlip - Apple til Google',
        'shortDescription': 'Åpne Apple Maps-lenker & URL-er i Google Maps. Rask kartkonverterer.',
        'fullDescription': """Vennene dine sender deg Apple Maps-lenker – men du bruker Google Maps?

MapFlip er den ultimate Apple Maps-konverteren og viderekoblingsløsningen for Android. Sett det opp én gang, og alt fungerer automatisk som en viderekobling til Google Maps: Hver Apple Maps-lenke, URL eller posisjon åpnes direkte i Google Maps. Ingen kopiering, ingen liming, ingen omveier via nettleseren.

Enten du trenger Apple Maps for Android eller en automatisk Apple til Google Maps-konverter, håndterer MapFlip alle lenker sømløst i bakgrunnen.

🔧 Slik fungerer det:
1. Åpne MapFlip
2. Trykk på "Åpne innstillinger"
3. Aktiver lenke-viderekobling for maps.apple.com
4. Ferdig! Apple Maps-lenker åpnes nå direkte i Google Maps.

✨ Funksjoner & Personvern:
• 🚗 Turn-by-turn navigasjon & Android Auto – Starter umiddelbart ruteveiledning i Google Maps
• 🔄 Automatisk viderekobling – Apple Maps til Google Maps lenkekonverter
• 🔒 100% frakoblet & privat – Null internetttillatelser (ingen android.permission.INTERNET), ingen sporing, ingen reklame
• ⏸️ Pausemodus & Hurtiginnstillinger-knapp – Sett viderekobling på pause i varslingspanelet
• 💬 Universell app-kompatibilitet – Fungerer sømløst med WhatsApp, Telegram, Signal, SMS, Slack og e-post
• 👻 Usynlig bakgrunnsdrift – Ingen ekstra skjermer, null batteribruk

🗺️ Støttede lenkeformater:
• Apple Maps URL-er & søk (f.eks. "Eiffeltårnet")
• GPS-koordinater & delte posisjoner
• Adresser & stedmarkører
• Navigasjonsinstruksjoner

MapFlip er bygget for Android-brukere som regelmessig mottar Apple Maps-lenker fra iPhone-brukere."""
    },
    'pl-PL': {
        'title': 'MapFlip - Apple na Google',
        'shortDescription': 'Otwieraj linki i URL Apple Maps w Google Maps. Szybki konwerter map.',
        'fullDescription': """Znajomi przysyłają Ci linki z Apple Maps – ale Ty używasz Google Maps?

MapFlip to najlepszy konwerter Apple Maps i narzędzie do przekierowywania linków dla Androida. Skonfiguruj raz, a wszystko zadziała automatycznie jako przekierowanie do Google Maps: każdy link, adres URL lub lokalizacja Apple Maps otworzy się bezpośrednio w Google Maps. Bez kopiowania, bez wklejania, bez przechodzenia przez przeglądarkę.

Niezależnie od tego, czy potrzebujesz Apple Maps na Androida, czy automatycznego konwertera z Apple na Google Maps, MapFlip obsługuje wszystko w tle.

🔧 Jak to działa:
1. Otwórz MapFlip
2. Stuknij "Otwórz ustawienia"
3. Włącz przekierowanie linków dla maps.apple.com
4. Gotowe! Linki Apple Maps otwierają się teraz bezpośrednio w Google Maps.

✨ Funkcje & Prywatność:
• 🚗 Nawigacja zakręt po zakręcie & Android Auto – Błyskawicznie uruchamia trasę w Google Maps
• 🔄 Automatyczne przekierowanie – Konwerter linków Apple Maps na Google Maps
• 🔒 100% offline i prywatność – Zero uprawnień internetowych (brak android.permission.INTERNET), zero śledzenia, brak reklam
• ⏸️ Tryb pauzy i kafelek szybkich ustawień – Wstrzymuj przekierowanie z panelu powiadomień
• 💬 Uniwersalna kompatybilność – Działa bezproblemowo z WhatsApp, Telegram, Signal, SMS, Slack i e-mail
• 👻 Niewidoczna praca w tle – Brak zbędnych ekranów, zerowe zużycie baterii

🗺️ Obsługiwane formaty linków:
• Adresy URL i wyszukiwania Apple Maps (np. "Wieża Eiffla")
• Współrzędne GPS & udostępnione lokalizacje
• Adresy & znaczniki miejsc
• Wskazówki nawigacyjne

MapFlip powstał z myślą o użytkownikach Androida, którzy regularnie otrzymują linki Apple Maps od użytkowników iPhone'ów."""
    },
    'pt-BR': {
        'title': 'MapFlip - Apple para Google',
        'shortDescription': 'Abra links e URLs do Apple Maps no Google Maps. Conversor rápido de mapas.',
        'fullDescription': """Seus amigos enviam links do Apple Maps – mas você usa o Google Maps?

O MapFlip é o conversor definitivo de Apple Maps e redirecionador de links para Android. Configure uma vez e tudo funcionará automaticamente como um redirecionamento para o Google Maps: cada link, URL ou localização do Apple Maps abre diretamente no Google Maps. Sem copiar, sem colar, sem atalhos pelo navegador.

Quer você precise do Apple Maps para Android ou de um conversor automático de Apple para Google Maps, o MapFlip processa tudo em segundo plano.

🔧 Como funciona:
1. Abra o MapFlip
2. Toque em "Abrir configurações"
3. Ative o redirecionamento de links para maps.apple.com
4. Pronto! Os links do Apple Maps agora abrem diretamente no Google Maps.

✨ Recursos & Privacidade:
• 🚗 Navegação curva a curva & Android Auto – Inicia instantaneamente a rota no Google Maps
• 🔄 Redirecionamento automático – Conversor de links Apple Maps para Google Maps
• 🔒 100% offline e seguro – Zero permissões de internet (sem android.permission.INTERNET), sem rastreamento, sem anúncios
• ⏸️ Modo pausa e atalho de Configurações Rápidas – Pause o redirecionamento no painel de notificações
• 💬 Compatibilidade universal – Funciona perfeitamente com WhatsApp, Telegram, Signal, SMS, Slack e e-mail
• 👻 Operação invisível em segundo plano – Sem telas extras, sem consumo de bateria

🗺️ Formatos de links suportados:
• URLs do Apple Maps & buscas (ex: "Torre Eiffel")
• Coordenadas GPS & localizações compartilhadas
• Endereços & marcadores de locais
• Instruções de navegação

O MapFlip foi feito para usuários de Android que recebem frequentemente links do Apple Maps de usuários de iPhone."""
    },
    'pt-PT': {
        'title': 'MapFlip - Apple para Google',
        'shortDescription': 'Abra links e URLs do Apple Maps no Google Maps. Conversor de mapas rápido.',
        'fullDescription': """Os seus amigos enviam-lhe links do Apple Maps – mas você usa o Google Maps?

O MapFlip é o conversor definitivo do Apple Maps e redirecionador de links para Android. Configure uma vez e tudo funcionará automaticamente como um redirecionamento para o Google Maps: cada link, URL ou localização do Apple Maps abre diretamente no Google Maps. Sem copiar, sem colar, sem atalhos pelo navegador.

Quer precise do Apple Maps para Android ou de um conversor automático de Apple para Google Maps, o MapFlip processa tudo em segundo plano.

🔧 Como funciona:
1. Abra o MapFlip
2. Toque em "Abrir definições"
3. Ative o redirecionamento de links para maps.apple.com
4. Concluído! Os links do Apple Maps abrem agora diretamente no Google Maps.

✨ Funcionalidades & Privacidade:
• 🚗 Navegação passo a passo & Android Auto – Inicia instantaneamente o trajeto no Google Maps
• 🔄 Redirecionamento automático – Conversor de links Apple Maps para Google Maps
• 🔒 100% offline e privado – Zero permissões de internet (sem android.permission.INTERNET), sem rastreio, sem anúncios
• ⏸️ Modo pausa e atalho de Definições Rápidas – Pause o redirecionamento no painel de notificações
• 💬 Compatibilidade universal – Funciona perfeitamente com WhatsApp, Telegram, Signal, SMS, Slack e e-mail
• 👻 Funcionamento invisível em segundo plano – Sem ecrãs adicionais, zero consumo de bateria

🗺️ Formatos de links suportados:
• URLs do Apple Maps & pesquisas (ex: "Torre Eiffel")
• Coordenadas GPS & localizações partilhadas
• Endereços & marcadores de locais
• Instruções de navegação

O MapFlip foi desenvolvido para utilizadores de Android que recebem frequentemente links do Apple Maps de utilizadores de iPhone."""
    },
    'sv-SE': {
        'title': 'MapFlip - Apple till Google',
        'shortDescription': 'Öppna Apple Maps-länkar & URL:er i Google Maps. Snabb kartkonverterare.',
        'fullDescription': """Dina vänner skickar Apple Maps-länkar – men du använder Google Maps?

MapFlip är den ultimata Apple Maps-konverteraren och länkomdirigeringen för Android. Ställ in det en gång så fungerar allt automatiskt som en omdirigering till Google Maps: Varje Apple Maps-länk, URL eller plats öppnas direkt i Google Maps. Ingen kopiering, ingen klistra in, inga omvägar via webbläsaren.

Oavsett om du behöver Apple Maps för Android eller en automatisk Apple till Google Maps-konverterare, hanterar MapFlip alla länkar sömlöst i bakgrunden.

🔧 Så fungerar det:
1. Öppna MapFlip
2. Tryck på "Öppna inställningar"
3. Aktivera länk-omdirigering för maps.apple.com
4. Klart! Apple Maps-länkar öppnas nu direkt i Google Maps.

✨ Funktioner & Integritet:
• 🚗 Sväng-för-sväng-navigering & Android Auto – Startar omedelbart rutt i Google Maps
• 🔄 Automatisk omdirigering – Apple Maps till Google Maps länkkonverterare
• 🔒 100% offline & integritetsvänlig – Noll internetbehörigheter (ingen android.permission.INTERNET), ingen spårning, inga annonser
• ⏸️ Pausläge & Snabbinställnings-knapp – Pausa omdirigeringen från aviseringspanelen
• 💬 Universell app-kompatibilitet – Fungerar sömlöst med WhatsApp, Telegram, Signal, SMS, Slack och e-post
• 👻 Osynlig bakgrundsdrift – Inga extra skärmar, noll batteriförbrukning

🗺️ Länkformat som stöds:
• Apple Maps URL:er & sökningar (t.ex. "Eiffeltornet")
• GPS-koordinater & delade platser
• Adresser & platsmarkörer
• Navigeringsanvisningar

MapFlip är skapat för Android-användare som regelbundet tar emot Apple Maps-länkar från iPhone-användare."""
    },
    'es-ES': {
        'title': 'MapFlip - Apple a Google',
        'shortDescription': 'Abre enlaces y URLs de Apple Maps en Google Maps. Conversor de mapas rápido.',
        'fullDescription': """¿Tus amigos te envían enlaces de Apple Maps, pero tú usas Google Maps?

MapFlip es el conversor definitivo de Apple Maps y la herramienta de redirección de enlaces para Android. Configúralo una vez y todo funcionará automáticamente como una redirección a Google Maps: cada enlace, URL o ubicación de Apple Maps se abre directamente en Google Maps. Sin copiar, sin pegar, sin desvíos en el navegador.

Tanto si buscas Apple Maps para Android como un conversor automático de Apple a Google Maps, MapFlip lo gestiona todo en segundo plano.

🔧 Cómo funciona:
1. Abre MapFlip
2. Toca "Abrir ajustes"
3. Activa la redirección de enlaces para maps.apple.com
4. ¡Listo! Los enlaces de Apple Maps ahora se abren directamente en Google Maps.

✨ Características & Privacidad:
• 🚗 Navegación paso a paso & Android Auto – Inicia al instante la ruta en Google Maps
• 🔄 Redirección automática – Convertidor de enlaces Apple Maps a Google Maps
• 🔒 100% offline y privado – Cero permisos de internet (sin android.permission.INTERNET), sin rastreo, sin anuncios
• ⏸️ Modo pausa y atajo de Ajustes Rápidos – Pausa la redirección desde el panel de notificaciones
• 💬 Compatibilidad universal – Funciona sin problemas con WhatsApp, Telegram, Signal, SMS, Slack y correo electrónico
• 👻 Funcionamiento invisible en segundo plano – Sin pantallas adicionales, sin consumo de batería

🗺️ Formatos de enlace compatibles:
• URLs de Apple Maps & búsquedas (p. ej., "Torre Eiffel")
• Coordenadas GPS & ubicaciones compartidas
• Direcciones & marcadores de lugares
• Indicaciones de navegación

MapFlip fue creado para usuarios de Android que reciben con frecuencia enlaces de Apple Maps de usuarios de iPhone."""
    },
    'tr-TR': {
        'title': "MapFlip - Apple'dan Google",
        'shortDescription': "Apple Maps bağlantı & URL'lerini Google Maps'te açın. Hızlı harita dönüştürücü.",
        'fullDescription': """Arkadaşlarınız size Apple Maps bağlantıları gönderiyor ama siz Google Maps mi kullanıyorsunuz?

MapFlip, Android için nihai Apple Maps dönüştürücüsü ve bağlantı yönlendirme aracıdır. Bir kez kurun, Google Maps yönlendirmesi olarak her şey otomatik çalışsın: Her Apple Maps bağlantısı, URL veya konum doğrudan Google Maps'te açılır. Kopyalama yok, yapıştırma yok, tarayıcıyla uğraşmak yok.

İster Android için Apple Maps çözümüne ister otomatik Apple'dan Google Maps'e dönüştürücüye ihtiyacınız olsun, MapFlip tüm bağlantıları arka planda anında çözer.

🔧 Nasıl çalışır:
1. MapFlip'i açın
2. "Ayarları Aç"a dokunun
3. maps.apple.com için bağlantı yönlendirmesini etkinleştirin
4. Tamam! Apple Maps bağlantıları artık doğrudan Google Maps'te açılır.

✨ Özellikler & Gizlilik:
• 🚗 Adım adım navigasyon ve Android Auto – Google Maps'te anında rota ve canlı navigasyon başlatır
• 🔄 Otomatik yönlendirme – Apple Maps'ten Google Maps'e bağlantı dönüştürücü
• 🔒 %100 çevrimdışı ve gizli – Sıfır internet izni (android.permission.INTERNET izni yok), takip yok, reklam yok
• ⏸️ Duraklatma modu ve Hızlı Ayarlar kutusu – Yönlendirmeyi bildirim panelinden duraklatın
• 💬 Evrensel uygulama uyumluluğu – WhatsApp, Telegram, Signal, SMS, Slack ve e-posta ile sorunsuz çalışır
• 👻 Arka planda görünmez çalışma – Ekstra ekran yok, sıfır pil tüketimi

🗺️ Desteklenen bağlantı formatları:
• Apple Maps URL'leri & arama sorguları (ör. "Eyfel Kulesi")
• GPS koordinatları & paylaşılan konumlar
• Adresler & yer işaretleri
• Navigasyon tarifleri

MapFlip, iPhone kullanıcılarından düzenli olarak Apple Maps bağlantısı alan Android kullanıcıları için tasarlanmıştır."""
    },
    'ko-KR': {
        'title': 'MapFlip - Apple to Google',
        'shortDescription': 'Apple Maps 링크를 Google Maps에서 열기. 빠른 지도 링크 변환기.',
        'fullDescription': """친구들이 Apple Maps 링크를 보내는데, 당신은 Android에서 Google Maps를 사용하시나요?

MapFlip은 Android를 위한 가장 완벽한 Apple Maps 변환기 및 링크 리디렉션 도구입니다. 한 번만 설정하면 모든 Apple Maps 링크, URL 및 위치가 Google Maps에서 자동으로 열립니다. 복사, 붙여넣기, 브라우저 우회가 필요 없습니다.

Android용 Apple Maps 솔루션이나 자동 변환기가 필요할 때, MapFlip이 백그라운드에서 모든 것을 처리합니다.

🔧 작동 방법:
1. MapFlip 열기
2. "설정 열기" 탭하기
3. maps.apple.com에 대한 링크 전달 활성화
4. 완료! 이제 Apple Maps 링크가 Google Maps에서 바로 열립니다.

✨ 주요 기능 및 개인정보 보호:
• 🚗 턴바이턴 내비게이션 & Android Auto – Google Maps 기본 내비게이션 즉시 실행
• 🔄 자동 지도 리디렉션 – 실시간 기기 내 링크 변환
• 🔒 100% 오프라인 & 프라이버시 – 인터넷 권한 없음(0 Permissions), 추적 없음, 광고 없음
• ⏸️ 일시중지 모드 & 빠른 설정 타일 – 알림창에서 언제든 간편하게 일시중지
• 💬 모든 메신저 호환 – WhatsApp, Telegram, Signal, SMS, Slack, 카카오톡, 이메일 완벽 지원
• 👻 보이지 않는 백그라운드 작동 – 불필요한 화면이나 배터리 소모 없음

🗺️ 지원되는 링크 형식:
• Apple Maps URL 및 검색어
• GPS 좌표 및 공유된 위치
• 주소 및 장소 마커
• 내비게이션 경로 및 길찾기"""
    },
    'zh-CN': {
        'title': 'MapFlip - Apple to Google',
        'shortDescription': '在 Google Maps 中打开 Apple Maps 链接。极速地图重定向。',
        'fullDescription': """朋友发送了 Apple Maps 地图链接，但你在安卓上习惯使用 Google Maps？

MapFlip 是专为 Android 打造的 Apple Maps 链接转换与自动重定向神器。只需简单配置一次，所有 Apple Maps 链接、网址和共享位置都将无缝直接在 Google Maps 中打开。无需手动复制粘贴，无需经过浏览器跳转。

🔧 使用方法：
1. 打开 MapFlip
2. 点击“打开设置”
3. 为 maps.apple.com 开启支持的链接转发
4. 完成！从此 Apple Maps 链接将自动在 Google Maps 中打开。

✨ 核心特色与隐私保证：
• 🚗 实时导航与 Android Auto – 一键立即启动 Google Maps 原生路线指引与导航
• 🔄 全自动即时重定向 – 零延迟本地链路转换
• 🔒 100% 离线与隐私保护 – 无网络访问权限（0 权限），无数据追踪，无广告
• ⏸️ 快捷暂停模式 – 在通知栏下拉快捷开关中随时暂停与恢复
• 💬 广泛兼容各类通讯软件 – 完美支持微信、WhatsApp、Telegram、Signal、短信与邮件
• 👻 极简无感后台运行 – 零冗余界面，不耗电

🗺️ 支持的链接格式：
• Apple Maps 网址与搜索关键词
• GPS 经纬度坐标与分享定位
• 具体地址与地点标注
• 路线规划与导航指令"""
    },
    'zh-TW': {
        'title': 'MapFlip - Apple to Google',
        'shortDescription': '在 Google Maps 中開啟 Apple Maps 連結。極速地圖重新導向。',
        'fullDescription': """朋友傳送了 Apple Maps 地圖連結，但你在 Android 上習慣使用 Google Maps？

MapFlip 是專為 Android 設計的 Apple Maps 連結轉換與自動重新導向工具。只需設定一次，所有 Apple Maps 連結、網址和分享位置都將直接在 Google Maps 中開啟。無需手動複製貼上，無需瀏覽器跳轉。

🔧 使用步驟：
1. 開啟 MapFlip
2. 點擊「開啟設定」
3. 為 maps.apple.com 啟用支援的連結轉發
4. 完成！Apple Maps 連結將自動在 Google Maps 中開啟。

✨ 核心功能與隱私保護：
• 🚗 路線導航與 Android Auto – 立即啟動 Google Maps 原生導航與路線指引
• 🔄 全自動無縫轉向 – 本機即時轉換，流暢順暢
• 🔒 100% 離線與隱私安全 – 無網路權限（0 權限），無追蹤，無廣告
• ⏸️ 快速設定控制磚 – 從下拉通知欄隨時暫停與恢復重定向
• 💬 完美支援各類通訊軟體 – LINE、WhatsApp、Telegram、Signal、簡訊與電子郵件
• 👻 隱形背景運作 – 零干擾，不耗電

🗺️ 支援的連結格式：
• Apple Maps 網址與搜尋關鍵字
• GPS 經緯度座標與共享定位
• 地址與地點標籤
• 導航路徑與路線指引"""
    },
    'ar': {
        'title': 'MapFlip - Apple to Google',
        'shortDescription': 'فتح روابط Apple Maps في Google Maps. محول روابط الخرائط السريع.',
        'fullDescription': """هل يرسل لك أصدقاؤك روابط Apple Maps بينما تستخدم Google Maps على أندرويد؟

MapFlip هو الأداة المثالية لتحويل وإعادة توجيه روابط خرائط Apple Maps إلى Google Maps على نظام أندرويد. قم بإعداده مرة واحدة وسيعمل كل شيء تلقائياً: يفتح كل رابط أو موقع من Apple Maps مباشرة في Google Maps. دون نسخ أو لصق أو انتظار.

🔧 كيفية الاستخدام:
1. افتح تطبيق MapFlip
2. اضغط على "فتح الإعدادات"
3. قم بتفعيل فتح الروابط المدعومة لـ maps.apple.com
4. تم! تفتح روابط Apple Maps الآن مباشرة في Google Maps.

✨ الميزات والخصوصية:
• 🚗 الملاحة خطوة بخطوة و Android Auto – تشغيل فوري للتوجيه في Google Maps
• 🔄 إعادة توجيه تلقائية وسلسة – تحويل فوري للروابط داخل الجهاز
• 🔒 100% بدون إنترنت وأمان تام – لا أذونات إنترنت (0 Permissions)، بدون تتبع، بدون إعلانات
• ⏸️ وضع الإيقاف المؤقت ولوحة الإعدادات السريعة – إيقاف واستئناف من لوحة الإشعارات
• 💬 توافق شامل مع التطبيقات – يعمل مع WhatsApp و Telegram و Signal و SMS والبريد
• 👻 خفيف ويعمل في الخلفية – بدون استهلاك للبطارية

🗺️ صيغ الروابط المدعومة:
• روابط Apple Maps وكلمات البحث
• إحداثيات GPS والمواقع المشتركة
• العناوين ونقاط الاهتمام
• مسارات واتجاهات القيادة"""
    },
    'ru-RU': {
        'title': 'MapFlip - Apple to Google',
        'shortDescription': 'Открывайте ссылки Apple Maps в Google Maps. Быстрый конвертер ссылок.',
        'fullDescription': """Друзья отправляют ссылки на Apple Maps, а вы пользуетесь Google Maps на Android?

MapFlip — это надежный конвертер и инструмент перенаправления ссылок Apple Maps для Android. Настройте один раз, и каждая ссылка, адрес или геопозиция Apple Maps будет автоматически открываться в Google Maps. Без копирования, вставки и браузера.

🔧 Как это работает:
1. Откройте MapFlip
2. Нажмите «Открыть настройки»
3. Включите открытие ссылок для maps.apple.com
4. Готово! Ссылки Apple Maps теперь открываются прямо в Google Maps.

✨ Возможности и безопасность:
• 🚗 Пошаговая навигация и Android Auto – мгновенный запуск маршрутов в Google Maps
• 🔄 Автоматическое перенаправление – быстрая локальная обработка ссылок
• 🔒 100% офлайн и конфиденциальность – без интернет-разрешений (0 Permissions), без слежки, без рекламы
• ⏸️ Режим паузы и плитка быстрых настроек – приостановка в панели уведомлений
• 💬 Поддержка всех мессенджеров – Telegram, WhatsApp, Signal, SMS, Slack и почта
• 👻 Незаметная фоновая работа – без лишних экранов и расхода батареи

🗺️ Поддерживаемые форматы:
• Ссылки и поисковые запросы Apple Maps
• GPS-координаты и точки на карте
• Адреса и метки мест
• Маршруты и навигация"""
    },
    'id': {
        'title': 'MapFlip - Apple to Google',
        'shortDescription': 'Buka tautan Apple Maps di Google Maps. Konverter tautan peta cepat.',
        'fullDescription': """Teman Anda mengirim tautan Apple Maps, tetapi Anda menggunakan Google Maps di Android?

MapFlip adalah alat pengonversi dan pengalih tautan Apple Maps terbaik untuk Android. Cukup atur sekali, dan semua tautan, URL, atau lokasi Apple Maps akan terbuka secara otomatis di Google Maps. Tanpa salin-tempel dan tanpa membuka browser.

🔧 Cara Penggunaan:
1. Buka MapFlip
2. Ketuk "Buka Pengaturan"
3. Aktifkan penerusan tautan untuk maps.apple.com
4. Selesai! Tautan Apple Maps sekarang langsung terbuka di Google Maps.

✨ Fitur & Privasi:
• 🚗 Navigasi Belokan demi Belokan & Android Auto – Langsung meluncurkan rute asli di Google Maps
• 🔄 Pengalihan Otomatis – Konversi tautan instan di dalam perangkat
• 🔒 100% Offline & Privasi Terjaga – Tanpa izin internet (0 Permissions), tanpa pelacakan, tanpa iklan
• ⏸️ Mode Jeda & Ubin Pengaturan Cepat – Jeda kapan saja dari panel notifikasi
• 💬 Kompatibel dengan Semua Aplikasi – WhatsApp, Telegram, Signal, SMS, Slack, dan email
• 👻 Berjalan Halus di Latar Belakang – Hemat baterai tanpa gangguan

🗺️ Format tautan yang didukung:
• URL & pencarian Apple Maps
• Koordinat GPS & lokasi bersama
• Alamat & penanda tempat
• Petunjuk arah navigasi"""
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
        print(f"🚀 All {len(LISTINGS)} Play Store listings updated successfully!")

    except Exception as e:
        print(f"❌ Error updating Play Store listings: {e}")
        sys.exit(1)

if __name__ == '__main__':
    main()
