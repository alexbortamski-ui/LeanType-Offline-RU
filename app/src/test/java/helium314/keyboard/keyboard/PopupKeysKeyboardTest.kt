// SPDX-License-Identifier: GPL-3.0-only
package helium314.keyboard.keyboard

import android.graphics.Paint
import helium314.keyboard.ShadowInputMethodManager2
import helium314.keyboard.ShadowProximityInfo
import helium314.keyboard.keyboard.internal.KeyboardParams
import helium314.keyboard.latin.LatinIME
import helium314.keyboard.latin.common.Constants
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(shadows = [
    ShadowInputMethodManager2::class,
    ShadowProximityInfo::class,
])
class PopupKeysKeyboardTest {
    private lateinit var latinIME: LatinIME
    private lateinit var kbdParams: KeyboardParams

    @BeforeTest fun setUp() {
        latinIME = Robolectric.setupService(LatinIME::class.java)
        ShadowLog.setupLogging()
        ShadowLog.stream = System.out

        kbdParams = KeyboardParams()
        kbdParams.mOccupiedWidth = 1000
        kbdParams.mBaseWidth = 1000
        kbdParams.mOccupiedHeight = 400
        kbdParams.mBaseHeight = 400
        kbdParams.mDefaultRowHeight = 100f
        kbdParams.mDefaultAbsoluteKeyWidth = 100
        kbdParams.mAbsolutePopupKeyWidth = 100
        kbdParams.mMaxPopupKeysKeyboardColumn = 8
        kbdParams.GRID_WIDTH = 10
        kbdParams.GRID_HEIGHT = 4
        val layoutParams = KeyboardLayoutSet.Params()
        layoutParams.mEditorInfo = android.view.inputmethod.EditorInfo()
        layoutParams.mSubtype = helium314.keyboard.latin.RichInputMethodSubtype.emojiSubtype
        layoutParams.mKeyboardWidth = 1000
        layoutParams.mKeyboardHeight = 400
        kbdParams.mId = KeyboardId(KeyboardId.ELEMENT_ALPHABET, layoutParams)
    }

    @Test fun testCustomSpacebarPopupKeysGridLayout() {
        // Reproduce issue #569: wide spacebar (e.g. 600px) with 20 custom popup keys
        val emojis = listOf(
            "😊", "👍", "🤣", "😉", "👏", "😍", "😋", "👌", "✌️", "🤔",
            "😬", "☹️", "😲", "🙌", "😱", "🤷", "🤬", "☝️", "🤘", "🤦"
        )
        val popupKeySpecs = emojis.joinToString(",")
        val keyParams = Key.KeyParams("space", Constants.CODE_SPACE, null, popupKeySpecs, 0, kbdParams)
        keyParams.mWidth = 0.6f
        keyParams.mHeight = 100f
        keyParams.mAbsoluteWidth = 600f
        keyParams.mAbsoluteHeight = 100f
        val spaceKey = Key(keyParams)

        assertEquals(600, spaceKey.drawWidth)
        assertEquals(20, spaceKey.popupKeys?.size)

        val keyboard = Keyboard(kbdParams)
        val builder = PopupKeysKeyboard.Builder(latinIME, spaceKey, keyboard, Paint())
        val popupKeyboard = builder.build()

        // Key width must use standard popup key width (100px), not spacebar width (600px)
        val popupKeys = popupKeyboard.sortedKeys.filter { !it.isSpacer }
        assertEquals(20, popupKeys.size)
        assertEquals(100, popupKeys.first().drawWidth)

        // Columns must be multi-column grid layout (7 keys per row across 3 rows), not collapsed into 1 single column
        val keysByRow = popupKeys.groupBy { it.y }
        assertEquals(3, keysByRow.size, "Expected 3 rows in grid layout")
        val maxKeysInRow = keysByRow.values.maxOf { it.size }
        assertEquals(7, maxKeysInRow, "Expected 7 columns in grid layout")
    }

    @Test fun testFixedOrderCustomSpacebarPopupKeys() {
        // With explicit !fixedOrder!5 directive
        val emojis = listOf(
            "!fixedOrder!5",
            "😊", "👍", "🤣", "😉", "👏", "😍", "😋", "👌", "✌️", "🤔",
            "😬", "☹️", "😲", "🙌", "😱", "🤷", "🤬", "☝️", "🤘", "🤦"
        )
        val popupKeySpecs = emojis.joinToString(",")
        val keyParams = Key.KeyParams("space", Constants.CODE_SPACE, null, popupKeySpecs, 0, kbdParams)
        keyParams.mWidth = 0.6f
        keyParams.mHeight = 100f
        keyParams.mAbsoluteWidth = 600f
        keyParams.mAbsoluteHeight = 100f
        val spaceKey = Key(keyParams)

        val keyboard = Keyboard(kbdParams)
        val builder = PopupKeysKeyboard.Builder(latinIME, spaceKey, keyboard, Paint())
        val popupKeyboard = builder.build()

        val popupKeys = popupKeyboard.sortedKeys.filter { !it.isSpacer }
        assertEquals(20, popupKeys.size)
        assertEquals(100, popupKeys.first().drawWidth)

        val columnCount = popupKeys.map { it.x }.distinct().size
        val rowCount = popupKeys.map { it.y }.distinct().size
        assertEquals(5, columnCount)
        assertEquals(4, rowCount)
    }
}
