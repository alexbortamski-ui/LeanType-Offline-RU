## 🧪 LeanType 4.2.7 Beta (Build 5)

> [!NOTE]
> This is the **fifth pre-release testing build** for LeanType 4.2.7. Please test and report any issues or feedback on [GitHub Issues](https://github.com/LeanBitLab/LeanType/issues).

---

### ✨ Added
- **Storage & Cache Management Screen**: Dedicated management screen under Plugins & Capabilities providing categorized storage breakdowns (Dictionaries, Voice Models, Translation Models, Handwriting Models, Layouts, Backups, Caches), one-tap cache cleaning, and selective category purging.
- **Word-by-Word Delete Swipe Gesture**: Added a dedicated option and gesture engine for word-by-word swipe deletion on the backspace key, allowing smooth deletion of entire words at a time.
- **Configurable Suggestion Count**: Added a setting in Suggestions to customize the number of displayed candidate words in the suggestion strip (from 3 to 7 candidates).
- **Configurable Vertical Swipe Threshold**: Added a sensitivity slider (10 dp to 60 dp) in Gesture Typing settings to customize the trigger distance for spacebar and toolbar vertical swipe gestures.
- **Personal Dictionary Sort by Last Added**: Added an option to sort personal dictionary words by last added timestamp alongside alphabetical sorting (#548).
- **Enhanced Gboard Text Editing Layout**: Bundled an enhanced Gboard-style text editing layout as default, featuring dedicated Close and Action/Enter keys alongside cursor navigation.
- **Modernized Welcome Setup Wizard**: Redesigned setup wizard landing screen with a clean text card, feature highlights, and refined typography.

### 🐛 Fixed
- **Spacebar Custom Popup Keys Grid Layout**: Fixed a visual regression where custom popup keys on the spacebar expanded to the physical spacebar width and collapsed into a single vertical column instead of organizing into a multi-column grid (#569).
- **Translation & Handwriting Storage Bloat**: Eliminated redundant model alias duplication and purged redundant storage copies during translation and handwriting model imports.
- **More Suggestions Swipe Gesture**: Fixed vertical swipe-up gesture on the suggestion strip to reliably open the expanded "More Suggestions" candidate grid.
- **Text Selection Auto-Dismiss**: Automatically unselects active text upon toggling selection mode off or copying selected text to the clipboard.
- **Non-Functional App Icon Hide Toggle**: Removed the ineffective "Hide app icon" setting and deprecated broadcast receiver logic on Android 10+.

### 🔄 Changed
- **Gesture Haptics Smoothing & Pacing**: Gated cursor gliding and delete swipe vibrations to alternate steps (50% duty cycle) and softened tick intensities to eliminate motor chatter and battery drain.
- **Continuous Backspace Acceleration**: Refined hold-to-repeat acceleration curve and batched IPC deletion calls for low-latency continuous character deletion.
- **Storage Settings Consolidation**: Centered Storage & Cache management exclusively under the Plugins & Capabilities hub for a streamlined settings hierarchy.

---

### 📦 Beta Build Artifacts

| File | Flavor | Description | Size |
|:---|:---|:---|:---:|
| `1-LeanType_4.2.7-standard-release.apk` | Standard | Full features & online/offline AI voice | - |
| `2-LeanType_4.2.7-offline-release.apk` | Offline | Fully air-gapped (zero internet permission) | - |
