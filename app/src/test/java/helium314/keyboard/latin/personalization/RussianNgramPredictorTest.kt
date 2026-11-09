/*
 * SPDX-License-Identifier: GPL-3.0-only
 */
package helium314.keyboard.latin.personalization

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import helium314.keyboard.latin.NgramContext
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File

@RunWith(RobolectricTestRunner::class)
class RussianNgramPredictorTest {
    private lateinit var context: Context
    private lateinit var predictor: RussianNgramPredictor

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        predictor = requireNotNull(RussianNgramPredictor.openBlocking(context)) {
            "The bundled Russian n-gram model must load"
        }
    }

    @Test
    fun predictsTrigramContinuation() {
        val result = predictor.predict(
            NgramContext(NgramContext.WordInfo("то"), NgramContext.WordInfo("это"))
        )
        assertTrue(result.isNotEmpty())
        assertTrue(result.first().word == "что")
    }

    @Test
    fun predictsBigramContinuation() {
        val result = predictor.predict(NgramContext(NgramContext.WordInfo("я")))
        assertTrue(result.isNotEmpty())
        assertTrue(result.any { it.word == "не" })
    }

    @Test
    fun fallsBackToBigramWhenTrigramIsMissing() {
        val result = predictor.predict(
            NgramContext(NgramContext.WordInfo("я"), NgramContext.WordInfo("неизвестное"))
        )
        assertTrue(result.isNotEmpty())
        assertTrue(result.any { it.word == "не" })
    }

    @Test
    fun unknownContextProducesNoCandidates() {
        assertTrue(predictor.predict(NgramContext(NgramContext.WordInfo("неизвестное"))).isEmpty())
    }

    @Test
    fun missingFileIsRejected() {
        assertNull(RussianNgramPredictor.openFile(File(context.cacheDir, "missing-model.bin")))
    }

    @Test
    fun corruptFileIsRejected() {
        val corrupt = File(context.cacheDir, "corrupt-model.bin")
        corrupt.writeBytes(ByteArray(256))
        assertNull(RussianNgramPredictor.openFile(corrupt))
    }

    @Test
    fun modelProvidesOnlyContextCandidates() {
        assertNotNull(predictor)
        assertTrue(predictor.predict(NgramContext(NgramContext.WordInfo("это"))).isNotEmpty())
    }
}
