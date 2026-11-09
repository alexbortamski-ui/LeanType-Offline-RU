## 🧪 LeanType 4.2.7 Beta (Build 4)

> [!NOTE]
> This is the **fourth pre-release testing build** for LeanType 4.2.7. Please test and report any issues or feedback on [GitHub Issues](https://github.com/LeanBitLab/LeanType/issues).

---

### ✨ Added
- **System Wallpaper & Blurred Background**: Seamlessly use your live or static device wallpaper as the keyboard background, paired with an optimized downsampled StackBlur engine (`FastBlurEngine`) and adjustable blur intensity slider in Appearance settings.
- **Dynamic Custom Layout Profiles (1-5)**: Expanded secondary layout architecture with dynamic profile management in settings (add or remove up to 5 custom layout profiles) and unified storage under `layouts/custom`.
- **Dual-Layer Symbol Popups**: Alphabet keys now automatically inherit popups from both Symbols and More Symbols layouts, granting instant access to extended punctuation (brackets, tilde, angle brackets) without switching modes.
- **Progressive Backspace Acceleration**: Holding down the delete key now accelerates smoothly from character deletion into word-by-word deletion across both composing and non-composing text.
- **Independent Downward Spacebar Swipe**: Added a dedicated swipe-down gesture setting on the spacebar with customizable action routing and configurable vertical distance thresholds.
- **Configurable Toolbar Swipe Gestures**: Added independent vertical swipe-up and swipe-down gestures on the suggestion/toolbar strip, neatly grouped under Gesture Typing settings.
- **Prebuilt Gboard Editing Layout**: Bundled a standard Gboard-style directional text editing layout (`editing_gboard.json`) for quick cursor positioning and block selection.

### 🐛 Fixed
- **Custom Layout & Shortcut Crash**: Fixed `RuntimeException: Unknown event` in `InputLogic` when activating custom layout profiles (`CUSTOM1` through `CUSTOM5`), OCR, or long-pressing toolbar keys mapped to custom shortcut keycodes.
- **Selection State in Page Navigation**: Preserved active text selection mode (`META_SHIFT_ON`) during `MOVE_START_OF_PAGE` and `MOVE_END_OF_PAGE` actions rather than collapsing selection bounds.
- **Custom Spacebar Popups**: Fixed an issue where custom popup keys on the spacebar failed to restore on long press.
- **Functional Key Popup Contamination**: Prevented symbol hints and popup characters from contaminating functional keys (`SHIFT`, `DELETE`, `ACTION`, `SYMBOL_ALPHA`, `ALPHA`, `NUMPAD`, `LANGUAGE_SWITCH`).
- **Split Keyboard Consistency**: Enforced split layout geometry consistently across all alphabet and symbol keyboard elements.

### 🔄 Changed
- **Haptic Feedback Alignment**: Aligned gesture cursor gliding and trackball movement with physical keypress vibration primitives and clamped duration boundaries for unified tactile response.
- **Settings Hierarchy Polish**: Relocated toolbar swipe gesture options under the primary Gesture Typing preference category for cleaner navigation.

---

### 📦 Beta Build Artifacts

| File | Flavor | Description | Size |
|:---|:---|:---|:---:|
| `1-LeanType_4.2.7-standard-release.apk` | Standard | Full features & online/offline AI voice | - |
| `2-LeanType_4.2.7-offline-release.apk` | Offline | Fully air-gapped (zero internet permission) | - |
