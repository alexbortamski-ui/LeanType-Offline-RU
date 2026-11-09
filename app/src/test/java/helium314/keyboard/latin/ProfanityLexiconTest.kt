/*
 * SPDX-License-Identifier: GPL-3.0-only
 */
package helium314.keyboard.latin

import helium314.keyboard.latin.dictionary.Dictionary
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProfanityLexiconTest {
    @Test
    fun blocksConfiguredWordWhenSettingIsEnabled() {
        assertTrue(ProfanityLexicon.shouldBlock("блять", Dictionary.TYPE_USER_HISTORY, true, false))
    }

    @Test
    fun doesNotBlockWhenSettingIsDisabled() {
        assertFalse(ProfanityLexicon.shouldBlock("блять", Dictionary.TYPE_USER_HISTORY, false, false))
    }

    @Test
    fun explicitUserDictionaryIsNotBlocked() {
        assertFalse(ProfanityLexicon.shouldBlock("блять", Dictionary.TYPE_USER, true, false))
    }

    @Test
    fun matchesCaseAndYoNormalization() {
        assertTrue(ProfanityLexicon.shouldBlock("БЛЯТЬ", Dictionary.TYPE_USER_HISTORY, true, false))
        assertTrue(ProfanityLexicon.shouldBlock("ебёт", Dictionary.TYPE_USER_HISTORY, true, false))
    }
}
