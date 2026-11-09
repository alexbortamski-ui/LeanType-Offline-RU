### 🐛 Fixed
- **Multi-Touch Shift Chording**: Restored uppercase letter input when chording characters while holding down the Shift key, eliminating dropped capitalizations during rapid two-finger typing.
- **Full-Screen Touchpad Exit & Toolbar Recovery**: Resolved toolbar and suggestion strip disappearing upon exiting full-screen touchpad mode, properly preserving manual toolbar expansion states and preventing auto-hide overrides.
- **Auto-Caps Text Cache Fallback**: Prevented auto-capitalization errors and crashes when the input connection text cache is momentarily unavailable or out of sync.
- **Shift Recapitalization Caps Lock Release**: Automatically release caps lock state once the recapitalized word has completed typing.
- **Delete Swipe & Capitalization Consistency**: Refined touch interaction to prevent delete swipe from conflicting with capitalization states.
- **Clipboard Edit Mode Functional Popups**: Restored functional popup keys and ensured automatic unshift after typing in the clipboard edit view.
- **Emoji Grid Alignment & Long-Press Descriptions**: Centered emoji grid in key fit mode, added regex support for Unicode 16.0 emojis, and added fallback descriptions for newer emojis on long press.
- **Theme Icon Colors & Accent Feedback**: Preserved icon colors and accent press feedback when "Show all colors" mode is enabled.
- **GIF Picker Layout Switching**: Prevented unwanted toolbar display and premature panel dismissal when switching layouts while inside the GIF picker.

### ✨ Added
- **CJK (Chinese, Japanese & Korean) Plugin Engine**: Added full architecture to load the companion CJK engine plugin from internal storage, supporting Chinese, Japanese, and Korean typing with dedicated candidate strip UI, dynamic keyboard theme coloring, native Hangul combiner, and companion APK import.
- **Hold Delete to Delete Whole Words**: Added an option under Text Correction to continuously delete whole words instead of single characters after holding down the Delete key.
- **Skip Sensitive Data in Clipboard History**: Added a privacy toggle to automatically ignore passwords (marked with EXTRA_IS_SENSITIVE) and one-time numeric codes (4 to 8 digits) from clipboard history.
- **Stepper Buttons on Settings Sliders**: Added plus/minus step buttons and value snapping to slider preference dialogs across settings for precise adjustment.
- **Dedicated CJK Preferences Screen**: Added a CJK settings screen in Plugins & Hub with engine status, uninstall/app-info redirection, and localized strings across 29 languages.

### 🔄 Changed
- **Diacritic Normalization Performance**: Eliminated DIACRITIC_REGEX in Suggest.kt in favor of allocation-free ASCII diacritic normalization on the typing hot path.
- **Asset & Resource Cleanup**: Removed unused legacy drawables (keyboard_key_feedback_more_background_lxx_light, ocr_progress_two_tone.xml).
- **Unit Test Coverage Expansion**: Added comprehensive Robolectric unit test suites for LocaleUtils and CombinedFormatUtils.
- **Localization Updates**: Translated CJK settings, hold-to-delete options, and privacy clipboard toggles across supported locales.
