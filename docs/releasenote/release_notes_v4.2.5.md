### 💖 Support Our Work

As an open-source, community-funded project, we operate on a very limited budget. If LeanType helps you daily, please consider supporting us on [GitHub Sponsors](https://github.com/sponsors/LeanBitLab) or [Open Collective](https://opencollective.com/leanbitlab-org). Sharing LeanType with friends and family makes a huge difference!

## 🚀 What's New in v4.2.5

### ✨ Highlights
- **Native Floating Window Migration**: Re-engineered the floating keyboard from an overlay window into native IME window architecture using `TOUCHABLE_INSETS_REGION`. Completely eliminated the sensitive `SYSTEM_ALERT_WINDOW` permission and setup wizard step, added pass-through background touch handling, bottom control bar, multi-touch tracking, and persistent mode memory.
- **Privacy Hardening & Permission Removal**: Completely removed `READ_CONTACTS` permission and eliminated the contact dictionary. Removed `REQUEST_INSTALL_PACKAGES` permission from `standardfull`, transitioning both `standard` and `standardfull` to direct GitHub Releases viewing in preparation for the v4.2.6 flavor merger.
- **App Profiles (Compatibility Quirks Engine)**: Added per-app **Allow Symbol Composing** to keep symbols (such as underscores `_`) inside the active composing span instead of immediately committing them (resolving input filter rejections in apps like Tasker for `%output_var`), and added **Force Non-Incognito** to force learning/suggestions in private fields.

### 🛠️ Improvements & Enhancements
- **Web & Browser Text Editor Fix**: Exempted web text editors (Gecko/Firefox contenteditable, Chromium) from transient `TYPE_NULL` focus suppression, preventing dropped characters and composing lockups.
- **Offline GGUF Translation Hardening**: Hardened on-device translation output parsing, stripped `<think>...</think>` tags from reasoning models, and improved local model alias synchronization.
- **Atomic Dictionary Staging & Validation**: Hardened dictionary downloads with atomic file staging and strict file size verification to prevent corrupted dictionary states.
- **Voice Plugin Polling & Log Optimization**: Silenced repetitive `getModelState` logcat polling and scaled background check intervals for lower power consumption.

## 📦 Choose Your Flavor

> ⚠️ **Flavor Merger Notice**: Starting in **v4.2.6**, `standardfull` will be merged into `standard`. In preparation, both flavors now share a unified release viewing mechanism with zero `REQUEST_INSTALL_PACKAGES` permission overhead. Users currently using `standardfull` can transition directly to `standard`.

| Flavor | Primary Focus | AI Engine | Plugins Setup | Internet | Release Updater |
|:---|:---|:---|:---|:---|:---|
| **`1-LeanType_4.2.5-standard-release.apk`** | **Recommended** | Cloud AI | In-app download or File import | Optional (AI/plugins) | ✅ View Release |
| **`1-LeanType_4.2.5-standardfull-release.apk`** | **Transitioning (Merging in v4.2.6)** | Cloud AI | In-app download or File import | Optional (AI/plugins) | ✅ View Release |
| **`2-LeanType_4.2.5-offline-release.apk`** | **Offline** | Local LLM Plugin (8.0+) | Browser download + File import | 🚫 Zero Internet (No Permission) | ❌ None |

> 💡 **Plugin Compatibility**: All flavors support **Offline Voice Dictation** (Android 8.1+), **Offline Translation** (Android 6.0+), **Offline Handwriting Recognition** (Android 6.0+), **Offline OCR Text Extraction** (Android 5.0+), and **Offline AI Proofreading** (Android 8.0+, 64-bit) via modular plugins, and work 100% offline.
