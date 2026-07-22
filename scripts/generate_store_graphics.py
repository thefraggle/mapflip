import os
import math
from PIL import Image, ImageDraw, ImageFont, ImageFilter

# Base directories
RAW_DIR = "docs/assets/raw"
OUTPUT_BASE = "docs/assets/playstore"

# Canvas Dimensions
SCREEN_W, SCREEN_H = 1080, 2400
FEATURE_W, FEATURE_H = 1024, 500

# Fonts
FONT_BOLD = "/System/Library/Fonts/Supplemental/Arial Bold.ttf"
FONT_REGULAR = "/System/Library/Fonts/Supplemental/Arial.ttf"

def get_font(path, size):
    try:
        return ImageFont.truetype(path, size)
    except Exception:
        return ImageFont.load_default()

def draw_gradient_background(w, h, color1, color2):
    """Generates a vertical gradient background image."""
    base = Image.new("RGBA", (w, h))
    draw = ImageDraw.Draw(base)
    r1, g1, b1 = color1
    r2, g2, b2 = color2
    for y in range(h):
        ratio = y / float(h)
        r = int(r1 + (r2 - r1) * ratio)
        g = int(g1 + (g2 - g1) * ratio)
        b = int(b1 + (b2 - b1) * ratio)
        draw.line([(0, y), (w, y)], fill=(r, g, b, 255))
    return base

def draw_rounded_rect(draw, coords, radius, fill, outline=None, width=1):
    """Draws a rounded rectangle."""
    draw.rounded_rectangle(coords, radius=radius, fill=fill, outline=outline, width=width)

def draw_vector_icon(draw, cx, cy, size, icon_type, color=(255, 255, 255)):
    """Draws crisp vector icons guaranteed to render on all systems without font dependencies."""
    w = max(4, int(size * 0.12))
    if icon_type == 'check':
        p1 = (cx - size * 0.32, cy - size * 0.05)
        p2 = (cx - size * 0.1, cy + size * 0.25)
        p3 = (cx + size * 0.32, cy - size * 0.25)
        draw.line([p1, p2, p3], fill=color, width=w, joint='round')
    elif icon_type == 'arrow':
        p1 = (cx - size * 0.32, cy)
        p2 = (cx + size * 0.32, cy)
        a1 = (cx + size * 0.1, cy - size * 0.2)
        a2 = (cx + size * 0.1, cy + size * 0.2)
        draw.line([p1, p2], fill=color, width=w)
        draw.line([a1, p2, a2], fill=color, width=w, joint='round')
    elif icon_type == 'star':
        points = []
        r_outer = size * 0.36
        r_inner = size * 0.16
        for i in range(10):
            r = r_outer if i % 2 == 0 else r_inner
            angle = i * math.pi / 5 - math.pi / 2
            points.append((cx + r * math.cos(angle), cy + r * math.sin(angle)))
        draw.polygon(points, fill=color)
    elif icon_type == 'zap':
        p = [
            (cx + size * 0.05, cy - size * 0.38),
            (cx - size * 0.25, cy + size * 0.02),
            (cx - size * 0.02, cy + size * 0.02),
            (cx - size * 0.08, cy + size * 0.38),
            (cx + size * 0.25, cy - size * 0.02),
            (cx + size * 0.02, cy - size * 0.02)
        ]
        draw.polygon(p, fill=color)

def draw_icon_badge(draw, x, y, size, bg_color, icon_type, symbol_color=(255, 255, 255)):
    """Draws a circular badge containing a vector icon."""
    draw.ellipse((x, y, x + size, y + size), fill=bg_color)
    cx = x + size // 2
    cy = y + size // 2
    draw_vector_icon(draw, cx, cy, int(size * 0.7), icon_type, color=symbol_color)

def add_drop_shadow(image, radius=20, offset=(0, 15), shadow_color=(0, 0, 0, 120)):
    """Creates a shadow behind an image."""
    shadow = Image.new("RGBA", (image.width + radius * 2, image.height + radius * 2), (0, 0, 0, 0))
    shadow_draw = ImageDraw.Draw(shadow)
    shadow_draw.rounded_rectangle(
        (radius, radius, radius + image.width, radius + image.height),
        radius=30,
        fill=shadow_color
    )
    shadow = shadow.filter(ImageFilter.GaussianBlur(radius))
    
    result = Image.new("RGBA", (shadow.width + abs(offset[0]), shadow.height + abs(offset[1])), (0, 0, 0, 0))
    result.paste(shadow, (max(0, offset[0]), max(0, offset[1])), shadow)
    result.paste(image, (radius, radius), image)
    return result

