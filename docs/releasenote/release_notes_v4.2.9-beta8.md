### 🐛 Fixed
- **Core Stability, Lifecycle Leaks & Concurrency**: Guaranteed beginBatchEdit/endBatchEdit integrity with try-finally, guarded async AI and translation completions against stale editor fields, resolved background ContentObserver leaks in ClipboardHistoryManager, and added robust dead object handling for voice plugin IPC calls.
- **Fast-Typing Auto-Shift Drop**: Adapted the second key to the unshifted layout when the shift key is released during rapid typing, preventing dropped or incorrect characters.
- **2D Backspace Gesture Upward & Diagonal Selection**: Restored full 2D delete swipe support across editor lines and words with accurate reverse line and boundary calculations.
- **Dynamic Toolbar Auto-Spanning**: Automatically span toolbar keys to fill available horizontal space across main, clipboard, and OCR toolbars without key shrinking, while capping at 42dp minimum key width to ensure 9+ keys smoothly scroll without overscroll wobble.
- **Bluetooth & Earphone Sound Output**: Silenced custom keyboard sound playback when headphones or Bluetooth audio devices are connected with "Play over Bluetooth" disabled, avoiding audio interruption during music or call playback.
- **Gesture Typing Preview Alignment & Tracking**: Aligned floating gesture preview balloon height with long-press popups and smoothed dynamic vertical finger tracking across all keyboard height configurations.
- **Clipboard Font Multiplier Scaling**: Restored proper clipboard history entry text sizing baseline and dynamically scaled entries with the Appearance font size multiplier.
- **Web Editor Selection Feedback Loop**: Mitigated selection loops in web browser editors by introducing span recovery debouncing and throttled cursor update requests.

### ✨ Added
- **Native x86_64 Support for Waydroid**: Added x86_64 ABI filter to debug builds for seamless Waydroid testing, alongside native compilation flags (-O3, -fvectorize, -funroll-loops) for accelerated C++ dictionary and engine operations.

### 🔄 Changed
- **First-Word Suggestion Removal**: Removed obsolete first-word suggestion toggle and cleanly decoupled suggestion linkages across 29 languages.
- **Keystroke Latency & Cache Optimizations**: Cached TextExpander preferences in memory, cached applied key border radii, avoided layout thrashing in suggestion strips, and kept sound pools warm across input sessions to reduce typing latency.
- **Memory & Resource Trim**: Added low-memory trimming for offline AI and sound managers, and offloaded package browser scanning to background threads.
- **Localization Updates**: Localized hold-to-delete words and privacy clipboard strings across 29 target languages.
