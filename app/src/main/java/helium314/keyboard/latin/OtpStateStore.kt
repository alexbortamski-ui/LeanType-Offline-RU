// SPDX-License-Identifier: GPL-3.0-only
package helium314.keyboard.latin

/** A process-local OTP value whose mutations are serialized across the SMS receiver and IME. */
internal class OtpStateStore(
    private val elapsedRealtime: () -> Long,
    private val ttlMillis: Long = TTL_MILLIS,
) {
    data class Snapshot(
        val generation: Long,
        val code: String?,
        val receivedAtElapsedRealtime: Long,
        val expiresAtElapsedRealtime: Long,
    )

    private var generation = 0L
    private var state = Snapshot(0L, null, 0L, 0L)

    @Synchronized
    fun publish(code: String): Snapshot {
        require(code.isNotBlank())
        val receivedAt = elapsedRealtime()
        state = Snapshot(
            generation = ++generation,
            code = code,
            receivedAtElapsedRealtime = receivedAt,
            expiresAtElapsedRealtime = receivedAt + ttlMillis,
        )
        return state
    }

    @Synchronized
    fun current(): Snapshot? {
        val current = state
        if (current.code == null) return null
        if (elapsedRealtime() >= current.expiresAtElapsedRealtime) {
            clearLocked(current.generation)
            return null
        }
        return current
    }

    @Synchronized
    fun currentGeneration(): Long = state.generation

    /** Returns null when an expected generation is stale. */
    @Synchronized
    fun clear(expectedGeneration: Long? = null): Snapshot? {
        val current = state
        if (expectedGeneration != null && current.generation != expectedGeneration) return null
        if (current.code == null) return current
        return clearLocked(current.generation)
    }

    /** An expiry may clear only the exact OTP generation for which its timer was created. */
    @Synchronized
    fun expire(expectedGeneration: Long): Snapshot? {
        val current = state
        if (current.code == null || current.generation != expectedGeneration) return null
        if (elapsedRealtime() < current.expiresAtElapsedRealtime) return null
        return clearLocked(expectedGeneration)
    }

    /** An old chip can neither insert nor clear a newer OTP. */
    @Synchronized
    fun consume(expectedGeneration: Long): String? {
        val current = state
        if (current.code == null || current.generation != expectedGeneration) return null
        if (elapsedRealtime() >= current.expiresAtElapsedRealtime) {
            clearLocked(expectedGeneration)
            return null
        }
        clearLocked(expectedGeneration)
        return current.code
    }

    @Synchronized
    fun remainingMillis(expectedGeneration: Long): Long? {
        val current = state
        if (current.code == null || current.generation != expectedGeneration) return null
        return (current.expiresAtElapsedRealtime - elapsedRealtime()).coerceAtLeast(0L)
    }

    private fun clearLocked(expectedGeneration: Long): Snapshot? {
        if (state.generation != expectedGeneration || state.code == null) return null
        state = Snapshot(++generation, null, 0L, 0L)
        return state
    }

    companion object {
        const val TTL_MILLIS = 120_000L
    }
}
