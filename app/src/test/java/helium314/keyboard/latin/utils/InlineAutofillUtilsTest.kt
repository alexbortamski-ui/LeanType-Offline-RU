// SPDX-License-Identifier: GPL-3.0-only
package helium314.keyboard.latin.utils

import android.view.inputmethod.InlineSuggestionInfo
import androidx.autofill.HintConstants
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class InlineAutofillUtilsTest {
    @Test
    fun localSmsOtpSuppressesOnlySmsOtpSuggestion() {
        assertTrue(
            shouldSuppressSmsOtpInlineSuggestion(
                localOtpHasPriority = true,
                source = InlineSuggestionInfo.SOURCE_AUTOFILL,
                type = InlineSuggestionInfo.TYPE_SUGGESTION,
                autofillHints = arrayOf(HintConstants.AUTOFILL_HINT_SMS_OTP),
            )
        )
    }

    @Test
    fun splitSmsOtpFieldHintsAreAlsoSuppressed() {
        val positionHint = HintConstants.generateSmsOtpHintForCharacterPosition(1)
        assertTrue(
            shouldSuppressSmsOtpInlineSuggestion(
                localOtpHasPriority = true,
                source = InlineSuggestionInfo.SOURCE_PLATFORM,
                type = InlineSuggestionInfo.TYPE_SUGGESTION,
                autofillHints = arrayOf(positionHint),
            )
        )
    }

    @Test
    fun smsOtpAutofillRemainsAvailableWithoutLocalOtp() {
        assertFalse(
            shouldSuppressSmsOtpInlineSuggestion(
                localOtpHasPriority = false,
                source = InlineSuggestionInfo.SOURCE_AUTOFILL,
                type = InlineSuggestionInfo.TYPE_SUGGESTION,
                autofillHints = arrayOf(HintConstants.AUTOFILL_HINT_SMS_OTP),
            )
        )
    }

    @Test
    fun passwordsCardsAddressesAndUnknownHintsRemainVisible() {
        val ordinaryHints = arrayOf(
            HintConstants.AUTOFILL_HINT_PASSWORD,
            HintConstants.AUTOFILL_HINT_CREDIT_CARD_NUMBER,
            HintConstants.AUTOFILL_HINT_POSTAL_ADDRESS,
            "vendorSpecificHint",
        )
        ordinaryHints.forEach { hint ->
            assertFalse(
                shouldSuppressSmsOtpInlineSuggestion(
                    localOtpHasPriority = true,
                    source = InlineSuggestionInfo.SOURCE_AUTOFILL,
                    type = InlineSuggestionInfo.TYPE_SUGGESTION,
                    autofillHints = arrayOf(hint),
                ),
                hint,
            )
        }
    }

    @Test
    fun autofillActionsAreNotFilteredAsOtpValues() {
        assertFalse(
            shouldSuppressSmsOtpInlineSuggestion(
                localOtpHasPriority = true,
                source = InlineSuggestionInfo.SOURCE_AUTOFILL,
                type = InlineSuggestionInfo.TYPE_ACTION,
                autofillHints = arrayOf(HintConstants.AUTOFILL_HINT_SMS_OTP),
            )
        )
    }
}
