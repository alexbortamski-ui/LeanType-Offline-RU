### 💖 Support Our Work

As an open-source, community-funded project, we operate on a very limited budget. If LeanType helps you daily, please consider supporting us on [GitHub Sponsors](https://github.com/sponsors/LeanBitLab) or [Open Collective](https://opencollective.com/leanbitlab-org). Sharing LeanType with friends and family makes a huge difference!

## 🚀 LeanType 4.3.0-beta2

---

### ✨ Added
- **Foldable & Large Display Fine-Tuning (#400)**: Added dedicated fine-tuning controls in Appearance settings for foldable devices, tablets, and large screens (keyboard scale factor, side padding, and split keyboard center gap). Features automatic screen profile and aspect ratio detection without requiring manual toggle overrides.
- **Tamil V4 Layout Support**: Added the Tamil V4 layout as an optional selectable keyboard layout across Tamil locales (India, Sri Lanka, Singapore, Malaysia), dynamically available without cluttering system input methods.
- **Context-Aware Custom AI Toolbar Keys**: Automatically hides unconfigured custom AI prompt keys in toolbar customization and pinned keys dialogs until configured in AI settings, reducing clutter.

### 🐛 Fixed
- **Browser & Dialog Window Focus Retention**: Configured input method picker dialogs with non-focusable window flags and outside touch handling to prevent stealing window focus from host web browsers (such as Brave and Chrome), and properly intercepted the Back key to dismiss the dialog instead of closing the keyboard.
- **Window Background Flash on Keyboard Launch**: Eliminated white and black window background flashes when opening the keyboard by applying a transparent platform theme and zero-delay window animation from frame zero.
- **Shift Suggestion Recapitalization & Backspace Completions (#692)**: Enabled Shift key recapitalization on accepted word suggestions (cycling lowercase, capitalized, and uppercase), and restored backspace completions for words chosen from suggestions.
- **Caps Lock Cold Start Persistence (#743)**: Prevented caps lock state from incorrectly persisting across cold starts and newly opened input fields by verifying cursor position and resetting shift state on fresh connections.
- **Decoupled Secondary Font Scaling & Slider Precision (#589)**: Decoupled secondary symbol font size from primary label font scale, allowing secondary key symbols to scale independently, and resolved floating-point rounding jitter on slider step adjustments.
- **Persian Tatweel Period Popup Alignment (#732)**: Positioned tatweel as the default first popup key for the period key in the Persian layout.

### 🔄 Changed
- **Animation Speed & Transition Tightening**: Eliminated scale and translation delays from key preview popups for instant keystroke feedback with a crisp 35ms exit fade, removed vertical jumps on view switching (Emoji, Clipboard, Main), and tightened transition durations across secondary views, toolbar expand/collapse, and popup key panels.
- **Settings Drawer & Section Simplification**: Streamlined drawer section headers across all 30 languages by removing redundant "Fine-tune: " prefixes for cleaner navigation.
- **Settings Switch & Dialog Icon Polish**: Eliminated switch recycling animation flashes during settings list scrolling, and optimized vector icon tinting and rendering across selection and reorder dialogs.
- **Unit Test Coverage Expansion (#744)**: Added comprehensive unit test suites for `IntentUtils`, `ScreenProfile`, `SubtypeLocaleUtils`, and `KeyboardParser`.

---

### 📦 Choose Your Flavor

| Flavor | Primary Focus | AI Engine | Plugins Setup | Internet | Release Updater | Size |
|:---|:---|:---|:---|:---|:---|:---:|
| **`1-LeanType_4.3.0-standard-debug.apk`** | **Recommended** | Cloud AI | In-app download or File import | Optional (AI/plugins) | ✅ View Release | - |
| **`2-LeanType_4.3.0-offline-debug.apk`** | **Offline** | Local LLM Plugin (8.0+) | Browser download + File import | 🚫 Zero Internet (No Permission) | ❌ None | - |

> 💡 **Plugin Compatibility**: All flavors support **Offline Voice Dictation** (Android 8.1+), **Offline Translation** (Android 6.0+), **Offline Handwriting Recognition** (Android 6.0+), **Offline OCR Text Extraction** (Android 5.0+), and **Offline AI Proofreading** (Android 8.0+, 64-bit) via modular plugins, and work 100% offline.
