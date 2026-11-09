### 💖 Support Our Work

As an open-source, community-funded project, we operate on a very limited budget. If LeanType helps you daily, please consider supporting us on [GitHub Sponsors](https://github.com/sponsors/LeanBitLab) or [Open Collective](https://opencollective.com/leanbitlab-org). Sharing LeanType with friends and family makes a huge difference!

## 🚀 What's New in v4.2.3

### ✨ Highlights
- **Web & Browser Typing Stability**: Resolved contenteditable word duplication, cursor jump desyncs, and stutter syllable predictions in browser input fields and web text editors.
- **Online AI Voice Input (Standard Flavor)**: Added optional cloud-based speech-to-text dictation supporting Groq Whisper, Gemini, and OpenAI-compatible endpoints with full RFC 2046 compliance, while maintaining strict air-gapped zero-network isolation in the offline flavor.
- **Procedural Soft Sound Packs**: Added 6 new zero-latency custom sound packs to the fallback catalog, including the procedurally synthesized restoration of **Soft Pudding (Synth)**, **Muted Marshmallow**, **Felted Thock**, **Membrane Squish**, **Cork Tap**, and **Velvet Whisper**.
- **Visual Shift & Caps Lock Distinction**: Redesigned Shift key arrow icons with clear visual states: outlined arrow for normal unshifted, filled arrow for shifted single-character, and underlined arrow for locked Caps Lock.

### 🛠️ Improvements & Enhancements
- **Immediate Clipboard Suggestions**: Copied text now surfaces immediately in the suggestion strip upon copy without delay or prediction conflicts.
- **Independent Action Key Corner Radius**: Added a dedicated corner radius slider for the Action/Enter key in Theme Settings, allowing custom pill or rectangular styling independent of other functional keys.
- **Dynamic Theme Action Accent**: Restored vibrant primary accent styling for the action key in dynamic light theme modes.
- **Auto-Show Toolbar**: Added new preference to automatically display the toolbar when no word suggestions are present.
- **Settings UX Polish**: Streamlined Voice Input settings with dynamic option gating and eliminated duplicate IME window insets and keyboard glitches across dialog screens.

## 📦 Choose Your Flavor

| Flavor | Primary Focus | AI Engine | Plugins Setup | Internet | Self-Updater |
|:---|:---|:---|:---|:---|:---|
| **`1-LeanType_4.2.3-standardfull-release.apk`** | **Convenience (Recommended)** | Cloud AI | In-app download or File import | Optional (AI/Updates/plugins) | ✅ In-App Auto Update |
| **`1-LeanType_4.2.3-standard-release.apk`** | **F-Droid** | Cloud AI | In-app download or File import | Optional (AI/plugins) | ❌ None |
| **`2-LeanType_4.2.3-offline-release.apk`** | **Offline** | Local LLM Plugin (8.0+) | Browser download + File import | 🚫 Zero Internet (No Permission) | ❌ None |

> 💡 **Plugin Compatibility**: All flavors support **Offline Voice Dictation** (Android 8.1+), **Offline Translation** (Android 6.0+), **Offline Handwriting Recognition** (Android 6.0+), **Offline OCR Text Extraction** (Android 5.0+), and **Offline AI Proofreading** (Android 8.0+, 64-bit) via modular plugins, and work 100% offline.
