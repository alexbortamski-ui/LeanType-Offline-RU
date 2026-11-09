/*
 * SPDX-License-Identifier: GPL-3.0-only
 */
package helium314.keyboard.latin.personalization

import android.content.Context
import helium314.keyboard.latin.NgramContext
import helium314.keyboard.latin.utils.WordTokenizer
import java.util.Locale

/**
 * Minimal offline next-word model for Russian.
 *
 * The bundled static Russian dictionary is unigram-only, so the native engine
 * cannot produce next-word candidates from it. This predictor supplies a small
 * hand-authored bigram/trigram bootstrap. It is deliberately not a full language
 * model: user history remains the primary source for personal context.
 */
internal class RussianContextPredictor private constructor(
    private val predictionsByContextSize: Map<Int, Map<String, List<Prediction>>>
) {
    data class Prediction(val word: String, val score: Int)

    fun predict(ngramContext: NgramContext): List<Prediction> {
        if (ngramContext.prevWordCount <= 0) return emptyList()

        // NgramContext stores the most recent word first. Stop at <BOS>.
        val recentWords = ArrayList<String>(3)
        for (i in 1..minOf(MAX_CONTEXT_WORDS, ngramContext.prevWordCount)) {
            if (ngramContext.isNthPrevWordBeginningOfSentence(i)) break
            val word = ngramContext.getNthPrevWord(i)?.toString() ?: break
            if (word.isEmpty()) break
            recentWords.add(normalize(word))
        }
        if (recentWords.isEmpty()) return emptyList()

        val merged = LinkedHashMap<String, Prediction>()
        for (size in recentWords.size downTo 1) {
            val key = recentWords.take(size).asReversed().joinToString(" ")
            val entries = predictionsByContextSize[size]?.get(key) ?: continue
            for (entry in entries) {
                val existing = merged[entry.word]
                if (existing == null || entry.score > existing.score) {
                    merged[entry.word] = entry
                }
            }
            if (merged.size >= MAX_PREDICTIONS) break
        }

        return merged.values
            .sortedWith(compareByDescending<Prediction> { it.score }.thenBy { it.word })
            .take(MAX_PREDICTIONS)
    }

    companion object {
        private const val ASSET_PATH = "predictions/ru_context.tsv"
        private const val MAX_CONTEXT_WORDS = 3
        private const val MAX_PREDICTIONS = 5

        @Volatile
        private var instance: RussianContextPredictor? = null

        fun get(context: Context): RussianContextPredictor {
            return instance ?: synchronized(this) {
                instance ?: load(context.applicationContext).also { instance = it }
            }
        }

        private fun load(context: Context): RussianContextPredictor {
            val lines = context.assets.open(ASSET_PATH).bufferedReader(Charsets.UTF_8).use { it.readLines() }
            return parse(lines.asSequence())
        }

        internal fun parse(lines: Sequence<String>): RussianContextPredictor {
            val bySize = HashMap<Int, MutableMap<String, MutableList<Prediction>>>()
            for (rawLine in lines) {
                val line = rawLine.trim()
                if (line.isEmpty() || line.startsWith('#')) continue
                val fields = line.split('\t')
                if (fields.size < 3) continue
                val contextWords = fields[0].trim()
                    .split(Regex("\\s+"))
                    .filter { it.isNotEmpty() }
                    .map(::normalize)
                if (contextWords.isEmpty() || contextWords.size > MAX_CONTEXT_WORDS) continue
                val word = fields[1].trim()
                if (word.isEmpty()) continue
                val score = fields[2].trim().toIntOrNull() ?: continue
                val key = contextWords.joinToString(" ")
                bySize.getOrPut(contextWords.size) { HashMap() }
                    .getOrPut(key) { ArrayList() }
                    .add(Prediction(word, score))
            }
            return RussianContextPredictor(
                bySize.mapValues { (_, entries) ->
                    entries.mapValues { (_, values) ->
                        values.sortedWith(compareByDescending<Prediction> { it.score }.thenBy { it.word })
                    }
                }
            )
        }

        private fun normalize(word: String): String =
            WordTokenizer.normalizeForLookup(word)
                .lowercase(Locale.ROOT)
                .replace('ё', 'е')
    }
}
