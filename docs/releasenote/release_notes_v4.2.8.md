### 💖 Support Our Work

As an open-source, community-funded project, we operate on a very limited budget. If LeanType helps you daily, please consider supporting us on [GitHub Sponsors](https://github.com/sponsors/LeanBitLab) or [Open Collective](https://opencollective.com/leanbitlab-org). Sharing LeanType with friends and family makes a huge difference!

## 🚀 LeanType 4.2.8

---

### 🐛 Fixed
- **Punctuation & Backspace Input Freeze**: Resolved a severe UI freeze / ANR when typing punctuation (such as colons `:`) or backspacing by optimizing dictionary blacklist evaluation with instant O(1) hash lookups and lazy candidate scoring.
- **Android 12 Crash Loop on Keypress**: Resolved a fatal `NoSuchMethodError` crash loop on Android 12 (API 31/32) caused by `VibrationAttributes.createForUsage` (introduced in API 33), restoring smooth typing on affected devices.
- **Vibration Intensity Slider**: Restored vibration amplitude scaling when custom intensity is set, resolving an issue where the haptic slider only operated at full intensity or off on certain devices.
- **Crash Log Reporting in Release Builds**: Hardened crash report generation to use reliable internal storage and exposed internal crash reports in settings dialogs across release builds.

### ✨ Added
- **Auto-Capitalization Toolbar Controls & JSON Actions**: Added optional toolbar buttons, pinned controls, and custom JSON layout key mappings for toggling auto-capitalization and force auto-capitalization, with real-time state refresh and accessible screen reader descriptions.
- **Comprehensive Global Translations**: Reached 100% complete translation coverage across 25 target languages and regional variants for all recent settings and UI strings.

---

### 📦 Choose Your Flavor

| Flavor | Primary Focus | AI Engine | Plugins Setup | Internet | Release Updater | Size |
|:---|:---|:---|:---|:---|:---|:---:|
| **`1-LeanType_4.2.8-standard-release.apk`** | **Recommended** | Cloud AI | In-app download or File import | Optional (AI/plugins) | ✅ View Release | - |
| **`2-LeanType_4.2.8-offline-release.apk`** | **Offline** | Local LLM Plugin (8.0+) | Browser download + File import | 🚫 Zero Internet (No Permission) | ❌ None | - |

> 💡 **Plugin Compatibility**: All flavors support **Offline Voice Dictation** (Android 8.1+), **Offline Translation** (Android 6.0+), **Offline Handwriting Recognition** (Android 6.0+), **Offline OCR Text Extraction** (Android 5.0+), and **Offline AI Proofreading** (Android 8.0+, 64-bit) via modular plugins, and work 100% offline.
