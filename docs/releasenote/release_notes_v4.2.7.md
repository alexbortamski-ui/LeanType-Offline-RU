### 💖 Support Our Work

As an open-source, community-funded project, we operate on a very limited budget. If LeanType helps you daily, please consider supporting us on [GitHub Sponsors](https://github.com/sponsors/LeanBitLab) or [Open Collective](https://opencollective.com/leanbitlab-org). Sharing LeanType with friends and family makes a huge difference!

## 🚀 LeanType 4.2.7

---

### ✨ Added
- **Hexagonal Honeycomb Layout Engine (`hex_typewise` & `hex_qwerty`)**: Native integration of authentic Typewise and hexagonal QWERTY layouts directly into the keyboard engine pipeline. Features custom axial hexagonal hit testing, vector path rendering, interlocking row geometry, dual spacebars, and half-hex functional keys (Shift/Delete).
- **Landscape Hex Ergonomic Split**: In landscape orientation, hexagonal keys are ergonomically split into left and right clusters positioned at screen edges with a central gap, preserving comfortable key dimensions and enabling natural thumb reachability while maintaining vertical honeycomb alignment.
- **Storage & Cache Management Screen**: Dedicated management screen under **Plugins & Capabilities** providing categorized storage breakdowns (Dictionaries, Voice Models, Translation Models, Handwriting Models, Layouts, Backups, Caches), one-tap cache cleaning, and selective category purging.
- **Missing Dictionary Suggestion Strip Prompt**: Integrated missing dictionary detection directly into the suggestion strip with an idle banner prompt, dedicated `ic_dictionary_download` vector with accent tinting, and a native Compose in-keyboard download dialog attached to the window token.
- **Emoticon Sub-Categories with Localized Tabs**: Added categorized emoticon browsing (Happy, Sad, Surprised, Love, Anger, Animals, Actions, Other) with localized tab pills, auto-spanning, and touch isolation to prevent accidental category jump or drag interference.
- **System Wallpaper & Blurred Background**: Seamlessly use live or static device wallpaper as the keyboard background, powered by an optimized downsampled StackBlur engine (`FastBlurEngine`) with an adjustable blur intensity slider in Appearance settings.
- **Word-by-Word Delete Swipe Gesture**: Added a dedicated option and gesture engine for word-by-word backspace swipe deletion, allowing smooth deletion of entire words at a time.
- **SMS OTP Auto-Detection**: Support for WebOTP (`@domain #code`), hashtag formats (`#code`), multi-digit carrier phrasing, and automatic active status bar notification scanning on startup.
- **Physical Keyboard Layout Mapping**: Added physical keyboard layout preferences under **Settings → Languages & Layouts** with real-time hardware scan code translation (supporting QWERTY, QWERTZ, AZERTY, Dvorak, Colemak, Workman, or matching on-screen layout).
- **Dual-Layer Symbol Popups**: Alphabet keys now automatically inherit popups from both Symbols and More Symbols layouts, granting instant access to extended punctuation without switching modes.
- **Dynamic Custom Layout Profiles (1-5)**: Expanded secondary layout architecture with dynamic profile management in settings (add or remove up to 5 custom layout profiles) unified under `layouts/custom`.
- **Configurable Suggestion Count & Swipe Gestures**: Added customizable candidate word count (3 to 7) in Suggestions settings, sensitivity slider (10 to 60 dp) for vertical swipes, independent spacebar swipe-down gesture, and configurable toolbar swipe-up/down gestures.
- **Modernized Haptic Engine**: Upgraded tactile feedback on Android 11+ using `VibrationAttributes` (`USAGE_TOUCH`) and composition primitives (`PRIMITIVE_CLICK`, `PRIMITIVE_TICK`, `PRIMITIVE_LOW_TICK`).
- **Animation Scale & Speed Control**: Added an animation scale slider in Appearance settings (0.0x to 2.0x, with 0.0x as instant) and smooth entrance transitions for emoji palettes, clipboard history, and toolbar toggles.
- **Enhanced Gboard Text Editing Layout**: Bundled an enhanced Gboard-style text editing layout as default, featuring dedicated Close and Action/Enter keys alongside precision cursor navigation.
- **Personal Dictionary Sort by Last Added**: Added an option to sort personal dictionary words by last added timestamp alongside alphabetical sorting (#548).

### 🐛 Fixed
- **Word Replacement & Backspace Desync Fix**: Resolved cursor desync and text duplication during word replacement (such as suggestion replacement in web search fields) and rapid backspacing in `RichInputConnection`.
- **Backup & Restore UI Freeze Elimination**: Removed blocking `CountDownLatch` calls on the main looper during backup and restore operations, eliminating ANRs and keyboard freezes.
- **Mode Switch (`?123` / `ABC`) Input Protection**: Resolved a touch-tracker state transition issue where switching layouts on touch-down caused accidental spacebar or 'x' character insertions upon release due to differing key coordinates between alphabet and symbol layouts.
- **Spacebar Custom Popup Keys Grid Layout**: Fixed a visual regression where custom popup keys on the spacebar expanded to the physical spacebar width and collapsed into a single vertical column instead of organizing into a multi-column grid (#569).
- **Translation & Handwriting Storage Bloat**: Eliminated redundant model alias duplication and purged redundant storage copies during translation and handwriting model imports.
- **Custom Layout & Shortcut Key Crashes**: Fixed `RuntimeException: Unknown event` in `InputLogic` when activating custom layout profiles (`CUSTOM1` through `CUSTOM5`), OCR, or long-pressing toolbar keys mapped to custom shortcut keycodes.
- **Selection State in Navigation**: Preserved active text selection mode (`META_SHIFT_ON`) during `MOVE_START_OF_PAGE` and `MOVE_END_OF_PAGE` actions; automatically unselects active text upon toggling selection mode off or copying selected text to clipboard.
- **Popup Key Dimension & Overlap Fixes**: Proportional popup key offset (0% to 8% of keyboard height); fixed single-entry popup dimensions; prevented symbol contamination on functional keys (`SHIFT`, `DELETE`, `ACTION`, `SYMBOL_ALPHA`, etc.).
- **Resource & Lifecycle Hardening**: Fixed `ParcelFileDescriptor` leak in voice dictation; wrapped file streams/queries in `use` blocks; decoupled startup settings listeners; and fixed voice status `NullPointerException`.

### 🔄 Changed
- **Progressive Backspace Acceleration**: Refined hold-to-repeat acceleration curve and batched IPC deletion calls for low-latency continuous character and word deletion across composing and non-composing text (#510).
- **Gesture Haptics Smoothing & Pacing**: Gated cursor gliding and delete swipe vibrations to alternate steps (50% duty cycle) and softened tick intensities to eliminate motor chatter and battery drain.
- **Storage Settings Consolidation**: Centered Storage & Cache management exclusively under the Plugins & Capabilities hub for a streamlined settings hierarchy.
- **Unified Download Progress**: Added real-time percentage tracking and `LinearProgressIndicator` across dictionary downloads, plugins, libraries, models, and sound packs.
- **Dynamic Theme Action Key**: Hexagonal action keys now dynamically inherit active theme accent and Material You wallpaper colors with proper icon contrast and borders.
- **Comprehensive Global Translations**: Completed missing settings strings across 25+ languages and added localized Fastlane store metadata for 13 primary international languages.
- **Removed Ineffective Settings**: Removed non-functional "Hide app icon" toggle on Android 10+ and redundant SMS app allowlist dropdown in favor of automatic default SMS app detection.

---

### 📦 Choose Your Flavor

| Flavor | Primary Focus | AI Engine | Plugins Setup | Internet | Release Updater | Size |
|:---|:---|:---|:---|:---|:---|:---:|
| **`1-LeanType_4.2.7-standard-release.apk`** | **Recommended** | Cloud AI | In-app download or File import | Optional (AI/plugins) | ✅ View Release | - |
| **`2-LeanType_4.2.7-offline-release.apk`** | **Offline** | Local LLM Plugin (8.0+) | Browser download + File import | 🚫 Zero Internet (No Permission) | ❌ None | - |

> 💡 **Plugin Compatibility**: All flavors support **Offline Voice Dictation** (Android 8.1+), **Offline Translation** (Android 6.0+), **Offline Handwriting Recognition** (Android 6.0+), **Offline OCR Text Extraction** (Android 5.0+), and **Offline AI Proofreading** (Android 8.0+, 64-bit) via modular plugins, and work 100% offline.
