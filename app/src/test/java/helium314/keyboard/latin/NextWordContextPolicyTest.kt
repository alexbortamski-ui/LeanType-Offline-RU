/*
 * SPDX-License-Identifier: GPL-3.0-only
 */
package helium314.keyboard.latin

import helium314.keyboard.latin.dictionary.Dictionary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NextWordContextPolicyTest {

    @Test
    fun strictNgramKeepsOnlyContextMatchedHistory() {
        assertFalse(NextWordContextPolicy.shouldKeepHistoryCandidate(strictNgram = true, hasImmediateContext = false))
        assertTrue(NextWordContextPolicy.shouldKeepHistoryCandidate(strictNgram = true, hasImmediateContext = true))
    }

    @Test
    fun relaxedModeKeepsHistoryButDoesNotBoostUnmatchedUnigram() {
        assertTrue(NextWordContextPolicy.shouldKeepHistoryCandidate(strictNgram = false, hasImmediateContext = false))
        assertEquals(
            0,
            NextWordContextPolicy.personalizationBoost(
                Dictionary.TYPE_USER_HISTORY, baseBoost = 32, hasImmediateContext = false
            )
        )
        assertEquals(
            32,
            NextWordContextPolicy.personalizationBoost(
                Dictionary.TYPE_USER_HISTORY, baseBoost = 32, hasImmediateContext = true
            )
        )
    }

    @Test
    fun personalDictionaryKeepsItsExplicitBoost() {
        assertEquals(
            32,
            NextWordContextPolicy.personalizationBoost(
                Dictionary.TYPE_USER, baseBoost = 32, hasImmediateContext = false
            )
        )
        assertEquals(
            32,
            NextWordContextPolicy.personalizationBoost(
                Dictionary.TYPE_USER, baseBoost = 64, hasImmediateContext = false
            )
        )
    }

    @Test
    fun boostLevelIsClampedToNativeScoreBudget() {
        assertEquals(0, NextWordContextPolicy.clampBoostLevel(-1))
        assertEquals(0, NextWordContextPolicy.clampBoostLevel(0))
        assertEquals(16, NextWordContextPolicy.clampBoostLevel(16))
        assertEquals(32, NextWordContextPolicy.clampBoostLevel(500))
        assertEquals(32, NextWordContextPolicy.clampBoostLevel(Int.MAX_VALUE))
    }
}
