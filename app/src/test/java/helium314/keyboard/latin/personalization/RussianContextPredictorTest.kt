/*
 * SPDX-License-Identifier: GPL-3.0-only
 */
package helium314.keyboard.latin.personalization

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import helium314.keyboard.latin.NgramContext
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RussianContextPredictorTest {
    private lateinit var predictor: RussianContextPredictor

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        predictor = RussianContextPredictor.get(context)
    }

    @Test
    fun predictsCommonBigramContinuation() {
        val predictions = predictor.predict(NgramContext(NgramContext.WordInfo("хочу"), NgramContext.WordInfo("я")))
        assertTrue(predictions.any { it.word == "пойти" })
    }

    @Test
    fun predictsAfterPrepositionAndNoun() {
        val predictions = predictor.predict(NgramContext(NgramContext.WordInfo("работе"), NgramContext.WordInfo("на")))
        assertTrue(predictions.any { it.word == "был" })
    }

    @Test
    fun predictsThanksContinuation() {
        val predictions = predictor.predict(NgramContext(NgramContext.WordInfo("за"), NgramContext.WordInfo("спасибо")))
        assertTrue(predictions.any { it.word == "помощь" })
    }

    @Test
    fun predictsGreetingContinuation() {
        val predictions = predictor.predict(NgramContext(NgramContext.WordInfo("утро"), NgramContext.WordInfo("доброе")))
        assertTrue(predictions.any { it.word == "всем" })
    }

    @Test
    fun prefersTrigramContextWhenAvailable() {
        val predictions = predictor.predict(
            NgramContext(
                NgramContext.WordInfo("знаю"),
                NgramContext.WordInfo("не"),
                NgramContext.WordInfo("я")
            )
        )
        assertEquals("что", predictions.first().word)
    }

    @Test
    fun unknownContextProducesNoCandidates() {
        assertTrue(predictor.predict(NgramContext(NgramContext.WordInfo("неизвестное"))).isEmpty())
    }

    @Test
    fun beginningOfSentenceProducesNoCandidates() {
        val predictions = predictor.predict(
            NgramContext(NgramContext.WordInfo.BEGINNING_OF_SENTENCE_WORD_INFO)
        )
        assertTrue(predictions.isEmpty())
    }
}
