// SPDX-License-Identifier: GPL-3.0-only
package helium314.keyboard.latin

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class OtpSuggestionManagerTest {

    @Test
    fun testExplicitOtpPatterns() {
        assertEquals("123456", OtpSuggestionManager.extractOtp("Your OTP is 123456."))
        assertEquals("482910", OtpSuggestionManager.extractOtp("Your verification code: 482910"))
        assertEquals("847291", OtpSuggestionManager.extractOtp("Login passcode: 847291"))
        assertEquals("314159", OtpSuggestionManager.extractOtp("Use OTP 314159 to confirm transaction."))
        assertEquals("654321", OtpSuggestionManager.extractOtp("Your one-time password is 654321."))
        assertEquals("9988", OtpSuggestionManager.extractOtp("Your 2FA pin is: 9988"))
    }

    @Test
    fun testReverseOtpPatterns() {
        assertEquals("482910", OtpSuggestionManager.extractOtp("482910 is your Google verification code."))
        assertEquals("789101", OtpSuggestionManager.extractOtp("789101 is your OTP."))
        assertEquals("123456", OtpSuggestionManager.extractOtp("123456 is your security code."))
        assertEquals("297053", OtpSuggestionManager.extractOtp("297053 is your One time password (OTP) to sign-in to your account on www.jio.com. Please enter the OTP to proceed. @www.jio.com #297053"))
        assertEquals("864627", OtpSuggestionManager.extractOtp("864627 is your One time password (OTP) to sign-in to your account on www.jio.com.\nPlease enter the OTP to proceed.\n@www.jio.com #864627"))
    }

    @Test
    fun testWebOtpAndHashtagPatterns() {
        assertEquals("492810", OtpSuggestionManager.extractOtp("@example.com #492810"))
        assertEquals("654321", OtpSuggestionManager.extractOtp("Verify your account with OTP. @login.site.com #654321"))
        assertEquals("981245", OtpSuggestionManager.extractOtp("Your authentication passcode is #981245."))
    }

    @Test
    fun testMessagesWithYearsDatesAndAmounts() {
        assertEquals("492015", OtpSuggestionManager.extractOtp("Account accessed on 2026-09-28. Verification code: 492015."))
        assertEquals("314159", OtpSuggestionManager.extractOtp("Order #982345: Use OTP 314159 to confirm delivery."))
        assertEquals("789012", OtpSuggestionManager.extractOtp("Dear user, OTP for INR 2500.00 at Store is 789012."))
    }

    @Test
    fun testDoesNotTreatArbitraryNumbersAsOtp() {
        assertNull(OtpSuggestionManager.extractOtp("582914"))
        assertNull(OtpSuggestionManager.extractOtp("Your number is 1234"))
        assertNull(OtpSuggestionManager.extractOtp("@first.example #123456 and @second.example #987654"))
    }

    @Test
    fun testRussianOtpContext() {
        assertEquals("482910", OtpSuggestionManager.extractOtp("Код подтверждения: 482910"))
    }

    @Test
    fun testIrrelevantMessages() {
        assertNull(OtpSuggestionManager.extractOtp(""))
        assertNull(OtpSuggestionManager.extractOtp("   "))
        assertNull(OtpSuggestionManager.extractOtp("Hello! How are you doing today?"))
        assertNull(OtpSuggestionManager.extractOtp("See you tomorrow at 5pm."))
    }
}
