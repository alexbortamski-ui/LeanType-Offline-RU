## 🧪 LeanType 4.2.7 Beta (Build 3)

> [!NOTE]
> This is the **third pre-release testing build** for LeanType 4.2.7. Please test and report any issues or feedback on [GitHub Issues](https://github.com/LeanBitLab/LeanType/issues).

---

### ✨ Added
- **SMS OTP Auto-Detection**: Support for WebOTP (`@domain #code`), hashtag formats (`#code`), multi-digit carrier phrasing, and case-insensitive keyword boundaries.
- **Active Notification Scan**: Automatically scans existing active status bar notifications on startup or connection for incoming OTPs.
- **Proportional Popup Offset**: Popup key vertical offset is now calculated proportionally to keyboard height (0% to 8%, default 4%).
- **Unified Download Progress**: Added real-time percentage tracking and `LinearProgressIndicator` across dictionary downloads, plugins, libraries, models, and sound packs.
- **Dynamic Service Lifecycle**: Disables and unbinds notification listener component dynamically when auto-read OTP is toggled off, ensuring zero battery drain.

### 🐛 Fixed
- **Duplicate OTP Toggles & Permission Prompt**: Removed redundant OTP toggle in settings; fixed notification access permission request and direct settings navigation.
- **OTP Suggestion Strip Wipes**: Prevented initial recorrection (`INPUT_STYLE_RECORRECTION`) and empty clipboard evaluation from clearing external OTP suggestions.
- **Resource & Descriptor Leaks**: Fixed `ParcelFileDescriptor` leak in voice dictation bridge and wrapped file streams/ContentResolver queries in `use` blocks.
- **IME Lifecycle & Scope Teardown**: Hardened teardown with proper coroutine cancellation and safe cursor extraction in dictionary facilitator and clipboard history.

### 🔄 Changes
- **Default SMS App Focus**: Restricted OTP notification listener strictly to the system Default SMS app.
- **Key Preview Animations**: Stabilized preview popups with cubic bezier `PathInterpolator` curves (0.1, 0.9, 0.2, 1.0), 6% vertical travel, and 55ms dismissal.
- **Long-Press Popup Motion**: Replaced `OvershootInterpolator` with smooth deceleration and tightened initial scale to 0.84 to eliminate wobble.
- **Toolbar & Mode Transitions**: Smoothed entrance and exit animations for toolbar strip, suggestions, pinned keys, emoji palettes, clipboard, and touchpad.
- **Settings Naming**: Renamed "Animation speed" to "Animation scale" in Appearance settings to match Android system conventions.
- **Haptics Performance**: Cached vibrator primitive support queries to avoid redundant HAL roundtrips.
- **Binary & Asset Optimization**: Converted launcher foreground raster icon to lossless WebP and enabled dead code section stripping (`--gc-sections`) with hidden symbol visibility in native C++ libraries.

### 🗑️ Removed
- **Redundant SMS App Settings**: Removed "Allowed SMS app" dropdown preference and fallback allowlists in favor of automatic default SMS app detection.
- **Unused Build Properties**: Removed stale `adi-registration.properties`.

---

### 📦 Beta Build Artifacts

| File | Flavor | Description | Size |
|:---|:---|:---|:---:|
| `1-LeanType_4.2.7-standard-release.apk` | Standard | Full features & online/offline AI voice | - |
| `2-LeanType_4.2.7-offline-release.apk` | Offline | Fully air-gapped (zero internet permission) | - |
