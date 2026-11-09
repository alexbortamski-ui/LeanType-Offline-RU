### 🐛 Fixed
- **Composing Region Crash**: Fixed `StringIndexOutOfBoundsException` in `setComposingRegion` triggered during cursor space-swiping in custom and hybrid editors (such as ChatGPT) when cursor positions and cached text reload out of sync.
- **Pasting Long Text (>1000 Chars)**: Removed the 1000-character threshold bypass in clipboard history and input logic, resolving failures and crashes when pasting long clipboard entries into certain apps (#614).
- **Suggestion Strip Close Button**: Enlarged dismiss touch targets to 44dp across clipboard, screenshot, and OTP suggestions; eliminated edge dead zones; and resolved re-inflation loops upon dismissal.
- **Key Touch Coordinate Correction**: Dynamically update `KeyDetector` coordinate corrections upon `MainKeyboardView` padding changes, ensuring accurate key touch detection when keyboard padding changes.

### ✨ Added
- **100% Translation Coverage**: Achieved complete 100% translation coverage across all 30 supported languages, translating thousands of previously missing settings and engine strings.
- **Voice Settings Localization**: Fully localized voice settings screens, speech language categories, provider options, mic permissions, and download status badges into string resources.
- **Emoji Dictionary Localization**: Localized dictionary type labels (`Emojis` / `Main dictionary`) in the available dictionaries list and translated `Emoji for <Language> words` headers into the user's active language.

### 🔄 Changed
- **Clipboard Visuals & Animations**: Refined clipboard item long-press scale animation, added theme-aware ripple feedback, and removed redundant border strokes on action pills and selected items.
- **Emoji Palette & Toolbar Download Labels**: Replaced hardcoded English download buttons and toast messages in emoji palettes and split toolbar with localized string resources.
- **Rendering Performance**: Optimized character and string width calculations in `TypefaceUtils` using `Paint.measureText`, avoiding unnecessary layout allocations.
- **Portuguese Translation Polish**: Updated `%clipboard:slug%` tag description to `Área de transferência (URL kebab-slug)` and text capitalization in Text Recognition settings to `Iniciais Maiúsculas (Capitalizar palavras)`.
