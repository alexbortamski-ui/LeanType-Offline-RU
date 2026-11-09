/*
 * SPDX-License-Identifier: GPL-3.0-only
 */
package helium314.keyboard.latin

import helium314.keyboard.latin.dictionary.Dictionary

/**
 * Ranking rules for empty-composition (next-word) suggestions.
 *
 * These rules deliberately require evidence from the learned history instead of
 * promoting every learned unigram. A learned word without a matching preceding
 * word is not a contextual prediction.
 */
internal object NextWordContextPolicy {
    /**
     * Native binary-dictionary scores are bounded near 255. Personalization must
     * support, not replace, a strong dictionary bigram.
     */
    const val MAX_PERSONALIZATION_BOOST = 32

    fun clampBoostLevel(level: Int): Int = level.coerceIn(0, MAX_PERSONALIZATION_BOOST)

    fun shouldKeepHistoryCandidate(strictNgram: Boolean, hasImmediateContext: Boolean): Boolean =
        !strictNgram || hasImmediateContext

    fun personalizationBoost(dictType: String, baseBoost: Int, hasImmediateContext: Boolean): Int {
        val requested = if (dictType == Dictionary.TYPE_USER_HISTORY) {
            if (hasImmediateContext) baseBoost else 0
        } else {
            baseBoost
        }
        return requested.coerceIn(0, MAX_PERSONALIZATION_BOOST)
    }
}
