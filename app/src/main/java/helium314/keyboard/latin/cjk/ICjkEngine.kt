// SPDX-License-Identifier: GPL-3.0-only
package helium314.keyboard.latin.cjk

import android.content.Context

/**
 * Contract interface for CJK composition engines (e.g. librime, fcitx).
 * Implemented by external in-process companion plugins or internal stubs.
 */
interface ICjkEngine {
    /**
     * Interface version for backward and forward compatibility.
     */
    fun getInterfaceVersion(): Int = 2

    /**
     * Sets the active language or locale (e.g. "zh", "ja", "zh_CN").
     */
    fun setLocale(locale: String): Boolean = true

    /**
     * Returns true if the engine supports composition for the given language tag.
     */
    fun supportsLanguage(language: String): Boolean = language == "zh"

    /**
     * Initializes the composition engine with shared schemas and user data storage paths.
     */
    fun init(context: Context, sharedDir: String, userDir: String): Boolean

    /**
     * Checks if the engine is initialized and ready for keystroke processing.
     */
    fun isAvailable(): Boolean

    /**
     * Feeds an X11 keysym with modifier mask into the composition engine.
     * Returns true if the key was consumed by the engine.
     */
    fun processKey(keysym: Int, mask: Int = 0): Boolean

    /**
     * Retrieves the current pre-edit and candidate context.
     */
    fun getContext(): CjkContextData?

    /**
     * Retrieves the committed text if a composition was completed, null otherwise.
     */
    fun getCommit(): String?

    /**
     * Selects a candidate by its 0-based page index.
     */
    fun selectCandidate(index: Int): Boolean

    /**
     * Clears any active composition without committing text.
     */
    fun clearComposition()

    /**
     * Releases active session resources when the input view finishes.
     */
    fun destroySession()

    /**
     * Cleans up engine resources on shutdown.
     */
    fun cleanup()
}
