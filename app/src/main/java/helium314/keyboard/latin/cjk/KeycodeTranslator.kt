// SPDX-License-Identifier: GPL-3.0-only
package helium314.keyboard.latin.cjk

import android.view.KeyEvent
import helium314.keyboard.event.Event
import helium314.keyboard.keyboard.internal.keyboard_parser.floris.KeyCode
import helium314.keyboard.latin.common.Constants

/**
 * Translates Android IME Events and KeyCodes into standard X11 Keysyms for headless CJK engines.
 */
object KeycodeTranslator {
    // Standard X11 keysym constants
    const val XK_BACKSPACE = 0xff08
    const val XK_TAB = 0xff09
    const val XK_LINEFEED = 0xff0a
    const val XK_CLEAR = 0xff0b
    const val XK_RETURN = 0xff0d
    const val XK_PAUSE = 0xff13
    const val XK_ESCAPE = 0xff1b
    const val XK_DELETE = 0xffff

    // Cursor and navigation keys
    const val XK_LEFT = 0xff51
    const val XK_UP = 0xff52
    const val XK_RIGHT = 0xff53
    const val XK_DOWN = 0xff54
    const val XK_PAGE_UP = 0xff55
    const val XK_PAGE_DOWN = 0xff56

    // Common keysyms
    const val XK_SPACE = 0x0020

    // Modifier bitmasks
    const val MASK_SHIFT = 1 shl 0
    const val MASK_LOCK = 1 shl 1
    const val MASK_CONTROL = 1 shl 2
    const val MASK_ALT = 1 shl 3

    /**
     * Translates an Event to an X11 keysym.
     * Returns 0 if the event does not correspond to an actionable CJK keystroke.
     */
    fun toKeysym(event: Event): Int {
        val keyCode = event.keyCode
        val codePoint = event.codePoint

        // 1. FlorisBoard / HeliBoard internal keycodes
        when (keyCode) {
            KeyCode.DELETE -> return XK_BACKSPACE
            KeyCode.ARROW_LEFT -> return XK_LEFT
            KeyCode.ARROW_RIGHT -> return XK_RIGHT
            KeyCode.ARROW_UP -> return XK_UP
            KeyCode.ARROW_DOWN -> return XK_DOWN
        }

        // 2. Android KeyEvent keycodes
        when (keyCode) {
            KeyEvent.KEYCODE_DEL -> return XK_BACKSPACE
            KeyEvent.KEYCODE_ENTER -> return XK_RETURN
            KeyEvent.KEYCODE_SPACE -> return XK_SPACE
            KeyEvent.KEYCODE_TAB -> return XK_TAB
            KeyEvent.KEYCODE_FORWARD_DEL -> return XK_DELETE
            KeyEvent.KEYCODE_DPAD_LEFT -> return XK_LEFT
            KeyEvent.KEYCODE_DPAD_RIGHT -> return XK_RIGHT
            KeyEvent.KEYCODE_DPAD_UP -> return XK_UP
            KeyEvent.KEYCODE_DPAD_DOWN -> return XK_DOWN
            KeyEvent.KEYCODE_ESCAPE -> return XK_ESCAPE
        }

        // 3. Constants code points
        when (codePoint) {
            Constants.CODE_ENTER -> return XK_RETURN
            Constants.CODE_SPACE -> return XK_SPACE
            Constants.CODE_TAB -> return XK_TAB
        }

        // 4. ASCII printable range (letters, digits, symbols)
        if (codePoint in 32..126) {
            return codePoint
        }

        // 5. General unicode code point if positive
        if (codePoint > 0) {
            return codePoint
        }

        return 0
    }

    /**
     * Extracts modifier bitmask from Android metaState.
     */
    fun getModifierMask(metaState: Int): Int {
        var mask = 0
        if ((metaState and KeyEvent.META_SHIFT_ON) != 0) mask = mask or MASK_SHIFT
        if ((metaState and KeyEvent.META_CTRL_ON) != 0) mask = mask or MASK_CONTROL
        if ((metaState and KeyEvent.META_ALT_ON) != 0) mask = mask or MASK_ALT
        return mask
    }
}
