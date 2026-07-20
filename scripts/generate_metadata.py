import os
import re
import subprocess
import glob

def truncate_to_bytes(text, max_bytes=500, suffix="..."):
    """Play Store zählt UTF-8-Bytes, nicht Unicode-Zeichen.
    Sonderzeichen wie é, ñ, ó belegen 2 Bytes – daher bytebasiertes Kürzen."""
    encoded = text.encode('utf-8')
    if len(encoded) <= max_bytes:
        return text
    # Kürzen auf (max_bytes - Suffix-Größe), danach Suffix anhängen
    suffix_bytes = suffix.encode('utf-8')
    truncated = encoded[:max_bytes - len(suffix_bytes)]
    # Sicherstellen, dass kein halbiertes Multibyte-Zeichen entsteht
    return truncated.decode('utf-8', errors='ignore') + suffix

def get_latest_changelog(file_path):
    if not os.path.exists(file_path):
        return None
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Match the first ## [Version] header and everything until the next one
    match = re.search(r'## \d+\.\d+\.\d+.*?\n(.*?)(?=\n## \d+\.\d+\.\d+|$)', content, re.DOTALL)
    if match:
        lines = match.group(1).strip().split('\n')
        # Clean up lines (remove ###, **, etc. and keep it user-centric)
        cleaned_lines = []
        for line in lines:
            line = line.strip()
            if not line or line.startswith('###'): continue
            # Remove Markdown bold/italic
            line = re.sub(r'(\*\*|\*|__|_)', '', line)
            # Remove leading bullet points
            line = line.lstrip('- ').strip()
            if line:
                cleaned_lines.append(line)
        
        result = ". ".join(cleaned_lines)
        return truncate_to_bytes(result)
    return None

def main():
    # All supported app languages for Play Store release notes.
    # Play Store silently ignores locales not yet published – safe to add early.
    target_locales = {
        'de-DE': 'docs/CHANGELOG.md',
        'en-US': 'docs/CHANGELOG.en.md',
        'en-GB': 'docs/CHANGELOG.en.md', # English (UK)
        'en-IN': 'docs/CHANGELOG.en.md', # English (India)
        'fr-FR': None,
        'fr-CA': None,   # French (Canada)
        'it-IT': None,
        'es-ES': None,
        'es-US': None,   # Spanish (US)
        'es-419': None,  # Spanish (Latin America)
        'pt-PT': None,
        'pt-BR': None,   # Brazilian Portuguese
        'pl-PL': None,
        'nl-NL': None,
        'sv-SE': None,   # Swedish
        'tr-TR': None,   # Turkish
        'ru-RU': None,   # Russian
        'uk': None,       # Ukrainian – Play Store uses 'uk', not 'uk-UA'
        'nb-NO': None,   # Norwegian (Bokmål)
        'da-DK': None,   # Danish
        'ja-JP': None,   # Japanese
        'id': None,      # Indonesian
        'vi': None,      # Vietnamese
        'bn-IN': None,   # Bengali (India)
        'bn-BD': None,   # Bengali (Bangladesh)
        'mr-IN': None,   # Marathi
        'hi-IN': None,   # Hindi
        'zh-CN': None,   # Simplified Chinese
        'ko-KR': None,   # Korean
    }

    # deep-translator nutzt 'no' für Norwegisch Bokmål statt 'nb'
    LANG_CODE_MAP = {
        'nb': 'no',
        'zh': 'zh-CN',
        'es': 'es',      # es-419 / es-US → split ergibt 'es', passt direkt
    }
    
    changelog_en = get_latest_changelog('docs/CHANGELOG.en.md') or "Maintenance update and performance optimizations."
    
    # deep-translator statt googletrans: stabil, aktiv gewartet, unterstützt nb/no
    try:
        from deep_translator import GoogleTranslator
        translator_available = True
    except Exception as e:
        print(f"deep-translator setup failed: {e}")
        translator_available = False

    # New base directory for all metadata as requested by user
    dest_dir = 'release-notes'
    if os.path.exists(dest_dir):
        import shutil
        shutil.rmtree(dest_dir)
    os.makedirs(dest_dir, exist_ok=True)

    for locale, changelog_path in target_locales.items():
        # Flat naming convention: whatsnew-<locale>
        dest_file = f'{dest_dir}/whatsnew-{locale}'
        
        content = ""
        if changelog_path:
            content = get_latest_changelog(changelog_path)
        
        # Translation logic for non-DE/EN
        if not content:
            lang_code = locale.split('-')[0]
            # Mapping für Sprachen mit abweichenden Codes in deep-translator
            target_lang = LANG_CODE_MAP.get(lang_code, lang_code)
            if translator_available and changelog_en:
                try:
                    print(f"Translating for {locale}...")
                    translation = GoogleTranslator(source='en', target=target_lang).translate(changelog_en)
                    content = translation
                except Exception as e:
                    print(f"Translation failed for {locale}: {e}")
            
            # FINAL FALLBACK: Never leave it empty. Use English if translation failed.
            if not content:
                content = changelog_en
        
        if content:
            # Sanitize: remove double dots if they were generated
            content = content.replace("..", ".")
            # Bytebasiertes Kürzen: Play Store zählt UTF-8-Bytes, nicht Python-Zeichen.
            content = truncate_to_bytes(content)

            with open(dest_file, 'w', encoding='utf-8') as f:
                f.write(content)
            
            byte_len = len(content.encode('utf-8'))
            print(f"--- META FOR {locale} ({byte_len} bytes / {len(content)} chars) ---")
            print(content)
            print(f"Path: {dest_file}")

if __name__ == "__main__":
    main()
