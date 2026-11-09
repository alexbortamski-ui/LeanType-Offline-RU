### 💖 Support Our Work

As an open-source, community-funded project, we operate on a very limited budget. If LeanType helps you daily, please consider supporting us on [GitHub Sponsors](https://github.com/sponsors/LeanBitLab) or [Open Collective](https://opencollective.com/leanbitlab-org). Sharing LeanType with friends and family makes a huge difference!

## 🚀 LeanType 4.2.9

---

### ✨ Added
- **GIF Search & Media Plugin Integration**: Full integration with the companion GIF plugin via secure IPC AIDL contracts and SHA-256 certificate pinning (supporting GIPHY and Tenor providers). Features a modern picker interface with category tabs (Recent, GIFs, Stickers, Memes, Emojis), capsule search bar, live preview on long press, dynamic connection binding, and direct WhatsApp sticker insertion with automatic WebP normalization.
- **CJK (Chinese, Japanese & Korean) Plugin Engine**: Added modular architecture to load the companion CJK engine plugin from internal storage, supporting Pinyin Chinese, Japanese Kana/Romaji, and native Korean Hangul combiner with a dedicated candidate strip UI, dynamic keyboard theme coloring, and dedicated CJK settings screen.
- **Full Keyboard Horizontal Swipe Gesture**: Added configurable full-keyboard horizontal swipe gestures (switch language, move cursor, toggle numpad, hide keyboard, or none), allowing users to switch languages or trigger actions by swiping across any part of the keyboard, complete with sensitivity slider and automatic mutual exclusion with gesture typing.
- **2D Backspace Swipe Gesture**: Introduced 2D delete swipe gesture support from the Backspace key, enabling diagonal and vertical selection adjustments to quickly select and delete words or entire lines, with horizontal-right immediate deselection.
- **Auto Floating Keyboard in Landscape**: Added an option under Window settings to automatically switch to floating keyboard mode when rotating to landscape orientation, and smoothly restore docked mode upon returning to portrait.
- **Clipboard In-Keyboard Action Pill & Multi-Select**: Added a Gboard-style compact floating action pill on long press (Pin/Unpin, Edit, Delete, Select) anchored directly next to clipboard items, along with batch deletion, batch pinning/unpinning, and Select All / Deselect All controls.
- **Hold Delete to Delete Whole Words**: Added an option under Text Correction to continuously delete whole words instead of single characters after holding down the Delete key.
- **Granular Backup & Encrypted API Key Export**: Separated custom wordlists (Dictionaries) and learned history (Typing History) into distinct selectable categories in Backup & Restore, with an optional encrypted API Keys export category with security confirmation.
- **Dynamic Sound Wave & Harmonic Voice Visualizer**: Expanded voice input visualizer to a full-width dynamic sound wave graph preview synchronized with the active keyboard theme, featuring organic acoustic harmonic waves, traveling light shimmer during voice AI processing, and a screen wake lock option during voice recording.
- **Custom Date Formats in Text Expander**: Supported custom date formatting patterns within the `%date%` placeholder (e.g. `%date:yyyy-MM-dd%`, `%date:MMM d, yyyy%`, `%date:slug%`), with localized descriptions across 30 languages.
- **Per-App Suggestion Strip & Toolbar Controls**: Added dedicated App Profile toggles to force "Always Show Suggestions" or selectively hide the suggestion strip and toolbar for specific apps.
- **Secondary & Clipboard Font Scale Multipliers**: Added independent font scaling settings for secondary keys/sub-labels and clipboard entries.
- **Proportional Key Preview Dimensions & Stepper Dialogs**: Added proportional key preview popup dimensions matching key size in normal and floating modes, with customization sliders in Appearance settings for width scale, height scale (up to 200%), and corner radius, alongside plus/minus stepper buttons across all preference sliders.
- **Shift Word Recapitalization & Center Typed Word**: Added options under Text Editing settings to cycle word capitalization with Shift (lowercase -> Title Case -> UPPERCASE) and to always show the actively typed word in the center slot of the suggestion strip.
- **Sensitive Data Filtering in Clipboard History**: Added a privacy toggle to automatically ignore passwords (marked with EXTRA_IS_SENSITIVE) and one-time numeric verification codes (4 to 8 digits) from clipboard history.
- **Haptic Feedback Engine Selection**: Added an option in preferences to select between system haptic feedback engines, restoring crisp Pixel haptic feedback.
- **Native x86_64 Support**: Added x86_64 ABI filter to debug builds for seamless Waydroid testing, alongside native compilation flags (-O3, -fvectorize, -funroll-loops) for accelerated C++ dictionary and engine operations.

### 🐛 Fixed
- **Internal Text Fields Gesture Typing & Swipe Delete**: Enabled gesture typing (with floating preview bubble and gesture trails) across internal search and edit fields (Emoji search, Clipboard search & edit mode, GIF search) and unified 2D swipe delete with cursor navigation.
- **Web Editor & Chromium Suggestion Replacement**: Resolved cursor desync and text corruption when selecting suggestion strip words in Chrome, Brave, Firefox, Edge Omnibox, and web inputs, utilizing debounced span recovery and selection-based replacement.
- **Core Stability, Lifecycle Leaks & Memory Protection**: Guaranteed `beginBatchEdit`/`endBatchEdit` integrity with `try-finally`, guarded async AI and translation completions against stale editor fields, resolved `ContentObserver` leaks in `ClipboardHistoryManager`, protected active clipboard views from memory trim deallocations, and added dead object handling for voice IPC calls.
- **Composing Region Crash & Long Text Paste (>1000 Chars)**: Eliminated `StringIndexOutOfBoundsException` in `setComposingRegion` during cursor space-swiping in custom/hybrid editors (such as ChatGPT), and removed the 1000-character threshold bypass that caused failures when pasting long clipboard entries into certain apps (#614).
- **Text Expander Cursor Positioning & Separators**: Fixed cursor positioning after expanding shortcuts and prevented unwanted separator insertion inside expanded text (#703).
- **Multi-Touch Shift Chording & Rapid Typing**: Restored uppercase letter input when chording characters while holding down the Shift key, and adapted subsequent keys to the unshifted layout during fast typing to prevent dropped or incorrect characters (#598).
- **Unified Bottom Row Geometry**: Synchronized vertical position and key height of the spacebar row across main keyboard, emoji palettes, clipboard history, search overlays, numpad, and text edit modes using centralized geometry to eliminate visual shifting and layout jumping.
- **Voice Dictation in Browser Address Bars**: Allowed voice input in browser Omnibox and URL address bars (#516).
- **Claude Temperature & AI Omission**: Allowed disabling temperature and auto-omitted unsupported temperature values for Anthropic Claude reasoning models (#690).
- **Theme Custom Colors & Action Key Contrast**: Resolved missing custom colors on popup keys and suggestion strip backgrounds, ensured action key and popup key colors render accurately in "Show All Colors" mode (#681), and preserved child toolbar arrangements across theme reloads.
- **Clipboard Screenshot Suggestions**: Prioritized newest timestamps and prevented persistent or stuck screenshot suggestions from freezing the suggestion strip (#322).
- **Full-Screen Touchpad Exit & Toolbar Recovery**: Resolved toolbar and suggestion strip disappearing upon exiting full-screen touchpad mode, properly preserving manual toolbar expansion states and preventing auto-hide overrides.
- **Audio Routing & Bluetooth Isolation**: Silenced custom keyboard sound playback over headphones or Bluetooth audio devices when "Play over Bluetooth" is disabled, avoiding audio interruption during music or call playback.
- **Subtype Switch & Secondary View State**: Fixed suggestion strip failing to reset when switching subtypes, and preserved active secondary views (emoji, clipboard, GIF picker, OCR, handwriting) when restarting input on reactive search bars in Compose apps.
- **Suggestion Strip Dismiss Touch Targets**: Enlarged dismiss touch targets to 44dp across clipboard, screenshot, and OTP suggestions, eliminating edge dead zones and re-inflation loops upon dismissal.
- **Emoji Grid Alignment & ViewPager2 Lockups**: Centered emoji grid in key fit mode, added fallback descriptions for Unicode 16.0 emojis on long press, and disabled smooth scroll on category switches to eliminate ViewPager2 animation lockups.
- **Arabic Diacritics & Alif Popups**: Matched Gboard horizontal 2-row layout for Arabic diacritics, isolated Arabic secondary symbols order from English, and restored intuitive 5-letter Alif popup ordering (آ ء أ إ ٱ).
- **Backspace Lag & Double Capitalization**: Resolved backspace lag in Obsidian and accelerated web editor deletions, and prevented double capitalization during rapid typing and chording.

### 🔄 Changed
- **100% Complete Global Translations**: Achieved complete translation coverage across 30 supported languages and regional variants, including all recent settings, voice screens, CJK hub, GIF categories, and custom icon labels.
- **Regex Pre-compilation & Typing Hot-Path Optimization**: Pre-compiled regex patterns across `TextExpanderUtils`, `LanguageDetector`, `DictionaryFacilitatorImpl`, `AndroidWordLevelSpellCheckerSession`, `VoiceInputManager`, and `OcrTextFormatter`, eliminating object allocations and CPU overhead during typing.
- **Allocation-Free ASCII Diacritic Normalization**: Replaced runtime diacritic regex in `Suggest.kt` with allocation-free ASCII normalization.
- **Dynamic Toolbar Auto-Spanning**: Automatically span toolbar keys to fill available horizontal space across main, clipboard, and OCR toolbars without key shrinking, while capping at 42dp minimum key width for smooth horizontal scrolling.
- **App Profiles & Storage Settings Relocation**: Centered App Profiles and Storage & Cache management exclusively under the Plugins & Capabilities hub for a streamlined preferences hierarchy.
- **Migrated Legacy Dialogs to Compose**: Migrated legacy `SponsorDialog` and alert dialogs to native Jetpack Compose `PreferenceDialog` and `ThreeButtonAlertDialog` with adaptive `FlowRow` layouts.
- **Resource & Asset Cleanup**: Removed obsolete dimensions, unused legacy drawables, and deprecated debug XML strings.
- **Robolectric Unit Test Expansion**: Added extensive unit test suites covering `LanguageDetector`, `TextRange`, `ScriptUtils`, `SpannableStringUtils`, `LocaleUtils`, `CombinedFormatUtils`, `RecapitalizeMode`, `LayoutType`, and `ExecutorUtils`.

---

### 📦 Choose Your Flavor

| Flavor | Primary Focus | AI Engine | Plugins Setup | Internet | Release Updater | Size |
|:---|:---|:---|:---|:---|:---|:---:|
| **`1-LeanType_4.2.9-standard-release.apk`** | **Recommended** | Cloud AI | In-app download or File import | Optional (AI/plugins) | ✅ View Release | - |
| **`2-LeanType_4.2.9-offline-release.apk`** | **Offline** | Local LLM Plugin (8.0+) | Browser download + File import | 🚫 Zero Internet (No Permission) | ❌ None | - |

> 💡 **Plugin Compatibility**: All flavors support **Offline Voice Dictation** (Android 8.1+), **Offline Translation** (Android 6.0+), **Offline Handwriting Recognition** (Android 6.0+), **Offline OCR Text Extraction** (Android 5.0+), and **Offline AI Proofreading** (Android 8.0+, 64-bit) via modular plugins, and work 100% offline.
