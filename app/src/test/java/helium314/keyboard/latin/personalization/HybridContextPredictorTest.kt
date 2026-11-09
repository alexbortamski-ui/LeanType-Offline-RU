/*
 * SPDX-License-Identifier: GPL-3.0-only
 */
package helium314.keyboard.latin.personalization

import helium314.keyboard.latin.personalization.RussianContextPredictor.Prediction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HybridContextPredictorTest {
    @Test
    fun mergesDuplicateWordsWithoutDuplicatingThem() {
        val merged = HybridContextPredictor.merge(
            listOf(Prediction("пойти", 160), Prediction("есть", 155)),
            listOf(Prediction("пойти", 240), Prediction("что", 200))
        )
        assertEquals(listOf("пойти", "что", "есть"), merged.map { it.word })
        assertEquals(248, merged.first().score) // max(240) + agreement bonus
    }

    @Test
    fun strongStage3CandidateIsNotDisplacedByStage2Origin() {
        val merged = HybridContextPredictor.merge(
            listOf(Prediction("старое", 160)),
            listOf(Prediction("сильное", 200))
        )
        assertEquals("сильное", merged.first().word)
    }

    @Test
    fun strongCuratedCandidateCanOutrankWeakStage3Candidate() {
        val merged = HybridContextPredictor.merge(
            listOf(Prediction("curated", 165)),
            listOf(Prediction("weak", 120))
        )
        assertEquals("curated", merged.first().word)
    }

    @Test
    fun capsAtFiveCandidates() {
        val stage3 = (1..8).map { Prediction("w$it", 240 - it) }
        val merged = HybridContextPredictor.merge(emptyList(), stage3)
        assertEquals(5, merged.size)
    }

    @Test
    fun emptySourcesProduceNoCandidates() {
        assertTrue(HybridContextPredictor.merge(emptyList(), emptyList()).isEmpty())
    }

    @Test
    fun emptyStage3FallsBackToStage2() {
        val merged = HybridContextPredictor.merge(
            listOf(Prediction("пойти", 160)),
            emptyList()
        )
        assertEquals(listOf("пойти"), merged.map { it.word })
    }
}
