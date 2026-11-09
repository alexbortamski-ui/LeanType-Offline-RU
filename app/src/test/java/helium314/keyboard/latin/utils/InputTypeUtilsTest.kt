package helium314.keyboard.latin.utils

import android.text.InputType
import android.view.inputmethod.EditorInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class InputTypeUtilsTest {

    @Test
    fun testIsNumberInputType() {
        // Valid number inputs
        assertTrue(InputTypeUtils.isNumberInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL))
        assertTrue(InputTypeUtils.isNumberInputType(InputType.TYPE_CLASS_NUMBER))
        assertTrue(InputTypeUtils.isNumberInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED))

        // Invalid number inputs
        assertFalse(InputTypeUtils.isNumberInputType(InputType.TYPE_CLASS_TEXT))
    }

    @Test
    fun testIsEmailVariation() {
        assertTrue(InputTypeUtils.isEmailVariation(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS))
        assertTrue(InputTypeUtils.isEmailVariation(InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS))
        assertFalse(InputTypeUtils.isEmailVariation(InputType.TYPE_TEXT_VARIATION_NORMAL))
        assertFalse(InputTypeUtils.isEmailVariation(InputType.TYPE_TEXT_VARIATION_URI))
    }

    @Test
    fun testIsUriOrEmailType() {
        assertTrue(InputTypeUtils.isUriOrEmailType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_URI))
        assertTrue(InputTypeUtils.isUriOrEmailType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS))
        assertTrue(InputTypeUtils.isUriOrEmailType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS))

        // Non-text class but same variation should return false
        assertFalse(InputTypeUtils.isUriOrEmailType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_TEXT_VARIATION_URI))
        assertFalse(InputTypeUtils.isUriOrEmailType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL))
    }

    @Test
    fun testIsWebEditText() {
        assertTrue(InputTypeUtils.isWebEditText(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT))
        assertTrue(InputTypeUtils.isWebEditText(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS))
        assertTrue(InputTypeUtils.isWebEditText(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD))

        // Non-text class
        assertFalse(InputTypeUtils.isWebEditText(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT))
        // Normal text
        assertFalse(InputTypeUtils.isWebEditText(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL))
    }

    @Test
    fun testIsWebEditor() {
        val editorInfo = EditorInfo()
        editorInfo.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT
        editorInfo.packageName = "com.example.app"

        assertTrue(InputTypeUtils.isWebEditor(editorInfo))

        // Check with null
        assertFalse(InputTypeUtils.isWebEditor(null))

        // Check with app quirks
        val nonWebEditorInfo = EditorInfo()
        nonWebEditorInfo.inputType = InputType.TYPE_CLASS_TEXT
        nonWebEditorInfo.packageName = "md.obsidian" // known quirk in AppQuirksManager

        assertTrue(InputTypeUtils.isWebEditor(nonWebEditorInfo))

        val normalEditorInfo = EditorInfo()
        normalEditorInfo.inputType = InputType.TYPE_CLASS_TEXT
        normalEditorInfo.packageName = "com.example.normal"

        assertFalse(InputTypeUtils.isWebEditor(normalEditorInfo))
    }

    @Test
    fun testIsPasswordInputType() {
        assertTrue(InputTypeUtils.isPasswordInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD))
        assertTrue(InputTypeUtils.isPasswordInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD))
        assertTrue(InputTypeUtils.isPasswordInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD))

        assertFalse(InputTypeUtils.isPasswordInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD))
        assertFalse(InputTypeUtils.isPasswordInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL))
    }

    @Test
    fun testIsVisiblePasswordInputType() {
        assertTrue(InputTypeUtils.isVisiblePasswordInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD))
        assertFalse(InputTypeUtils.isVisiblePasswordInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD))
    }

    @Test
    fun testIsAnyPasswordInputType() {
        assertTrue(InputTypeUtils.isAnyPasswordInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD))
        assertTrue(InputTypeUtils.isAnyPasswordInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD))
        assertTrue(InputTypeUtils.isAnyPasswordInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD))

        assertFalse(InputTypeUtils.isAnyPasswordInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL))
    }

    @Test
    fun testIsAutoSpaceFriendlyType() {
        // Text classes that are friendly
        assertTrue(InputTypeUtils.isAutoSpaceFriendlyType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL))
        assertTrue(InputTypeUtils.isAutoSpaceFriendlyType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE))

        // Not text class
        assertFalse(InputTypeUtils.isAutoSpaceFriendlyType(InputType.TYPE_CLASS_NUMBER))

        // Variations that are not friendly
        assertFalse(InputTypeUtils.isAutoSpaceFriendlyType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS))
        assertFalse(InputTypeUtils.isAutoSpaceFriendlyType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS))
        assertFalse(InputTypeUtils.isAutoSpaceFriendlyType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD))
        assertFalse(InputTypeUtils.isAutoSpaceFriendlyType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD))
        assertFalse(InputTypeUtils.isAutoSpaceFriendlyType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD))
    }

    @Test
    fun testGetImeOptionsActionIdFromEditorInfo() {
        val editorInfo = EditorInfo()

        // Test NO_ENTER_ACTION
        editorInfo.imeOptions = EditorInfo.IME_FLAG_NO_ENTER_ACTION
        assertEquals(EditorInfo.IME_ACTION_NONE, InputTypeUtils.getImeOptionsActionIdFromEditorInfo(editorInfo))

        // Test custom label
        editorInfo.imeOptions = EditorInfo.IME_ACTION_GO
        editorInfo.actionLabel = "Custom"
        assertEquals(InputTypeUtils.IME_ACTION_CUSTOM_LABEL, InputTypeUtils.getImeOptionsActionIdFromEditorInfo(editorInfo))

        // Test standard actions
        editorInfo.actionLabel = null
        editorInfo.imeOptions = EditorInfo.IME_ACTION_SEARCH
        assertEquals(EditorInfo.IME_ACTION_SEARCH, InputTypeUtils.getImeOptionsActionIdFromEditorInfo(editorInfo))

        // Test unspecified with navigation flags
        editorInfo.imeOptions = EditorInfo.IME_ACTION_UNSPECIFIED or EditorInfo.IME_FLAG_NAVIGATE_NEXT
        assertEquals(EditorInfo.IME_ACTION_NEXT, InputTypeUtils.getImeOptionsActionIdFromEditorInfo(editorInfo))

        editorInfo.imeOptions = EditorInfo.IME_ACTION_UNSPECIFIED or EditorInfo.IME_FLAG_NAVIGATE_PREVIOUS
        assertEquals(EditorInfo.IME_ACTION_PREVIOUS, InputTypeUtils.getImeOptionsActionIdFromEditorInfo(editorInfo))

        // Test unspecified without flags
        editorInfo.imeOptions = EditorInfo.IME_ACTION_UNSPECIFIED
        assertEquals(EditorInfo.IME_ACTION_UNSPECIFIED, InputTypeUtils.getImeOptionsActionIdFromEditorInfo(editorInfo))

        // Test with quirks (nexuslauncher strips no_enter_action)
        editorInfo.imeOptions = EditorInfo.IME_FLAG_NO_ENTER_ACTION or EditorInfo.IME_ACTION_SEARCH
        editorInfo.packageName = "com.google.android.apps.nexuslauncher"
        assertEquals(EditorInfo.IME_ACTION_SEARCH, InputTypeUtils.getImeOptionsActionIdFromEditorInfo(editorInfo))
    }
}