def create_screen(config, output_path):
    os.makedirs(os.path.dirname(output_path), exist_ok=True)
    
    # 1. Background Gradient (Deep Navy to Rich Slate/Indigo)
    bg = draw_gradient_background(SCREEN_W, SCREEN_H, (15, 23, 42), (49, 46, 129))
    draw = ImageDraw.Draw(bg)
    
    # Subtle background decorative glow circles
    glow = Image.new("RGBA", (600, 600), (0, 0, 0, 0))
    glow_draw = ImageDraw.Draw(glow)
    glow_draw.ellipse((0, 0, 600, 600), fill=(99, 102, 241, 40))
    glow = glow.filter(ImageFilter.GaussianBlur(80))
    bg.paste(glow, (-100, -100), glow)
    bg.paste(glow, (600, 1200), glow)
    
    # 2. Text Content Top Section
    category_font = get_font(FONT_BOLD, 28)
    headline_font = get_font(FONT_BOLD, 54)
    subtitle_font = get_font(FONT_REGULAR, 34)
    
    y_offset = 120
    
    # Category Pill
    cat_text = config['category'].upper()
    cat_bbox = category_font.getbbox(cat_text)
    cat_w = cat_bbox[2] - cat_bbox[0]
    cat_h = cat_bbox[3] - cat_bbox[1]
    
    pill_padding_x = 24
    pill_padding_y = 10
    pill_x = 80
    pill_w = cat_w + pill_padding_x * 2
    pill_h = cat_h + pill_padding_y * 2
    
    draw_rounded_rect(
        draw,
        (pill_x, y_offset, pill_x + pill_w, y_offset + pill_h),
        radius=14,
        fill=(99, 102, 241, 60),
        outline=(129, 140, 248, 120),
        width=2
    )
    draw.text((pill_x + pill_padding_x, y_offset + pill_padding_y - 2), cat_text, font=category_font, fill=(199, 210, 254))
    
    y_offset += pill_h + 30
    
    # Headline
    lines = config['headline'].split('\n')
    for line in lines:
        draw.text((80, y_offset), line, font=headline_font, fill=(255, 255, 255))
        y_offset += 68
        
    y_offset += 15
    
    # Subtitle
    draw.text((80, y_offset), config['subtitle'], font=subtitle_font, fill=(203, 213, 225))
    
    # 3. Image Frame Section
    raw_img_path = config.get('raw_image')
    if raw_img_path and os.path.exists(raw_img_path):
        raw_img = Image.open(raw_img_path).convert("RGBA")
        
        if config.get('is_chat'):
            # Crop and highlight chat snippet
            target_w = 920
            ratio = target_w / float(raw_img.width)
            target_h = int(raw_img.height * ratio)
            raw_img = raw_img.resize((target_w, target_h), Image.Resampling.LANCZOS)
            
            # Create a card frame for the chat
            framed = Image.new("RGBA", (target_w + 40, target_h + 40), (0, 0, 0, 0))
            f_draw = ImageDraw.Draw(framed)
            draw_rounded_rect(f_draw, (0, 0, target_w + 40, target_h + 40), radius=30, fill=(30, 41, 59, 240), outline=(71, 85, 105, 180), width=3)
            framed.paste(raw_img, (20, 20), raw_img)
            
            shadow_img = add_drop_shadow(framed, radius=30, offset=(0, 20))
            bg.paste(shadow_img, (60, 680), shadow_img)
            
            # Draw callout badge underneath
            badge_font = get_font(FONT_BOLD, 34)
            callout = Image.new("RGBA", (840, 140), (0, 0, 0, 0))
            c_draw = ImageDraw.Draw(callout)
            draw_rounded_rect(c_draw, (0, 0, 840, 140), radius=24, fill=(16, 185, 129, 230))
            
            # Vector icons on callout
            draw_vector_icon(c_draw, 50, 48, 30, 'check', color=(255, 255, 255))
            c_draw.text((80, 28), config['callout_text_1'], font=badge_font, fill=(255, 255, 255))
            
            draw_vector_icon(c_draw, 50, 96, 30, 'arrow', color=(236, 253, 245))
            c_draw.text((80, 78), config['callout_text_2'], font=subtitle_font, fill=(236, 253, 245))
            
            c_shadow = add_drop_shadow(callout, radius=20, offset=(0, 10))
            bg.paste(c_shadow, (100, 1350), c_shadow)
            
        else:
            # Full device screenshot mockup
            target_w = 880
            ratio = target_w / float(raw_img.width)
            target_h = int(raw_img.height * ratio)
            raw_img = raw_img.resize((target_w, target_h), Image.Resampling.LANCZOS)
            
            # Mask rounded corners
            mask = Image.new("L", (target_w, target_h), 0)
            mask_draw = ImageDraw.Draw(mask)
            mask_draw.rounded_rectangle((0, 0, target_w, target_h), radius=48, fill=255)
            
            framed = Image.new("RGBA", (target_w, target_h), (0, 0, 0, 0))
            framed.paste(raw_img, (0, 0), mask)
            
            # Add phone bezel outline
            bezel = Image.new("RGBA", (target_w + 16, target_h + 16), (0, 0, 0, 0))
            b_draw = ImageDraw.Draw(bezel)
            draw_rounded_rect(b_draw, (0, 0, target_w + 16, target_h + 16), radius=54, fill=(15, 23, 42, 255), outline=(100, 116, 139, 255), width=4)
            bezel.paste(framed, (8, 8), framed)
            
            shadow_img = add_drop_shadow(bezel, radius=35, offset=(0, 25))
            bg.paste(shadow_img, ((SCREEN_W - shadow_img.width) // 2, 580), shadow_img)
            
    elif config.get('is_features'):
        # Custom Feature Cards Grid (Screen 4)
        y_card = 600
        cards = config['feature_cards']
        
        card_title_font = get_font(FONT_BOLD, 42)
        card_desc_font = get_font(FONT_REGULAR, 30)
        
        for badge_color, icon_type, title, desc in cards:
            card = Image.new("RGBA", (920, 260), (0, 0, 0, 0))
            c_draw = ImageDraw.Draw(card)
            draw_rounded_rect(c_draw, (0, 0, 920, 260), radius=28, fill=(30, 41, 59, 230), outline=(71, 85, 105, 180), width=2)
            
            # Circular Vector Icon Badge
            draw_icon_badge(c_draw, 40, 45, 80, badge_color, icon_type)
            
            # Title & Desc
            c_draw.text((150, 45), title, font=card_title_font, fill=(255, 255, 255))
            c_draw.text((150, 105), desc, font=card_desc_font, fill=(148, 163, 184))
            
            c_shadow = add_drop_shadow(card, radius=20, offset=(0, 12))
            bg.paste(c_shadow, (80, y_card), c_shadow)
            y_card += 310

    bg.save(output_path, "PNG")
    print(f"Generated Promo Screen: {output_path}")

def create_feature_graphic(config, output_path):
    os.makedirs(os.path.dirname(output_path), exist_ok=True)
    
    # 1024x500 Gradient Canvas
    bg = draw_gradient_background(FEATURE_W, FEATURE_H, (15, 23, 42), (49, 46, 129))
    draw = ImageDraw.Draw(bg)
    
    # Ambient glows
    glow = Image.new("RGBA", (400, 400), (0, 0, 0, 0))
    glow_draw = ImageDraw.Draw(glow)
    glow_draw.ellipse((0, 0, 400, 400), fill=(99, 102, 241, 50))
    glow = glow.filter(ImageFilter.GaussianBlur(60))
    bg.paste(glow, (-50, -50), glow)
    bg.paste(glow, (600, 100), glow)
    
    title_font = get_font(FONT_BOLD, 72)
    sub_font = get_font(FONT_BOLD, 36)
    tagline_font = get_font(FONT_REGULAR, 28)
    
    # Left Text Block
    draw.text((70, 90), "MapFlip", font=title_font, fill=(255, 255, 255))
    
    # Draw vector arrow between Apple Maps and Google Maps
    draw.text((70, 185), "Apple Maps", font=sub_font, fill=(199, 210, 254))
    draw_vector_icon(draw, 320, 203, 30, 'arrow', color=(199, 210, 254))
    draw.text((360, 185), "Google Maps", font=sub_font, fill=(199, 210, 254))
    
    # Tagline Pill
    pill_text = config['tagline']
    pill_bbox = tagline_font.getbbox(pill_text)
    pill_w = pill_bbox[2] - pill_bbox[0] + 40
    
    draw_rounded_rect(draw, (70, 270, 70 + pill_w, 330), radius=16, fill=(16, 185, 129, 220))
    draw.text((90, 283), pill_text, font=tagline_font, fill=(255, 255, 255))
    
    # Right Image Preview
    raw_img_path = f"{RAW_DIR}/raw_mainscreen.png"
    if os.path.exists(raw_img_path):
        raw_img = Image.open(raw_img_path).convert("RGBA")
        target_h = 420
        ratio = target_h / float(raw_img.height)
        target_w = int(raw_img.width * ratio)
        raw_img = raw_img.resize((target_w, target_h), Image.Resampling.LANCZOS)
        
        mask = Image.new("L", (target_w, target_h), 0)
        mask_draw = ImageDraw.Draw(mask)
        mask_draw.rounded_rectangle((0, 0, target_w, target_h), radius=28, fill=255)
        
        framed = Image.new("RGBA", (target_w, target_h), (0, 0, 0, 0))
        framed.paste(raw_img, (0, 0), mask)
        
        shadow_img = add_drop_shadow(framed, radius=25, offset=(0, 15))
        bg.paste(shadow_img, (680, 50), shadow_img)

    bg.save(output_path, "PNG")
    print(f"Generated Feature Graphic: {output_path}")

LOCALES = {
    'de-DE': {
        'tagline': 'Automatisch. Unsichtbar. Datenschutzfreundlich.',
        'screens': [
            {
                'category': 'AUTOMATISCHER MAP-CONVERTER',
                'headline': 'Apple Maps Links in\nGoogle Maps öffnen',
                'subtitle': 'Automatisch. Unsichtbar. Ohne Umwege.',
                'raw_image': f"{RAW_DIR}/raw_mainscreen.png"
            },
            {
                'category': 'EINFACHE EINRICHTUNG',
                'headline': 'Einmal einrichten in\n3 einfachen Schritten',
                'subtitle': 'Ein Klick in den Android-Einstellungen genügt.',
                'raw_image': f"{RAW_DIR}/raw_settings.png"
            },
            {
                'category': 'SCHNELL & NAHTLOS',
                'headline': 'Link im Chat antippen –\nGoogle Maps öffnet sich!',
                'subtitle': 'Kein Kopieren, kein Einfügen, 100% automatisch.',
                'raw_image': f"{RAW_DIR}/raw_chat.png",
                'is_chat': True,
                'callout_text_1': 'Apple Maps Link angetippt',
                'callout_text_2': 'Öffnet sich direkt in Google Maps!'
            },
            {
                'category': 'DATENSCHUTZ & FREIHEIT',
                'headline': '100% Kostenlos,\nOhne Werbung & Privat',
                'subtitle': 'Keine Datenerfassung. Minimalistisch & Schnell.',
                'is_features': True,
                'feature_cards': [
                    ((16, 185, 129), "check", "100% Datenschutz", "Keine Datenerfassung, kein Tracking, keine Server."),
                    ((99, 102, 241), "arrow", "0ms Verzögerung", "Direktes Umleiten im Hintergrund ohne Ladezeit."),
                    ((245, 158, 11), "star", "100% Gratis", "Keine In-App Käufe, keine Werbung, Open Source."),
                    ((6, 182, 212), "zap", "Akkuschonend", "Verbraucht 0% Akku – läuft nur bei Link-Klick.")
                ]
            }
        ]
    },
    'en-US': {
        'tagline': 'Automatic. Invisible. Privacy-First.',
        'screens': [
            {
                'category': 'AUTOMATIC MAP CONVERTER',
                'headline': 'Open Apple Maps Links\nin Google Maps',
                'subtitle': 'Automatic. Invisible. Seamless.',
                'raw_image': f"{RAW_DIR}/raw_mainscreen.png"
            },
            {
                'category': 'EASY SETUP',
                'headline': 'Set up once in\n3 simple steps',
                'subtitle': 'Just one tap in Android settings.',
                'raw_image': f"{RAW_DIR}/raw_settings.png"
            },
            {
                'category': 'FAST & SEAMLESS',
                'headline': 'Tap link in chat –\nGoogle Maps opens!',
                'subtitle': 'No copying, no pasting, 100% automatic.',
                'raw_image': f"{RAW_DIR}/raw_chat.png",
                'is_chat': True,
                'callout_text_1': 'Tapped Apple Maps Link',
                'callout_text_2': 'Opens directly in Google Maps!'
            },
            {
                'category': 'PRIVACY & FREEDOM',
                'headline': '100% Free, No Ads\n& Privacy-First',
                'subtitle': 'No data collection. Minimalist & Fast.',
                'is_features': True,
                'feature_cards': [
                    ((16, 185, 129), "check", "100% Privacy", "Zero data collection, no tracking, no servers."),
                    ((99, 102, 241), "arrow", "Instant Redirect", "Direct background routing with zero delay."),
                    ((245, 158, 11), "star", "100% Free", "No in-app purchases, no ads, open source."),
                    ((6, 182, 212), "zap", "Battery Friendly", "Zero battery impact – runs only on link tap.")
                ]
            }
        ]
    },
    'da-DK': {
        'tagline': 'Automatisk. Usynlig. Privatlivsvenlig.',
        'screens': [
            {
                'category': 'AUTOMATISK MAP-KONVERTER',
                'headline': 'Åbn Apple Maps-links\ni Google Maps',
                'subtitle': 'Automatisk. Usynlig. Problemfri.',
                'raw_image': f"{RAW_DIR}/raw_mainscreen.png"
            },
            {
                'category': 'NEMT OPSÆTNING',
                'headline': 'Opsæt én gang i\n3 enkle trin',
                'subtitle': 'Kun ét tryk i Android-indstillinger.',
                'raw_image': f"{RAW_DIR}/raw_settings.png"
            },
            {
                'category': 'HURTIG OG SØMLØS',
                'headline': 'Tryk på link i chat –\nGoogle Maps åbnes!',
                'subtitle': 'Ingen kopiering, ingen indsættelse, 100% automatisk.',
                'raw_image': f"{RAW_DIR}/raw_chat.png",
                'is_chat': True,
                'callout_text_1': 'Trykkede på Apple Maps-link',
                'callout_text_2': 'Åbnes direkte i Google Maps!'
            },
            {
                'category': 'PRIVATLIV OG FRIHED',
                'headline': '100% Gratis, Ingen Reklamer\nog Privatliv i Top',
                'subtitle': 'Ingen dataindsamling. Minimalistisk og hurtig.',
                'is_features': True,
                'feature_cards': [
                    ((16, 185, 129), "check", "100% Privatliv", "Ingen dataindsamling, ingen sporing, ingen servere."),
                    ((99, 102, 241), "arrow", "Øjeblikkelig omdirigering", "Direkte baggrundsomdirigering uden forsinkelse."),
                    ((245, 158, 11), "star", "100% Gratis", "Ingen køb i appen, ingen reklamer, Open Source."),
                    ((6, 182, 212), "zap", "Batterivenlig", "Nul batteripåvirkning – kører kun ved linktryk.")
                ]
            }
        ]
    },
    'fr-FR': {
        'tagline': 'Automatique. Invisible. Respectueux de la vie privée.',
        'screens': [
            {
                'category': 'CONVERTISSEUR AUTOMATIQUE',
                'headline': 'Ouvrez les liens Apple Maps\ndans Google Maps',
                'subtitle': 'Automatique. Invisible. Fluide.',
                'raw_image': f"{RAW_DIR}/raw_mainscreen.png"
            },
            {
                'category': 'CONFIGURATION FACILE',
                'headline': 'Configurez une fois en\n3 étapes simples',
                'subtitle': 'Un seul clic dans les paramètres Android.',
                'raw_image': f"{RAW_DIR}/raw_settings.png"
            },
            {
                'category': 'RAPIDE ET FLUIDE',
                'headline': 'Touchez un lien –\nGoogle Maps s\'ouvre !',
                'subtitle': 'Ni copier, ni coller, 100% automatique.',
                'raw_image': f"{RAW_DIR}/raw_chat.png",
                'is_chat': True,
                'callout_text_1': 'Lien Apple Maps touché',
                'callout_text_2': 'S\'ouvre directement dans Google Maps !'
            },
            {
                'category': 'VIE PRIVÉE ET LIBERTÉ',
                'headline': '100% Gratuit, Sans Pubs\net Confidentiel',
                'subtitle': 'Aucune collecte de données. Minimaliste et rapide.',
                'is_features': True,
                'feature_cards': [
                    ((16, 185, 129), "check", "100% Vie Privée", "Zéro collecte de données, aucun suivi, aucun serveur."),
                    ((99, 102, 241), "arrow", "Redirection Instantanée", "Redirection directe en arrière-plan sans délai."),
                    ((245, 158, 11), "star", "100% Gratuit", "Sans achats intégrés, sans publicité, Open Source."),
                    ((6, 182, 212), "zap", "Économe en Batterie", "Impact batterie nul – s'exécute uniquement au clic.")
                ]
            }
        ]
    },
    'it-IT': {
        'tagline': 'Automatico. Invisibile. Rispetto della privacy.',
        'screens': [
            {
                'category': 'CONVERTITORE AUTOMATICO',
                'headline': 'Apri i link Apple Maps\nin Google Maps',
                'subtitle': 'Automatico. Invisibile. Diretto.',
                'raw_image': f"{RAW_DIR}/raw_mainscreen.png"
            },
            {
                'category': 'CONFIGURAZIONE FACILE',
                'headline': 'Configura una volta in\n3 semplici passaggi',
                'subtitle': 'Basta un tocco nelle impostazioni Android.',
                'raw_image': f"{RAW_DIR}/raw_settings.png"
            },
            {
                'category': 'VELOCE E DIRETTO',
                'headline': 'Tocca il link in chat –\nsi apre Google Maps!',
                'subtitle': 'Niente copia, niente incolla, 100% automatico.',
                'raw_image': f"{RAW_DIR}/raw_chat.png",
                'is_chat': True,
                'callout_text_1': 'Link Apple Maps toccato',
                'callout_text_2': 'Si apre direttamente in Google Maps!'
            },
            {
                'category': 'PRIVACY E LIBERTÀ',
                'headline': '100% Gratuito, Senza Pubblicità\ne Riservato',
                'subtitle': 'Nessuna raccolta dati. Minimalista e veloce.',
                'is_features': True,
                'feature_cards': [
                    ((16, 185, 129), "check", "100% Privacy", "Zero raccolta dati, nessun tracciamento, nessun server."),
                    ((99, 102, 241), "arrow", "Reindirizzamento Istantaneo", "Reindirizzamento in background senza attesa."),
                    ((245, 158, 11), "star", "100% Gratis", "Senza acquisti in-app, senza pubblicità, Open Source."),
                    ((6, 182, 212), "zap", "Risparmio Batteria", "Zero impatto sulla batteria – attivo solo al tocco.")
                ]
            }
        ]
    },
    'ja-JP': {
        'tagline': '完全自動。透明動作。プライバシー重視。',
        'screens': [
            {
                'category': '自動マップ変換',
                'headline': 'Apple Mapsのリンクを\nGoogle Mapsで開く',
                'subtitle': '完全自動。透明動作。手間なし。',
                'raw_image': f"{RAW_DIR}/raw_mainscreen.png"
            },
            {
                'category': '簡単セットアップ',
                'headline': '3つのステップで\n1回設定するだけ',
                'subtitle': 'Androidの設定画面からワンタップで完了。',
                'raw_image': f"{RAW_DIR}/raw_settings.png"
            },
            {
                'category': '高速＆シームレス',
                'headline': 'チャットでリンクをタップ –\nGoogle Mapsがすぐ開く！',
                'subtitle': 'コピペ不要、ブラウザ経由なし、100%自動。',
                'raw_image': f"{RAW_DIR}/raw_chat.png",
                'is_chat': True,
                'callout_text_1': 'Apple Mapsリンクをタップ',
                'callout_text_2': 'Google Mapsで直接開きます！'
            },
            {
                'category': 'プライバシー＆安心',
                'headline': '完全無料・広告なし\n安心のプライバシー設計',
                'subtitle': 'データ収集ゼロ。ミニマル＆高速。',
                'is_features': True,
                'feature_cards': [
                    ((16, 185, 129), "check", "100% プライバシー", "データ収集なし、トラッキングなし、サーバーなし。"),
                    ((99, 102, 241), "arrow", "即時転送（0秒）", "バックグラウンドで待ち時間なしで直接転送。"),
                    ((245, 158, 11), "star", "完全無料", "課金要素なし、広告なし、オープンソース。"),
                    ((6, 182, 212), "zap", "省電力設計", "バッテリー消費ゼロ – タップ時のみ動作。")
                ]
            }
        ]
    },
    'nl-NL': {
        'tagline': 'Automatisch. Onzichtbaar. Privacyvriendelijk.',
        'screens': [
            {
                'category': 'AUTOMATISCHE MAP-CONVERTER',
                'headline': 'Open Apple Maps-links\nin Google Maps',
                'subtitle': 'Automatisch. Onzichtbaar. Naadloos.',
                'raw_image': f"{RAW_DIR}/raw_mainscreen.png"
            },
            {
                'category': 'EENVOUDIGE INSTELLING',
                'headline': 'Eenmalig instellen in\n3 eenvoudige stappen',
                'subtitle': 'Slechts één tik in de Android-instellingen.',
                'raw_image': f"{RAW_DIR}/raw_settings.png"
            },
            {
                'category': 'SNEL EN NAADLOOS',
                'headline': 'Tik op link in chat –\nGoogle Maps opent direct!',
                'subtitle': 'Geen kopiëren, geen plakken, 100% automatisch.',
                'raw_image': f"{RAW_DIR}/raw_chat.png",
                'is_chat': True,
                'callout_text_1': 'Apple Maps-link aangetikt',
                'callout_text_2': 'Opent direct in Google Maps!'
            },
            {
                'category': 'PRIVACY EN VRIJHEID',
                'headline': '100% Gratis, Zonder Reclame\nen Privacygericht',
                'subtitle': 'Geen gegevensverzameling. Minimalistisch & snel.',
                'is_features': True,
                'feature_cards': [
                    ((16, 185, 129), "check", "100% Privacy", "Geen gegevensverzameling, geen tracking, geen servers."),
                    ((99, 102, 241), "arrow", "Directe Omleiding", "Directe achtergrondomleiding zonder vertraging."),
                    ((245, 158, 11), "star", "100% Gratis", "Geen in-app aankopen, geen reclame, Open Source."),
                    ((6, 182, 212), "zap", "Batterijvriendelijk", "Nul batterij-impact – werkt alleen bij klik.")
                ]
            }
        ]
    },
    'no-NO': {
        'tagline': 'Automatisk. Usynlig. Personvernvennlig.',
        'screens': [
            {
                'category': 'AUTOMATISK MAP-KONVERTER',
                'headline': 'Åpne Apple Maps-lenker\ni Google Maps',
                'subtitle': 'Automatisk. Usynlig. Sømløst.',
                'raw_image': f"{RAW_DIR}/raw_mainscreen.png"
            },
            {
                'category': 'ENKELT OPPSETT',
                'headline': 'Sett opp én gang i\n3 enkle trinn',
                'subtitle': 'Bare ett trykk i Android-innstillingene.',
                'raw_image': f"{RAW_DIR}/raw_settings.png"
            },
            {
                'category': 'RASK OG SØMLØS',
                'headline': 'Trykk på lenke i chat –\nGoogle Maps åpnes!',
                'subtitle': 'Ingen kopiering, ingen liming, 100% automatisk.',
                'raw_image': f"{RAW_DIR}/raw_chat.png",
                'is_chat': True,
                'callout_text_1': 'Trykket på Apple Maps-lenke',
                'callout_text_2': 'Åpnes direkte i Google Maps!'
            },
            {
                'category': 'PERSONVERN OG FRIHET',
                'headline': '100% Gratis, Uten Reklame\nog Personvern i Fokus',
                'subtitle': 'Ingen datainnsamling. Minimalistisk og rask.',
                'is_features': True,
                'feature_cards': [
                    ((16, 185, 129), "check", "100% Personvern", "Ingen datainnsamling, ingen sporing, ingen servere."),
                    ((99, 102, 241), "arrow", "Umiddelbar Omdirigering", "Direkte bakgrunnsomdirigering uten forsinkelse."),
                    ((245, 158, 11), "star", "100% Gratis", "Ingen kjøp i appen, ingen reklame, Open Source."),
                    ((6, 182, 212), "zap", "Batterivennlig", "Null batteripåvirkning – kjører kun ved trykk.")
                ]
            }
        ]
    },
    'pl-PL': {
        'tagline': 'Automatycznie. Niewidocznie. Bezpiecznie.',
        'screens': [
            {
                'category': 'AUTOMATYCZNY KONWERTER',
                'headline': 'Otwieraj linki Apple Maps\nw Google Maps',
                'subtitle': 'Automatycznie. Niewidocznie. Bezproblemowo.',
                'raw_image': f"{RAW_DIR}/raw_mainscreen.png"
            },
            {
                'category': 'ŁATWA KONFIGURACJA',
                'headline': 'Skonfiguruj raz w\n3 prostych krokach',
                'subtitle': 'Wystarczy jedno kliknięcie w ustawieniach Androida.',
                'raw_image': f"{RAW_DIR}/raw_settings.png"
            },
            {
                'category': 'SZYBKO I PŁYNNIE',
                'headline': 'Kliknij link w czacie –\nGoogle Maps się otwiera!',
                'subtitle': 'Bez kopiowania, bez wklejania, 100% automatycznie.',
                'raw_image': f"{RAW_DIR}/raw_chat.png",
                'is_chat': True,
                'callout_text_1': 'Kliknięto link Apple Maps',
                'callout_text_2': 'Otwiera się bezpośrednio w Google Maps!'
            },
            {
                'category': 'PRYWATNOŚĆ I WOLNOŚĆ',
                'headline': '100% Bezpłatnie, Bez Reklam\ni Bezpiecznie',
                'subtitle': 'Brak zbierania danych. Minimalistycznie i szybko.',
                'is_features': True,
                'feature_cards': [
                    ((16, 185, 129), "check", "100% Prywatności", "Zero zbierania danych, śledzenia i serwerów."),
                    ((99, 102, 241), "arrow", "Natychmiastowo", "Przekierowanie w tle bez opóźnień."),
                    ((245, 158, 11), "star", "100% Darmo", "Bez zakupów w aplikacji, bez reklam, Open Source."),
                    ((6, 182, 212), "zap", "Oszczędza Baterię", "Zero wpływu na baterię – działa tylko przy kliknięciu.")
                ]
            }
        ]
    },
    'pt-BR': {
        'tagline': 'Automático. Invisível. Privacidade em primeiro lugar.',
        'screens': [
            {
                'category': 'CONVERSOR AUTOMÁTICO',
                'headline': 'Abra links do Apple Maps\nno Google Maps',
                'subtitle': 'Automático. Invisível. Sem complicações.',
                'raw_image': f"{RAW_DIR}/raw_mainscreen.png"
            },
            {
                'category': 'CONFIGURAÇÃO FÁCIL',
                'headline': 'Configure uma vez em\n3 passos simples',
                'subtitle': 'Apenas um toque nas configurações do Android.',
                'raw_image': f"{RAW_DIR}/raw_settings.png"
            },
            {
                'category': 'RÁPIDO E FLUIDO',
                'headline': 'Toque no link no chat –\no Google Maps abre!',
                'subtitle': 'Sem copiar, sem colar, 100% automático.',
                'raw_image': f"{RAW_DIR}/raw_chat.png",
                'is_chat': True,
                'callout_text_1': 'Link do Apple Maps tocado',
                'callout_text_2': 'Abre diretamente no Google Maps!'
            },
            {
                'category': 'PRIVACIDADE E LIBERDADE',
                'headline': '100% Grátis, Sem Anúncios\ne Privado',
                'subtitle': 'Sem coleta de dados. Minimalista e rápido.',
                'is_features': True,
                'feature_cards': [
                    ((16, 185, 129), "check", "100% Privacidade", "Zero coleta de dados, sem rastreamento, sem servidores."),
                    ((99, 102, 241), "arrow", "Redirecionamento Instantâneo", "Redirecionamento direto em segundo plano sem espera."),
                    ((245, 158, 11), "star", "100% Grátis", "Sem compras no app, sem anúncios, Open Source."),
                    ((6, 182, 212), "zap", "Economiza Bateria", "Zero impacto na bateria – roda apenas ao tocar.")
                ]
            }
        ]
    },
    'pt-PT': {
        'tagline': 'Automático. Invisível. Privacidade em primeiro lugar.',
        'screens': [
            {
                'category': 'CONVERSOR AUTOMÁTICO',
                'headline': 'Abra links do Apple Maps\nno Google Maps',
                'subtitle': 'Automático. Invisível. Sem complicações.',
                'raw_image': f"{RAW_DIR}/raw_mainscreen.png"
            },
            {
                'category': 'CONFIGURAÇÃO FÁCIL',
                'headline': 'Configure uma vez em\n3 passos simples',
                'subtitle': 'Apenas um toque nas definições do Android.',
                'raw_image': f"{RAW_DIR}/raw_settings.png"
            },
            {
                'category': 'RÁPIDO E FLUIDO',
                'headline': 'Toque no link no chat –\no Google Maps abre!',
                'subtitle': 'Sem copiar, sem colar, 100% automático.',
                'raw_image': f"{RAW_DIR}/raw_chat.png",
                'is_chat': True,
                'callout_text_1': 'Link do Apple Maps tocado',
                'callout_text_2': 'Abre diretamente no Google Maps!'
            },
            {
                'category': 'PRIVACIDADE E LIBERDADE',
                'headline': '100% Grátis, Sem Anúncios\ne Privado',
                'subtitle': 'Sem recolha de dados. Minimalista e rápido.',
                'is_features': True,
                'feature_cards': [
                    ((16, 185, 129), "check", "100% Privacidade", "Zero recolha de dados, sem rastreio, sem servidores."),
                    ((99, 102, 241), "arrow", "Redirecionamento Instantâneo", "Redirecionamento direto em segundo plano sem espera."),
                    ((245, 158, 11), "star", "100% Grátis", "Sem compras na app, sem anúncios, Open Source."),
                    ((6, 182, 212), "zap", "Poupa Bateria", "Zero impacto na bateria – executa apenas ao tocar.")
                ]
            }
        ]
    },
    'sv-SE': {
        'tagline': 'Automatisk. Osynlig. Integritetsvänlig.',
        'screens': [
            {
                'category': 'AUTOMATISK MAP-KONVERTERARE',
                'headline': 'Öppna Apple Maps-länkar\ni Google Maps',
                'subtitle': 'Automatisk. Osynlig. Smidig.',
                'raw_image': f"{RAW_DIR}/raw_mainscreen.png"
            },
            {
                'category': 'ENKEL INSTÄLLNING',
                'headline': 'Ställ in en gång i\n3 enkla steg',
                'subtitle': 'Bara ett tryck i Android-inställningarna.',
                'raw_image': f"{RAW_DIR}/raw_settings.png"
            },
            {
                'category': 'SNABB OCH SMIDIG',
                'headline': 'Tryck på länk i chatten –\nGoogle Maps öppnas!',
                'subtitle': 'Ingen kopiering, ingen klistra in, 100% automatiskt.',
                'raw_image': f"{RAW_DIR}/raw_chat.png",
                'is_chat': True,
                'callout_text_1': 'Tryckte på Apple Maps-länk',
                'callout_text_2': 'Öppnas direkt i Google Maps!'
            },
            {
                'category': 'INTEGRITET OCH FRIHET',
                'headline': '100% Gratis, Utan Annonser\noch Integritetssäker',
                'subtitle': 'Ingen datainsamling. Minimalistisk och snabb.',
                'is_features': True,
                'feature_cards': [
                    ((16, 185, 129), "check", "100% Integritet", "Noll datainsamling, ingen spårning, inga servrar."),
                    ((99, 102, 241), "arrow", "Omedelbar omdirigering", "Direkt bakgrundsomdirigering utan dröjsmål."),
                    ((245, 158, 11), "star", "100% Gratis", "Inga köp i appen, inga annonser, Open Source."),
                    ((6, 182, 212), "zap", "Batterivänlig", "Noll batteripåverkan – körs endast vid länk-tryck.")
                ]
            }
        ]
    },
    'es-ES': {
        'tagline': 'Automático. Invisible. Respeta la privacidad.',
        'screens': [
            {
                'category': 'CONVERTIDOR AUTOMÁTICO',
                'headline': 'Abre enlaces de Apple Maps\nen Google Maps',
                'subtitle': 'Automático. Invisible. Sin rodeos.',
                'raw_image': f"{RAW_DIR}/raw_mainscreen.png"
            },
            {
                'category': 'CONFIGURACIÓN FÁCIL',
                'headline': 'Configura una vez en\n3 sencillos pasos',
                'subtitle': 'Solo un toque en los ajustes de Android.',
                'raw_image': f"{RAW_DIR}/raw_settings.png"
            },
            {
                'category': 'RÁPIDO Y FLUIDO',
                'headline': 'Toca un enlace en el chat –\n¡se abre Google Maps!',
                'subtitle': 'Sin copiar, sin pegar, 100% automático.',
                'raw_image': f"{RAW_DIR}/raw_chat.png",
                'is_chat': True,
                'callout_text_1': 'Enlace de Apple Maps tocado',
                'callout_text_2': '¡Se abre directamente en Google Maps!'
            },
            {
                'category': 'PRIVACIDAD Y LIBERTAD',
                'headline': '100% Gratis, Sin Anuncios\ny Privado',
                'subtitle': 'Sin recopilación de datos. Minimalista y rápido.',
                'is_features': True,
                'feature_cards': [
                    ((16, 185, 129), "check", "100% Privacidad", "Zero recopilación de datos, sin rastreo, sin servidores."),
                    ((99, 102, 241), "arrow", "Redirección Instantánea", "Redirección directa en segundo plano sin espera."),
                    ((245, 158, 11), "star", "100% Gratis", "Sin compras en la app, sin anuncios, Open Source."),
                    ((6, 182, 212), "zap", "Ahorra Batería", "Cero impacto en la batería – funciona solo al tocar.")
                ]
            }
        ]
    },
    'tr-TR': {
        'tagline': 'Otomatik. Görünmez. Gizlilik Odaklı.',
        'screens': [
            {
                'category': 'OTOMATİK HARİTA DÖNÜŞTÜRÜCÜ',
                'headline': 'Apple Maps Bağlantılarını\nGoogle Maps\'te Açın',
                'subtitle': 'Otomatik. Görünmez. Sorunsuz.',
                'raw_image': f"{RAW_DIR}/raw_mainscreen.png"
            },
            {
                'category': 'KOLAY KURULUM',
                'headline': '3 Kolay Adımda\nBir Kez Kurun',
                'subtitle': 'Android ayarlarında sadece tek bir dokunuş.',
                'raw_image': f"{RAW_DIR}/raw_settings.png"
            },
            {
                'category': 'HIZLI VE SORUNSUZ',
                'headline': 'Sohbetteki bağlantıya dokunun –\nGoogle Maps açılsın!',
                'subtitle': 'Kopyalama yok, yapıştırma yok, %100 otomatik.',
                'raw_image': f"{RAW_DIR}/raw_chat.png",
                'is_chat': True,
                'callout_text_1': 'Apple Maps bağlantısına dokunuldu',
                'callout_text_2': 'Doğrudan Google Maps\'te açılır!'
            },
            {
                'category': 'GİZLİLİK VE ÖZGÜRLÜK',
                'headline': '%100 Ücretsiz, Reklamsız\nve Gizlilik Odaklı',
                'subtitle': 'Veri toplama yok. Minimalist ve hızlı.',
                'is_features': True,
                'feature_cards': [
                    ((16, 185, 129), "check", "%100 Gizlilik", "Sıfır veri toplama, takip yok, sunucu yok."),
                    ((99, 102, 241), "arrow", "Anında Yönlendirme", "Gecikmesiz doğrudan arka plan yönlendirmesi."),
                    ((245, 158, 11), "star", "%100 Ücretsiz", "Uygulama içi satın alma yok, reklam yok, Açık Kaynak."),
                    ((6, 182, 212), "zap", "Pil Dostu", "Sıfır pil etkisi – sadece tıklandığında çalışır.")
                ]
            }
        ]
    }
}

def main():
    for locale, data in LOCALES.items():
        print(f"Generating store graphics for locale: {locale}...")
        for idx, screen in enumerate(data['screens'], 1):
            create_screen(screen, f"{OUTPUT_BASE}/{locale}/screen_{idx}.png")
        create_feature_graphic({
            'tagline': data['tagline']
        }, f"{OUTPUT_BASE}/{locale}/feature_graphic.png")

if __name__ == "__main__":
    main()
