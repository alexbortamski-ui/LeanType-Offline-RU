package helium314.keyboard.latin.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Locale

class RecapitalizeStatusTest {

    @Test
    fun testLifecycle() {
        val status = RecapitalizeStatus()

        // Initial state is stopped and enabled
        assertFalse(status.isStarted())
        assertTrue(status.isEnabled())

        // Start
        status.start("hello", 0, Locale.US, intArrayOf())
        assertTrue(status.isStarted())
        assertTrue(status.isEnabled())

        // Stop
        status.stop()
        assertFalse(status.isStarted())

        // Disable
        status.disable()
        assertFalse(status.isEnabled())

        // Ensure start fails when disabled
        status.start("hello", 0, Locale.US, intArrayOf())
        assertFalse(status.isStarted())

        // Enable
        status.enable()
        assertTrue(status.isEnabled())

        // Ensure start works when enabled
        status.start("hello", 0, Locale.US, intArrayOf())
        assertTrue(status.isStarted())
    }

    @Test
    fun testIsSetAt() {
        val status = RecapitalizeStatus()
        status.start("hello", 2, Locale.US, intArrayOf())

        // text length is 5, selectionStart is 2. selectionEnd() should be 2 + 5 = 7
        assertTrue(status.isSetAt(2, 7))
        assertFalse(status.isSetAt(2, 6))
        assertFalse(status.isSetAt(3, 7))
    }

    @Test
    fun testRotate() {
        val status = RecapitalizeStatus()
        // "hello" matches ALL_LOWER
        status.start("hello", 0, Locale.US, intArrayOf())

        assertEquals(RecapitalizeMode.ALL_LOWER, status.getCurrentMode())

        status.rotate()
        assertEquals(RecapitalizeMode.FIRST_WORD_UPPER, status.getCurrentMode())
        assertEquals("Hello", status.textReplacement().text)

        status.rotate()
        assertEquals(RecapitalizeMode.ALL_UPPER, status.getCurrentMode())
        assertEquals("HELLO", status.textReplacement().text)

        status.rotate()
        // since skipOriginalMixedCaseMode is true (original mode was ALL_LOWER, not ORIGINAL_MIXED_CASE)
        // it skips ORIGINAL_MIXED_CASE and wraps to ALL_LOWER
        assertEquals(RecapitalizeMode.ALL_LOWER, status.getCurrentMode())
        assertEquals("hello", status.textReplacement().text)
    }

    @Test
    fun testRotate_originalMixedCase() {
        val status = RecapitalizeStatus()
        // "tEsT" matches ORIGINAL_MIXED_CASE
        status.start("tEsT", 0, Locale.US, intArrayOf())

        assertEquals(RecapitalizeMode.ORIGINAL_MIXED_CASE, status.getCurrentMode())

        status.rotate()
        assertEquals(RecapitalizeMode.ALL_LOWER, status.getCurrentMode())
        assertEquals("test", status.textReplacement().text)

        status.rotate()
        assertEquals(RecapitalizeMode.FIRST_WORD_UPPER, status.getCurrentMode())
        assertEquals("Test", status.textReplacement().text)

        status.rotate()
        assertEquals(RecapitalizeMode.ALL_UPPER, status.getCurrentMode())
        assertEquals("TEST", status.textReplacement().text)

        status.rotate()
        // since skipOriginalMixedCaseMode is false (original mode was ORIGINAL_MIXED_CASE)
        // it wraps back to ORIGINAL_MIXED_CASE
        assertEquals(RecapitalizeMode.ORIGINAL_MIXED_CASE, status.getCurrentMode())
        assertEquals("tEsT", status.textReplacement().text)
    }

    @Test
    fun testEmptyStringAndDefaultStates() {
        val status = RecapitalizeStatus()
        // Test default state initialization internally matching empty logic
        assertFalse(status.isStarted())

        status.start("", 0, Locale.US, intArrayOf())
        assertTrue(status.isStarted())
        // Empty string -> ALL_UPPER
        assertEquals(RecapitalizeMode.ALL_UPPER, status.getCurrentMode())

        // Verify rotation on empty string does not infinite loop, finishes quickly
        status.rotate()
        // Because text replacement is always "", it breaks loop after scanning carousel
        // skip flag is true (ALL_UPPER != ORIGINAL_MIXED_CASE)
        // ALL_UPPER -> ALL_LOWER -> ALL_UPPER
        // The rotation loops until replacement differs from current text OR hits mode count bound
        assertEquals("", status.textReplacement().text)
    }
}
