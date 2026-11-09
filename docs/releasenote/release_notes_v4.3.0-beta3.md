### 💖 Support Our Work

As an open-source, community-funded project, we operate on a very limited budget. If LeanType helps you daily, please consider supporting us on [GitHub Sponsors](https://github.com/sponsors/LeanBitLab) or [Open Collective](https://opencollective.com/leanbitlab-org). Sharing LeanType with friends and family makes a huge difference!

## 🚀 LeanType 4.3.0-beta3

---

### 🐛 Fixed
- **Fast-Typing Second Key Uppercase (#743, #598)**: Refined pointer tracker queue inspection during rapid typing and multi-finger gestures to ensure one-shot shift is properly consumed by the initial character, preventing inadvertent capitalization of the second letter.
- **Word Suggestion Resumption on Backspace (#692)**: Restored word suggestion strip resumption when backspacing after accepting suggestions, with graceful selection fallback for editors that do not support composing regions (such as Chromium browsers).
- **Folded Foldable Landscape Profile (#400)**: Correctly identified folded foldable devices in landscape orientation as compact profiles based on smallest screen width, preventing unintended tablet and large-display layout expansion.
- **Toolbar Configuration & AI Key Delimiters (#745)**: Repaired toolbar delimiter migration, deduplicated preferences, preserved hidden keys during reordering, and enabled custom action keycodes for custom AI keys.
- **Toolbar Non-Vector Drawable Crash Prevention**: Guarded icon rendering in toolbar customization to safely draw bitmap and layer drawables, preventing crashes in Compose when previewing non-vector icons.
- **Holo Icon Style Action Keys & Inheritance (#400)**: Completed missing action keys (Go, Send, Done, Next, Previous) for the Holo icon theme and added fallback inheritance from Material icons to prevent blank action keys.

---

### 📦 Choose Your Flavor

| Flavor | Primary Focus | AI Engine | Plugins Setup | Internet | Release Updater | Size |
|:---|:---|:---|:---|:---|:---|:---:|
| **`1-LeanType_4.3.0-standard-debug.apk`** | **Recommended** | Cloud AI | In-app download or File import | Optional (AI/plugins) | ✅ View Release | - |
| **`2-LeanType_4.3.0-offline-debug.apk`** | **Offline** | Local LLM Plugin (8.0+) | Browser download + File import | 🚫 Zero Internet (No Permission) | ❌ None | - |

> 💡 **Plugin Compatibility**: All flavors support **Offline Voice Dictation** (Android 8.1+), **Offline Translation** (Android 6.0+), **Offline Handwriting Recognition** (Android 6.0+), **Offline OCR Text Extraction** (Android 5.0+), and **Offline AI Proofreading** (Android 8.0+, 64-bit) via modular plugins, and work 100% offline.
