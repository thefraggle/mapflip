import os
import sys

sys.path.append(os.path.dirname(os.path.abspath(__file__)))
from update_playstore_listing import LISTINGS

base_dir = os.path.abspath(os.path.join(os.path.dirname(__file__), '..', 'fastlane', 'metadata', 'android'))
os.makedirs(base_dir, exist_ok=True)

for locale, data in LISTINGS.items():
    loc_dir = os.path.join(base_dir, locale)
    os.makedirs(loc_dir, exist_ok=True)
    
    with open(os.path.join(loc_dir, 'title.txt'), 'w', encoding='utf-8') as f:
        f.write(data['title'].strip())
        
    with open(os.path.join(loc_dir, 'short_description.txt'), 'w', encoding='utf-8') as f:
        f.write(data['shortDescription'].strip())
        
    with open(os.path.join(loc_dir, 'full_description.txt'), 'w', encoding='utf-8') as f:
        f.write(data['fullDescription'].strip())

print("Fastlane metadata generated successfully for all locales!")
