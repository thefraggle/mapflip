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

CJK_FONT_FALLBACKS = [
    "/System/Library/Fonts/Supplemental/Arial Unicode.ttf",
    "/System/Library/Fonts/STHeiti Light.ttc",
    "/System/Library/Fonts/Supplemental/AppleGothic.ttf",
    "/System/Library/Fonts/PingFang.ttc",
    "/System/Library/Fonts/Hiragino Sans W3.ttc"
]

def get_font(path, size, text=None):
    """
    Returns an ImageFont. If text contains CJK or extended Unicode (> 0x024F),
    or if standard font fails, uses CJK Unicode fallback fonts.
    """
    is_cjk_or_extended = False
    if text:
        is_cjk_or_extended = any(ord(char) > 0x024F for char in str(text))

    if is_cjk_or_extended:
        for cjk_path in CJK_FONT_FALLBACKS:
            if os.path.exists(cjk_path):
                try:
                    return ImageFont.truetype(cjk_path, size)
                except Exception:
                    continue

    try:
        return ImageFont.truetype(path, size)
    except Exception:
        for cjk_path in CJK_FONT_FALLBACKS:
            if os.path.exists(cjk_path):
                try:
                    return ImageFont.truetype(cjk_path, size)
                except Exception:
                    continue
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
    elif icon_type == 'down_arrow':
        p1 = (cx, cy - size * 0.35)
        p2 = (cx, cy + size * 0.35)
        a1 = (cx - size * 0.28, cy + size * 0.1)
        a2 = (cx + size * 0.28, cy + size * 0.1)
        draw.line([p1, p2], fill=color, width=w)
        draw.line([a1, p2, a2], fill=color, width=w, joint='round')
    elif icon_type == 'nav':
        p = [
            (cx, cy - size * 0.42),
            (cx + size * 0.36, cy + size * 0.36),
            (cx, cy + size * 0.15),
            (cx - size * 0.36, cy + size * 0.36)
        ]
        draw.polygon(p, fill=color)
    elif icon_type == 'shield':
        p = [
            (cx, cy - size * 0.40),
            (cx + size * 0.34, cy - size * 0.25),
            (cx + size * 0.34, cy + size * 0.08),
            (cx, cy + size * 0.44),
            (cx - size * 0.34, cy + size * 0.08),
            (cx - size * 0.34, cy - size * 0.25)
        ]
        draw.polygon(p, fill=color)
    elif icon_type == 'pause':
        bar_w = max(5, int(size * 0.20))
        bar_h = int(size * 0.60)
        gap = int(size * 0.16)
        draw_rounded_rect(draw, (cx - gap - bar_w, cy - bar_h // 2, cx - gap, cy + bar_h // 2), radius=bar_w // 2, fill=color)
        draw_rounded_rect(draw, (cx + gap, cy - bar_h // 2, cx + gap + bar_w, cy + bar_h // 2), radius=bar_w // 2, fill=color)
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
    draw_vector_icon(draw, cx, cy, int(size * 0.65), icon_type, color=symbol_color)

def add_drop_shadow(image, radius=24, offset=(0, 18), shadow_color=(0, 0, 0, 150)):
    """Creates a shadow behind an image."""
    shadow = Image.new("RGBA", (image.width + radius * 2, image.height + radius * 2), (0, 0, 0, 0))
    shadow_draw = ImageDraw.Draw(shadow)
    shadow_draw.rounded_rectangle(
        (radius, radius, radius + image.width, radius + image.height),
        radius=36,
        fill=shadow_color
    )
    shadow = shadow.filter(ImageFilter.GaussianBlur(radius))
    
    result = Image.new("RGBA", (shadow.width + abs(offset[0]), shadow.height + abs(offset[1])), (0, 0, 0, 0))
    result.paste(shadow, (max(0, offset[0]), max(0, offset[1])), shadow)
    result.paste(image, (radius, radius), image)
    return result

def wrap_text(text, font, max_width):
    """Splits text into lines that fit within max_width (supports CJK & space-separated languages)."""
    lines = []
    paragraphs = text.split('\n')
    for paragraph in paragraphs:
        if not paragraph:
            continue
        if ' ' in paragraph:
            words = paragraph.split(' ')
            current_line = []
            for word in words:
                test_line = ' '.join(current_line + [word])
                bbox = font.getbbox(test_line)
                w = bbox[2] - bbox[0]
                if w <= max_width:
                    current_line.append(word)
                else:
                    if current_line:
                        lines.append(' '.join(current_line))
                    current_line = [word]
            if current_line:
                lines.append(' '.join(current_line))
        else:
            current_line = ""
            for char in paragraph:
                test_line = current_line + char
                bbox = font.getbbox(test_line)
                w = bbox[2] - bbox[0]
                if w <= max_width:
                    current_line += char
                else:
                    if current_line:
                        lines.append(current_line)
                    current_line = char
            if current_line:
                lines.append(current_line)
    return lines

def create_screen(config, output_path):
    os.makedirs(os.path.dirname(output_path), exist_ok=True)
    
    # 1. Background Gradient (Deep Navy / Indigo)
    bg = draw_gradient_background(SCREEN_W, SCREEN_H, (15, 23, 42), (49, 46, 129))
    draw = ImageDraw.Draw(bg)
    
    # Subtle background decorative glow circles
    glow = Image.new("RGBA", (700, 700), (0, 0, 0, 0))
    glow_draw = ImageDraw.Draw(glow)
    glow_draw.ellipse((0, 0, 700, 700), fill=(99, 102, 241, 50))
    glow = glow.filter(ImageFilter.GaussianBlur(100))
    bg.paste(glow, (-150, -150), glow)
    bg.paste(glow, (500, 1100), glow)
    
    # 2. Text Content Top Section
    category_font = get_font(FONT_BOLD, 36, text=config['category'])
    headline_font = get_font(FONT_BOLD, 72, text=config['headline'])
    subtitle_font = get_font(FONT_REGULAR, 44, text=config['subtitle'])
    
    y_offset = 85
    
    # Category Pill
    cat_text = config['category'].upper()
    cat_bbox = category_font.getbbox(cat_text)
    cat_w = cat_bbox[2] - cat_bbox[0]
    cat_h = cat_bbox[3] - cat_bbox[1]
    
    pill_padding_x = 32
    pill_padding_y = 14
    pill_x = 70
    pill_w = cat_w + pill_padding_x * 2
    pill_h = cat_h + pill_padding_y * 2
    
    draw_rounded_rect(
        draw,
        (pill_x, y_offset, pill_x + pill_w, y_offset + pill_h),
        radius=20,
        fill=(79, 70, 229, 230),
        outline=(165, 180, 252, 220),
        width=2
    )
    draw.text((pill_x + pill_padding_x, y_offset + pill_padding_y - 2), cat_text, font=category_font, fill=(255, 255, 255))
    
    y_offset += pill_h + 30
    
    # Headline (Automated Multiline Word-Wrap)
    raw_lines = config['headline'].split('\n')
    for raw_line in raw_lines:
        h_lines = wrap_text(raw_line, headline_font, max_width=940)
        for h_line in h_lines:
            draw.text((70, y_offset), h_line, font=headline_font, fill=(255, 255, 255))
            y_offset += 94
        
    y_offset += 16
    
    # Subtitle (Automated Multiline Word-Wrap)
    sub_lines = wrap_text(config['subtitle'], subtitle_font, max_width=940)
    for s_line in sub_lines:
        draw.text((70, y_offset), s_line, font=subtitle_font, fill=(203, 213, 225))
        y_offset += 56
    
    # 3. Content Body Section
    raw_img_path = config.get('raw_image')
    if raw_img_path and os.path.exists(raw_img_path):
        raw_img = Image.open(raw_img_path).convert("RGBA")
        
        if config.get('is_chat'):
            # Chat bubble mockup card
            target_w = 940
            ratio = target_w / float(raw_img.width)
            target_h = int(raw_img.height * ratio)
            raw_img = raw_img.resize((target_w, target_h), Image.Resampling.LANCZOS)
            
            framed = Image.new("RGBA", (target_w + 40, target_h + 40), (0, 0, 0, 0))
            f_draw = ImageDraw.Draw(framed)
            draw_rounded_rect(f_draw, (0, 0, target_w + 40, target_h + 40), radius=32, fill=(30, 41, 59, 245), outline=(71, 85, 105, 190), width=3)
            framed.paste(raw_img, (20, 20), raw_img)
            
            shadow_img = add_drop_shadow(framed, radius=32, offset=(0, 22))
            bg.paste(shadow_img, (50, 710), shadow_img)
            
            # Flow arrow connector badge between Chat and Callout
            draw_icon_badge(draw, 540 - 35, 1315, 70, (99, 102, 241, 230), 'down_arrow')
            
            # Primary Action Callout Badge
            badge_font = get_font(FONT_BOLD, 46, text=config['callout_text_1'])
            callout_sub_font = get_font(FONT_REGULAR, 40, text=config['callout_text_2'])
            callout = Image.new("RGBA", (940, 180), (0, 0, 0, 0))
            c_draw = ImageDraw.Draw(callout)
            draw_rounded_rect(c_draw, (0, 0, 940, 180), radius=30, fill=(16, 185, 129, 240))
            
            draw_vector_icon(c_draw, 60, 58, 38, 'check', color=(255, 255, 255))
            c_draw.text((100, 34), config['callout_text_1'], font=badge_font, fill=(255, 255, 255))
            
            draw_vector_icon(c_draw, 60, 122, 38, 'arrow', color=(236, 253, 245))
            c_draw.text((100, 100), config['callout_text_2'], font=callout_sub_font, fill=(236, 253, 245))
            
            c_shadow = add_drop_shadow(callout, radius=24, offset=(0, 14))
            bg.paste(c_shadow, (70, 1420), c_shadow)
            
            # Secondary Sub-Banner: Universal Compatibility
            compat_title = config.get('compat_title', 'Kompatibel mit allen Messengern')
            compat_sub = config.get('compat_sub', 'WhatsApp, Telegram, Signal, SMS, Slack & E-Mail')
            compat_font_1 = get_font(FONT_BOLD, 38, text=compat_title)
            compat_font_2 = get_font(FONT_REGULAR, 32, text=compat_sub)
            
            compat_card = Image.new("RGBA", (940, 145), (0, 0, 0, 0))
            cmp_draw = ImageDraw.Draw(compat_card)
            draw_rounded_rect(cmp_draw, (0, 0, 940, 145), radius=26, fill=(30, 41, 59, 220), outline=(71, 85, 105, 170), width=2)
            draw_vector_icon(cmp_draw, 55, 72, 40, 'zap', color=(245, 158, 11))
            cmp_draw.text((95, 30), compat_title, font=compat_font_1, fill=(255, 255, 255))
            cmp_draw.text((95, 82), compat_sub, font=compat_font_2, fill=(148, 163, 184))
            
            cmp_shadow = add_drop_shadow(compat_card, radius=20, offset=(0, 10))
            bg.paste(cmp_shadow, (70, 1680), cmp_shadow)
            
            # Third Highlight Banner: Navigation / Android Auto
            nav_highlight_text = config.get('nav_highlight', 'Startet direkt Routenführung & Navigation in Google Maps')
            nav_font = get_font(FONT_BOLD, 32, text=nav_highlight_text)
            nav_card = Image.new("RGBA", (940, 105), (0, 0, 0, 0))
            n_draw = ImageDraw.Draw(nav_card)
            draw_rounded_rect(n_draw, (0, 0, 940, 105), radius=22, fill=(15, 23, 42, 190), outline=(99, 102, 241, 130), width=2)
            draw_vector_icon(n_draw, 50, 52, 34, 'nav', color=(16, 185, 129))
            n_draw.text((85, 35), nav_highlight_text, font=nav_font, fill=(199, 210, 254))
            
            n_shadow = add_drop_shadow(nav_card, radius=18, offset=(0, 8))
            bg.paste(n_shadow, (70, 1880), n_shadow)
            
        else:
            # Full device screenshot mockup (960px width)
            target_w = 960
            ratio = target_w / float(raw_img.width)
            target_h = int(raw_img.height * ratio)
            raw_img = raw_img.resize((target_w, target_h), Image.Resampling.LANCZOS)
            
            mask = Image.new("L", (target_w, target_h), 0)
            mask_draw = ImageDraw.Draw(mask)
            mask_draw.rounded_rectangle((0, 0, target_w, target_h), radius=50, fill=255)
            
            framed = Image.new("RGBA", (target_w, target_h), (0, 0, 0, 0))
            framed.paste(raw_img, (0, 0), mask)
            
            bezel = Image.new("RGBA", (target_w + 18, target_h + 18), (0, 0, 0, 0))
            b_draw = ImageDraw.Draw(bezel)
            draw_rounded_rect(b_draw, (0, 0, target_w + 18, target_h + 18), radius=58, fill=(15, 23, 42, 255), outline=(100, 116, 139, 255), width=4)
            bezel.paste(framed, (9, 9), framed)
            
            shadow_img = add_drop_shadow(bezel, radius=38, offset=(0, 26))
            bg.paste(shadow_img, ((SCREEN_W - shadow_img.width) // 2, 670), shadow_img)
            
    elif config.get('is_features'):
        # 3 Power Cards Grid (Screen 4)
        y_card = 680
        cards = config['feature_cards']
        
        for badge_color, icon_type, title, desc in cards:
            card_title_font = get_font(FONT_BOLD, 48, text=title)
            card_desc_font = get_font(FONT_REGULAR, 36, text=desc)
            card = Image.new("RGBA", (940, 320), (0, 0, 0, 0))
            c_draw = ImageDraw.Draw(card)
            draw_rounded_rect(c_draw, (0, 0, 940, 320), radius=30, fill=(30, 41, 59, 235), outline=(71, 85, 105, 190), width=2)
            
            # Vector Icon Badge
            draw_icon_badge(c_draw, 40, 50, 100, badge_color, icon_type)
            
            # Word-wrapped Card Title
            title_lines = wrap_text(title, card_title_font, max_width=730)
            t_y = 42 if len(title_lines) == 1 else 32
            for t_line in title_lines:
                c_draw.text((170, t_y), t_line, font=card_title_font, fill=(255, 255, 255))
                t_y += 54
            
            # Multiline Description Text
            desc_lines = wrap_text(desc, card_desc_font, max_width=730)
            desc_y = max(t_y + 10, 115)
            for line in desc_lines:
                c_draw.text((170, desc_y), line, font=card_desc_font, fill=(148, 163, 184))
                desc_y += 48
            
            c_shadow = add_drop_shadow(card, radius=24, offset=(0, 14))
            bg.paste(c_shadow, (70, y_card), c_shadow)
            y_card += 360
            
        # Bottom Trust Badge
        trust_text = config.get('trust_text', '100% Open Source • Keine Werbung • Keine In-App Käufe')
        trust_font = get_font(FONT_BOLD, 32, text=trust_text)
        trust_card = Image.new("RGBA", (940, 110), (0, 0, 0, 0))
        t_draw = ImageDraw.Draw(trust_card)
        draw_rounded_rect(t_draw, (0, 0, 940, 110), radius=22, fill=(15, 23, 42, 180), outline=(99, 102, 241, 120), width=2)
        draw_vector_icon(t_draw, 50, 55, 34, 'star', color=(245, 158, 11))
        t_draw.text((85, 38), trust_text, font=trust_font, fill=(199, 210, 254))
        
        t_shadow = add_drop_shadow(trust_card, radius=18, offset=(0, 8))
        bg.paste(t_shadow, (70, y_card + 30), t_shadow)

    bg.save(output_path, "PNG")
    print(f"Generated Promo Screen: {output_path}")

def create_feature_graphic(config, output_path):
    os.makedirs(os.path.dirname(output_path), exist_ok=True)
    
    # 1024x500 Gradient Canvas
    bg = draw_gradient_background(FEATURE_W, FEATURE_H, (15, 23, 42), (49, 46, 129))
    draw = ImageDraw.Draw(bg)
    
    # Ambient glows
    glow = Image.new("RGBA", (450, 450), (0, 0, 0, 0))
    glow_draw = ImageDraw.Draw(glow)
    glow_draw.ellipse((0, 0, 450, 450), fill=(99, 102, 241, 55))
    glow = glow.filter(ImageFilter.GaussianBlur(70))
    bg.paste(glow, (-60, -60), glow)
    bg.paste(glow, (600, 80), glow)
    
    title_font = get_font(FONT_BOLD, 74)
    sub_font = get_font(FONT_BOLD, 36)
    tagline_font = get_font(FONT_REGULAR, 26, text=config['tagline'])
    
    # Left Text Block
    draw.text((60, 75), "MapFlip", font=title_font, fill=(255, 255, 255))
    
    # Draw vector arrow between Apple Maps and Google Maps
    draw.text((60, 175), "Apple Maps", font=sub_font, fill=(199, 210, 254))
    draw_vector_icon(draw, 305, 192, 28, 'arrow', color=(199, 210, 254))
    draw.text((345, 175), "Google Maps", font=sub_font, fill=(199, 210, 254))
    
    # Tagline Pill (max width 560 to prevent overlap with mockup)
    pill_text = config['tagline']
    tag_lines = wrap_text(pill_text, tagline_font, max_width=520)
    
    pill_h = 60 if len(tag_lines) == 1 else 95
    pill_w = min(560, max(tagline_font.getbbox(l)[2] - tagline_font.getbbox(l)[0] for l in tag_lines) + 40)
    
    draw_rounded_rect(draw, (60, 265, 60 + pill_w, 265 + pill_h), radius=16, fill=(16, 185, 129, 235))
    t_y = 280 if len(tag_lines) == 1 else 274
    for l in tag_lines:
        draw.text((80, t_y), l, font=tagline_font, fill=(255, 255, 255))
        t_y += 34
    
    # Feature Vector Badges under Tagline
    badge_items = config.get('badges', [
        ('zap', (245, 158, 11), '0ms Ladezeit'),
        ('nav', (16, 185, 129), 'Android Auto'),
        ('shield', (129, 140, 248), '0 Permissions')
    ])
    
    b_x = 60
    b_y = 390
    badge_font = get_font(FONT_BOLD, 22, text=' '.join(item[2] for item in badge_items))
    for icon_t, col, lbl in badge_items:
        draw_vector_icon(draw, b_x + 14, b_y + 14, 24, icon_t, color=col)
        draw.text((b_x + 34, b_y + 2), lbl, font=badge_font, fill=(224, 231, 255))
        lbl_bbox = badge_font.getbbox(lbl)
        b_x += (lbl_bbox[2] - lbl_bbox[0]) + 55
    
    # Right Image Preview
    raw_img_path = config.get('raw_image')
    if raw_img_path and os.path.exists(raw_img_path):
        raw_img = Image.open(raw_img_path).convert("RGBA")
        target_h = 430
        ratio = target_h / float(raw_img.height)
        target_w = int(raw_img.width * ratio)
        raw_img = raw_img.resize((target_w, target_h), Image.Resampling.LANCZOS)
        
        mask = Image.new("L", (target_w, target_h), 0)
        mask_draw = ImageDraw.Draw(mask)
        mask_draw.rounded_rectangle((0, 0, target_w, target_h), radius=30, fill=255)
        
        framed = Image.new("RGBA", (target_w, target_h), (0, 0, 0, 0))
        framed.paste(raw_img, (0, 0), mask)
        
        shadow_img = add_drop_shadow(framed, radius=26, offset=(0, 16))
        bg.paste(shadow_img, (675, 45), shadow_img)

    bg.save(output_path, "PNG")
    print(f"Generated Feature Graphic: {output_path}")

LOCALES = {
    'de-DE': {
        'tagline': 'Automatisch. Unsichtbar. Datenschutzfreundlich.',
        'badges': [
            ('zap', (245, 158, 11), '0ms Ladezeit'),
            ('nav', (16, 185, 129), 'Android Auto'),
            ('shield', (129, 140, 248), '0 Berechtigungen')
        ],
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
                'callout_text_2': 'Öffnet sich direkt in Google Maps!',
                'compat_title': 'Kompatibel mit allen Messengern',
                'compat_sub': 'WhatsApp, Telegram, Signal, SMS, Slack & E-Mail',
                'nav_highlight': 'Startet sofort Navigation & Routenführung'
            },
            {
                'category': 'DATENSCHUTZ & FUNKTIONEN',
                'headline': '100% Offline, Sicher\n& für Android Auto',
                'subtitle': 'Keine Internet-Berechtigungen. Maximaler Komfort.',
                'is_features': True,
                'feature_cards': [
                    ((16, 185, 129), "nav", "Turn-by-Turn & Android Auto", "Startet sofort die echte Routenführung in Google Maps."),
                    ((99, 102, 241), "shield", "100% Offline & Datenschutz", "Keine Internet-Berechtigung (0 Permissions), kein Tracking."),
                    ((245, 158, 11), "pause", "Pausen-Modus & Schnelle Kachel", "Jederzeit im Android-Kontrollzentrum pausieren & fortsetzen.")
                ],
                'trust_text': '100% Open Source • Keine Werbung • Keine In-App Käufe'
            }
        ]
    },
    'en-US': {
        'tagline': 'Automatic. Invisible. Privacy-First.',
        'badges': [
            ('zap', (245, 158, 11), '0ms Latency'),
            ('nav', (16, 185, 129), 'Android Auto'),
            ('shield', (129, 140, 248), '0 Permissions')
        ],
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
                'callout_text_2': 'Opens directly in Google Maps!',
                'compat_title': 'Universal App Compatibility',
                'compat_sub': 'WhatsApp, Telegram, Signal, SMS, Slack & Email',
                'nav_highlight': 'Launches Native Routing & Navigation'
            },
            {
                'category': 'PRIVACY & FEATURES',
                'headline': '100% Offline, Secure\n& Android Auto Ready',
                'subtitle': 'Zero internet permissions. Maximum comfort.',
                'is_features': True,
                'feature_cards': [
                    ((16, 185, 129), "nav", "Turn-by-Turn & Android Auto", "Instantly launches native Google Maps navigation & routing."),
                    ((99, 102, 241), "shield", "100% Offline & Private", "Zero internet permissions (0 permissions), no tracking or ads."),
                    ((245, 158, 11), "pause", "Smart Pause & Quick Tile", "Suspend & resume anytime directly from notification shade.")
                ],
                'trust_text': '100% Open Source • No Ads • No In-App Purchases'
            }
        ]
    },
    'da-DK': {
        'tagline': 'Automatisk. Usynlig. Privatlivsvenlig.',
        'badges': [
            ('zap', (245, 158, 11), '0ms forsinkelse'),
            ('nav', (16, 185, 129), 'Android Auto'),
            ('shield', (129, 140, 248), '0 Tilladelser')
        ],
        'screens': [
            {
                'category': 'AUTOMATISK MAP-KONVERTER',
                'headline': 'Åbn Apple Maps-links\ni Google Maps',
                'subtitle': 'Automatisk. Usynlig. Problemfri.',
                'raw_image': f"{RAW_DIR}/raw_mainscreen.png"
            },
            {
                'category': 'NEM OPSÆTNING',
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
                'callout_text_2': 'Åbnes direkte i Google Maps!',
                'compat_title': 'Kompatibel med alle apps',
                'compat_sub': 'WhatsApp, Telegram, Signal, SMS & e-mail',
                'nav_highlight': 'Starter øjeblikkeligt rutevejledning'
            },
            {
                'category': 'PRIVATLIV OG FUNKTIONER',
                'headline': '100% Offline, Sikker\nog til Android Auto',
                'subtitle': 'Nul internettilladelser. Maksimal komfort.',
                'is_features': True,
                'feature_cards': [
                    ((16, 185, 129), "nav", "Turn-by-turn & Android Auto", "Starter øjeblikkeligt rutevejledning i Google Maps."),
                    ((99, 102, 241), "shield", "100% Offline & Privat", "Nul internettilladelser (0 tilladelser), ingen sporing."),
                    ((245, 158, 11), "pause", "Pause-tilstand & Hurtig-knap", "Sæt på pause når som helst i notifikationspanelet.")
                ],
                'trust_text': '100% Open Source • Ingen reklamer • Helt gratis'
            }
        ]
    },
    'fr-FR': {
        'tagline': 'Automatique. Invisible. Respectueux de la vie privée.',
        'badges': [
            ('zap', (245, 158, 11), 'Zéro latence'),
            ('nav', (16, 185, 129), 'Android Auto'),
            ('shield', (129, 140, 248), '0 Permission')
        ],
        'screens': [
            {
                'category': 'CONVERTISSEUR AUTOMATIQUE',
                'headline': 'Ouvrez Apple Maps\ndans Google Maps',
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
                'callout_text_2': 'S\'ouvre directement dans Google Maps !',
                'compat_title': 'Compatible avec toutes les apps',
                'compat_sub': 'WhatsApp, Telegram, Signal, SMS & E-mails',
                'nav_highlight': 'Lance instantanément la navigation'
            },
            {
                'category': 'CONFIDENTIALITÉ ET OPTIONS',
                'headline': '100% Hors Ligne, Sûr\net Android Auto',
                'subtitle': 'Aucune permission Internet. Confort total.',
                'is_features': True,
                'feature_cards': [
                    ((16, 185, 129), "nav", "Navigation & Android Auto", "Lance instantanément l'itinéraire dans Google Maps."),
                    ((99, 102, 241), "shield", "100% Hors ligne & Privé", "Zéro permission Internet (0 permission), aucun suivi."),
                    ((245, 158, 11), "pause", "Mode Pause & Tuile Rapide", "Interrompez la redirection depuis le panneau de notification.")
                ],
                'trust_text': '100% Open Source • Sans publicité • Totalement gratuit'
            }
        ]
    },
    'it-IT': {
        'tagline': 'Automatico. Invisibile. Rispetto della privacy.',
        'badges': [
            ('zap', (245, 158, 11), 'Zero attesa'),
            ('nav', (16, 185, 129), 'Android Auto'),
            ('shield', (129, 140, 248), '0 Permessi')
        ],
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
                'callout_text_2': 'Si apre direttamente in Google Maps!',
                'compat_title': 'Compatibile con tutte le app',
                'compat_sub': 'WhatsApp, Telegram, Signal, SMS ed e-mail',
                'nav_highlight': 'Avvia immediatamente la navigazione'
            },
            {
                'category': 'PRIVACY E FUNZIONI',
                'headline': '100% Offline, Sicuro\ne per Android Auto',
                'subtitle': 'Zero permessi Internet. Massima comodità.',
                'is_features': True,
                'feature_cards': [
                    ((16, 185, 129), "nav", "Navigazione & Android Auto", "Avvia istantaneamente l'itinerario in Google Maps."),
                    ((99, 102, 241), "shield", "100% Offline & Privacy", "Zero permessi Internet (0 permessi), nessun tracciamento."),
                    ((245, 158, 11), "pause", "Modalità Pausa & Tessera", "Sospendi dal pannello notifiche in qualsiasi momento.")
                ],
                'trust_text': '100% Open Source • Nessuna pubblicità • Gratis'
            }
        ]
    },
    'ja-JP': {
        'tagline': '完全自動。透明動作。プライバシー重視。',
        'badges': [
            ('zap', (245, 158, 11), '0秒転送'),
            ('nav', (16, 185, 129), 'Android Auto'),
            ('shield', (129, 140, 248), '権限ゼロ')
        ],
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
                'headline': 'チャットのリンクをタップ\nGoogle Mapsがすぐ開く！',
                'subtitle': 'コピペ不要、ブラウザ経由なし、100%自動。',
                'raw_image': f"{RAW_DIR}/raw_chat.png",
                'is_chat': True,
                'callout_text_1': 'Apple Mapsリンクをタップ',
                'callout_text_2': 'Google Mapsで直接開きます！',
                'compat_title': '主要アプリに完全対応',
                'compat_sub': 'LINE、WhatsApp、Telegram、SMS、メール',
                'nav_highlight': 'Google Mapsで即座にナビを開始'
            },
            {
                'category': '機能＆プライバシー',
                'headline': '100% オフライン・安全\nAndroid Auto対応',
                'subtitle': 'インターネット権限ゼロ。高い利便性。',
                'is_features': True,
                'feature_cards': [
                    ((16, 185, 129), "nav", "音声ナビ＆Android Auto", "Google Mapsで即座にルート案内を開始します。"),
                    ((99, 102, 241), "shield", "100% オフライン＆安心", "インターネット権限ゼロ、追跡なし、広告なし。"),
                    ((245, 158, 11), "pause", "一時停止＆クイック設定", "通知シェードからいつでもワンタップで一時停止。")
                ],
                'trust_text': '完全無料 • 広告なし • オープンソース'
            }
        ]
    },
    'nl-NL': {
        'tagline': 'Automatisch. Onzichtbaar. Privacyvriendelijk.',
        'badges': [
            ('zap', (245, 158, 11), '0ms vertraging'),
            ('nav', (16, 185, 129), 'Android Auto'),
            ('shield', (129, 140, 248), '0 Permissies')
        ],
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
                'callout_text_2': 'Opent direct in Google Maps!',
                'compat_title': 'Universeel compatibel',
                'compat_sub': 'WhatsApp, Telegram, Signal, sms en e-mail',
                'nav_highlight': 'Start direct turn-by-turn navigatie'
            },
            {
                'category': 'PRIVACY EN FUNCTIES',
                'headline': '100% Offline, Veilig\nen Android Auto',
                'subtitle': 'Geen internetrechten. Maximaal gemak.',
                'is_features': True,
                'feature_cards': [
                    ((16, 185, 129), "nav", "Navigatie & Android Auto", "Start direct routebeschrijving in Google Maps."),
                    ((99, 102, 241), "shield", "100% Offline & Privacy", "Geen internetrechten (0 permissies), geen tracking."),
                    ((245, 158, 11), "pause", "Pauzemodus & Snelle Tegel", "Pauzeer direct vanuit de meldingenbalk.")
                ],
                'trust_text': '100% Open Source • Geen advertenties • Gratis'
            }
        ]
    },
    'no-NO': {
        'tagline': 'Automatisk. Usynlig. Personvernvennlig.',
        'badges': [
            ('zap', (245, 158, 11), '0ms forsinkelse'),
            ('nav', (16, 185, 129), 'Android Auto'),
            ('shield', (129, 140, 248), '0 Tillatelser')
        ],
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
                'callout_text_2': 'Åpnes direkte i Google Maps!',
                'compat_title': 'Universell kompatibilitet',
                'compat_sub': 'WhatsApp, Telegram, Signal, SMS & e-post',
                'nav_highlight': 'Starter ruteveiledning umiddelbart'
            },
            {
                'category': 'PERSONVERN OG FUNKSJONER',
                'headline': '100% Frakoblet, Sikker\nog for Android Auto',
                'subtitle': 'Null internetttillatelser. Maksimal komfort.',
                'is_features': True,
                'feature_cards': [
                    ((16, 185, 129), "nav", "Navigasjon & Android Auto", "Starter umiddelbart ruteveiledning i Google Maps."),
                    ((99, 102, 241), "shield", "100% Frakoblet & Privat", "Null internetttillatelser (0 tillatelser), ingen sporing."),
                    ((245, 158, 11), "pause", "Pausemodus & Hurtig-knapp", "Sett på pause når som helst i varslingspanelet.")
                ],
                'trust_text': '100% Open Source • Ingen reklame • Gratis'
            }
        ]
    },
    'pl-PL': {
        'tagline': 'Automatycznie. Niewidocznie. Bezpiecznie.',
        'badges': [
            ('zap', (245, 158, 11), '0ms opóźnienia'),
            ('nav', (16, 185, 129), 'Android Auto'),
            ('shield', (129, 140, 248), '0 Uprawnień')
        ],
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
                'callout_text_2': 'Otwiera się bezpośrednio w Google Maps!',
                'compat_title': 'Uniwersalna kompatybilność',
                'compat_sub': 'WhatsApp, Telegram, Signal, SMS i e-mail',
                'nav_highlight': 'Błyskawicznie uruchamia nawigację'
            },
            {
                'category': 'PRYWATNOŚĆ I FUNKCJE',
                'headline': '100% Offline, Bezpiecznie\ni dla Android Auto',
                'subtitle': 'Zero uprawnień internetowych. Maksymalny komfort.',
                'is_features': True,
                'feature_cards': [
                    ((16, 185, 129), "nav", "Nawigacja & Android Auto", "Błyskawicznie uruchamia trasę w Google Maps."),
                    ((99, 102, 241), "shield", "100% Offline & Prywatność", "Zero uprawnień internetowych (0 uprawnień), brak śledzenia."),
                    ((245, 158, 11), "pause", "Tryb Pauzy & Szybki Kafelek", "Wstrzymuj w każdej chwili z panelu powiadomień.")
                ],
                'trust_text': '100% Open Source • Bez reklam • Całkowicie za darmo'
            }
        ]
    },
    'pt-BR': {
        'tagline': 'Automático. Invisível. Privacidade em primeiro lugar.',
        'badges': [
            ('zap', (245, 158, 11), 'Zero atraso'),
            ('nav', (16, 185, 129), 'Android Auto'),
            ('shield', (129, 140, 248), '0 Permissões')
        ],
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
                'callout_text_2': 'Abre diretamente no Google Maps!',
                'compat_title': 'Compatibilidade Universal',
                'compat_sub': 'WhatsApp, Telegram, Signal, SMS e e-mail',
                'nav_highlight': 'Inicia instantaneamente a navegação'
            },
            {
                'category': 'PRIVACIDADE E RECURSOS',
                'headline': '100% Offline, Seguro\ne para Android Auto',
                'subtitle': 'Zero permissões de internet. Máximo conforto.',
                'is_features': True,
                'feature_cards': [
                    ((16, 185, 129), "nav", "Navegação & Android Auto", "Inicia instantaneamente a rota no Google Maps."),
                    ((99, 102, 241), "shield", "100% Offline e Seguro", "Zero permissões de internet (0 permissões), sem rastreamento."),
                    ((245, 158, 11), "pause", "Modo Pausa & Atalho Rápido", "Pause a qualquer momento no painel de notificações.")
                ],
                'trust_text': '100% Open Source • Sem anúncios • Totalmente grátis'
            }
        ]
    },
    'pt-PT': {
        'tagline': 'Automático. Invisível. Privacidade em primeiro lugar.',
        'badges': [
            ('zap', (245, 158, 11), 'Zero atraso'),
            ('nav', (16, 185, 129), 'Android Auto'),
            ('shield', (129, 140, 248), '0 Permissões')
        ],
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
                'callout_text_2': 'Abre diretamente no Google Maps!',
                'compat_title': 'Compatibilidade Universal',
                'compat_sub': 'WhatsApp, Telegram, Signal, SMS e e-mail',
                'nav_highlight': 'Inicia instantaneamente o trajeto'
            },
            {
                'category': 'PRIVACIDADE E RECURSOS',
                'headline': '100% Offline, Seguro\ne para Android Auto',
                'subtitle': 'Zero permissões de internet. Máximo conforto.',
                'is_features': True,
                'feature_cards': [
                    ((16, 185, 129), "nav", "Navegação & Android Auto", "Inicia instantaneamente o trajeto no Google Maps."),
                    ((99, 102, 241), "shield", "100% Offline e Privado", "Zero permissões de internet (0 permissões), sem rastreio."),
                    ((245, 158, 11), "pause", "Modo Pausa & Atalho Rápido", "Pause a qualquer momento no painel de notificações.")
                ],
                'trust_text': '100% Open Source • Sem anúncios • Totalmente grátis'
            }
        ]
    },
    'sv-SE': {
        'tagline': 'Automatisk. Osynlig. Integritetsvänlig.',
        'badges': [
            ('zap', (245, 158, 11), '0ms fördröjning'),
            ('nav', (16, 185, 129), 'Android Auto'),
            ('shield', (129, 140, 248), '0 Behörigheter')
        ],
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
                'callout_text_2': 'Öppnas direkt i Google Maps!',
                'compat_title': 'Universell kompatibilitet',
                'compat_sub': 'WhatsApp, Telegram, Signal, SMS och e-post',
                'nav_highlight': 'Startar rutt & navigering omedelbart'
            },
            {
                'category': 'INTEGRITET OCH FUNKTIONER',
                'headline': '100% Offline, Säker\noch för Android Auto',
                'subtitle': 'Noll internetbehörigheter. Maximal komfort.',
                'is_features': True,
                'feature_cards': [
                    ((16, 185, 129), "nav", "Navigering & Android Auto", "Startar omedelbart rutt i Google Maps."),
                    ((99, 102, 241), "shield", "100% Offline & Integritet", "Noll internetbehörigheter (0 behörigheter), ingen spårning."),
                    ((245, 158, 11), "pause", "Pausläge & Snabb-knapp", "Pausa när som helst från aviseringspanelen.")
                ],
                'trust_text': '100% Open Source • Inga annonser • Helt gratis'
            }
        ]
    },
    'es-ES': {
        'tagline': 'Automático. Invisible. Respeta la privacidad.',
        'badges': [
            ('zap', (245, 158, 11), 'Cero retraso'),
            ('nav', (16, 185, 129), 'Android Auto'),
            ('shield', (129, 140, 248), '0 Permisos')
        ],
        'screens': [
            {
                'category': 'CONVERTIDOR AUTOMÁTICO',
                'headline': 'Abre enlaces Apple Maps\nen Google Maps',
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
                'headline': 'Toca el enlace en el chat –\n¡Google Maps se abre!',
                'subtitle': 'Sin copiar, sin pegar, 100% automático.',
                'raw_image': f"{RAW_DIR}/raw_chat.png",
                'is_chat': True,
                'callout_text_1': 'Enlace de Apple Maps tocado',
                'callout_text_2': '¡Se abre directamente en Google Maps!',
                'compat_title': 'Compatibilidad Universal',
                'compat_sub': 'WhatsApp, Telegram, Signal, SMS y correo',
                'nav_highlight': 'Inicia de inmediato la navegación'
            },
            {
                'category': 'PRIVACIDAD Y FUNCIONES',
                'headline': '100% Offline, Seguro\ny para Android Auto',
                'subtitle': 'Cero permisos de internet. Máxima comodidad.',
                'is_features': True,
                'feature_cards': [
                    ((16, 185, 129), "nav", "Navegación & Android Auto", "Inicia al instante la ruta en Google Maps."),
                    ((99, 102, 241), "shield", "100% Offline y Privado", "Cero permisos de internet (0 permisos), sin rastreo."),
                    ((245, 158, 11), "pause", "Modo Pausa y Ajuste Rápido", "Pausa la redirección desde el panel de notificaciones.")
                ],
                'trust_text': '100% Open Source • Sin anuncios • Totalmente gratis'
            }
        ]
    },
    'tr-TR': {
        'tagline': 'Otomatik. Görünmez. Gizlilik Odaklı.',
        'badges': [
            ('zap', (245, 158, 11), '0ms Gecikme'),
            ('nav', (16, 185, 129), 'Android Auto'),
            ('shield', (129, 140, 248), '0 İzin')
        ],
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
                'headline': 'Sohbetteki linke dokunun –\nGoogle Maps açılsın!',
                'subtitle': 'Kopyalama yok, yapıştırma yok, %100 otomatik.',
                'raw_image': f"{RAW_DIR}/raw_chat.png",
                'is_chat': True,
                'callout_text_1': 'Apple Maps bağlantısına dokunuldu',
                'callout_text_2': 'Doğrudan Google Maps\'te açılır!',
                'compat_title': 'Evrensel Uygulama Uyumluluğu',
                'compat_sub': 'WhatsApp, Telegram, Signal, SMS ve E-posta',
                'nav_highlight': 'Anında navigasyon ve rota başlatır'
            },
            {
                'category': 'GİZLİLİK VE ÖZELLİKLER',
                'headline': '%100 Çevrimdışı, Güvenli\nve Android Auto Uyumlu',
                'subtitle': 'Sıfır internet izni. Maksimum konfor.',
                'is_features': True,
                'feature_cards': [
                    ((16, 185, 129), "nav", "Navigasyon ve Android Auto", "Google Maps'te anında rota ve canlı navigasyon başlatır."),
                    ((99, 102, 241), "shield", "%100 Çevrimdışı ve Gizli", "Sıfır internet izni (0 izin), takip ve reklam yok."),
                    ((245, 158, 11), "pause", "Duraklatma & Hızlı Ayar", "Bildirim panelinden istediğiniz an duraklatın.")
                ],
                'trust_text': '%100 Açık Kaynak • Reklam Yok • Tamamen Ücretsiz'
            }
        ]
    },
    'ko-KR': {
        'tagline': '자동. 투명함. 개인정보 보호 최우선.',
        'badges': [
            ('zap', (245, 158, 11), '0ms 지연 시간'),
            ('nav', (16, 185, 129), 'Android Auto'),
            ('shield', (129, 140, 248), '0 권한 (권한 없음)')
        ],
        'screens': [
            {
                'category': '자동 지도 변환기',
                'headline': 'Apple Maps 링크를\nGoogle Maps에서 열기',
                'subtitle': '자동. 투명함. 끊김 없는 경험.',
                'raw_image': f"{RAW_DIR}/raw_mainscreen.png"
            },
            {
                'category': '간편한 설정',
                'headline': '3단계로 끝나는\n초간편 일회성 설정',
                'subtitle': 'Android 설정에서 탭 한 번이면 완료.',
                'raw_image': f"{RAW_DIR}/raw_settings.png"
            },
            {
                'category': '빠르고 매끄러운 전환',
                'headline': '채팅 속 링크를 탭하면 –\nGoogle Maps가 즉시 열립니다!',
                'subtitle': '복사나 붙여넣기 없이 100% 자동 실행.',
                'raw_image': f"{RAW_DIR}/raw_chat.png",
                'is_chat': True,
                'callout_text_1': 'Apple Maps 링크 탭함',
                'callout_text_2': 'Google Maps에서 바로 열림!',
                'compat_title': '모든 메신저 완벽 호환',
                'compat_sub': '카카오톡, WhatsApp, Telegram, Signal, 문자',
                'nav_highlight': '즉시 길찾기 및 내비게이션 시작'
            },
            {
                'category': '개인정보 보호 및 핵심 기능',
                'headline': '100% 오프라인, 안전\n& Android Auto 지원',
                'subtitle': '인터넷 권한 0개. 극대화된 편리함.',
                'is_features': True,
                'feature_cards': [
                    ((16, 185, 129), "nav", "턴바이턴 & Android Auto", "Google Maps의 실시간 경로 안내를 즉시 실행합니다."),
                    ((99, 102, 241), "shield", "100% 오프라인 & 프라이버시", "인터넷 권한(0 Permissions) 및 데이터 추적이 없습니다."),
                    ((245, 158, 11), "pause", "일시중지 모드 & 빠른 타일", "알림창 퀵 패널에서 언제든 간편하게 제어하세요.")
                ],
                'trust_text': '100% 오픈소스 • 광고 없음 • 인앱 결제 없음'
            }
        ]
    },
    'zh-CN': {
        'tagline': '自动。无缝。极致隐私保护。',
        'badges': [
            ('zap', (245, 158, 11), '0ms 超低延迟'),
            ('nav', (16, 185, 129), 'Android Auto'),
            ('shield', (129, 140, 248), '0 隐私权限')
        ],
        'screens': [
            {
                'category': '全自动地图转换器',
                'headline': 'Apple Maps 链接\n直接在 Google Maps 打开',
                'subtitle': '全自动。无感后台。无缝切换。',
                'raw_image': f"{RAW_DIR}/raw_mainscreen.png"
            },
            {
                'category': '极简配置',
                'headline': '简单三步配置\n一键永久生效',
                'subtitle': '只需在系统设置中轻轻一点。',
                'raw_image': f"{RAW_DIR}/raw_settings.png"
            },
            {
                'category': '极速流畅',
                'headline': '点击聊天中的链接 –\nGoogle Maps 瞬间开启！',
                'subtitle': '无需手动复制粘贴，100% 全自动完成。',
                'raw_image': f"{RAW_DIR}/raw_chat.png",
                'is_chat': True,
                'callout_text_1': '点击了 Apple Maps 链接',
                'callout_text_2': '瞬间直接打开 Google Maps！',
                'compat_title': '全面兼容主流通讯工具',
                'compat_sub': '微信、WhatsApp、Telegram、Signal、短信与邮件',
                'nav_highlight': '一键直达原生路线规划与导航'
            },
            {
                'category': '隐私安全与核心功能',
                'headline': '100% 离线、安全\n完美支持 Android Auto',
                'subtitle': '零网络访问权限。畅享极致便捷。',
                'is_features': True,
                'feature_cards': [
                    ((16, 185, 129), "nav", "实时导航 & Android Auto", "立即启动 Google Maps 官方路线导航与车机投影。"),
                    ((99, 102, 241), "shield", "100% 离线 & 隐私安全", "无联网权限（0 Permissions），无追踪无广告。"),
                    ((245, 158, 11), "pause", "便捷暂停 & 快捷控制开关", "随时在通知栏下拉快捷面板中暂停与恢复。")
                ],
                'trust_text': '100% 开源项目 • 零广告 • 免费纯粹'
            }
        ]
    },
    'zh-TW': {
        'tagline': '自動。無縫。極致隱私安全。',
        'badges': [
            ('zap', (245, 158, 11), '0ms 零延遲'),
            ('nav', (16, 185, 129), 'Android Auto'),
            ('shield', (129, 140, 248), '0 隱私權限')
        ],
        'screens': [
            {
                'category': '自動地圖轉換器',
                'headline': 'Apple Maps 連結\n直接在 Google Maps 開啟',
                'subtitle': '自動。背景無感。無縫銜接。',
                'raw_image': f"{RAW_DIR}/raw_mainscreen.png"
            },
            {
                'category': '輕鬆設定',
                'headline': '簡單三步驟設定\n一次搞定終身受用',
                'subtitle': '只需在 Android 系統設定中點擊一下。',
                'raw_image': f"{RAW_DIR}/raw_settings.png"
            },
            {
                'category': '極速順暢',
                'headline': '輕觸聊天中的連結 –\nGoogle Maps 立即開啟！',
                'subtitle': '無需複製貼上，100% 全自動處理。',
                'raw_image': f"{RAW_DIR}/raw_chat.png",
                'is_chat': True,
                'callout_text_1': '點擊了 Apple Maps 連結',
                'callout_text_2': '直接開啟 Google Maps！',
                'compat_title': '完美相容所有通訊軟體',
                'compat_sub': 'LINE、WhatsApp、Telegram、Signal、簡訊與郵件',
                'nav_highlight': '立即展開精準路線導航'
            },
            {
                'category': '隱私安全與強大功能',
                'headline': '100% 離線、安全\n完美支援 Android Auto',
                'subtitle': '零網路權限。享受極致便利。',
                'is_features': True,
                'feature_cards': [
                    ((16, 185, 129), "nav", "路線導航 & Android Auto", "立即在 Google Maps 中展開即時語音導航。"),
                    ((99, 102, 241), "shield", "100% 離線與隱私安全", "無網路權限（0 Permissions），無追蹤無廣告。"),
                    ((245, 158, 11), "pause", "暫停模式 & 快捷控制磚", "隨時在下拉通知中心暫停與繼續重新導向。")
                ],
                'trust_text': '100% 開源 • 無廣告 • 完全免費'
            }
        ]
    },
    'ar': {
        'tagline': 'تلقائي. غير مرئي. أمان وخصوصية تامة.',
        'badges': [
            ('zap', (245, 158, 11), 'استجابة 0ms'),
            ('nav', (16, 185, 129), 'Android Auto'),
            ('shield', (129, 140, 248), '0 أذونات')
        ],
        'screens': [
            {
                'category': 'محول الخرائط التلقائي',
                'headline': 'فتح روابط Apple Maps\nفي Google Maps تلقائياً',
                'subtitle': 'تلقائي. غير مرئي. بدون خطوات إضافية.',
                'raw_image': f"{RAW_DIR}/raw_mainscreen.png"
            },
            {
                'category': 'إعداد بسيط وسريع',
                'headline': 'إعداد لمرة واحدة في\n3 خطوات سهلة',
                'subtitle': 'نقرة واحدة في إعدادات أندرويد تكفي.',
                'raw_image': f"{RAW_DIR}/raw_settings.png"
            },
            {
                'category': 'سريع وسلس',
                'headline': 'اضغط على الرابط في المحادثة –\nيفتح Google Maps فوراً!',
                'subtitle': 'بدون نسخ أو لصق، 100% تلقائي.',
                'raw_image': f"{RAW_DIR}/raw_chat.png",
                'is_chat': True,
                'callout_text_1': 'تم الضغط على رابط Apple Maps',
                'callout_text_2': 'يفتح مباشرة في Google Maps!',
                'compat_title': 'متوافق مع جميع تطبيقات المراسلة',
                'compat_sub': 'WhatsApp و Telegram و Signal و SMS والبريد',
                'nav_highlight': 'يبدأ الملاحة وتوجيه المسار فوراً'
            },
            {
                'category': 'الخصوصية والميزات',
                'headline': '100% بدون إنترنت وآمن\nومناسب لـ Android Auto',
                'subtitle': 'بدون أذونات إنترنت. راحة تامة.',
                'is_features': True,
                'feature_cards': [
                    ((16, 185, 129), "nav", "الملاحة خطوة بخطوة & Android Auto", "بدء مسار الملاحة الفعلي في Google Maps فوراً."),
                    ((99, 102, 241), "shield", "100% بدون إنترنت وأمان تام", "بدون إذن إنترنت (0 أذونات) وبدون تتبع أو إعلانات."),
                    ((245, 158, 11), "pause", "وضع الإيقاف المؤقت ولوحة التحكم", "إيقاف واستئناف التحويل في أي وقت من لوحة الإشعارات.")
                ],
                'trust_text': '100% مفتوح المصدر • بدون إعلانات • مجاني بالكامل'
            }
        ]
    },
    'ru-RU': {
        'tagline': 'Автоматически. Незаметно. Полная конфиденциальность.',
        'badges': [
            ('zap', (245, 158, 11), '0 мс задержки'),
            ('nav', (16, 185, 129), 'Android Auto'),
            ('shield', (129, 140, 248), '0 разрешений')
        ],
        'screens': [
            {
                'category': 'АВТОМАТИЧЕСКИЙ КОНВЕРТЕР',
                'headline': 'Ссылки Apple Maps\nв Google Maps',
                'subtitle': 'Автоматически. Незаметно. Без задержек.',
                'raw_image': f"{RAW_DIR}/raw_mainscreen.png"
            },
            {
                'category': 'ПРОСТАЯ НАСТРОЙКА',
                'headline': 'Настройка один раз в\n3 простых шага',
                'subtitle': 'Всего одно нажатие в настройках Android.',
                'raw_image': f"{RAW_DIR}/raw_settings.png"
            },
            {
                'category': 'БЫСТРО И УДОБНО',
                'headline': 'Нажмите на ссылку в чате –\nGoogle Maps сразу откроется!',
                'subtitle': 'Без копирования и вставки, 100% автоматически.',
                'raw_image': f"{RAW_DIR}/raw_chat.png",
                'is_chat': True,
                'callout_text_1': 'Нажата ссылка Apple Maps',
                'callout_text_2': 'Открывается сразу в Google Maps!',
                'compat_title': 'Совместимость со всеми мессенджерами',
                'compat_sub': 'Telegram, WhatsApp, Signal, SMS и почта',
                'nav_highlight': 'Мгновенный старт маршрута и навигации'
            },
            {
                'category': 'БЕЗОПАСНОСТЬ И ФУНКЦИИ',
                'headline': '100% Офлайн, Надежно\nи для Android Auto',
                'subtitle': 'Ноль интернет-разрешений. Максимальный комфорт.',
                'is_features': True,
                'feature_cards': [
                    ((16, 185, 129), "nav", "Маршруты и Android Auto", "Мгновенно запускает навигацию в Google Maps."),
                    ((99, 102, 241), "shield", "100% Офлайн и Приватно", "Без доступа в интернет (0 Permissions), без трекинга."),
                    ((245, 158, 11), "pause", "Режим паузы и быстрая плитка", "Приостанавливайте перенаправление в шторке уведомлений.")
                ],
                'trust_text': '100% Open Source • Без рекламы • Бесплатно'
            }
        ]
    },
    'id': {
        'tagline': 'Otomatis. Tak Terlihat. Privasi Terjaga.',
        'badges': [
            ('zap', (245, 158, 11), '0ms Latensi'),
            ('nav', (16, 185, 129), 'Android Auto'),
            ('shield', (129, 140, 248), '0 Izin (0 Permissions)')
        ],
        'screens': [
            {
                'category': 'PENGALIH PETA OTOMATIS',
                'headline': 'Buka Tautan Apple Maps\ndi Google Maps',
                'subtitle': 'Otomatis. Tak terlihat. Tanpa hambatan.',
                'raw_image': f"{RAW_DIR}/raw_mainscreen.png"
            },
            {
                'category': 'PENGATURAN MUDAH',
                'headline': 'Atur Sekali Saja dalam\n3 Langkah Mudah',
                'subtitle': 'Cukup satu ketukan di pengaturan Android.',
                'raw_image': f"{RAW_DIR}/raw_settings.png"
            },
            {
                'category': 'CEPAT & MULUS',
                'headline': 'Ketuk tautan di chat –\nGoogle Maps langsung terbuka!',
                'subtitle': 'Tanpa salin-tempel, 100% otomatis.',
                'raw_image': f"{RAW_DIR}/raw_chat.png",
                'is_chat': True,
                'callout_text_1': 'Tautan Apple Maps diketuk',
                'callout_text_2': 'Langsung terbuka di Google Maps!',
                'compat_title': 'Kompatibel dengan Semua Aplikasi Chat',
                'compat_sub': 'WhatsApp, Telegram, Signal, SMS, dan email',
                'nav_highlight': 'Langsung memulai navigasi dan panduan rute'
            },
            {
                'category': 'PRIVASI & FITUR UNGGULAN',
                'headline': '100% Offline, Aman\n& Siap untuk Android Auto',
                'subtitle': 'Nol izin internet. Kenyamanan maksimal.',
                'is_features': True,
                'feature_cards': [
                    ((16, 185, 129), "nav", "Navigasi & Android Auto", "Langsung memulai rute dan panduan di Google Maps."),
                    ((99, 102, 241), "shield", "100% Offline & Privasi", "Tanpa izin internet (0 Permissions), tanpa pelacakan."),
                    ((245, 158, 11), "pause", "Mode Jeda & Tombol Cepat", "Jeda pengalihan kapan saja dari panel notifikasi.")
                ],
                'trust_text': '100% Open Source • Tanpa Iklan • Gratis Sepenuhnya'
            }
        ]
    }
}

