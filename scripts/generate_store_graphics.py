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

def main():
    # Deutsch (DE) Configuration
    de_screens = [
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

    # English (EN) Configuration
    en_screens = [
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

    # Generate DE
    for idx, screen in enumerate(de_screens, 1):
        create_screen(screen, f"{OUTPUT_BASE}/de-DE/screen_{idx}.png")
    create_feature_graphic({
        'tagline': 'Automatisch. Unsichtbar. Datenschutzfreundlich.'
    }, f"{OUTPUT_BASE}/de-DE/feature_graphic.png")

    # Generate EN
    for idx, screen in enumerate(en_screens, 1):
        create_screen(screen, f"{OUTPUT_BASE}/en-US/screen_{idx}.png")
    create_feature_graphic({
        'tagline': 'Automatic. Invisible. Privacy-First.'
    }, f"{OUTPUT_BASE}/en-US/feature_graphic.png")

if __name__ == "__main__":
    main()
