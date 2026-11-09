/*
 * SPDX-License-Identifier: GPL-3.0-only
 */
package helium314.keyboard.latin

import android.content.Context
import androidx.core.content.edit
import androidx.test.core.app.ApplicationProvider
import helium314.keyboard.ShadowInputMethodManager2
import helium314.keyboard.ShadowLocaleManagerCompat
import helium314.keyboard.latin.SuggestedWords.SuggestedWordInfo
import helium314.keyboard.latin.common.ComposedData
import helium314.keyboard.latin.common.InputPointers
import helium314.keyboard.latin.dictionary.Dictionary
import helium314.keyboard.latin.dictionary.ExpandableBinaryDictionary
import helium314.keyboard.latin.personalization.RussianNgramPredictor
import helium314.keyboard.latin.settings.Settings
import helium314.keyboard.latin.settings.SettingsValuesForSuggestion
import helium314.keyboard.latin.utils.prefs
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
@Config(shadows = [ShadowLocaleManagerCompat::class, ShadowInputMethodManager2::class])
class NextWordSuggestionBehaviorTest {
    private lateinit var context: Context
    private lateinit var facilitator: DictionaryFacilitatorImpl
    private lateinit var history: FakeHistory

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        facilitator = DictionaryFacilitatorImpl()
        history = FakeHistory(context)
        context.prefs().edit {
            putInt(Settings.PREF_SUGGESTION_BALANCE, Settings.SUGGESTION_BALANCE_BALANCED)
            putBoolean(Settings.PREF_PRIORITIZE_PERSONAL_SUGGESTIONS, false)
            putString(Settings.PREF_NEXT_WORD_BOOST_LEVEL, "32")
        }
        Settings.init(context)
        Settings.getInstance().loadSettings(context)
        RussianNgramPredictor.preload(context)
        val deadline = System.currentTimeMillis() + 5_000
        while (RussianNgramPredictor.getIfReady() == null && System.currentTimeMillis() < deadline) {
            Thread.sleep(10)
        }
    }

    @Test
    fun strictNgramDropsLearnedWordWithoutContextMatch() {
        setStrictNgram(true)
        history.candidates = arrayListOf(historyCandidate("прыгать", 200))
        history.contextWords = emptySet()

        val result = nextWordSuggestions("неизвестное")

        assertTrue("strict mode must not keep context-free learned words", result.isEmpty())
    }

    @Test
    fun strictNgramKeepsLearnedWordWithContextMatch() {
        setStrictNgram(true)
        history.candidates = arrayListOf(historyCandidate("прыгать", 200))
        history.contextWords = setOf("прыгать")

        val result = nextWordSuggestions("неизвестное")

        assertEquals(1, result.size)
        assertEquals("прыгать", result.first().mWord)
        assertEquals(232, result.first().mScore) // 200 native + 32 context-matched boost
    }

    @Test
    fun relaxedModeKeepsUnmatchedHistoryButWithoutPersonalizationBoost() {
        setStrictNgram(false)
        history.candidates = arrayListOf(historyCandidate("прыгать", 200))
        history.contextWords = emptySet()

        val result = nextWordSuggestions("неизвестное")

        assertEquals(1, result.size)
        assertEquals("прыгать", result.first().mWord)
        assertEquals(200, result.first().mScore)
    }

    @Test
    fun contextMatchedHistoryOutranksMatchedUnigramCandidate() {
        setStrictNgram(false)
        history.candidates = arrayListOf(
            historyCandidate("прыгать", 200),
            historyCandidate("скакать", 200)
        )
        history.contextWords = setOf("прыгать")

        val result = nextWordSuggestions("неизвестное")

        assertEquals(2, result.size)
        assertEquals("прыгать", result.first().mWord)
        assertEquals(232, result.first().mScore)
        assertEquals(200, result.last().mScore)
    }

    @Test
    fun emptyDictionariesUseRussianContextBootstrap() {
        history.candidates = arrayListOf()

        val result = nextWordSuggestions("я", "хочу")

        assertTrue("known Russian context must produce candidates", result.isNotEmpty())
        assertTrue(result.any { it.mWord == "пойти" })
    }

    @Test
    fun customCandidatesMergeWithNonEmptyNativeHistory() {
        history.candidates = arrayListOf(historyCandidate("прыгать", 200))
        history.contextWords = emptySet()

        val result = nextWordSuggestions("я", "хочу")

        assertTrue(result.any { it.mWord == "прыгать" })
        assertTrue(result.any { it.mWord == "пойти" })
        assertTrue(result.size <= NextWordCandidateMerger.MAX_CANDIDATES)
    }

    @Test
    fun nativeCandidatesParticipateWhenStage3AlsoHasCandidates() {
        val main = FakeMainDictionary().apply {
            candidates = listOf("один", "два", "три", "четыре", "пять")
                .mapIndexed { index, word -> candidate(word, 220 - index * 10) }
        }

        val result = nextWordSuggestions(Locale("ru"), false, listOf("я", "хочу"), main)

        assertTrue(result.any { it.mWord == "один" })
        assertTrue(result.any { it.mWord == "пойти" })
        assertTrue(result.size <= NextWordCandidateMerger.MAX_CANDIDATES)
    }

    @Test
    fun duplicateWordIsMergedOnce() {
        history.candidates = arrayListOf(historyCandidate("пойти", 200))
        history.contextWords = emptySet()

        val result = nextWordSuggestions("я", "хочу")

        assertEquals(1, result.count { it.mWord == "пойти" })
    }

    @Test
    fun contextMatchedHistoryOutranksCustomCandidate() {
        history.candidates = arrayListOf(historyCandidate("прыгать", 200))
        history.contextWords = setOf("прыгать")

        val result = nextWordSuggestions("я", "хочу")

        assertEquals("прыгать", result.first().mWord)
        assertTrue(result.any { it.mWord == "пойти" })
    }

    @Test
    fun contextFreeHistoryDoesNotOutrankCustomCandidate() {
        history.candidates = arrayListOf(historyCandidate("прыгать", 200))
        history.contextWords = emptySet()

        val result = nextWordSuggestions("я", "хочу")

        assertEquals("чтобы", result.first().mWord)
        assertTrue(result.any { it.mWord == "прыгать" })
    }

    @Test
    fun blockOffensiveWordsFiltersHistoryWhenEnabled() {
        history.candidates = arrayListOf(historyCandidate("блять", 200))
        history.contextWords = emptySet()

        val result = nextWordSuggestions(Locale("ru"), true, listOf("я", "хочу"))

        assertTrue(result.none { it.mWord == "блять" })
    }

    @Test
    fun blockOffensiveWordsAllowsHistoryWhenDisabled() {
        history.candidates = arrayListOf(historyCandidate("блять", 200))
        history.contextWords = emptySet()

        val result = nextWordSuggestions(Locale("ru"), false, listOf("я", "хочу"))

        assertTrue(result.any { it.mWord == "блять" })
    }

    @Test
    fun nonEmptyCompositionDoesNotAddRussianCandidates() {
        history.candidates = arrayListOf(historyCandidate("прыгать", 200))
        history.contextWords = emptySet()

        val result = nextWordSuggestions(Locale("ru"), false, listOf("я", "хочу"), null, "я")

        assertTrue(result.none { it.mWord == "пойти" })
    }

    @Test
    fun unknownContextProducesNoCandidates() {
        history.candidates = arrayListOf()

        val result = nextWordSuggestions("неизвестное")

        assertTrue("unknown context must not produce arbitrary words", result.isEmpty())
    }

    @Test
    fun englishLocaleDoesNotUseRussianBootstrap() {
        history.candidates = arrayListOf()

        val result = nextWordSuggestions(Locale.ENGLISH, "хочу")

        assertTrue("Russian bootstrap must not leak into English", result.isEmpty())
    }

    @Test
    fun twoWordContextIsPassedToHistoryDictionary() {
        history.candidates = arrayListOf(historyCandidate("прыгать", 200))
        history.contextWords = setOf("прыгать")

        val result = nextWordSuggestions("очень", "хочу")

        assertTrue(result.isNotEmpty())
        assertEquals(2, history.lastPrevWordCount)
    }

    private fun setStrictNgram(enabled: Boolean) {
        val prefs = context.prefs()
        prefs.edit { putBoolean(Settings.PREF_NEXT_WORD_STRICT_NGRAM, enabled) }
        Settings.getInstance().onSharedPreferenceChanged(prefs, Settings.PREF_NEXT_WORD_STRICT_NGRAM)
    }

    private fun historyCandidate(word: String, score: Int): SuggestedWordInfo =
        SuggestedWordInfo(
            word,
            "",
            score,
            SuggestedWordInfo.KIND_PREDICTION,
            history,
            SuggestedWordInfo.NOT_AN_INDEX,
            SuggestedWordInfo.NOT_A_CONFIDENCE
        )

    private fun nextWordSuggestions(vararg previousWords: String): List<SuggestedWordInfo> =
        nextWordSuggestions(Locale("ru"), false, previousWords.toList())

    private fun nextWordSuggestions(locale: Locale, vararg previousWords: String): List<SuggestedWordInfo> =
        nextWordSuggestions(locale, false, previousWords.toList())

    private fun nextWordSuggestions(
        locale: Locale,
        blockOffensive: Boolean,
        previousWords: List<String>,
        main: Dictionary? = null,
        typedWord: String = ""
    ): List<SuggestedWordInfo> {
        val groupClass = Class.forName("helium314.keyboard.latin.DictionaryGroup")
        val constructor = groupClass.declaredConstructors.first { it.parameterCount == 4 }
        constructor.isAccessible = true
        val group = constructor.newInstance(
            locale,
            main,
            mapOf(Dictionary.TYPE_USER_HISTORY to history),
            null
        )

        val method = DictionaryFacilitatorImpl::class.java.getDeclaredMethod(
            "getSuggestions",
            ComposedData::class.java,
            NgramContext::class.java,
            SettingsValuesForSuggestion::class.java,
            Int::class.javaPrimitiveType,
            Long::class.javaPrimitiveType,
            FloatArray::class.java,
            groupClass
        )
        method.isAccessible = true
        val composedData = ComposedData(InputPointers(1), false, typedWord)
        val ngramContext = NgramContext(
            *previousWords.reversed().map { NgramContext.WordInfo(it) }.toTypedArray()
        )
        @Suppress("UNCHECKED_CAST")
        return method.invoke(
            facilitator,
            composedData,
            ngramContext,
            SettingsValuesForSuggestion(blockOffensive, false, "fallback"),
            0,
            0L,
            floatArrayOf(Dictionary.NOT_A_WEIGHT_OF_LANG_MODEL_VS_SPATIAL_MODEL),
            group
        ) as List<SuggestedWordInfo>
    }

    private class FakeMainDictionary : Dictionary(Dictionary.TYPE_MAIN, Locale("ru")) {
        var candidates: List<SuggestedWordInfo> = emptyList()

        fun candidate(word: String, score: Int): SuggestedWordInfo =
            SuggestedWordInfo(
                word,
                "",
                score,
                SuggestedWordInfo.KIND_PREDICTION,
                this,
                SuggestedWordInfo.NOT_AN_INDEX,
                SuggestedWordInfo.NOT_A_CONFIDENCE
            )

        override fun getSuggestions(
            composedData: ComposedData,
            ngramContext: NgramContext,
            proximityInfoHandle: Long,
            settingsValuesForSuggestion: SettingsValuesForSuggestion,
            sessionId: Int,
            weightForLocale: Float,
            inOutWeightOfLangModelVsSpatialModel: FloatArray?
        ): ArrayList<SuggestedWordInfo> = ArrayList(candidates)

        override fun isInDictionary(word: String): Boolean = false
    }

    private class FakeHistory(context: Context) : ExpandableBinaryDictionary(
        context,
        "PredictionStage1TestHistory",
        Locale("ru"),
        Dictionary.TYPE_USER_HISTORY,
        null
    ) {
        var candidates: List<SuggestedWordInfo> = emptyList()
        var contextWords: Set<String> = emptySet()
        var lastPrevWordCount: Int = -1

        override fun loadInitialContentsLocked() {}

        override fun getSuggestions(
            composedData: ComposedData,
            ngramContext: NgramContext,
            proximityInfoHandle: Long,
            settingsValuesForSuggestion: SettingsValuesForSuggestion,
            sessionId: Int,
            weightForLocale: Float,
            inOutWeightOfLangModelVsSpatialModel: FloatArray?
        ): ArrayList<SuggestedWordInfo> {
            lastPrevWordCount = ngramContext.prevWordCount
            return ArrayList(candidates)
        }

        override fun getNgramProbability(ngramContext: NgramContext, word: String): Int =
            if (word in contextWords) 200 else Dictionary.NOT_A_PROBABILITY

        override fun isInDictionary(word: String): Boolean = false
    }
}