def main():
    import glob
    import shutil
    for locale, data in LOCALES.items():
        print(f"Generating store graphics for locale: {locale}...")
        for idx, screen in enumerate(data['screens'], 1):
            # For screen 1, look for a localized screengrab screenshot first
            if idx == 1:
                search_pattern = f"fastlane/metadata/android/{locale}/images/phoneScreenshots/main_screen_*.png"
                found_files = glob.glob(search_pattern)
                if found_files:
                    screen['raw_image'] = found_files[-1]
                    print(f"  Using localized screengrab screenshot for screen 1: {found_files[-1]}")
            
            # Create screen in temp output base
            output_path = f"{OUTPUT_BASE}/{locale}/screen_{idx}.png"
            create_screen(screen, output_path)
            
            # Copy to fastlane/metadata/android/
            fastlane_dest_dir = f"fastlane/metadata/android/{locale}/images/phoneScreenshots"
            os.makedirs(fastlane_dest_dir, exist_ok=True)
            shutil.copy(output_path, f"{fastlane_dest_dir}/screen_{idx}.png")
            
        # Feature Graphic
        feature_path = f"{OUTPUT_BASE}/{locale}/feature_graphic.png"
        
        feature_raw_image = f"{RAW_DIR}/raw_mainscreen.png"
        search_pattern = f"fastlane/metadata/android/{locale}/images/phoneScreenshots/main_screen_*.png"
        found_files = glob.glob(search_pattern)
        if found_files:
            feature_raw_image = found_files[-1]
            
        create_feature_graphic({
            'tagline': data['tagline'],
            'badges': data.get('badges'),
            'raw_image': feature_raw_image
        }, feature_path)
        
        # Copy feature graphic to fastlane
        fastlane_feat_dir = f"fastlane/metadata/android/{locale}/images"
        os.makedirs(fastlane_feat_dir, exist_ok=True)
        shutil.copy(feature_path, f"{fastlane_feat_dir}/featureGraphic.png")

if __name__ == "__main__":
    main()
