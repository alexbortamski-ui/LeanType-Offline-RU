### 🐛 Fixed
- **Clipboard Card Background**: Prevented clipboard item cards from losing background color and borders after exiting selection mode.
- **Screenshot Suggestion Strip**: Enlarged dismiss touch target to 38dp and prevented suggestion strip from freezing by resetting external view visibility immediately upon dismissal.
- **TalkBack Accessibility**: Added missing accessibility labels, content descriptions, and touch target polish to decorative views, touchpads, and clear search actions.
- **Portuguese Translations**: Corrected Portuguese (pt-BR/pt/pt-PT) translations for AI API keys ("Chave configurada") and background services ("Monitoramento da área de transferência", "Ativo").
- **Translation Spacing & Verbs**: Fixed missing space formatting after numbers across 29 locales and updated Pin/Unpin actions to standard software action verbs across major languages.

### ✨ Added
- **Clipboard In-Keyboard Action Pill**: Added Gboard-style compact floating action pill on long press (Pin/Unpin, Edit, Delete, Select) anchored next to clipboard items.
- **Clipboard Multi-Select Mode**: Added batch deletion, batch pinning/unpinning, and Select All / Deselect All controls for clipboard history.
- **Shift Word Recapitalization**: Added option under Text Editing settings to cycle word capitalization with Shift (lowercase -> Title Case -> UPPERCASE).
- **Center Typed Word**: Added option under Text Editing settings to always show the actively typed word in the center slot of the suggestion strip.
- **Forward Delete Key Icon**: Provided dedicated right-pointing icon (`ic_forward_delete`) for layouts using forward delete keys.

### 🔄 Changed
- **Clipboard Toolbar Refactor**: Streamlined clipboard toolbar by removing redundant selection toggle key in favor of the long-press action pill.
- **Text Expander Regex Performance**: Pre-compiled regex patterns in `TextExpanderUtils` for faster shortcut expansion matching.
- **Language Detector Regex Performance**: Pre-compiled script and language detection regex patterns in `LanguageDetector` to reduce CPU overhead during input.
