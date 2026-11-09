### 💖 Support Our Work

As an open-source, community-funded project, we operate on a very limited budget. If LeanType helps you daily, please consider supporting us on [GitHub Sponsors](https://github.com/sponsors/LeanBitLab) or [Open Collective](https://opencollective.com/leanbitlab-org). Sharing LeanType with friends and family makes a huge difference!

## 🚀 What's New in v4.2.4

### ✨ Highlights
- **Hardware Keyboard Overhaul**: Restored suggestions, auto-correction, and dictionary prediction when typing with physical/hardware keyboards. Fixed NumLock state handling that previously bypassed text composition, resolved physical numpad keycodes, and guarded dead-key combining against invalid code points.
- **App Profiles (Compatibility Quirks Engine)**: Introduced offline **App Profiles** (`AppQuirksManager`) for custom per-app compatibility settings, including direct commits, auto-space overrides, and per-app auto-correction toggles (resolving glitches such as Tasker underscore input).
- **Calibrated Cursor Gestures**: Refined swipe-up cursor movement and spacebar touchpad scrolling with calibrated velocity dampening, eliminating jitter, overshoot, and erratic cursor jumping.
- **Settings Overhaul & Reorganization**: Separated **Suggestions** and **Text Correction** into dedicated top-level categories, introduced coordinated preset sliders with fine-tuning drawers for confidence and aggressiveness, and indexed all sub-screens and granular settings in the in-app Search Registry.

### 🛠️ Improvements & Enhancements
- **Voice Routing & Settings Navigation**: Unified voice input routing across Offline Plugin, Online AI, System Voice, and Disabled states, and routed the system settings shortcut directly to Android's **Manage On-Screen Keyboards** page instead of the digital assistant picker.
- **Plugin Compatibility & Defensive Fallbacks**: Added backward-compatible checks for legacy translation plugin APKs lacking v2 interfaces and handled uninstalled voice plugins gracefully without requiring package installation permissions on F-Droid builds.
- **UI Polish**: Added rounded corners and ripple touch feedback to toolbar screenshot suggestions, and styled the floating gesture word preview with anti-aliased capsule pill rendering.
- **Stability & Crash Fixes**: Resolved a `MissingFormatArgumentException` crash in `MissingDictionaryDialog` on non-English system locales, converted arrow drawables to vector format, eliminated `requestLayout` during layout passes, and muted debug logcat spam.
- **Hardware Keyboard Emoji Navigation**: Added full D-Pad navigation, key event pairing, and selection support inside the emoji picker when using a physical keyboard.

## 📦 Choose Your Flavor

> ⚠️ **Flavor Merger Notice**: Starting in **v4.2.6**, `standardfull` will be merged into `standard`. In preparation, both flavors now share a unified release viewing mechanism with zero `REQUEST_INSTALL_PACKAGES` permission overhead. Users currently using `standardfull` can transition directly to `standard`.

| Flavor | Primary Focus | AI Engine | Plugins Setup | Internet | Release Updater |
|:---|:---|:---|:---|:---|:---|
| **`1-LeanType_4.2.4-standard-release.apk`** | **Recommended** | Cloud AI | In-app download or File import | Optional (AI/plugins) | ✅ View Release |
| **`1-LeanType_4.2.4-standardfull-release.apk`** | **Transitioning (Merging in v4.2.6)** | Cloud AI | In-app download or File import | Optional (AI/plugins) | ✅ View Release |
| **`2-LeanType_4.2.4-offline-release.apk`** | **Offline** | Local LLM Plugin (8.0+) | Browser download + File import | 🚫 Zero Internet (No Permission) | ❌ None |

> 💡 **Plugin Compatibility**: All flavors support **Offline Voice Dictation** (Android 8.1+), **Offline Translation** (Android 6.0+), **Offline Handwriting Recognition** (Android 6.0+), **Offline OCR Text Extraction** (Android 5.0+), and **Offline AI Proofreading** (Android 8.0+, 64-bit) via modular plugins, and work 100% offline.
