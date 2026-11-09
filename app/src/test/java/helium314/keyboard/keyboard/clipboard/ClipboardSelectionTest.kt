// SPDX-License-Identifier: GPL-3.0-only
package helium314.keyboard.keyboard.clipboard

import helium314.keyboard.latin.utils.ToolbarKey
import helium314.keyboard.latin.utils.defaultClipboardToolbarPref
import helium314.keyboard.latin.utils.defaultToolbarPref
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ClipboardSelectionTest {

    @Test
    fun testClipboardToolbarPrefKeys() {
        assertTrue(
            defaultClipboardToolbarPref.contains("${ToolbarKey.CLIPBOARD_SEARCH.name}:true"),
            "defaultClipboardToolbarPref should include CLIPBOARD_SEARCH"
        )
        assertTrue(
            defaultClipboardToolbarPref.contains("${ToolbarKey.CLEAR_CLIPBOARD.name}:true"),
            "defaultClipboardToolbarPref should include CLEAR_CLIPBOARD"
        )
    }

    @Test
    fun testToolbarPrefExcludesClipboardSelect() {
        assertFalse(
            defaultClipboardToolbarPref.contains("CLIPBOARD_SELECT"),
            "defaultClipboardToolbarPref should not include CLIPBOARD_SELECT"
        )
        assertFalse(
            defaultToolbarPref.contains("CLIPBOARD_SELECT"),
            "Main defaultToolbarPref should not include CLIPBOARD_SELECT"
        )
        assertFalse(
            ToolbarKey.entries.any { it.name == "CLIPBOARD_SELECT" },
            "ToolbarKey should not define CLIPBOARD_SELECT"
        )
    }
}
