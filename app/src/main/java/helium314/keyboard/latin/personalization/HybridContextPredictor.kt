/*
 * SPDX-License-Identifier: GPL-3.0-only
 */
package helium314.keyboard.latin.personalization

import android.content.Context
import helium314.keyboard.latin.NgramContext
import java.util.Locale

/**
 * Combines the Stage 2 curated bootstrap with the Stage 3 n-gram model.
 *
 * Scores are normalized to the same range before merging:
 * - Stage 2 authored scores 100..165 -> 160..200
 * - Stage 3 count-relative scores 120..240
 *
 * Duplicate words take the stronger score; agreement between sources adds a
 * small bonus. No source is unconditionally preferred.
 */
internal object HybridContextPredictor {
    private const val STAGE2_MIN = 160
    private const val STAGE2_MAX = 200
    private const val STAGE3_MIN = 120
    private const val STAGE3_MAX = 240
    private const val AGREEMENT_BONUS = 8
    private const val MAX_PREDICTIONS = 5

    fun predict(context: Context, ngramContext: NgramContext): List<RussianContextPredictor.Prediction> {
        val stage2 = RussianContextPredictor.get(context).predict(ngramContext)
        val stage3 = RussianNgramPredictor.getIfReady()?.predict(ngramContext).orEmpty()
            .map { RussianContextPredictor.Prediction(it.word, it.score) }
        return merge(stage2, stage3)
    }

    internal fun merge(
        stage2: List<RussianContextPredictor.Prediction>,
        stage3: List<RussianContextPredictor.Prediction>
    ): List<RussianContextPredictor.Prediction> {
        val merged = LinkedHashMap<String, MutableEntry>()
        stage2.forEach { add(merged, it.word, normalizeStage2(it.score), "s2") }
        stage3.forEach { add(merged, it.word, it.score.coerceIn(STAGE3_MIN, STAGE3_MAX), "s3") }
        return merged.values
            .sortedWith(compareByDescending<MutableEntry> { it.score }.thenBy { it.word })
            .take(MAX_PREDICTIONS)
            .map { RussianContextPredictor.Prediction(it.word, it.score) }
    }

    private fun add(target: MutableMap<String, MutableEntry>, word: String, score: Int, source: String) {
        if (word.isEmpty()) return
        val key = word.lowercase(Locale.ROOT)
        val existing = target[key]
        if (existing == null) {
            target[key] = MutableEntry(word, score, source)
        } else {
            val agreed = existing.source != source
            existing.score = (maxOf(existing.score, score) + if (agreed) AGREEMENT_BONUS else 0)
                .coerceAtMost(255)
            if (score > existing.score) existing.word = word
            existing.source = "both"
        }
    }

    private fun normalizeStage2(score: Int): Int {
        val clamped = score.coerceIn(100, 165)
        return STAGE2_MIN + ((clamped - 100) * (STAGE2_MAX - STAGE2_MIN) / 65)
    }

    private data class MutableEntry(var word: String, var score: Int, var source: String)
}
