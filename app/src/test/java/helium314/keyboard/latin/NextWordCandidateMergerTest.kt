/*
 * SPDX-License-Identifier: GPL-3.0-only
 */
package helium314.keyboard.latin

import helium314.keyboard.latin.SuggestedWords.SuggestedWordInfo
import helium314.keyboard.latin.dictionary.Dictionary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NextWordCandidateMergerTest {
    @Test
    fun equalScoresUseStableWordOrder() {
        val result = NextWordCandidateMerger.merge(
            listOf(
                candidate("бета", 200, NextWordCandidateMerger.Tier.CUSTOM),
                candidate("альфа", 200, NextWordCandidateMerger.Tier.CUSTOM)
            )
        )

        assertEquals(listOf("альфа", "бета"), result.map { it.mWord })
    }

    @Test
    fun nativeAndCustomCandidatesAreBothRetained() {
        val native = (1..5).map { candidate("native$it", 200 - it, NextWordCandidateMerger.Tier.CONTEXT) }
        val custom = (1..5).map { candidate("custom$it", 240 - it, NextWordCandidateMerger.Tier.CUSTOM) }

        val result = NextWordCandidateMerger.merge(native + custom)

        assertEquals(NextWordCandidateMerger.MAX_CANDIDATES, result.size)
        assertTrue(result.any { it.mWord.startsWith("native") })
        assertTrue(result.any { it.mWord.startsWith("custom") })
    }

    @Test
    fun duplicatesKeepTheHigherPriorityTier() {
        val result = NextWordCandidateMerger.merge(
            listOf(
                candidate("слово", 100, NextWordCandidateMerger.Tier.CUSTOM),
                candidate("СЛОВО", 240, NextWordCandidateMerger.Tier.CONTEXT)
            )
        )

        assertEquals(1, result.size)
        assertEquals("СЛОВО", result.first().mWord)
    }

    @Test
    fun resultIsCappedAtFive() {
        val result = NextWordCandidateMerger.merge(
            (1..12).map { candidate("w$it", 255 - it, NextWordCandidateMerger.Tier.CUSTOM) }
        )

        assertEquals(5, result.size)
    }

    private fun candidate(
        word: String,
        score: Int,
        tier: NextWordCandidateMerger.Tier
    ): NextWordCandidateMerger.Candidate =
        NextWordCandidateMerger.Candidate(
            SuggestedWordInfo(
                word,
                "",
                score,
                SuggestedWordInfo.KIND_PREDICTION,
                Dictionary.DICTIONARY_USER_TYPED,
                SuggestedWordInfo.NOT_AN_INDEX,
                SuggestedWordInfo.NOT_A_CONFIDENCE
            ),
            tier
        )
}
