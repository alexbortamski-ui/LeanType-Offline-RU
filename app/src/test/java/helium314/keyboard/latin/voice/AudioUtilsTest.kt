/*
 * Copyright (C) 2026 LeanBitLab
 * SPDX-License-Identifier: GPL-3.0-only
 */
package helium314.keyboard.latin.voice

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.ByteBuffer
import java.nio.ByteOrder

class AudioUtilsTest {

    @Test
    fun testPcmToWav_headerStructure() {
        val samplePcm = ByteArray(100) { (it % 128).toByte() }
        val sampleRate = 16000
        val channels = 1
        val bitsPerSample = 16

        val wav = AudioUtils.pcmToWav(samplePcm, sampleRate, channels, bitsPerSample)

        assertEquals(44 + samplePcm.size, wav.size)

        val buffer = ByteBuffer.wrap(wav).order(ByteOrder.LITTLE_ENDIAN)

        // RIFF chunk descriptor
        val riff = ByteArray(4)
        buffer.get(riff)
        assertEquals("RIFF", String(riff, Charsets.US_ASCII))

        val chunkSize = buffer.int
        assertEquals(36 + samplePcm.size, chunkSize)

        val wave = ByteArray(4)
        buffer.get(wave)
        assertEquals("WAVE", String(wave, Charsets.US_ASCII))

        // "fmt " sub-chunk
        val fmt = ByteArray(4)
        buffer.get(fmt)
        assertEquals("fmt ", String(fmt, Charsets.US_ASCII))

        val subchunk1Size = buffer.int
        assertEquals(16, subchunk1Size)

        val audioFormat = buffer.short
        assertEquals(1, audioFormat.toInt()) // PCM = 1

        val numChannels = buffer.short
        assertEquals(channels, numChannels.toInt())

        val actualSampleRate = buffer.int
        assertEquals(sampleRate, actualSampleRate)

        val byteRate = buffer.int
        assertEquals(sampleRate * channels * bitsPerSample / 8, byteRate)

        val blockAlign = buffer.short
        assertEquals(channels * bitsPerSample / 8, blockAlign.toInt())

        val actualBitsPerSample = buffer.short
        assertEquals(bitsPerSample, actualBitsPerSample.toInt())

        // "data" sub-chunk
        val data = ByteArray(4)
        buffer.get(data)
        assertEquals("data", String(data, Charsets.US_ASCII))

        val subchunk2Size = buffer.int
        assertEquals(samplePcm.size, subchunk2Size)

        // Verify payload content matches PCM verbatim
        val pcmPayload = ByteArray(samplePcm.size)
        buffer.get(pcmPayload)
        for (i in samplePcm.indices) {
            assertEquals(samplePcm[i], pcmPayload[i])
        }
    }

    @Test
    fun testPcmToWav_emptyPcm() {
        val emptyPcm = ByteArray(0)
        val wav = AudioUtils.pcmToWav(emptyPcm, 16000, 1, 16)

        assertEquals(44, wav.size)
        val buffer = ByteBuffer.wrap(wav).order(ByteOrder.LITTLE_ENDIAN)
        buffer.position(40)
        val dataSize = buffer.int
        assertEquals(0, dataSize)
    }

    @Test
    fun testPcmToWav_stereoCustomRate() {
        val pcm = ByteArray(64)
        val wav = AudioUtils.pcmToWav(pcm, 44100, 2, 16)

        assertEquals(44 + 64, wav.size)
        val buffer = ByteBuffer.wrap(wav).order(ByteOrder.LITTLE_ENDIAN)
        buffer.position(22)
        assertEquals(2, buffer.short.toInt()) // channels
        assertEquals(44100, buffer.int) // sample rate
        assertEquals(44100 * 2 * 16 / 8, buffer.int) // byte rate
        assertEquals(4, buffer.short.toInt()) // block align
        assertEquals(16, buffer.short.toInt()) // bits per sample
    }
}
