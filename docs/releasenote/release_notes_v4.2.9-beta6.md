### 🐛 Fixed
- **Unified Bottom Row Geometry**: Synchronized vertical position and key height of the spacebar row across main keyboard, emoji palettes, clipboard history, search overlays, numpad, and text edit modes using a centralized `KeyboardBottomRowGeometry` to eliminate visual shifting and layout jumping.
- **Emoji & Emoticon Display & Tab Freezes**: Prevented emoticon recents overlap and font overflow with proportional text scaling in `EmojiPageKeyboardView`, and disabled smooth scroll on category switches to eliminate ViewPager2 animation lockups during rapid tab tapping.
- **Arabic Alif Diacritic Popup Order**: Restored intuitive letter ordering for the Arabic Alif popup keys to a familiar 5-letter layout (آ ء أ إ ٱ), preserving muscle memory by keeping primary variants centrally positioned.
- **Dual Toolbar Emoticons & Recents**: Removed redundant recents page when using dual toolbar mode and scaled emoticon items properly within the top strip.
- **GIF Picker UI & Config Overflow**: Cleaned up GIF search bar styling by removing redundant backgrounds, fixed configuration button overflow in settings, and resolved loading flicker.
- **Touchpad Transparency & Fullscreen Surface**: Preserved temporary touchpad background transparency and expanded the active fullscreen touch surface for smoother cursor movement.

### ✨ Added
- **Full Keyboard Horizontal Swipe Gesture**: Added configurable full keyboard horizontal swipe gestures (switch language, move cursor, toggle numpad, hide keyboard, or none), allowing users to switch languages or trigger actions by swiping across any part of the keyboard.
- **Automatic Mutual Exclusion with Gesture Typing**: Seamlessly turns off gesture typing when full keyboard swipe actions are enabled (and vice versa) to prevent touch conflicts, while tuning swipe thresholds to safeguard normal typing and popup keys.
- **Granular Backup & API Key Export**: Separated custom wordlists (Dictionaries) and learned history (Typing History) into distinct selectable categories in Backup & Restore, and added an optional API Keys category with security confirmation to export encrypted AI proofreading credentials.
- **Horizontal Swipe Threshold Slider**: Added a dedicated threshold slider under gesture settings to fine-tune full keyboard swipe distance and sensitivity.

### 🔄 Changed
- **GIF Provider Toggle Simplification**: Simplified provider management to a clean Enable/Disable toggle and removed obsolete Tenor entries from API key link mappings.
- **OCR Regex Optimization**: Pre-compiled line formatting regex (`BULLET_REGEX`) in `OcrTextFormatter` to eliminate object allocations on text recognition hot paths.
- **Asset & Resource Cleanup**: Removed unused vector drawable `ic_dollar.xml`.
- **Unit Test Coverage Expansion**: Added comprehensive Robolectric unit tests for `TextRange` validating cursor boundaries and suggestion span merges.
- **Localization Updates**: Translated full keyboard swipe gestures, threshold settings, and blocked word prompts across 29 supported languages.
