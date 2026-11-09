/*
 * SPDX-License-Identifier: GPL-3.0-only
 */
package helium314.keyboard.latin.personalization

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import helium314.keyboard.latin.NgramContext
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.system.measureNanoTime

@RunWith(RobolectricTestRunner::class)
class RussianNgramPerformanceTest {
    @Test
    fun measureHostLoadAndQueryLatency() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        System.gc()
        Thread.sleep(100)
        val before = Runtime.getRuntime().let { it.totalMemory() - it.freeMemory() }
        var predictor: RussianNgramPredictor? = null
        val loadNanos = measureNanoTime { predictor = RussianNgramPredictor.openBlocking(context) }
        System.gc()
        Thread.sleep(100)
        val after = Runtime.getRuntime().let { it.totalMemory() - it.freeMemory() }
        val model = requireNotNull(predictor) { "model must load" }

        val contexts = listOf(
            NgramContext(NgramContext.WordInfo("то"), NgramContext.WordInfo("это")),
            NgramContext(NgramContext.WordInfo("я")),
            NgramContext(NgramContext.WordInfo("неизвестное"))
        )
        val samples = LongArray(10_000)
        for (i in samples.indices) {
            val start = System.nanoTime()
            model.predict(contexts[i % contexts.size])
            samples[i] = System.nanoTime() - start
        }
        samples.sort()
        val p50 = samples[samples.size / 2] / 1_000.0
        val p95 = samples[samples.size * 95 / 100] / 1_000.0
        println("RussianNgram host: loadMs=${loadNanos / 1_000_000.0} p50Us=$p50 p95Us=$p95 heapDeltaKb=${(after - before) / 1024}")
        assertTrue(model.predict(contexts[0]).isNotEmpty())
    }
}
