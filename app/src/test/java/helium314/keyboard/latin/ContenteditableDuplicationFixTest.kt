// SPDX-License-Identifier: GPL-3.0-only
package helium314.keyboard.latin

import android.text.InputType
import android.view.inputmethod.EditorInfo
import helium314.keyboard.compat.AppQuirk
import helium314.keyboard.compat.AppQuirksManager
import helium314.keyboard.compat.BrowserDetector
import helium314.keyboard.latin.utils.InputTypeUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ContenteditableDuplicationFixTest {

    @Test
    fun testIsMalformedWord_detectsDuplicationArtifacts() {
        // Words from the bug report
        assertTrue(DictionaryFacilitatorImpl.isMalformedWord("ChChChecChChChecCheck"))
        assertTrue(DictionaryFacilitatorImpl.isMalformedWord("ChChChecChChChecCheckC"))
        assertTrue(DictionaryFacilitatorImpl.isMalformedWord("ChChChec"))
        assertTrue(DictionaryFacilitatorImpl.isMalformedWord("ChChCheck"))
        assertTrue(DictionaryFacilitatorImpl.isMalformedWord("ThThThis"))
        assertTrue(DictionaryFacilitatorImpl.isMalformedWord("StStStop"))

        // Excessive repetitions
        assertTrue(DictionaryFacilitatorImpl.isMalformedWord("abcabcabcabc"))
        assertTrue(DictionaryFacilitatorImpl.isMalformedWord("aaaaa"))

        // Excessive length
        val longString = "a".repeat(49)
        assertTrue(DictionaryFacilitatorImpl.isMalformedWord(longString))
    }

    @Test
    fun testIsMalformedWord_allowsLegitimateWords() {
        assertFalse(DictionaryFacilitatorImpl.isMalformedWord("Check"))
        assertFalse(DictionaryFacilitatorImpl.isMalformedWord("Hello"))
        assertFalse(DictionaryFacilitatorImpl.isMalformedWord("Christmas"))
        assertFalse(DictionaryFacilitatorImpl.isMalformedWord("Children"))
        assertFalse(DictionaryFacilitatorImpl.isMalformedWord("School"))
        assertFalse(DictionaryFacilitatorImpl.isMalformedWord("Through"))
        assertFalse(DictionaryFacilitatorImpl.isMalformedWord("chacha"))
        assertFalse(DictionaryFacilitatorImpl.isMalformedWord("cancan"))
        assertFalse(DictionaryFacilitatorImpl.isMalformedWord("murmur"))
        assertFalse(DictionaryFacilitatorImpl.isMalformedWord("hahaha"))
        assertFalse(DictionaryFacilitatorImpl.isMalformedWord("couscous"))
    }

    @Test
    fun testAppQuirksManager_webBrowserDetection() {
        assertTrue(AppQuirksManager.isWebEditor("com.brave.browser"))
        assertTrue(AppQuirksManager.isWebEditor("com.android.chrome"))
        assertTrue(AppQuirksManager.isWebEditor("org.mozilla.firefox"))
        assertTrue(AppQuirksManager.isWebEditor("com.sec.android.app.sbrowser"))
        assertTrue(AppQuirksManager.isWebEditor("org.cromite.cromite"))
        assertTrue(AppQuirksManager.isWebEditor("app.vanadium.browser"))
        assertTrue(AppQuirksManager.isWebEditor("org.torproject.torbrowser"))
        assertTrue(AppQuirksManager.isWebEditor("us.spotco.fennec_dos"))
        assertFalse(AppQuirksManager.isWebEditor("com.discord"))
        assertFalse(AppQuirksManager.isWebEditor("org.telegram.messenger"))
        assertFalse(AppQuirksManager.isWebEditor(null))
    }

    @Test
    fun testAppQuirksManager_adjustInputTypeWithAutoCorrect() {
        val baseType = InputType.TYPE_CLASS_TEXT or
                InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE or
                InputType.TYPE_TEXT_FLAG_AUTO_CORRECT

        val adjusted = AppQuirksManager.adjustInputType(baseType, "com.brave.browser")
        // With AUTO_CORRECT present, it should add WEB_EDIT_TEXT and NOT add NO_SUGGESTIONS
        val hasWebEditText = (adjusted and InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT) != 0
        val hasNoSuggestions = (adjusted and InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS) != 0

        assertTrue("Should add WEB_EDIT_TEXT", hasWebEditText)
        assertFalse("Should NOT add NO_SUGGESTIONS when AUTO_CORRECT is present", hasNoSuggestions)
    }

    @Test
    fun testAppQuirksManager_defaultNexusLauncherEnterAction() {
        val imeOptionsWithNoEnter = EditorInfo.IME_ACTION_SEARCH or EditorInfo.IME_FLAG_NO_ENTER_ACTION
        val adjusted = AppQuirksManager.adjustImeOptions(imeOptionsWithNoEnter, "com.google.android.apps.nexuslauncher")
        assertEquals(0, adjusted and EditorInfo.IME_FLAG_NO_ENTER_ACTION)
        assertEquals(EditorInfo.IME_ACTION_SEARCH, adjusted and EditorInfo.IME_MASK_ACTION)
    }

    @Test
    fun testAppQuirksManager_directCommitAndAutoSpaceUserQuirk() {
        val testApp = "com.test.directcommit.app"
        assertFalse(AppQuirksManager.isDirectCommitApp(testApp))
        assertFalse(AppQuirksManager.isAutoSpaceDisabled(testApp))

        AppQuirksManager.saveQuirk(AppQuirk(
            packageName = testApp,
            forceDirectCommit = true,
            disableAutoSpace = true
        ))

        assertTrue(AppQuirksManager.isDirectCommitApp(testApp))
        assertTrue(AppQuirksManager.isAutoSpaceDisabled(testApp))

        AppQuirksManager.removeQuirk(testApp)
        assertFalse(AppQuirksManager.isDirectCommitApp(testApp))
        assertFalse(AppQuirksManager.isAutoSpaceDisabled(testApp))
    }

    @Test
    fun testAppQuirk_serialization_withDirectCommitAndAutoSpace() {
        val original = AppQuirk(
            packageName = "com.test.app",
            forceDirectCommit = true,
            disableAutoSpace = true,
            forceWebEditor = true
        )
        val json = original.toJson()
        val restored = AppQuirk.fromJson(json)
        assertEquals(original.packageName, restored.packageName)
        assertEquals(original.forceDirectCommit, restored.forceDirectCommit)
        assertEquals(original.disableAutoSpace, restored.disableAutoSpace)
        assertEquals(original.forceWebEditor, restored.forceWebEditor)
        assertFalse(restored.forceIncognito)
    }

    @Test
    fun testAppQuirksManager_userCustomQuirks() {
        val pkg = "com.custom.app"
        assertFalse(AppQuirksManager.isWebEditor(pkg))
        assertFalse(AppQuirksManager.isIncognitoApp(pkg))

        // Save custom quirk
        AppQuirksManager.saveQuirk(AppQuirk(
            packageName = pkg,
            forceWebEditor = true,
            forceIncognito = true,
            forceEnterAction = EditorInfo.IME_ACTION_SEND
        ))

        assertTrue(AppQuirksManager.isWebEditor(pkg))
        assertTrue(AppQuirksManager.isIncognitoApp(pkg))

        val imeOptions = AppQuirksManager.adjustImeOptions(EditorInfo.IME_ACTION_NONE, pkg)
        assertEquals(EditorInfo.IME_ACTION_SEND, imeOptions and EditorInfo.IME_MASK_ACTION)

        // Clean up
        AppQuirksManager.removeQuirk(pkg)
        assertFalse(AppQuirksManager.isWebEditor(pkg))
        assertFalse(AppQuirksManager.isIncognitoApp(pkg))
    }

    @Test
    fun testAppQuirksManager_forceNonIncognito() {
        val pkg = "com.nonincognito.test.app"
        assertFalse(AppQuirksManager.isNonIncognitoApp(pkg))
        assertFalse(AppQuirksManager.isIncognitoApp(pkg))

        val quirk = AppQuirk(
            packageName = pkg,
            forceNonIncognito = true
        )
        // Verify JSON roundtrip
        val json = quirk.toJson()
        val restored = AppQuirk.fromJson(json)
        assertEquals(pkg, restored.packageName)
        assertTrue(restored.forceNonIncognito)
        assertFalse(restored.forceIncognito)

        AppQuirksManager.saveQuirk(quirk)
        assertTrue(AppQuirksManager.isNonIncognitoApp(pkg))
        assertFalse(AppQuirksManager.isIncognitoApp(pkg))

        AppQuirksManager.removeQuirk(pkg)
        assertFalse(AppQuirksManager.isNonIncognitoApp(pkg))
    }

    @Test
    fun testAppQuirksManager_allowSymbolComposing() {
        val pkg = "net.dinglisch.android.taskerm"
        assertFalse(AppQuirksManager.isSymbolComposingApp(pkg))

        val quirk = AppQuirk(
            packageName = pkg,
            allowSymbolComposing = true
        )
        // Verify JSON roundtrip
        val json = quirk.toJson()
        val restored = AppQuirk.fromJson(json)
        assertEquals(pkg, restored.packageName)
        assertTrue(restored.allowSymbolComposing)
        assertFalse(restored.forceDirectCommit)

        AppQuirksManager.saveQuirk(quirk)
        assertTrue(AppQuirksManager.isSymbolComposingApp(pkg))

        AppQuirksManager.removeQuirk(pkg)
        assertFalse(AppQuirksManager.isSymbolComposingApp(pkg))
    }

    @Test
    fun testInputTypeUtils_isWebEditText() {
        val webText = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT
        val webEmail = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS
        val webPassword = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD
        val normalText = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL

        assertTrue(InputTypeUtils.isWebEditText(webText))
        assertTrue(InputTypeUtils.isWebEditText(webEmail))
        assertTrue(InputTypeUtils.isWebEditText(webPassword))
        assertFalse(InputTypeUtils.isWebEditText(normalText))
        assertFalse(InputTypeUtils.isWebEditText(InputType.TYPE_CLASS_NUMBER))
    }

    @Test
    fun testInputTypeUtils_isWebEditor() {
        val webEi = android.view.inputmethod.EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT
            packageName = "com.some.app"
        }
        val browserEi = android.view.inputmethod.EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
            packageName = "com.android.chrome"
        }
        val nativeEi = android.view.inputmethod.EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
            packageName = "org.telegram.messenger"
        }

        assertTrue(InputTypeUtils.isWebEditor(webEi))
        assertTrue(InputTypeUtils.isWebEditor(browserEi))
        assertFalse(InputTypeUtils.isWebEditor(nativeEi))
        assertFalse(InputTypeUtils.isWebEditor(null))
    }

    @Test
    fun testIsBelatedExpectedUpdate_handlesMissingComposingSpan() {
        val mockIms = org.mockito.Mockito.mock(android.inputmethodservice.InputMethodService::class.java)
        val ric = RichInputConnection(mockIms)

        val expectedStartField = RichInputConnection::class.java.getDeclaredField("mExpectedSelStart").apply { isAccessible = true }
        val expectedEndField = RichInputConnection::class.java.getDeclaredField("mExpectedSelEnd").apply { isAccessible = true }
        val composingField = RichInputConnection::class.java.getDeclaredField("mComposingText").apply { isAccessible = true }
        expectedStartField.setInt(ric, 5)
        expectedEndField.setInt(ric, 5)
        (composingField.get(ric) as StringBuilder).append("Check")

        // In contenteditable, cs = -1, ce = -1. When newSel matches expectedSel, it must return true
        assertTrue(ric.isBelatedExpectedUpdate(0, 5, 0, 5, -1, -1))

        // When cs and ce are valid (e.g. 0 to 5), it returns true
        assertTrue(ric.isBelatedExpectedUpdate(0, 5, 0, 5, 0, 5))

        // If editor truncated the composing span (e.g. cs = 0, ce = 2 < 5), it returns false
        assertFalse(ric.isBelatedExpectedUpdate(0, 5, 0, 5, 0, 2))

        // If cursor moved somewhere unexpected (e.g. newSel = 10 != 5), it returns false
        assertFalse(ric.isBelatedExpectedUpdate(0, 10, 0, 10, -1, -1))
    }

    @Test
    fun testBrowserDetector_fallbackAndDelegation() {
        assertTrue(BrowserDetector.isWebBrowser("com.android.chrome"))
        assertTrue(BrowserDetector.isWebBrowser("org.mozilla.firefox"))
        assertTrue(BrowserDetector.isWebBrowser("app.vanadium.browser"))
        assertTrue(BrowserDetector.isWebBrowser("org.cromite.cromite"))
        assertTrue(BrowserDetector.isWebBrowser("com.sec.android.app.sbrowser"))
        assertFalse(BrowserDetector.isWebBrowser("org.telegram.messenger"))
        assertFalse(BrowserDetector.isWebBrowser(null))
    }

    @Test
    fun testRichInputConnection_webEditorDirectInspection() {
        val mockIms = org.mockito.Mockito.mock(android.inputmethodservice.InputMethodService::class.java)
        val mockIc = org.mockito.Mockito.mock(android.view.inputmethod.InputConnection::class.java)
        val webEi = android.view.inputmethod.EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT
            packageName = "com.some.app"
        }
        org.mockito.Mockito.`when`(mockIms.currentInputEditorInfo).thenReturn(webEi)
        org.mockito.Mockito.`when`(mockIms.currentInputConnection).thenReturn(mockIc)
        org.mockito.Mockito.`when`(mockIc.getTextBeforeCursor(2, 0)).thenReturn("A")
        org.mockito.Mockito.`when`(mockIc.getTextBeforeCursor(32, 0)).thenReturn("A")

        val ric = RichInputConnection(mockIms)
        ric.onStartInput()
        val icField = RichInputConnection::class.java.getDeclaredField("mIC").apply { isAccessible = true }
        icField.set(ric, mockIc)

        // codePointBeforeCursor in web mode with empty composing text queries getTextBeforeCursor(2, 0)
        assertEquals('A'.code, ric.codePointBeforeCursor)
        assertEquals(1, ric.charCountToDeleteBeforeCursor)
    }

    @Test
    fun testRichInputConnection_webEditorEmojiSurrogateInspection() {
        val mockIms = org.mockito.Mockito.mock(android.inputmethodservice.InputMethodService::class.java)
        val mockIc = org.mockito.Mockito.mock(android.view.inputmethod.InputConnection::class.java)
        val webEi = android.view.inputmethod.EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT
            packageName = "com.some.app"
        }
        val emoji = "\uD83D\uDE00" // 😀 (grinning face, surrogate pair length 2)
        org.mockito.Mockito.`when`(mockIms.currentInputEditorInfo).thenReturn(webEi)
        org.mockito.Mockito.`when`(mockIms.currentInputConnection).thenReturn(mockIc)
        org.mockito.Mockito.`when`(mockIc.getTextBeforeCursor(2, 0)).thenReturn(emoji)
        org.mockito.Mockito.`when`(mockIc.getTextBeforeCursor(32, 0)).thenReturn(emoji)

        val ric = RichInputConnection(mockIms)
        ric.onStartInput()
        val icField = RichInputConnection::class.java.getDeclaredField("mIC").apply { isAccessible = true }
        icField.set(ric, mockIc)

        assertEquals(0x1F600, ric.codePointBeforeCursor)
        assertEquals(2, ric.charCountToDeleteBeforeCursor)
    }

    @Test
    fun testAppQuirksManager_isTypeNullKeyboardAllowed() {
        // Known browsers must allow TYPE_NULL keyboard by default for Gecko/web compatibility
        assertTrue(AppQuirksManager.isTypeNullKeyboardAllowed("org.mozilla.firefox"))
        assertTrue(AppQuirksManager.isTypeNullKeyboardAllowed("org.mozilla.fenix"))
        assertTrue(AppQuirksManager.isTypeNullKeyboardAllowed("org.mozilla.fennec_fdroid"))
        assertTrue(AppQuirksManager.isTypeNullKeyboardAllowed("io.github.forkmaintainers.iceraven"))
        assertTrue(AppQuirksManager.isTypeNullKeyboardAllowed("com.android.chrome"))

        // Terminal apps must allow TYPE_NULL
        assertTrue(AppQuirksManager.isTypeNullKeyboardAllowed("com.termux"))
        assertTrue(AppQuirksManager.isTypeNullKeyboardAllowed("com.termux.test"))

        // Dialer and contacts apps must allow TYPE_NULL by default
        assertTrue(AppQuirksManager.isTypeNullKeyboardAllowed("com.android.dialer"))
        assertTrue(AppQuirksManager.isTypeNullKeyboardAllowed("com.google.android.dialer"))
        assertTrue(AppQuirksManager.isTypeNullKeyboardAllowed("com.android.contacts"))
        assertTrue(AppQuirksManager.isTypeNullKeyboardAllowed("com.android.phone"))
        assertTrue(AppQuirksManager.isTypeNullKeyboardAllowed("org.fossify.phone"))

        // Arbitrary non-browser app should NOT allow TYPE_NULL by default
        val nonBrowser = "com.example.regularapp"
        assertFalse(AppQuirksManager.isTypeNullKeyboardAllowed(nonBrowser))

        // Custom quirk with forceWebEditor should allow TYPE_NULL
        AppQuirksManager.saveQuirk(AppQuirk(packageName = nonBrowser, forceWebEditor = true))
        assertTrue(AppQuirksManager.isTypeNullKeyboardAllowed(nonBrowser))
        AppQuirksManager.removeQuirk(nonBrowser)
        assertFalse(AppQuirksManager.isTypeNullKeyboardAllowed(nonBrowser))

        // Custom quirk with allowTypeNullKeyboard should allow TYPE_NULL
        AppQuirksManager.saveQuirk(AppQuirk(packageName = nonBrowser, allowTypeNullKeyboard = true))
        assertTrue(AppQuirksManager.isTypeNullKeyboardAllowed(nonBrowser))
        AppQuirksManager.removeQuirk(nonBrowser)
        assertFalse(AppQuirksManager.isTypeNullKeyboardAllowed(nonBrowser))
    }
}

