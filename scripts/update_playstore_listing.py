import os
import json
import sys

LISTINGS = {
    'en-US': {
        'title': 'MapFlip - Apple to Google',
        'shortDescription': 'Redirect Apple Maps links to Google Maps. Fast Apple map redirect & converter.',
        'fullDescription': """Your friends send you Apple Maps links – but you use Google Maps?

MapFlip is the ultimate Apple Maps converter, fast Apple map redirect, and link converter for Android. Set it up once, and everything works automatically as a seamless Google Maps redirect: Every Apple Map link, Apple Maps URL, or location opens directly in Google Maps. No copying, no pasting, no browser detours.

Whether you need an Apple Maps for Android solution, an Apple to Google converter, or an automatic Apple to Google Maps converter, MapFlip handles all Apple Maps URLs in the background. Open Apple Maps links directly in your favorite navigation app.

🔧 How it works:
1. Open MapFlip
2. Tap "Open Settings"
3. Enable link forwarding for maps.apple.com
4. Done! Apple Maps links now open directly in Google Maps.

✨ Features & Privacy:
• 🚗 Turn-by-Turn Navigation & Android Auto – Instantly launches native routing in Google Maps
• 🧭 Choose Your Navigation App – Open destinations in Google Maps, Waze, Organic Maps, OsmAnd, or system picker
• 🔄 Automatic Apple to Google redirect – Instant on-device link converter & map converter
• 🔒 100% Offline & Private – Zero internet permissions (no android.permission.INTERNET), zero tracking, zero ads
• ⏸️ Pause Mode & Quick Settings Tile – Suspend redirect anytime directly from your notification shade
• 💬 Universal App Compatibility – Works seamlessly with WhatsApp, Telegram, Signal, SMS, Slack, and email
• 👻 Invisible Background Operation – No extra screens or battery drain

🗺️ Supported link formats & services:
• Apple Maps URLs & search queries (maps.apple.com)
• OpenStreetMap (osm.org), Bing Maps & Yandex Maps
• GPS coordinates & shared locations
• Addresses, place markers & navigation routes

MapFlip was built for Android users who regularly receive Apple Maps links from iPhone users."""
    },
    'de-DE': {
        'title': 'MapFlip - Apple zu Google',
        'shortDescription': 'Apple Maps & Karten Links in Google Maps öffnen. Schnelle Kartenweiterleitung.',
        'fullDescription': """Deine Freunde schicken dir Apple Maps Links – aber du nutzt Google Maps auf Android?

MapFlip ist der zuverlässige Apple Maps Konverter, smarte Apple Karten Konverter und die automatische Kartenweiterleitung für Android. Einmal einrichten, danach läuft alles automatisch als Google Maps Weiterleitung: Jeder Apple Karten Link, jeder Apple Maps Link und jede URL öffnet sich direkt in Google Maps. Kein Kopieren, kein Einfügen, kein Umweg über den Browser.

Egal ob Apple zu Google Maps, Apple Karten weiterleiten, Apple Karten öffnen oder Apple Karten auf Android: MapFlip ist die schnelle Apple Maps Weiterleitung und leitet alle Karten-Links blitzschnell um.

🔧 So funktioniert's:
1. Öffne MapFlip
2. Tippe auf „Einstellungen öffnen“
3. Aktiviere die Link-Weiterleitung für maps.apple.com
4. Fertig! Ab jetzt werden Apple Karten Links automatisch in Google Maps geöffnet.

✨ Features & Datenschutz:
• 🚗 Turn-by-Turn Navigation & Android Auto – Startet sofort die native Routenführung in Google Maps
• 🧭 Wähle deine Navigations-App – Öffne Ziele in Google Maps, Waze, Organic Maps, OsmAnd oder Systemauswahl
• 🔄 Automatische Kartenweiterleitung – Schneller Apple Karten Konverter, Apple Maps Weiterleitung & GPS-Link-Konverter
• 🔒 100% Offline & Datenschutz – Keine Internet-Berechtigung (0 Permissions), kein Tracking, keine Werbung
• ⏸️ Pausen-Modus & Schnelleinstellungs-Kachel – Umleitung jederzeit im Kontrollzentrum pausieren
• 💬 Universelle App-Kompatibilität – Funktioniert mit WhatsApp, Telegram, Signal, SMS, Slack und E-Mails
• 👻 Unsichtbar im Hintergrund – Keine störenden Zusatz-Bildschirme, null Akkuverbrauch

🗺️ Unterstützte Link-Formate & Dienste:
• Apple Maps / Apple Karten URLs & Suchanfragen (maps.apple.com)
• OpenStreetMap (osm.org), Bing Maps & Yandex Maps
• GPS-Koordinaten & geteilte Standorte
• Adressen, Ortsmarkierungen & Navigationsrouten

MapFlip wurde für Android-Nutzer entwickelt, die regelmäßig Apple Maps Links oder Apple Karten von iPhone-Nutzern erhalten."""
    },
    'da-DK': {
        'title': 'MapFlip - Apple til Google',
        'shortDescription': 'Omdiriger Apple Maps-links til Google Maps. Hurtig kortkonverter.',
        'fullDescription': """Dine venner sender dig Apple Maps-links – men du bruger Google Maps?

MapFlip er den ultimative Apple Maps-konverter, hurtige Apple Maps-omdirigering og link-konverter til Android. Sæt det op én gang, og alt sker automatisk som en Google Maps-omdirigering: Hvert Apple Maps-link, URL eller placering åbnes direkte i Google Maps. Ingen kopiering, ingen indsættelse, ingen omveje via browseren.

Uanset om du har brug for Apple Maps til Android, en Apple til Google-konverter eller en automatisk Apple til Google Maps-konverter, håndterer MapFlip alle links lynhurtigt i baggrunden. Åbn Apple Maps-links direkte i din foretrukne navigationsapp.

🔧 Sådan fungerer det:
1. Åbn MapFlip
2. Tryk på "Åbn indstillinger"
3. Aktiver link-omdirigering for maps.apple.com
4. Færdig! Apple Maps-links åbner nu direkte i Google Maps.

✨ Funktioner & Privatliv:
• 🚗 Turn-by-turn navigation & Android Auto – Starter øjeblikkeligt rutevejledning i Google Maps
• 🧭 Vælg din navigationsapp – Åbn destinationer i Google Maps, Waze, Organic Maps, OsmAnd eller systemvalg
• 🔄 Automatisk omdirigering – Lynhurtig Apple Maps link-konverter & kortkonverter
• 🔒 100% offline & privat – Nul internettilladelser (no android.permission.INTERNET), ingen sporing, ingen reklamer
• ⏸️ Pause-tilstand & Hurtigindstillinger-knap – Sæt omdirigering på pause i meddelelsespanelet
• 💬 Universel app-kompatibilitet – Fungerer problemfrit med WhatsApp, Telegram, Signal, SMS, Slack og e-mail
• 👻 Usynlig baggrundsdrift – Ingen ekstra skærme, intet batteriforbrug

🗺️ Understøttede linkformater & tjenester:
• Apple Maps URL'er & søgninger (maps.apple.com)
• OpenStreetMap (osm.org), Bing Maps & Yandex Maps
• GPS-koordinater & delte placeringer
• Adresser, stedmarkører & rutevejledning

MapFlip er bygget til Android-brugere, der regelmæssigt modtager Apple Maps-links fra iPhone-brugere."""
    },
    'fr-FR': {
        'title': 'MapFlip - Apple à Google',
        'shortDescription': 'Redirigez liens Apple Maps vers Google Maps. Convertisseur rapide.',
        'fullDescription': """Vos amis vous envoient des liens Apple Maps – mais vous utilisez Google Maps ?

MapFlip est le convertisseur Apple Maps ultime, l'outil de redirection rapide et de conversion de liens pour Android. Configurez-le une fois, et tout fonctionne automatiquement comme une redirection Google Maps : chaque lien Apple Maps, URL ou lieu s'ouvre directement dans Google Maps. Pas de copier-coller, pas de détour par le navigateur.

Que vous cherchiez une solution Apple Maps pour Android, un convertisseur Apple vers Google ou un convertisseur automatique Apple vers Google Maps, MapFlip traite tous les liens en arrière-plan. Ouvrez les liens Apple Maps directement dans votre application de navigation préférée.

🔧 Comment ça marche :
1. Ouvrez MapFlip
2. Appuyez sur "Ouvrir les paramètres"
3. Activez la redirection des liens pour maps.apple.com
4. Terminé ! Les liens Apple Maps s'ouvrent maintenant directement dans Google Maps.

✨ Fonctionnalités & Confidentialité :
• 🚗 Navigation étape par étape & Android Auto – Lance instantanément l'itinéraire dans Google Maps
• 🧭 Choisissez votre app de navigation – Ouvrez vos trajets dans Google Maps, Waze, Organic Maps, OsmAnd ou le sélecteur système
• 🔄 Redirection automatique – Convertisseur de liens et de cartes instantané
• 🔒 100% hors ligne & privé – Aucune permission Internet (sans android.permission.INTERNET), aucun suivi, aucune publicité
• ⏸️ Mode pause & Tuile Paramètres rapides – Interrompez la redirection depuis le panneau de notification
• 💬 Compatibilité universelle – Fonctionne parfaitement avec WhatsApp, Telegram, Signal, SMS, Slack et e-mails
• 👻 Fonctionnement invisible en arrière-plan – Aucun écran supplémentaire, aucune consommation de batterie

🗺️ Formats de liens & services pris en charge :
• URL Apple Maps & recherches (maps.apple.com)
• OpenStreetMap (osm.org), Bing Maps & Yandex Maps
• Coordonnées GPS & lieux partagés
• Adresses, repères & itinéraires de navigation

MapFlip a été conçu pour les utilisateurs d'Android qui reçoivent régulièrement des liens Apple Maps de la part d'utilisateurs d'iPhone."""
    },
    'es-ES': {
        'title': 'MapFlip - Apple a Google',
        'shortDescription': 'Redirige enlaces Apple Maps a Google Maps. Conversor rápido de mapas.',
        'fullDescription': """¿Tus amigos te envían enlaces de Apple Maps, pero usas Google Maps en Android?

MapFlip es el conversor definitivo de Apple Maps, redirección rápida y conversor de enlaces para Android. Configúralo una vez y todo funcionará automáticamente como una redirección a Google Maps: cada enlace, URL o ubicación de Apple Maps se abre directamente en Google Maps. Sin copiar, sin pegar, sin desvíos en el navegador.

Ya sea que busques Apple Maps para Android, un conversor de Apple a Google o un conversor automático de Apple a Google Maps, MapFlip gestiona todos los enlaces en segundo plano. Abre enlaces de Apple Maps directamente en tu app de navegación favorita.

🔧 Cómo funciona:
1. Abre MapFlip
2. Toca "Abrir ajustes"
3. Activa el reenvío de enlaces para maps.apple.com
4. ¡Listo! Los enlaces de Apple Maps ahora se abren directamente en Google Maps.

✨ Funciones y Privacidad:
• 🚗 Navegación paso a paso y Android Auto – Inicia al instante las rutas nativas en Google Maps
• 🧭 Elige tu app de navegación – Abre destinos en Google Maps, Waze, Organic Maps, OsmAnd o el selector del sistema
• 🔄 Redirección automática de Apple a Google – Conversor de enlaces y mapas instantáneo en el dispositivo
• 🔒 100% Offline y Privado – Cero permisos de internet (sin android.permission.INTERNET), sin rastreo, sin anuncios
• ⏸️ Modo pausa y Ajuste rápido – Pausa la redirección cuando quieras desde el panel de notificaciones
• 💬 Compatibilidad universal – Funciona con WhatsApp, Telegram, Signal, SMS, Slack y correos electrónicos
• 👻 Funcionamiento invisible en segundo plano – Sin pantallas adicionales, sin consumo de batería

🗺️ Formatos de enlaces y servicios compatibles:
• URLs y búsquedas de Apple Maps (maps.apple.com)
• OpenStreetMap (osm.org), Bing Maps y Yandex Maps
• Coordenadas GPS y ubicaciones compartidas
• Direcciones, marcadores de lugares y rutas de navegación

MapFlip fue creado para usuarios de Android que reciben con frecuencia enlaces de Apple Maps de usuarios de iPhone."""
    },
    'it-IT': {
        'title': 'MapFlip - Apple a Google',
        'shortDescription': 'Reindirizza link Apple Maps su Google Maps. Convertitore mappe veloce.',
        'fullDescription': """I tuoi amici ti inviano link di Apple Maps – ma tu usi Google Maps su Android?

MapFlip è il convertitore definitivo di Apple Maps, reindirizzamento veloce e convertitore di link per Android. Configuralo una volta e tutto funzionerà automaticamente: ogni link, URL o posizione di Apple Maps si apre direttamente in Google Maps. Senza copiare, senza incollare, senza passare dal browser.

Che tu abbia bisogno di una soluzione Apple Maps per Android, di un convertitore da Apple a Google o di una conversione automatica di mappe, MapFlip gestisce tutti gli URL in background. Apri i link di Apple Maps direttamente nella tua app di navigazione preferita.

🔧 Come funziona:
1. Apri MapFlip
2. Tocca "Apri impostazioni"
3. Attiva l'inoltro dei link per maps.apple.com
4. Fatto! I link di Apple Maps ora si aprono direttamente in Google Maps.

✨ Funzionalità e Privacy:
• 🚗 Navigazione passo-passo e Android Auto – Avvia subito l'itinerario nativo in Google Maps
• 🧭 Scegli la tua app di navigazione – Apri le destinazioni in Google Maps, Waze, Organic Maps, OsmAnd o selettore di sistema
• 🔄 Reindirizzamento automatico – Convertitore istantaneo di link e mappe sul dispositivo
• 🔒 100% Offline e Privato – Zero permessi Internet (nessun android.permission.INTERNET), nessun tracciamento, nessuna pubblicità
• ⏸️ Modalità Pausa e Tile Impostazioni Rapide – Sospendi il reindirizzamento in qualsiasi momento dalla tendina delle notifiche
• 💬 Compatibilità universale – Funziona perfettamente con WhatsApp, Telegram, Signal, SMS, Slack ed e-mail
• 👻 Funzionamento invisibile in background – Nessuna schermata extra, nessun consumo di batteria

🗺️ Formati di link e servizi supportati:
• URL e ricerche di Apple Maps (maps.apple.com)
• OpenStreetMap (osm.org), Bing Maps e Yandex Maps
• Coordinate GPS e posizioni condivise
• Indirizzi, segnaposto e percorsi di navigazione

MapFlip è stato creato per gli utenti Android che ricevono regolarmente link di Apple Maps da utenti iPhone."""
    },
    'ja-JP': {
        'title': 'MapFlip – Apple Maps変換',
        'shortDescription': 'Apple MapsリンクをGoogle Mapsへ自動転送。高速マップ変換ツール。',
        'fullDescription': """友達からApple Mapsのリンクが送られてくるけれど、普段はGoogle Mapsを使っていませんか？

MapFlipは、Android向け究極のApple Maps変換＆リンク自動転送ツールです。一度設定すれば、あとはすべて自動でGoogle Mapsへリダイレクト。すべてのApple Mapsリンク、URL、共有された位置情報がGoogle Mapsで直接開きます。コピー＆ペーストも、ブラウザを経由する手間も一切不要です。

AndroidでApple Mapsを開く方法をお探しの方、AppleからGoogleへの変換や自動マップコンバーターが必要な方に最適。MapFlipがバックグラウンドで高速処理します。お気に入りのナビアプリで直接開くことができます。

🔧 使い方：
1. MapFlipを開く
2. 「設定を開く」をタップ
3. maps.apple.com の対応リンク転送を有効化
4. 完了！これ以降、Apple Mapsリンクは自動でGoogle Mapsで開きます。

✨ 特徴とプライバシー保護：
• 🚗 ターンバイターン案内＆Android Auto – Google Mapsのネイティブルート案内を即座に開始
• 🧭 ナビアプリを自由に選択 – Google Maps、Waze、Organic Maps、OsmAnd、またはシステム選択で開く
• 🔄 自動リダイレクト – 端末内で瞬時にリンク＆マップを自動変換
• 🔒 100%オフライン＆プライバシー保護 – インターネット権限ゼロ（android.permission.INTERNETなし）、トラッキングなし、広告なし
• ⏸️ 一時停止モード＆クイック設定タイル – 通知パネルからワンタップでいつでも転送を一時停止可能
• 💬 幅広いアプリに対応 – LINE、WhatsApp、Telegram、Signal、SMS、Slack、メール等で動作
• 👻 目立たず動作するバックグラウンド設計 – 余計な画面表示なし、バッテリー消費ゼロ

🗺️ 対応リンク形式＆サービス：
• Apple Maps の URL および検索クエリ (maps.apple.com)
• OpenStreetMap (osm.org)、Bing Maps、Yandex Maps
• GPS座標および共有された位置情報
• 住所、ピン、ナビゲーションルート

MapFlipは、iPhoneユーザーから日常的にApple Mapsリンクを受け取るAndroidユーザーのために開発されました。"""
    },
    'nl-NL': {
        'title': 'MapFlip - Apple naar Google',
        'shortDescription': 'Stuur Apple Maps-links door naar Google Maps. Snelle kaartconverter.',
        'fullDescription': """Sturen je vrienden Apple Maps-links – maar gebruik jij Google Maps op Android?

MapFlip is de ultieme Apple Maps-converter, snelle Apple Maps-omleiding en linkconverter voor Android. Stel het eenmalig in en alles werkt automatisch: elke Apple Maps-link, URL of locatie opent direct in Google Maps. Niet meer kopiëren, plakken of omwegen via de browser.

Of je nu een Apple Maps voor Android-oplossing, een Apple naar Google-converter of een automatische kaartconverter zoekt, MapFlip regelt het op de achtergrond. Open Apple Maps-links direct in je favoriete navigatie-app.

🔧 Zo werkt het:
1. Open MapFlip
2. Tik op "Instellingen openen"
3. Schakel link-doorsturing in voor maps.apple.com
4. Klaar! Apple Maps-links openen nu direct in Google Maps.

✨ Functies & Privacy:
• 🚗 Turn-by-turn navigatie & Android Auto – Start direct de routebegeleiding in Google Maps
• 🧭 Kies je navigatie-app – Open bestemmingen in Google Maps, Waze, Organic Maps, OsmAnd of systeemkiezer
• 🔄 Automatische omleiding – Snelle link- en kaartconverter op je toestel
• 🔒 100% offline & privé – Nul internetrechten (geen android.permission.INTERNET), geen tracking, geen advertenties
• ⏸️ Pauzemodus & Snelle instellingen-tegel – Onderbreek de omleiding direct vanuit het meldingenpaneel
• 💬 Universele app-compatibiliteit – Werkt probleemloos met WhatsApp, Telegram, Signal, SMS, Slack en e-mail
• 👻 Onzichtbare werking op achtergrond – Geen extra schermen, geen batterijverbruik

🗺️ Ondersteunde formaten & diensten:
• Apple Maps URL's & zoekopdrachten (maps.apple.com)
• OpenStreetMap (osm.org), Bing Maps & Yandex Maps
• GPS-coördinaten & gedeelde locaties
• Adressen, markeringen & navigatieroutes

MapFlip is gebouwd voor Android-gebruikers die regelmatig Apple Maps-links ontvangen van iPhone-gebruikers."""
    },
    'no-NO': {
        'title': 'MapFlip - Apple til Google',
        'shortDescription': 'Omdiriger Apple Maps-lenker til Google Maps. Rask kartkonverterer.',
        'fullDescription': """Sender vennene dine Apple Maps-lenker – men du bruker Google Maps på Android?

MapFlip er den ultimate Apple Maps-konverteren, raske Apple Maps-omdirigeringen og lenkekonverteren for Android. Konfigurer det én gang, og alt skjer automatisk: Hver Apple Maps-lenke, URL eller plassering åpnes direkte i Google Maps. Ingen kopiering, ingen liming, ingen omveier via nettleseren.

Enten du trenger Apple Maps for Android, en Apple til Google-konverter eller en automatisk Apple til Google Maps-konverter, håndterer MapFlip alle lenker i bakgrunnen. Åpne Apple Maps-lenker direkte i din favoritt-navigasjonsapp.

🔧 Slik fungerer det:
1. Åpne MapFlip
2. Trykk på "Åpne innstillinger"
3. Aktiver koblingsviderekobling for maps.apple.com
4. Ferdig! Apple Maps-lenker åpnes nå direkte i Google Maps.

✨ Funksjoner & Personvern:
• 🚗 Turn-by-turn navigasjon & Android Auto – Starter umiddelbart veibeskrivelser i Google Maps
• 🧭 Velg din navigasjonsapp – Åpne destinasjoner i Google Maps, Waze, Organic Maps, OsmAnd eller systemvelger
• 🔄 Automatisk omdirigering – Rask lenke- og kartkonvertering direkte på enheten
• 🔒 100% offline & privat – Null internetttillatelser (no android.permission.INTERNET), ingen sporing, ingen reklame
• ⏸️ Pausemodus & Hurtiginnstillinger-flis – Sett omdirigering på pause fra varselpanelet
• 💬 Universell app-kompatibilitet – Fungerer sømløst med WhatsApp, Telegram, Signal, SMS, Slack og e-post
• 👻 Usynlig bakgrunnsdrift – Ingen unødvendige skjermer, null batteriforbruk

🗺️ Støttede lenkeformater & tjenester:
• Apple Maps URL-er & søk (maps.apple.com)
• OpenStreetMap (osm.org), Bing Maps & Yandex Maps
• GPS-koordinater & delte plasseringer
• Adresser, stedsmarkører & navigasjonsruter

MapFlip er bygget for Android-brukere som regelmessig mottar Apple Maps-lenker fra iPhone-brukere."""
    },
    'pl-PL': {
        'title': 'MapFlip - Apple na Google',
        'shortDescription': 'Przekieruj linki Apple Maps do Google Maps. Szybki konwerter map.',
        'fullDescription': """Znajomi przesyłają Ci linki z Apple Maps – ale na Androidzie używasz Google Maps?

MapFlip to niezawodny konwerter Apple Maps, szybkie przekierowanie i konwerter linków dla Androida. Skonfiguruj raz, a wszystko działa automatycznie: każdy link Apple Maps, URL lub lokalizacja otwiera się bezpośrednio w Google Maps. Bez kopiowania, wklejania i otwierania przeglądarki.

Niezależnie od tego, czy szukasz Apple Maps dla Androida, konwertera Apple na Google czy automatycznego przekierowywania map, MapFlip obsługuje wszystko w tle. Otwieraj linki Apple Maps bezpośrednio w ulubionej aplikacji nawigacyjnej.

🔧 Jak to działa:
1. Otwórz MapFlip
2. Stuknij "Otwórz ustawienia"
3. Włącz przekierowywanie linków dla maps.apple.com
4. Gotowe! Linki Apple Maps otwierają się teraz bezpośrednio w Google Maps.

✨ Funkcje i prywatność:
• 🚗 Nawigacja zakręt po zakręcie & Android Auto – Natychmiast uruchamia wyznaczanie trasy w Google Maps
• 🧭 Wybierz aplikację nawigacyjną – Otwieraj cele w Google Maps, Waze, Organic Maps, OsmAnd lub menu systemowym
• 🔄 Automatyczne przekierowanie – Błyskawiczny konwerter linków i map na urządzeniu
• 🔒 100% offline i prywatność – Zero uprawnień internetowych (brak android.permission.INTERNET), brak śledzenia, brak reklam
• ⏸️ Tryb pauzy & kafelek szybkich ustawień – Wstrzymaj przekierowanie w dowolnym momencie z paska powiadomień
• 💬 Uniwersalna kompatybilność – Działa bez problemu z WhatsApp, Telegram, Signal, SMS, Slack i e-mail
• 👻 Niewidoczne działanie w tle – Brak zbędnych ekranów, zerowe zużycie baterii

🗺️ Obsługiwane formaty linków i usługi:
• Adresy URL i wyszukiwania Apple Maps (maps.apple.com)
• OpenStreetMap (osm.org), Bing Maps i Yandex Maps
• Współrzędne GPS i udostępnione lokalizacje
• Adresy, znaczniki miejsc i trasy nawigacyjne

MapFlip powstał z myślą o użytkownikach Androida, którzy regularnie otrzymują linki Apple Maps od użytkowników iPhone'ów."""
    },
    'pt-BR': {
        'title': 'MapFlip - Apple para Google',
        'shortDescription': 'Redirecione links do Apple Maps para o Google Maps. Conversor de mapas.',
        'fullDescription': """Seus amigos enviam links do Apple Maps – mas você usa o Google Maps no Android?

O MapFlip é o conversor definitivo do Apple Maps, redirecionamento rápido e conversor de links para Android. Configure uma única vez e tudo funcionará automaticamente: cada link, URL ou localização do Apple Maps será aberto direto no Google Maps. Sem copiar, sem colar, sem desvios no navegador.

Seja para ter o Apple Maps no Android, um conversor de Apple para Google ou uma conversão automática de mapas, o MapFlip processa tudo em segundo plano. Abra links do Apple Maps direto no seu app de navegação favorito.

🔧 Como funciona:
1. Abra o MapFlip
2. Toque em "Abrir Configurações"
3. Ative o encaminhamento de links para maps.apple.com
4. Pronto! Links do Apple Maps agora abrem direto no Google Maps.

✨ Recursos e Privacidade:
• 🚗 Navegação passo a passo & Android Auto – Inicia instantaneamente as rotas nativas no Google Maps
• 🧭 Escolha seu app de navegação – Abra destinos no Google Maps, Waze, Organic Maps, OsmAnd ou seletor do sistema
• 🔄 Redirecionamento automático – Conversor instantâneo de links e mapas direto no dispositivo
• 🔒 100% Offline e Seguro – Zero permissões de internet (sem android.permission.INTERNET), sem rastreamento, sem anúncios
• ⏸️ Modo de Pausa e Bloco de Configurações Rápidas – Pause o redirecionamento quando quiser no painel de notificações
• 💬 Compatibilidade universal – Funciona com WhatsApp, Telegram, Signal, SMS, Slack e e-mails
• 👻 Execução invisível em segundo plano – Sem telas extras, sem consumo de bateria

🗺️ Formatos de links e serviços suportados:
• URLs e buscas do Apple Maps (maps.apple.com)
• OpenStreetMap (osm.org), Bing Maps e Yandex Maps
• Coordenadas GPS e localizações compartilhadas
• Endereços, marcadores de lugares e rotas de navegação

O MapFlip foi criado para usuários de Android que recebem frequentemente links do Apple Maps de usuários de iPhone."""
    },
    'pt-PT': {
        'title': 'MapFlip - Apple para Google',
        'shortDescription': 'Redirecione links do Apple Maps para o Google Maps. Conversor de mapas.',
        'fullDescription': """Os seus amigos enviam links do Apple Maps – mas usa o Google Maps no Android?

O MapFlip é o conversor definitivo do Apple Maps, redirecionamento rápido e conversor de links para Android. Configure uma única vez e tudo funcionará automaticamente: cada link, URL ou localização do Apple Maps abre diretamente no Google Maps. Sem copiar, sem colar, sem desvios pelo navegador.

Quer precise de uma solução Apple Maps para Android, de um conversor Apple para Google ou de um conversor automático de mapas, o MapFlip processa tudo em segundo plano. Abra links do Apple Maps diretamente na sua aplicação de navegação favorita.

🔧 Como funciona:
1. Abra o MapFlip
2. Toque em "Abrir Definições"
3. Ative o reencaminhamento de links para maps.apple.com
4. Concluído! Os links do Apple Maps agora abrem diretamente no Google Maps.

✨ Funcionalidades e Privacidade:
• 🚗 Navegação curva a curva & Android Auto – Inicia instantaneamente as rotas nativas no Google Maps
• 🧭 Escolha a sua app de navegação – Abra destinos no Google Maps, Waze, Organic Maps, OsmAnd ou seletor de sistema
• 🔄 Redirecionamento automático – Conversor instantâneo de links e mapas no próprio dispositivo
• 🔒 100% Offline e Privado – Zero permissões de internet (sem android.permission.INTERNET), sem rastreio, sem anúncios
• ⏸️ Modo de Pausa & Mosaico de Definições Rápidas – Pause o redirecionamento a qualquer momento no painel de notificações
• 💬 Compatibilidade universal – Funciona com WhatsApp, Telegram, Signal, SMS, Slack e e-mails
• 👻 Execução invisível em segundo plano – Sem ecrãs adicionais, sem consumo de bateria

🗺️ Formatos de links e serviços suportados:
• URLs e pesquisas do Apple Maps (maps.apple.com)
• OpenStreetMap (osm.org), Bing Maps e Yandex Maps
• Coordenadas GPS e localizações partilhadas
• Moradas, marcadores de locais e rotas de navegação

O MapFlip foi desenvolvido para utilizadores de Android que recebem regularmente links do Apple Maps de utilizadores de iPhone."""
    },
    'sv-SE': {
        'title': 'MapFlip - Apple till Google',
        'shortDescription': 'Omdirigera Apple Maps-länkar till Google Maps. Snabb kartkonverterare.',
        'fullDescription': """Skickar dina vänner Apple Maps-länkar – men du använder Google Maps på Android?

MapFlip är den ultimata Apple Maps-konverteraren, snabba Apple Maps-omdirigeringen och länkkonverteraren för Android. Konfigurera det en gång så sker allt automatiskt som en Google Maps-omdirigering: Varje Apple Maps-länk, URL eller plats öppnas direkt i Google Maps. Ingen kopiering, ingen klistring, inga omvägar via webbläsaren.

Oavsett om du behöver Apple Maps för Android, en Apple till Google-konverterare eller en automatisk kartkonverterare hanterar MapFlip alla länkar i bakgrunden. Öppna Apple Maps-länkar direkt i din favoritnavigeringsapp.

🔧 Så fungerar det:
1. Öppna MapFlip
2. Tryck på "Öppna inställningar"
3. Aktivera länkvidarebefordran för maps.apple.com
4. Klart! Apple Maps-länkar öppnas nu direkt i Google Maps.

✨ Funktioner & Integritet:
• 🚗 Sväng-för-sväng-navigering & Android Auto – Startar omedelbart ruttvägledning i Google Maps
• 🧭 Välj din navigationsapp – Öppna destinationer i Google Maps, Waze, Organic Maps, OsmAnd eller systemväljaren
• 🔄 Automatisk omdirigering – Blixtsnabb länk- och kartkonverterare direkt på enheten
• 🔒 100% offline & privat – Noll internetbehörigheter (inget android.permission.INTERNET), ingen spårning, ingen reklam
• ⏸️ Pausläge & Snabbinställningspanel – Pausa omdirigeringen när som helst från aviseringsfältet
• 💬 Universell appkompatibilitet – Fungerar sömlöst med WhatsApp, Telegram, Signal, SMS, Slack och e-post
• 👻 Osynlig bakgrundsdrift – Inga extra skärmar, noll batteriförbrukning

🗺️ Länkformat & tjänster som stöds:
• Apple Maps URL:er & sökningar (maps.apple.com)
• OpenStreetMap (osm.org), Bing Maps & Yandex Maps
• GPS-koordinater & delade platser
• Adresser, platsmarkörer & navigeringsrutter

MapFlip skapades för Android-användare som regelbundet får Apple Maps-länkar från iPhone-användare."""
    },
    'tr-TR': {
        'title': "MapFlip - Apple'dan Google",
        'shortDescription': "Apple Maps linklerini Google Maps'e yönlendirin. Hızlı harita dönüştürücü.",
        'fullDescription': """Arkadaşlarınız size Apple Maps bağlantıları gönderiyor ama siz Android'de Google Maps mi kullanıyorsunuz?

MapFlip, Android için en gelişmiş Apple Maps dönüştürücüsü, hızlı Apple harita yönlendirmesi ve bağlantı dönüştürme aracıdır. Bir kez kurun, her şey otomatik olarak bir Google Maps yönlendirmesi olarak çalışır: Her Apple Maps bağlantısı, URL veya konum doğrudan Google Maps'te açılır. Kopyalama yok, yapıştırma yok, tarayıcıda bekleme yok.

İster Android için Apple Maps çözümü, ister Apple'dan Google'a dönüştürücü veya otomatik harita yönlendirmesi arıyor olun, MapFlip tüm bağlantıları arka planda sorunsuzca işler. Apple Maps bağlantılarını doğrudan favori navigasyon uygulamanızda açın.

🔧 Nasıl çalışır:
1. MapFlip'i açın
2. "Ayarları Aç" düğmesine dokunun
3. maps.apple.com için bağlantı yönlendirmesini etkinleştirin
4. Tamamlandı! Apple Maps bağlantıları artık doğrudan Google Maps'te açılır.

✨ Özellikler ve Gizlilik:
• 🚗 Adım Adım Navigasyon & Android Auto – Google Maps'te yerel rota rehberliğini anında başlatır
• 🧭 Navigasyon Uygulamanızı Seçin – Hedefleri Google Maps, Waze, Organic Maps, OsmAnd veya sistem seçicide açın
• 🔄 Otomatik Yönlendirme – Cihaz üzerinde anında bağlantı ve harita dönüştürücü
• 🔒 %100 Çevrimdışı ve Gizli – Sıfır internet izni (android.permission.INTERNET yok), takip yok, reklam yok
• ⏸️ Duraklatma Modu & Hızlı Ayarlar Kutucuğu – Bildirim panelinden yönlendirmeyi istediğiniz zaman duraklatın
• 💬 Evrensel Uygulama Uyumluluğu – WhatsApp, Telegram, Signal, SMS, Slack ve e-postalarla kusursuz çalışır
• 👻 Arka Planda Görünmez Çalışma – Ekstra ekran yok, pil tüketimi yok

🗺️ Desteklenen bağlantı biçimleri ve servisler:
• Apple Maps URL'leri ve aramaları (maps.apple.com)
• OpenStreetMap (osm.org), Bing Maps ve Yandex Maps
• GPS koordinatları ve paylaşılan konumlar
• Adresler, yer işaretleri ve navigasyon rotaları

MapFlip, iPhone kullanıcılarından düzenli olarak Apple Maps bağlantıları alan Android kullanıcıları için geliştirildi."""
    },
    'ko-KR': {
        'title': 'MapFlip - Apple to Google',
        'shortDescription': 'Apple Maps 링크를 Google Maps로 리디렉션. 빠른 지도 링크 변환기.',
        'fullDescription': """친구들이 Apple Maps 링크를 보내지만, Android에서는 Google Maps를 사용하시나요?

MapFlip은 Android를 위한 최고의 Apple Maps 변환기이자 빠른 링크 리디렉션 도구입니다. 한 번만 설정하면 모든 과정이 자동으로 작동합니다. 모든 Apple Maps 링크, URL, 공유 위치가 Google Maps에서 바로 열립니다. 복사하고 붙여넣거나 브라우저를 거칠 필요가 없습니다.

Android용 Apple Maps 솔루션, Apple to Google 변환기 또는 자동 지도 리디렉션이 필요할 때 MapFlip이 백그라운드에서 모든 URL을 즉시 처리합니다. 선호하는 내비게이션 앱에서 Apple Maps 링크를 바로 여세요.

🔧 사용 방법:
1. MapFlip 열기
2. "설정 열기" 탭
3. maps.apple.com 지원 링크 전달 활성화
4. 완료! 이제 Apple Maps 링크가 Google Maps에서 자동으로 열립니다.

✨ 주요 기능 및 개인정보 보호:
• 🚗 턴바이턴 내비게이션 & Android Auto – Google Maps에서 네이티브 경로 안내 즉시 시작
• 🧭 원하는 내비게이션 앱 선택 – Google Maps, Waze, Organic Maps, OsmAnd 또는 시스템 선택기 지원
• 🔄 자동 리디렉션 – 기기 내 즉각적인 링크 및 지도 변환
• 🔒 100% 오프라인 & 프라이버시 보장 – 인터넷 권한 없음 (0 Permissions), 추적 없음, 광고 없음
• ⏸️ 일시 중지 모드 & 빠른 설정 타일 – 알림창에서 언제든지 리디렉션을 일시 중지
• 💬 폭넓은 앱 호환성 – WhatsApp, Telegram, Signal, SMS, Slack, 이메일과 완벽 호환
• 👻 보이지 않는 백그라운드 작동 – 불필요한 화면 없음, 배터리 소모 없음

🗺️ 지원되는 링크 형식 및 지도 서비스:
• Apple Maps URL 및 검색 쿼리 (maps.apple.com)
• OpenStreetMap (osm.org), Bing Maps 및 Yandex Maps
• GPS 좌표 및 공유된 위치
• 주소, 장소 마커 및 내비게이션 경로

MapFlip은 iPhone 사용자로부터 Apple Maps 링크를 자주 받는 Android 사용자를 위해 만들어졌습니다."""
    },
    'zh-CN': {
        'title': 'MapFlip - Apple to Google',
        'shortDescription': '将 Apple Maps 链接重定向至 Google Maps。极速地图转换器。',
        'fullDescription': """朋友发送了 Apple Maps 地图链接，但你在安卓上习惯使用 Google Maps？

MapFlip 是专为 Android 打造的 Apple Maps 终极转换器、极速地图重定向与链接转换神器。只需简单配置一次，所有 Apple Maps 链接、网址和共享位置都将无缝直接在 Google Maps 中打开。无需手动复制粘贴，无需经过浏览器跳转。

无论你需要 Android 上的 Apple Maps 解决方案、Apple to Google 转换器，还是全自动地图重定向，MapFlip 都能在后台疾速处理。在你喜爱的导航应用中直接开启 Apple Maps 链接。

🔧 使用方法：
1. 打开 MapFlip
2. 点击“打开设置”
3. 为 maps.apple.com 开启支持的链接转发
4. 完成！从此 Apple Maps 链接将自动在 Google Maps 中打开。

✨ 核心特色与隐私保证：
• 🚗 实时导航与 Android Auto – 一键立即启动 Google Maps 原生路线指引与导航
• 🧭 自由选择目标导航应用 – 支持在 Google Maps、Waze、Organic Maps、OsmAnd 或系统选择器中打开
• 🔄 全自动即时重定向 – 零延迟本地链接与地图转换
• 🔒 100% 离线与隐私保护 – 无网络访问权限（0 权限），无数据追踪，无广告
• ⏸️ 快捷暂停模式 – 在通知栏下拉快捷开关中随时暂停与恢复
• 💬 广泛兼容各类通讯软件 – 完美支持微信、WhatsApp、Telegram、Signal、短信与邮件
• 👻 极简无感后台运行 – 零冗余界面，不耗电

🗺️ 支持的链接格式与地图服务：
• Apple Maps 网址与搜索关键词 (maps.apple.com)
• OpenStreetMap (osm.org)、Bing Maps 与 Yandex Maps
• GPS 经纬度坐标与分享定位
• 具体地址、地点标注与导航路线

MapFlip 专为经常收到 iPhone 用户发送 Apple Maps 链接的 Android 用户量身打造。"""
    },
    'zh-TW': {
        'title': 'MapFlip - Apple to Google',
        'shortDescription': '將 Apple Maps 連結重新導向至 Google Maps。極速地圖轉換器。',
        'fullDescription': """朋友傳送了 Apple Maps 地圖連結，但你在 Android 上習慣使用 Google Maps？

MapFlip 是專為 Android 打造的 Apple Maps 終極轉換器、極速地圖重新導向與連結轉換神器。只需簡單設定一次，所有 Apple Maps 連結、網址和共享位置都將無縫直接在 Google Maps 中開啟。無需手動複製貼上，無需經過瀏覽器跳轉。

無論你需要 Android 上的 Apple Maps 解決方案、Apple to Google 轉換器，還是全自動地圖重新導向，MapFlip 都能在背景疾速處理。在你喜愛的導航應用程式中直接開啟 Apple Maps 連結。

🔧 使用方法：
1. 開啟 MapFlip
2. 點擊「開啟設定」
3. 為 maps.apple.com 開啟支援的連結轉發
4. 完成！從此 Apple Maps 連結將自動在 Google Maps 中開啟。

✨ 核心特色與隱私保證：
• 🚗 即時導航與 Android Auto – 一鍵立即啟動 Google Maps 原生路線指引與導航
• 🧭 自由選擇目標導航應用 – 支援在 Google Maps、Waze、Organic Maps、OsmAnd 或系統選擇器中開啟
• 🔄 全自動即時重新導向 – 零延遲裝置端連結與地圖轉換
• 🔒 100% 離線與隱私保護 – 無網路存取權限（0 權限），無資料追蹤，無廣告
• ⏸️ 快捷暫停模式 – 在通知列下拉快捷開關中隨時暫停與恢復
• 💬 廣泛相容各類通訊軟體 – 完美支援 LINE、WhatsApp、Telegram、Signal、簡訊與電子郵件
• 👻 極簡無感背景運作 – 零多餘介面，不耗電

🗺️ 支援的連結格式與地圖服務：
• Apple Maps 網址與搜尋關鍵字 (maps.apple.com)
• OpenStreetMap (osm.org)、Bing Maps 與 Yandex Maps
• GPS 經緯度座標與分享定位
• 具體地址、地點標註與導航路線

MapFlip 專為經常收到 iPhone 使用者傳送 Apple Maps 連結的 Android 使用者量身打造。"""
    },
    'ar': {
        'title': 'MapFlip - Apple to Google',
        'shortDescription': 'إعادة توجيه روابط Apple Maps إلى Google Maps. محول خرائط سريع.',
        'fullDescription': """هل يرسل لك أصدقاؤك روابط Apple Maps – بينما تستخدم Google Maps على Android؟

MapFlip هو المحول النهائي لروابط Apple Maps، وإعادة التوجيه السريعة ومحول الروابط لنظام Android. قم بإعداده مرة واحدة، وسيعمل كل شيء تلقائيًا كإعادة توجيه سلسة إلى Google Maps: يفتح كل رابط أو عنوان URL أو موقع من Apple Maps مباشرة في Google Maps. بدون نسخ، بدون لصق، وبدون فتح المتصفح.

سواء كنت تبحث عن حل لـ Apple Maps على Android، أو محول من Apple إلى Google، أو تحويل خرائط تلقائي، يتعامل MapFlip مع جميع الروابط في الخلفية. افتح روابط Apple Maps مباشرة في تطبيق الملاحة المفضل لديك.

🔧 طريقة الاستخدام:
1. افتح MapFlip
2. اضغط على "فتح الإعدادات"
3. قم بتفعيل إعادة توجيه الروابط لـ maps.apple.com
4. تم! تفتح روابط Apple Maps الآن مباشرة في Google Maps.

✨ الميزات والخصوصية:
• 🚗 ملاحة تفصيلية و Android Auto – يبدأ التوجيه الأصلي فورًا في Google Maps
• 🧭 اختر تطبيق الملاحة المفضل لديك – افتح الوجهات في Google Maps أو Waze أو Organic Maps أو OsmAnd أو محدد النظام
• 🔄 إعادة توجيه تلقائية – تحويل فوري للروابط والخرائط على جهازك
• 🔒 100% دون اتصال وخاص تمامًا – صفر أذونات إنترنت (بدون android.permission.INTERNET)، بدون تتبع، بدون إعلانات
• ⏸️ وضع الإيقاف المؤقت ومربع الإعدادات السريعة – أوقف إعادة التوجيه مؤقتًا في أي وقت من لوحة الإشعارات
• 💬 توافق شامل مع التطبيقات – يعمل بسلاسة مع WhatsApp و Telegram و Signal و SMS و Slack والبريد الإلكتروني
• 👻 تشغيل غير مرئي في الخلفية – بدون شاشات إضافية وبدون استهلاك للبطارية

🗺️ صيغ الروابط والخدمات المدعومة:
• روابط وبحث Apple Maps (maps.apple.com)
• OpenStreetMap (osm.org) و Bing Maps و Yandex Maps
• إحداثيات GPS والمواقع المشتركة
• العناوين وعلامات الأماكن ومسارات الملاحة

تم تصميم MapFlip لمستخدمي Android الذين يتلقون روابط Apple Maps بانتظام من مستخدمي iPhone."""
    },
    'ru-RU': {
        'title': 'MapFlip - Apple to Google',
        'shortDescription': 'Перенаправляйте ссылки Apple Maps в Google Maps. Быстрый конвертер карт.',
        'fullDescription': """Друзья присылают вам ссылки Apple Maps – а вы используете Google Maps на Android?

MapFlip — это лучший конвертер Apple Maps, быстрое перенаправление и конвертер ссылок для Android. Настройте один раз, и всё будет работать автоматически: каждая ссылка, URL или локация Apple Maps открывается прямо в Google Maps. Без копирования, без вставки и без лишних переходов через браузер.

Ищете решение для Apple Maps на Android, конвертер из Apple в Google или автоматическое перенаправление карт? MapFlip мгновенно обрабатывает все ссылки в фоновом режиме. Открывайте ссылки Apple Maps прямо в любимом навигаторе.

🔧 Как это работает:
1. Откройте MapFlip
2. Нажмите "Открыть настройки"
3. Включите пересылку ссылок для maps.apple.com
4. Готово! Ссылки Apple Maps теперь открываются прямо в Google Maps.

✨ Возможности и конфиденциальность:
• 🚗 Пошаговая навигация и Android Auto – Мгновенный запуск маршрута в Google Maps
• 🧭 Выбирайте навигационное приложение – Открывайте цели в Google Maps, Waze, Organic Maps, OsmAnd или через системный выбор
• 🔄 Автоматическое перенаправление – Мгновенный конвертер ссылок и карт на устройстве
• 🔒 100% офлайн и полная приватность – Ноль интернет-разрешений (без android.permission.INTERNET), никакого трекинга, никакой рекламы
• ⏸️ Режим паузы и плитка быстрых настроек – Приостанавливайте перенаправление в любой момент из шторки уведомлений
• 💬 Универсальная совместимость – Идеально работает с Telegram, WhatsApp, Signal, SMS, Slack и эл. почтой
• 👻 Незаметная работа в фоне – Никаких лишних экранов, нулевой расход батареи

🗺️ Поддерживаемые форматы ссылок и сервисы:
• Ссылки и поисковые запросы Apple Maps (maps.apple.com)
• OpenStreetMap (osm.org), Bing Maps и Yandex Maps
• GPS-координаты и геопозиции
• Адреса, метки мест и навигационные маршруты

MapFlip создан для пользователей Android, которые регулярно получают ссылки Apple Maps от владельцев iPhone."""
    },
    'id': {
        'title': 'MapFlip - Apple to Google',
        'shortDescription': 'Alihkan tautan Apple Maps ke Google Maps. Konverter peta cepat.',
        'fullDescription': """Teman Anda mengirim tautan Apple Maps – tetapi Anda menggunakan Google Maps di Android?

MapFlip adalah konverter Apple Maps terbaik, pengalihan cepat dan konverter tautan untuk Android. Cukup atur sekali, dan semua tautan, URL, atau lokasi Apple Maps akan terbuka secara otomatis di Google Maps. Tanpa salin-tempel dan tanpa membuka browser.

Apakah Anda memerlukan solusi Apple Maps untuk Android, konverter Apple ke Google, atau pengalihan peta otomatis, MapFlip menangani semua URL di latar belakang. Buka tautan Apple Maps langsung di aplikasi navigasi favorit Anda.

🔧 Cara Penggunaan:
1. Buka MapFlip
2. Ketuk "Buka Pengaturan"
3. Aktifkan penerusan tautan untuk maps.apple.com
4. Selesai! Tautan Apple Maps sekarang langsung terbuka di Google Maps.

✨ Fitur & Privasi:
• 🚗 Navigasi Belokan demi Belokan & Android Auto – Langsung meluncurkan rute asli di Google Maps
• 🧭 Pilih Aplikasi Navigasi Anda – Buka tujuan di Google Maps, Waze, Organic Maps, OsmAnd, atau pemilih sistem
• 🔄 Pengalihan Otomatis – Konverter tautan dan peta instan di dalam perangkat
• 🔒 100% Offline & Privasi Terjaga – Tanpa izin internet (0 Permissions), tanpa pelacakan, tanpa iklan
• ⏸️ Mode Jeda & Ubin Pengaturan Cepat – Jeda kapan saja langsung dari panel notifikasi
• 💬 Kompatibel dengan Semua Aplikasi – WhatsApp, Telegram, Signal, SMS, Slack, dan email
• 👻 Berjalan Halus di Latar Belakang – Tanpa layar tambahan, hemat baterai tanpa gangguan

🗺️ Format tautan & layanan yang didukung:
• URL & pencarian Apple Maps (maps.apple.com)
• OpenStreetMap (osm.org), Bing Maps & Yandex Maps
• Koordinat GPS & lokasi bersama
• Alamat, penanda tempat & rute navigasi

MapFlip dibuat untuk pengguna Android yang sering menerima tautan Apple Maps dari pengguna iPhone."""
    },
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
