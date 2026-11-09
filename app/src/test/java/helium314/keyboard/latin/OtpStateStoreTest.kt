// SPDX-License-Identifier: GPL-3.0-only
package helium314.keyboard.latin

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class OtpStateStoreTest {
    private class FakeClock(var now: Long = 0L) {
        fun elapsedRealtime(): Long = now
        fun advanceBy(millis: Long) { now += millis }
    }

    private fun store(clock: FakeClock) = OtpStateStore(clock::elapsedRealtime)

    @Test
    fun sequentialOtpMessagesAlwaysReplacePreviousValue() {
        val state = store(FakeClock())

        val first = state.publish("3987")
        val second = state.publish("7984")
        val third = state.publish("1234")

        assertTrue(first.generation < second.generation)
        assertTrue(second.generation < third.generation)
        assertEquals("1234", state.current()?.code)
    }

    @Test
    fun successiveMessagesFromSameSenderAreNotDeduplicated() {
        // Sender identity is intentionally not part of the state key or dedupe policy.
        val state = store(FakeClock())

        val firstSms = state.publish("3987")
        val secondSms = state.publish("7984")

        assertTrue(secondSms.generation > firstSms.generation)
        assertEquals("7984", state.current()?.code)
    }

    @Test
    fun newOtpBeforeOldTtlGetsANewFullTtl() {
        val clock = FakeClock()
        val state = store(clock)
        val first = state.publish("3987")
        clock.advanceBy(OtpStateStore.TTL_MILLIS - 1)
        val second = state.publish("7984")

        clock.advanceBy(1)
        assertNull(state.expire(first.generation))
        assertEquals("7984", state.current()?.code)

        clock.advanceBy(OtpStateStore.TTL_MILLIS - 1)
        assertTrue(state.expire(second.generation) != null)
        assertNull(state.current())
    }

    @Test
    fun oldTimerCannotDeleteMoreRecentOtp() {
        val clock = FakeClock()
        val state = store(clock)
        val old = state.publish("3987")
        clock.advanceBy(60_000)
        val current = state.publish("7984")
        clock.advanceBy(60_000) // old code's original expiry

        assertNull(state.expire(old.generation))
        assertEquals("7984", state.current()?.code)
        assertFalse(state.expire(current.generation) != null)
    }

    @Test
    fun broadcastWhileImeClosedIsAvailableOnNextOpen() {
        val clock = FakeClock()
        val state = store(clock)
        state.publish("3987")
        val beforeClose = state.current()
        assertEquals("3987", beforeClose?.code)

        // No IME view is observing the store while closed; the SMS receiver can still replace it.
        val currentWhileClosed = state.publish("7984")
        clock.advanceBy(30_000)
        val reopenedImeSnapshot = state.current()

        assertEquals(currentWhileClosed.generation, reopenedImeSnapshot?.generation)
        assertEquals("7984", reopenedImeSnapshot?.code)
    }

    @Test
    fun insertionConsumesOnlyTheCurrentGeneration() {
        val state = store(FakeClock())
        val old = state.publish("3987")
        val current = state.publish("7984")

        assertNull(state.consume(old.generation))
        assertEquals("7984", state.consume(current.generation))
    }

    @Test
    fun staleDismissalCannotClearMoreRecentOtp() {
        val state = store(FakeClock())
        val old = state.publish("3987")
        state.publish("7984")

        assertNull(state.clear(old.generation))
        assertEquals("7984", state.current()?.code)
    }

    @Test
    fun consumedOtpDoesNotReturnAfterImeReopen() {
        val state = store(FakeClock())
        val current = state.publish("1234")

        assertEquals("1234", state.consume(current.generation))
        assertNull(state.current())
        assertNull(state.current()) // subsequent IME open reads the empty state
    }

    @Test
    fun rapidBroadcastsLeaveOnlyTheLastOtpAvailable() {
        val state = store(FakeClock())
        val snapshots = listOf("3987", "7984", "1234", "2468").map(state::publish)

        assertEquals(4, snapshots.map { it.generation }.distinct().size)
        assertEquals("2468", state.current()?.code)
        snapshots.dropLast(1).forEach { assertNull(state.expire(it.generation)) }
        assertEquals("2468", state.current()?.code)
    }
}
