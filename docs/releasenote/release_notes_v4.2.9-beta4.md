### 🐛 Fixed
- **Web Editor Suggestion Replacement**: Resolved issue where tapping a candidate from the suggestion strip while the cursor touched an existing word failed to replace it in browser and web inputs (Chrome, Brave, Firefox, and web fields), ensuring the word under the cursor is properly replaced instead of inserting text into it.
- **Subtype Switch Suggestion Strip**: Fixed suggestion strip failing to reset and clear outdated suggestions when switching keyboard subtypes or active input languages.
- **Locale & Language Synchronization**: Restored dynamic reloading of localized layout strings when switching system default language, and ensured the active subtype locale and app localized name are correctly displayed in missing dictionary download prompts.
- **Touch & Popup Dismissal Reliability**: Prevented touch event dropping during rapid multi-touch typing and resolved popup panel dismissal state across the main keyboard view, emoji page, and pointer tracker.
- **Zero-Permission Offline Voice Warning**: Improved detection for online voice AI transcription when internet connection is unavailable, displaying an immediate warning banner on the suggestion strip without requiring extra device network permissions.

### ✨ Added
- **2D Backspace Swipe Gesture**: Introduced 2D swipe gesture support from the Delete/Backspace key, enabling diagonal and vertical selection adjustments to quickly select and delete words or entire lines.
- **Dynamic Sound Wave Visualizer**: Expanded voice input visualizer to a full-width dynamic sound wave graph preview synchronized with the active keyboard theme, replacing static status text.
- **Voice Processing Shimmer Animation**: Added minimal uniform single-height bars with a traveling light shimmer effect during voice AI transcription processing.
- **Custom Date Formats in Text Expander**: Supported custom date formatting patterns within the `%date%` placeholder (e.g. `%date:yyyy-MM-dd%`, `%date:MMM d, yyyy%`, `%date:slug%`), with localized descriptions across 30 languages.
- **Per-App Always Show Suggestions**: Added an "Always Show Suggestions" toggle in App Profiles to force the suggestion strip to remain visible even in applications requesting no suggestions.
- **Per-App Hide Suggestion Strip & Toolbar**: Added dedicated per-app profile toggles to hide the suggestion strip and toolbar for specific apps.
- **Proportional Key Preview Sizing**: Added proportional key preview popup dimensions matching key size in normal and floating modes, along with customization sliders in Appearance settings for popup width scale, height scale, and corner radius.
- **Arabic Diacritics 2-Row Layout**: Matched GBoard's horizontal 2-row layout for Arabic diacritics and added support for custom period popups.

### 🔄 Changed
- **App Profiles Relocation**: Moved the App Profiles management section into the Plugins & Hub settings screen for cleaner preferences organization.
- **Sponsor Dialog Compose Migration**: Migrated the legacy `SponsorDialog` to a native Jetpack Compose `PreferenceDialog`.
- **Regex Pre-compilation & Performance**: Pre-compiled regex instances across `String.splitOnWhitespace`, dictionary classes, model importers, and text expander utilities to reduce CPU allocations during typing.
- **Voice Visualizer Performance**: Cached voice visualizer bar dimensions and eliminated per-frame `dpToPx` conversion overhead during live audio rendering.
- **Asset & Resource Cleanup**: Removed unused legacy drawables (`floating_drag_handle`, `toolbar_expand_key_background`) and polished voice warning copy across all 30 languages.
