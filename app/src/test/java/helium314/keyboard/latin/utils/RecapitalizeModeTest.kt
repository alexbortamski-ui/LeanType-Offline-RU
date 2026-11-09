package helium314.keyboard.latin.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class RecapitalizeModeTest {

    @Test
    fun testApplyOriginalMixedCase() {
        val mode = RecapitalizeMode.ORIGINAL_MIXED_CASE
        assertEquals("testString", mode.apply("testString", intArrayOf(), Locale.US))
        assertEquals("", mode.apply("", intArrayOf(), Locale.US))
    }

    @Test
    fun testApplyAllLower() {
        val mode = RecapitalizeMode.ALL_LOWER
        assertEquals("teststring", mode.apply("TestString", intArrayOf(), Locale.US))
        assertEquals("test", mode.apply("TEST", intArrayOf(), Locale.US))
        assertEquals("", mode.apply("", intArrayOf(), Locale.US))
    }

    @Test
    fun testApplyFirstWordUpper() {
        val mode = RecapitalizeMode.FIRST_WORD_UPPER
        assertEquals("Teststring", mode.apply("testString", intArrayOf(), Locale.US))
        assertEquals("Test", mode.apply("test", intArrayOf(), Locale.US))
        // Multiple words handled by StringUtils.capitalizeEachWord
        assertEquals("Test string", mode.apply("test string", intArrayOf(), Locale.US))
        assertEquals("Test String", mode.apply("test string", intArrayOf(' '.code), Locale.US))
        assertEquals("", mode.apply("", intArrayOf(), Locale.US))
    }

    @Test
    fun testApplyAllUpper() {
        val mode = RecapitalizeMode.ALL_UPPER
        assertEquals("TESTSTRING", mode.apply("testString", intArrayOf(), Locale.US))
        assertEquals("TEST", mode.apply("test", intArrayOf(), Locale.US))
        assertEquals("", mode.apply("", intArrayOf(), Locale.US))
    }

    @Test
    fun testOf() {
        // all upper
        assertEquals(RecapitalizeMode.ALL_UPPER, RecapitalizeMode.of("TEST", intArrayOf()))

        // all lower
        assertEquals(RecapitalizeMode.ALL_LOWER, RecapitalizeMode.of("test", intArrayOf()))

        // first word upper
        assertEquals(RecapitalizeMode.FIRST_WORD_UPPER, RecapitalizeMode.of("Test", intArrayOf()))

        // mixed case
        assertEquals(RecapitalizeMode.ORIGINAL_MIXED_CASE, RecapitalizeMode.of("tEsT", intArrayOf()))

        // empty string behavior based on StringUtils
        // "isIdenticalAfterUpcase" checks if text.uppercase() == text
        assertEquals(RecapitalizeMode.ALL_UPPER, RecapitalizeMode.of("", intArrayOf()))
    }

    @Test
    fun testCount() {
        assertEquals(4, RecapitalizeMode.count())
    }

    @Test
    fun testRotate() {
        // ORIGINAL_MIXED_CASE -> ALL_LOWER
        assertEquals(RecapitalizeMode.ALL_LOWER, RecapitalizeMode.ORIGINAL_MIXED_CASE.rotate(false))
        assertEquals(RecapitalizeMode.ALL_LOWER, RecapitalizeMode.ORIGINAL_MIXED_CASE.rotate(true))

        // ALL_LOWER -> FIRST_WORD_UPPER
        assertEquals(RecapitalizeMode.FIRST_WORD_UPPER, RecapitalizeMode.ALL_LOWER.rotate(false))
        assertEquals(RecapitalizeMode.FIRST_WORD_UPPER, RecapitalizeMode.ALL_LOWER.rotate(true))

        // FIRST_WORD_UPPER -> ALL_UPPER
        assertEquals(RecapitalizeMode.ALL_UPPER, RecapitalizeMode.FIRST_WORD_UPPER.rotate(false))
        assertEquals(RecapitalizeMode.ALL_UPPER, RecapitalizeMode.FIRST_WORD_UPPER.rotate(true))

        // ALL_UPPER -> (wraps around) ORIGINAL_MIXED_CASE or ALL_LOWER based on skip flag
        assertEquals(RecapitalizeMode.ORIGINAL_MIXED_CASE, RecapitalizeMode.ALL_UPPER.rotate(false))
        assertEquals(RecapitalizeMode.ALL_LOWER, RecapitalizeMode.ALL_UPPER.rotate(true))
    }
}
