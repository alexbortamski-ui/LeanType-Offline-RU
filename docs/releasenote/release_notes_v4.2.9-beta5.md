### 🐛 Fixed
- **Chromium Suggestion Replacement**: Implemented selection-based word replacement for Chromium-based browser editors (Chrome, Brave, Edge Omnibox, etc.) to resolve replacement corruption when tapping suggestions, configurable via App Quirks.
- **Secondary View Preservation**: Preserved active secondary views (clipboard history, emoji palettes, GIF picker, OCR, handwriting) when restarting input on the same editor (such as reactive search bars in Compose apps), preventing unintended reset back to the alphabetical keyboard.
- **2D Backspace Swipe Deselect Drift**: Filtered out accidental upward vertical drift (unintended line selection) during rightward backspace swipe deselect by enforcing trajectory angle thresholds.
- **Adaptive Dialog & Tag Layouts**: Prevented text truncation and button wrapping in `ThreeButtonAlertDialog` and preference tag chips using adaptive `FlowRow` layouts.
- **Key Preview Stem Alignment**: Dynamically elevated the key preview stem to maintain seamless visual connection when adjusting popup vertical offsets.
- **Clipboard Search Focus Churn**: Marked legacy clipboard search bar as non-focusable to prevent focus hijacking and layout churn on view transitions.

### ✨ Added
- **GIF Search & Media Plugin Integration**: Added full integration with external GIF plugin via secure IPC AIDL contracts and SHA-256 certificate pinning, supporting GIPHY and Tenor providers with direct content URI committing (`commitContent`).
- **Modern GIF Picker Interface**: Introduced a modern picker view featuring category tabs (Recent, GIFs, Stickers, Memes, Emojis), capsule search bar, rounded card thumbnails, infinite scroll pagination, and live preview on long press.
- **Dedicated GIF Settings Screen**: Added Compose-based GIF settings screen accessible from Plugins & Hub, with provider selection, safe content toggles, local media management, and dynamic connection binding.
- **Voice Screen Wake Lock**: Added an option under Voice Settings to keep the screen awake during speech-to-text recording and processing, with automatic release on idle or dismiss.
- **Harmonic Voice Visualizer Animation**: Refined voice visualizer with organic acoustic harmonic waves, synchronized integer wave harmonics, continuous cross-fading, and adaptive frame pacing to save battery during silence.
- **AI Proofreading Max Completion Tokens**: Added support for `max_completion_tokens` parameter for OpenAI reasoning models (o1, o3, etc.) with automatic fallback recovery on HTTP 400 errors.
- **Expanded Key Popup Height Slider**: Increased key popup height scale upper bound to 200% in Appearance settings for improved visibility.

### 🔄 Changed
- **Spellchecker Regex Performance**: Pre-compiled regex instances for quotes, periods, and punctuation in `AndroidWordLevelSpellCheckerSession` hot path to eliminate per-keystroke allocations.
- **Voice Recognition Hot-Path Optimization**: Pre-compiled punctuation regex in `VoiceInputManager` to reduce garbage collection overhead during dictation.
- **Asset & Resource Cleanup**: Removed unused legacy drawables (`ic_screenshot`, `floating_resize_handle`) and guarded AIDL interfaces with R8 keep rules.
- **Unit Test Coverage Expansion**: Added comprehensive unit test suites for `ScriptUtils` and `SpannableStringUtils`.
- **Localization Updates**: Translated forward delete key, App Profile badges and toggles, GIF categories and providers, and voice screen wake toggle across 29 languages.
