/*
 * SPDX-License-Identifier: GPL-3.0-only
 */
package helium314.keyboard.latin

import helium314.keyboard.latin.SuggestedWords.SuggestedWordInfo
import java.util.Locale

internal object NextWordCandidateMerger {
    internal enum class Tier {
        PERSONAL,
        CONTEXT,
        CUSTOM,
        GENERIC
    }

    internal data class Candidate(val info: SuggestedWordInfo, val tier: Tier)

    internal const val MAX_CANDIDATES = 5
    private const val MAX_RANK_SCORE = 255

    fun merge(candidates: List<Candidate>, maxCandidates: Int = MAX_CANDIDATES): List<SuggestedWordInfo> {
        if (candidates.isEmpty() || maxCandidates <= 0) return emptyList()

        val queues = Tier.entries.map { tier ->
            ArrayDeque(
                candidates.asSequence()
                    .filter { it.tier == tier && it.info.mWord.isNotEmpty() }
                    .sortedWith(compareByDescending<Candidate> { it.info.mScore }.thenBy { normalize(it.info.mWord) })
                    .toList()
            )
        }
        val seen = HashSet<String>()
        val merged = ArrayList<SuggestedWordInfo>(maxCandidates)

        while (merged.size < maxCandidates) {
            var added = false
            for (queue in queues) {
                while (queue.isNotEmpty()) {
                    val candidate = queue.removeFirst()
                    val key = normalize(candidate.info.mWord)
                    if (!seen.add(key)) continue
                    val info = candidate.info
                    merged.add(
                        SuggestedWordInfo(
                            info.mWord,
                            info.mPrevWordsContext,
                            MAX_RANK_SCORE - merged.size,
                            info.mKindAndFlags,
                            info.mSourceDict,
                            info.mIndexOfTouchPointOfSecondWord,
                            info.mAutoCommitFirstWordConfidence
                        )
                    )
                    added = true
                    break
                }
                if (merged.size >= maxCandidates) break
            }
            if (!added) break
        }
        return merged
    }

    private fun normalize(word: String): String =
        word.lowercase(Locale.ROOT).replace('ё', 'е')
}
