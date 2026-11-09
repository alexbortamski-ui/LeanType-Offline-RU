### 💖 Support Our Work

As an open-source, community-funded project, we operate on a very limited budget. If LeanType helps you daily, please consider supporting us on [GitHub Sponsors](https://github.com/sponsors/LeanBitLab) or [Open Collective](https://opencollective.com/leanbitlab-org). Sharing LeanType with friends and family makes a huge difference!

## 🚀 LeanType 4.3.0

---

### ✨ Added
- **Exact Visual Line Delete Swipe (Android 14+) (#731)**: Integrated platform-native `requestTextBoundsInfo` (API 34+) to detect exact character bounding boxes and visual line wrap boundaries during multi-line delete swipes. Preserves horizontal caret column alignment during vertical swipes with non-blocking async execution, session-based app caching, immediate horizontal-right deselection, and seamless fallback to character estimates on unsupported apps and browsers.
- **Foldable & Large Display Ergonomics (#400)**: Added dedicated fine-tuning controls in Appearance settings for foldable devices, tablets, and large displays (keyboard scale factor, side padding, and split keyboard center gap). Features automatic screen profile detection (`LARGE` for tablets/unfolded foldables, `COMPACT` for folded in portrait/landscape) without requiring manual profile toggles.
- **Settings Progressive Disclosure**: Reorganized settings categories across the app (Appearance, Preferences, Toolbar, Gesture Typing, and Advanced) with expandable fine-tuning sections. Keeps the primary settings interface clean, minimal, and intuitive for everyday users while preserving full functionality and instant discoverability via global search for advanced users.
- **Chinese Full-Width Punctuation & CJK Symbols Layout**: Integrated direct input of full-width Chinese punctuation marks (comma `，`, period `。`, question mark `？`, exclamation `！`, colon `：`, semicolon `；`), added a dedicated CJK symbols layout, and enabled real-time preedit synchronization with the companion CJK plugin.
- **Offline AI Long Text Scaling**: Dynamically scaled maximum generation tokens based on input text length (scaling up to 1,536 tokens) and expanded the context limit buffer to 3,072 tokens, preventing truncated completions when proofreading long paragraphs.
- **Standalone Blocked Words Import & Export (#687)**: Added modular import and export for blocked words under Text Correction settings, supporting plain word lists, language tags, comments, and automatic deduplication.
- **Tamil V4 Standard Layout**: Added the standardized modern Tamil V4 layout as an optional selectable keyboard layout across Tamil locales (India, Sri Lanka, Singapore, Malaysia), dynamically available without cluttering system input methods.
- **Reusable Expandable Preference Component**: Created a reusable `ExpandablePreference` Compose component with animated rotating chevron indicators and persistent state in `SharedPreferences`, unifying fine-tuning across all settings screens.
- **Context-Aware Custom AI Toolbar Keys**: Automatically hides unconfigured custom AI prompt keys in toolbar customization and pinned keys dialogs until configured in AI settings, reducing clutter.

### 🐛 Fixed
- **Universal Web & Remote Workspace Keyboard Support (TYPE_NULL)**: Permitted soft input display for web editors, browsers (e.g. Brave), terminal environments, PWAs, and remote IDEs that instantiate an active `InputConnection`, without requiring package hardcoding or previous focus triggers.
- **Fast-Typing Shift State & Multi-Touch Chording (#743, #598)**: Reset unshifted-from-shifted transient state on non-shift key presses to prevent inadvertent caps lock during rapid typing, preserved initial uppercase characters on touch down, and refined pointer tracker queue inspection to prevent second key capitalization during multi-touch chording.
- **Word Suggestion Resumption on Backspace (#692)**: Restored word suggestion strip resumption when backspacing after accepting suggestions, with graceful selection fallback for editors that do not support composing regions (such as Chromium browsers).
- **Shift Recapitalization & Caps Lock Cold Start Persistence (#692, #743)**: Enabled Shift key recapitalization on accepted word suggestions (cycling lowercase, capitalized, and uppercase), maintained typed word status when adjusting capitalization with Shift, and prevented caps lock state from incorrectly persisting across cold starts.
- **Browser & Dialog Window Focus Retention**: Configured input method picker dialogs with non-focusable window flags and outside touch handling to prevent stealing window focus from host web browsers (such as Brave and Chrome), and properly intercepted the Back key to dismiss the dialog instead of closing the keyboard.
- **Zero-Delay Transparent Platform Theme & Flash Elimination**: Eliminated white and black window background flashes when opening the keyboard by applying a transparent platform theme and zero-delay window animation from frame zero.
- **Dynamic Colors (Material You) Theme Refresh (#560)**: Evaluated theme attributes via a fresh configuration context to bypass cached resources, ensuring the keyboard immediately reflects system wallpaper and palette changes on Android 12+.
- **Custom Emoji Font Compatibility & Validation (#528)**: Added glyph validation on pre-Android 13 devices to prevent blank emoji keyboards from unsupported COLRv1 fonts, and reloaded supported emoji caches upon font changes.
- **Toolbar Non-Vector Drawable Safety & Custom AI Actions (#745)**: Guarded icon rendering in toolbar customization to safely draw bitmap and layer drawables without crashing painterResource in Compose, repaired toolbar delimiter migration, deduplicated preferences, preserved hidden keys during reordering, and enabled custom action keycodes for custom AI keys.
- **Holo Icon Style Action Keys & Inheritance (#400)**: Completed missing action keys (Go, Send, Done, Next, Previous) for the Holo icon theme and added fallback inheritance from Material icons to prevent blank action keys.
- **Decoupled Secondary Font Scaling & Slider Precision (#589)**: Decoupled secondary symbol font size from primary label font scale, allowing secondary key symbols to scale independently, and resolved floating-point rounding jitter on slider step adjustments.
- **Touch Queue Reset & Edge Proximity Hitboxes (#542, #391)**: Cleared active pointer tracker queues on cancellations, released stuck pressed key visuals, dismissed popup panels on incoming touch down, and used key hitbox bounds in `squaredDistanceToEdge` calculations for edge keys.
- **Persian Tatweel Period Popup Alignment (#732)**: Positioned tatweel as the default first popup key for the period key in the Persian layout.
- **Folded Foldable Landscape Profile Classification (#400)**: Correctly identified folded foldable devices in landscape orientation as compact profiles based on smallest screen width, preventing unintended tablet and large-display layout expansion.

### 🔄 Changed
- **Key Preview Animation Speed & Transition Tightening**: Eliminated scale and translation delays from key preview popups for instant keystroke feedback with a crisp 35ms exit fade, removed vertical jumps on view switching (Emoji, Clipboard, Main), and tightened transition durations across secondary views, toolbar expand/collapse, and popup key panels.
- **Settings Screen Restructuring & Drawer Simplification**: Streamlined drawer section headers across all 30 languages by removing redundant "Fine-tune: " prefixes, eliminated switch recycling animation flashes during settings list scrolling, and optimized vector icon tinting and rendering across selection and reorder dialogs.
- **Update Notification Badging Scope**: Scoped update red dot indicators specifically to the Updates section and keyboard updater to reduce clutter across other settings categories.
- **100% Complete Global Translations**: Achieved complete translation coverage across all 30 supported languages and regional variants for all newly added settings, fine-tuning categories, and descriptions.
- **Robolectric Unit Test Suite Expansion (#744)**: Added comprehensive unit test suites for `IntentUtils`, `ScreenProfile`, `SubtypeLocaleUtils`, `KeyboardParser`, and settings helpers.

---

### 📦 Choose Your Flavor

| Flavor | Primary Focus | AI Engine | Plugins Setup | Internet | Release Updater | Size |
|:---|:---|:---|:---|:---|:---|:---:|
| **`1-LeanType_4.3.0-standard-release.apk`** | **Recommended** | Cloud AI | In-app download or File import | Optional (AI/plugins) | ✅ View Release | ~12.8 MB |
| **`2-LeanType_4.3.0-offline-release.apk`** | **Offline** | Local LLM Plugin (8.0+) | Browser download + File import | 🚫 Zero Internet (No Permission) | ❌ None | ~12.5 MB |

> 💡 **Plugin Compatibility**: All flavors support **Offline Voice Dictation** (Android 8.1+), **Offline Translation** (Android 6.0+), **Offline Handwriting Recognition** (Android 6.0+), **Offline OCR Text Extraction** (Android 5.0+), and **Offline AI Proofreading** (Android 8.0+, 64-bit) via modular plugins, and work 100% offline.
