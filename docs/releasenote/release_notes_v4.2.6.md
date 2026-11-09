### 💖 Support Our Work

As an open-source, community-funded project, we operate on a very limited budget. If LeanType helps you daily, please consider supporting us on [GitHub Sponsors](https://github.com/sponsors/LeanBitLab) or [Open Collective](https://opencollective.com/leanbitlab-org). Sharing LeanType with friends and family makes a huge difference!

## 🚀 What's New in v4.2.6

### ✨ Highlights
- **Floating Window Layout & Dead Space Resolution**: Completely eliminated the persistent bottom dead space inside the floating keyboard window. By intercepting Android 15/16 window insets dispatch, dynamically disabling `fitsSystemWindows` on floating subviews, clamping occupied heights during measure passes, and performing a deferred re-measurement after layout transitions, the floating bubble now hugs key rows seamlessly with zero extraneous padding.
- **Flavor Consolidation (`standardfull` Merged into `standard`)**: Successfully finalized the flavor streamlining announced in v4.2.5. The legacy `standardfull` flavor has been merged directly into `standard`. LeanType is now officially published as two clean, focused flavors (`standard` and `offline`), both operating with zero `REQUEST_INSTALL_PACKAGES` permission overhead. Existing `standardfull` users can install the `standard` APK directly as an in-place upgrade.
- **`TYPE_NULL` & Dialer Search Field Input**: Resolved an issue where input fields configured with `TYPE_NULL` (such as phone dialer search inputs and generic app search boxes) dropped input or caused keyboard dismissal. Explicit user show requests are now strictly respected.

### 🛠️ Improvements & Enhancements
- **Bluetooth Keypress Audio Interception**: Fixed an issue where custom sound pack audio tracks interrupted car stereos and Bluetooth headsets by triggering AVRCP "Now Playing" playback sessions. LeanType now automatically falls back to native system UI click effects on connected Bluetooth A2DP devices, with an optional user toggle (**Settings → Plugins → Keypress Audio → Play custom sounds over Bluetooth**).
- **Floating Popup Keys & Preview Clamping**: Corrected horizontal bounds calculation for key preview popups to prevent screen edge overflow in floating mode, and aligned popup rows strictly to parent key space coordinates.
- **Stray Navigation Bar Artifact Suppression**: Fixed an edge-case where a transparent navigation bar artifact could linger during floating mode transitions.
- **Pinned Selection State Fix**: Fixed an issue where the pinned text selection mode toggle in the suggestion strip did not reflect its active state properly.
- **Theme Contrast & Auto-Reload**: Improved high-contrast color evaluation for toolbar key icons and enhanced automatic reloading when the system switches between dark and light themes.

## 📦 Choose Your Flavor

> ℹ️ **Flavor Streamlining Completed**: As announced in v4.2.5, `standardfull` has been completely merged into `standard`. Users previously using `standardfull` can update directly using the `standard` APK with full data preservation.

| Flavor | Primary Focus | AI Engine | Plugins Setup | Internet | Release Updater |
|:---|:---|:---|:---|:---|:---|
| **`1-LeanType_4.2.6-standard-release.apk`** | **Recommended** | Cloud AI | In-app download or File import | Optional (AI/plugins) | ✅ View Release |
| **`2-LeanType_4.2.6-offline-release.apk`** | **Offline** | Local LLM Plugin (8.0+) | Browser download + File import | 🚫 Zero Internet (No Permission) | ❌ None |

> 💡 **Plugin Compatibility**: All flavors support **Offline Voice Dictation** (Android 8.1+), **Offline Translation** (Android 6.0+), **Offline Handwriting Recognition** (Android 6.0+), **Offline OCR Text Extraction** (Android 5.0+), and **Offline AI Proofreading** (Android 8.0+, 64-bit) via modular plugins, and work 100% offline.
