### 💖 Support Our Work

As an open-source, community-funded project, we operate on a very limited budget. If LeanType helps you daily, please consider supporting us on [GitHub Sponsors](https://github.com/sponsors/LeanBitLab) or [Open Collective](https://opencollective.com/leanbitlab-org). Sharing LeanType with friends and family makes a huge difference!

## 🚀 LeanType 4.3.0-beta1

---

### ✨ Added
- **Exact Visual Line Delete Swipe (Android 14+) (#731)**: Integrated platform-native `requestTextBoundsInfo` (API 34+) to detect exact character bounding boxes and visual line wrap boundaries during multi-line delete swipes. Preserves horizontal caret column alignment during vertical swipes with non-blocking async execution, session-based app caching, and seamless fallback to character estimates on unsupported apps and browsers.
- **Settings Progressive Disclosure**: Reorganized settings categories across the app (Appearance, Preferences, Toolbar, Gesture Typing, and Advanced) with expandable fine-tuning sections. Keeps the primary settings interface clean, minimal, and intuitive for everyday users while preserving full functionality and instant discoverability via global search for advanced users.
- **Unified Expandable Preference Component**: Created a reusable `ExpandablePreference` Compose component with animated rotating chevron indicators and persistent state in `SharedPreferences`, unifying fine-tuning across all settings screens.
- **100% Complete Global Translations**: Achieved complete translation coverage across all 30 supported languages and regional variants for all newly added settings, fine-tuning categories, and descriptions.

### 🐛 Fixed
- **Universal Web & Remote Workspace Keyboard Support (TYPE_NULL)**: Permitted soft input display for web editors, browsers (e.g. Brave), terminal environments, PWAs, and remote IDEs that instantiate an active `InputConnection`, without requiring package hardcoding or previous focus triggers.
- **Fast Typing Shift State & Capitalization (#598)**: Reset unshifted-from-shifted transient state on non-shift key presses to prevent inadvertent caps lock during rapid typing, and preserved initial uppercase characters on touch down.
- **Dynamic Colors (Material You) Theme Refresh (#560)**: Evaluated theme attributes via a fresh configuration context to bypass cached resources, ensuring the keyboard immediately reflects system wallpaper and palette changes on Android 12+.
- **Touch Queue & Key Freeze Prevention (#542)**: Cleared active pointer tracker queues on cancellations, released stuck pressed key visuals, and dismissed popup panels on incoming touch down to prevent keyboard lockups and frozen keys.
- **Custom Emoji Font Compatibility & Validation (#528)**: Added glyph validation on pre-Android 13 devices to prevent blank emoji keyboards from unsupported COLRv1 fonts, and reloaded supported emoji caches upon font changes.
- **Edge Key Popup Proximity (#391)**: Used key hitbox bounds in `squaredDistanceToEdge` calculations to ensure reliable popup activation for keys along keyboard edges.
- **Shift Recapitalization Persistence**: Maintained typed word status when adjusting capitalization with Shift on the actively composing word.

### 🔄 Changed
- **Settings Screen Restructuring**: Grouped 40+ settings in Appearance into primary views with targeted fine-tune sections for theme borders, key popups/previews, and keyboard sizing/fonts; consolidated haptics, secondary keys, and clipboard history in Preferences; streamlined gesture trails and swipe gestures; and grouped hardware keyboard layouts in Advanced.
- **Automated Translation Tooling**: Integrated automated multi-locale string translation and validation workflows to ensure zero missing strings across target locale folders.

---

### 📦 Choose Your Flavor

| Flavor | Primary Focus | AI Engine | Plugins Setup | Internet | Release Updater | Size |
|:---|:---|:---|:---|:---|:---|:---:|
| **`1-LeanType_4.3.0-standard-debug.apk`** | **Recommended** | Cloud AI | In-app download or File import | Optional (AI/plugins) | ✅ View Release | - |
| **`2-LeanType_4.3.0-offline-debug.apk`** | **Offline** | Local LLM Plugin (8.0+) | Browser download + File import | 🚫 Zero Internet (No Permission) | ❌ None | - |

> 💡 **Plugin Compatibility**: All flavors support **Offline Voice Dictation** (Android 8.1+), **Offline Translation** (Android 6.0+), **Offline Handwriting Recognition** (Android 6.0+), **Offline OCR Text Extraction** (Android 5.0+), and **Offline AI Proofreading** (Android 8.0+, 64-bit) via modular plugins, and work 100% offline.
