/*
 * SPDX-License-Identifier: GPL-3.0-only
 */
package helium314.keyboard.latin.personalization

import android.content.Context
import helium314.keyboard.latin.NgramContext
import helium314.keyboard.latin.utils.Log
import helium314.keyboard.latin.utils.WordTokenizer
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import java.security.MessageDigest
import java.util.Locale
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Read-only mmap-backed Russian bigram/trigram predictor.
 *
 * The model is never expanded into Kotlin word or n-gram objects. Only the
 * vocabulary hash index (small) and the mapped buffer are retained.
 *
 * Loading is asynchronous for the production singleton. If the asset is
 * missing, compressed, corrupt, or has an unexpected hash, [getIfReady]
 * returns null and the Stage 2 predictor remains active.
 */
internal class RussianNgramPredictor private constructor(
    private val buffer: ByteBuffer,
    private val header: Header,
    private val wordHashes: LongArray,
    private val wordIds: IntArray
) {
    data class Prediction(val word: String, val score: Int, val count: Int)

    private class Header(
        val vocabTableOffset: Int,
        val vocabBytesOffset: Int,
        val vocabCount: Int,
        val bigramCount: Int,
        val bigramIndexOffset: Int,
        val bigramRecordOffset: Int,
        val bigramRecordCount: Int,
        val trigramCount: Int,
        val trigramIndexOffset: Int,
        val trigramRecordOffset: Int,
        val trigramRecordCount: Int
    )

    fun predict(ngramContext: NgramContext): List<Prediction> {
        if (ngramContext.prevWordCount <= 0) return emptyList()

        val latestId = wordIdAt(ngramContext, 1) ?: return emptyList()
        if (ngramContext.prevWordCount >= 2) {
            val previousId = wordIdAt(ngramContext, 2)
            if (previousId != null) {
                val hash = hashIds(previousId.toLong(), latestId.toLong())
                val entries = findEntries(hash, header.trigramCount, header.trigramIndexOffset,
                    header.trigramRecordOffset, header.trigramRecordCount)
                if (entries.isNotEmpty()) return toPredictions(entries)
            }
        }

        val hash = hashId(latestId.toLong())
        val entries = findEntries(hash, header.bigramCount, header.bigramIndexOffset,
            header.bigramRecordOffset, header.bigramRecordCount)
        return if (entries.isEmpty()) emptyList() else toPredictions(entries)
    }

    private fun wordIdAt(ngramContext: NgramContext, position: Int): Int? {
        if (ngramContext.isNthPrevWordBeginningOfSentence(position)) return null
        val word = ngramContext.getNthPrevWord(position)?.toString() ?: return null
        if (word.isEmpty()) return null
        val id = wordId(hashWord(normalize(word)))
        return if (id < 0) null else id
    }

    private fun toPredictions(entries: List<RawEntry>): List<Prediction> {
        val topCount = entries.first().count.coerceAtLeast(1)
        return entries.map {
            val normalizedScore = 120 + (it.count * 120 / topCount).coerceIn(0, 120)
            Prediction(it.word, normalizedScore, it.count)
        }
    }

    private fun findEntries(
        hash: Long, count: Int, indexOffset: Int, recordOffset: Int, recordCount: Int
    ): List<RawEntry> {
        var lo = 0
        var hi = count - 1
        var found = -1
        while (lo <= hi) {
            val mid = (lo + hi) ushr 1
            val pos = indexOffset + mid * INDEX_ENTRY_SIZE
            val value = buffer.getLong(pos)
            val comparison = java.lang.Long.compareUnsigned(value, hash)
            if (comparison == 0) {
                found = mid
                break
            }
            if (comparison < 0) lo = mid + 1 else hi = mid - 1

        }
        if (found < 0) return emptyList()

        val indexPos = indexOffset + found * INDEX_ENTRY_SIZE
        val firstRecord = buffer.getInt(indexPos + 8)
        val entryCount = buffer.getShort(indexPos + 12).toInt() and 0xffff
        if (firstRecord < 0 || entryCount <= 0 || firstRecord + entryCount > recordCount) {
            return emptyList()
        }
        val result = ArrayList<RawEntry>(entryCount)
        for (i in 0 until entryCount) {
            val pos = recordOffset + (firstRecord + i) * RECORD_SIZE
            val wordId = buffer.getShort(pos).toInt() and 0xffff
            val entryScore = buffer.getShort(pos + 2).toInt() and 0xffff
            if (wordId >= header.vocabCount) continue
            result.add(RawEntry(decodeWord(wordId), entryScore))
        }
        return result.sortedWith(compareByDescending<RawEntry> { it.count }.thenBy { it.word })
    }

    private fun decodeWord(id: Int): String {
        val tablePos = header.vocabTableOffset + id * 4
        val start = buffer.getInt(tablePos)
        val end = buffer.getInt(tablePos + 4)
        if (start < 0 || end < start || header.vocabBytesOffset + end > buffer.capacity()) return ""
        val bytes = ByteArray(end - start)
        for (i in bytes.indices) bytes[i] = buffer.get(header.vocabBytesOffset + start + i)
        return String(bytes, Charsets.UTF_8)
    }

    private fun wordId(wordHash: Long): Int {
        var lo = 0
        var hi = wordHashes.size - 1
        while (lo <= hi) {
            val mid = (lo + hi) ushr 1
            val value = wordHashes[mid]
            val comparison = java.lang.Long.compareUnsigned(value, wordHash)
            if (comparison == 0) return wordIds[mid]
            if (comparison < 0) lo = mid + 1 else hi = mid - 1

        }
        return -1
    }

    private fun normalize(word: String): String =
        WordTokenizer.normalizeForLookup(word)
            .lowercase(Locale.ROOT)
            .replace('ё', 'е')

    private fun hashWord(word: String): Long {
        val bytes = word.toByteArray(Charsets.UTF_8)
        var h = FNV_OFFSET
        for (b in bytes) {
            h = h xor (b.toLong() and 0xff)
            h *= FNV_PRIME
        }
        return h
    }

    private fun hashIds(first: Long, second: Long): Long {
        var h = FNV_OFFSET
        h = h xor first
        h *= FNV_PRIME
        h = h xor second
        h *= FNV_PRIME
        return h
    }

    private fun hashId(id: Long): Long {
        var h = FNV_OFFSET
        h = h xor id
        h *= FNV_PRIME
        return h
    }

    private data class RawEntry(val word: String, val count: Int)

    companion object {
        private const val TAG = "RussianNgramPredictor"
        private const val ASSET_PATH = "predictions/ru_ngram_stage3_th2.bin"
        private const val EXPECTED_SHA256 =
            "ED8E78FB6EBEE590AB57EB94D911BF4F69201F1E24B39105B0647810CE9B102E"
        private const val EXPECTED_SIZE = 5_381_280L
        private const val MAGIC = 0x55524E4C
        private const val HEADER_SIZE = 128
        private const val INDEX_ENTRY_SIZE = 14
        private const val RECORD_SIZE = 4
        private const val FNV_OFFSET = 1469598103934665603L
        private const val FNV_PRIME = 1099511628211L
        private const val MAX_WORD_ID = 0xffff

        private val executor = Executors.newSingleThreadExecutor { runnable ->
            Thread(runnable, "ru-ngram-loader").apply { isDaemon = true }
        }

        @Volatile
        private var instance: RussianNgramPredictor? = null

        @Volatile
        private var failed = false

        private val started = AtomicBoolean(false)

        fun preload(context: Context) {
            if (instance != null || failed) return
            if (!started.compareAndSet(false, true)) return
            val appContext = context.applicationContext
            executor.execute {
                try {
                    instance = openAsset(appContext)
                    if (instance == null) Log.w(TAG, "Russian n-gram model unavailable; Stage 2 fallback active")
                } catch (e: Throwable) {
                    failed = true
                    Log.e(TAG, "Russian n-gram model could not be loaded; Stage 2 fallback active", e)
                }
            }
        }

        fun getIfReady(): RussianNgramPredictor? = instance

        internal fun openBlocking(context: Context): RussianNgramPredictor? {
            return try {
                openAsset(context.applicationContext)
            } catch (e: Throwable) {
                Log.e(TAG, "Russian n-gram model could not be opened", e)
                null
            }
        }

        internal fun openFile(file: File): RussianNgramPredictor? {
            if (!file.isFile) return null
            return try {
                FileInputStream(file).use { input ->
                    val mapped = input.channel.map(FileChannel.MapMode.READ_ONLY, 0, file.length())
                    parse(mapped)
                }
            } catch (e: Throwable) {
                Log.e(TAG, "Russian n-gram file rejected", e)
                null
            }
        }

        private fun openAsset(context: Context): RussianNgramPredictor? {
            try {
                val asset = context.assets.openFd(ASSET_PATH)
                try {
                    if (asset.length <= 0) return openCopiedAsset(context)
                    FileInputStream(asset.fileDescriptor).use { input ->
                        val mapped = input.channel.map(
                            FileChannel.MapMode.READ_ONLY, asset.startOffset, asset.length
                        )
                        return parse(mapped) ?: openCopiedAsset(context)
                    }
                } finally {
                    runCatching { asset.close() }
                }
            } catch (e: Throwable) {
                return openCopiedAsset(context)
            }
        }


        private fun openCopiedAsset(context: Context): RussianNgramPredictor? {
            val modelDir = File(context.filesDir, "ngram")
            if (!modelDir.exists() && !modelDir.mkdirs()) return null
            val target = File(modelDir, "ru_ngram_stage3_th2.bin")
            if (!target.isFile || target.length() != EXPECTED_SIZE) {
                val temp = File(modelDir, "ru_ngram_stage3_th2.bin.tmp")
                try {
                    context.assets.open(ASSET_PATH).use { input ->
                        FileOutputStream(temp).use { output -> input.copyTo(output) }
                    }
                    if (temp.length() != EXPECTED_SIZE) {
                        temp.delete()
                        return null
                    }
                    if (target.exists()) target.delete()
                    if (!temp.renameTo(target)) {
                        temp.delete()
                        return null
                    }
                } catch (e: Throwable) {
                    temp.delete()
                    return null
                }
            }
            return openFile(target)
        }

        private fun parse(mapped: ByteBuffer): RussianNgramPredictor? {
            if (mapped.capacity() < HEADER_SIZE) return null
            val buffer = mapped.order(ByteOrder.LITTLE_ENDIAN)
            if (buffer.getInt(0) != MAGIC) return null
            if (buffer.getShort(4).toInt() != 1 || buffer.getShort(6).toInt() != 1) return null
            if (buffer.getInt(8) != HEADER_SIZE) return null

            val digest = MessageDigest.getInstance("SHA-256")
            val duplicate = buffer.duplicate()
            val chunk = ByteArray(8192)
            while (duplicate.hasRemaining()) {
                val n = minOf(chunk.size, duplicate.remaining())
                duplicate.get(chunk, 0, n)
                digest.update(chunk, 0, n)
            }
            if (digest.digest().joinToString("") { "%02X".format(it.toInt() and 0xff) } != EXPECTED_SHA256) {
                return null
            }

            val vocabCount = buffer.getInt(12)
            val vocabTableOffset = buffer.getLong(16)
            val vocabBytesOffset = buffer.getLong(24)
            val bigramCount = buffer.getInt(32)
            val bigramIndexOffset = buffer.getLong(36)
            val bigramRecordOffset = buffer.getLong(44)
            val bigramRecordCount = buffer.getInt(52)
            val trigramCount = buffer.getInt(56)
            val trigramIndexOffset = buffer.getLong(60)
            val trigramRecordOffset = buffer.getLong(68)
            val trigramRecordCount = buffer.getInt(76)

            if (vocabCount <= 0 || vocabCount > MAX_WORD_ID) return null
            if (bigramCount < 0 || trigramCount < 0) return null
            if (bigramRecordCount < 0 || trigramRecordCount < 0) return null

            val cap = buffer.capacity().toLong()
            if (vocabTableOffset < HEADER_SIZE ||
                vocabTableOffset + (vocabCount + 1L) * 4 > vocabBytesOffset) return null
            if (vocabBytesOffset > bigramIndexOffset) return null
            if (bigramIndexOffset + bigramCount.toLong() * INDEX_ENTRY_SIZE > bigramRecordOffset) return null
            if (bigramRecordOffset + bigramRecordCount.toLong() * RECORD_SIZE > trigramIndexOffset) return null
            if (trigramIndexOffset + trigramCount.toLong() * INDEX_ENTRY_SIZE > trigramRecordOffset) return null
            if (trigramRecordOffset + trigramRecordCount.toLong() * RECORD_SIZE > cap) return null

            val vocabBytesLength = bigramIndexOffset - vocabBytesOffset
            val offsets = IntArray(vocabCount + 1)
            for (i in 0..vocabCount) {
                val value = buffer.getInt(vocabTableOffset.toInt() + i * 4)
                if (value < 0 || value > vocabBytesLength) return null
                offsets[i] = value
            }
            if (offsets[0] != 0 || offsets[vocabCount] > vocabBytesLength) return null
            for (i in 1..vocabCount) if (offsets[i] < offsets[i - 1]) return null

            val words = ArrayList<WordHash>(vocabCount)
            for (i in 0 until vocabCount) {
                val start = offsets[i]
                val end = offsets[i + 1]
                if (end < start || vocabBytesOffset + end > bigramIndexOffset) return null
                val bytes = ByteArray(end - start)
                for (j in bytes.indices) bytes[j] = buffer.get(vocabBytesOffset.toInt() + start + j)
                words.add(WordHash(hashBytes(bytes), i))
            }
            words.sortWith { a, b -> java.lang.Long.compareUnsigned(a.hash, b.hash) }
            for (i in 1 until words.size) if (words[i].hash == words[i - 1].hash) return null


            val header = Header(
                vocabTableOffset.toInt(), vocabBytesOffset.toInt(), vocabCount,
                bigramCount, bigramIndexOffset.toInt(), bigramRecordOffset.toInt(), bigramRecordCount,
                trigramCount, trigramIndexOffset.toInt(), trigramRecordOffset.toInt(), trigramRecordCount
            )
            if (!validateIndexes(buffer, header)) return null

            val hashes = LongArray(words.size) { words[it].hash }
            val ids = IntArray(words.size) { words[it].id }
            return RussianNgramPredictor(buffer, header, hashes, ids)
        }

        private fun validateIndexes(buffer: ByteBuffer, header: Header): Boolean {
            var previous = 0L
            var first = true
            for (i in 0 until header.bigramCount) {
                val pos = header.bigramIndexOffset + i * INDEX_ENTRY_SIZE
                val hash = buffer.getLong(pos)
                if (!first && java.lang.Long.compareUnsigned(hash, previous) <= 0) return false
                first = false
                previous = hash
                val offset = buffer.getInt(pos + 8)
                val count = buffer.getShort(pos + 12).toInt() and 0xffff
                if (offset < 0 || count <= 0 || offset + count > header.bigramRecordCount) return false
            }
            previous = 0L
            first = true
            for (i in 0 until header.trigramCount) {
                val pos = header.trigramIndexOffset + i * INDEX_ENTRY_SIZE
                val hash = buffer.getLong(pos)
                if (!first && java.lang.Long.compareUnsigned(hash, previous) <= 0) return false
                first = false
                previous = hash
                val offset = buffer.getInt(pos + 8)
                val count = buffer.getShort(pos + 12).toInt() and 0xffff
                if (offset < 0 || count <= 0 || offset + count > header.trigramRecordCount) return false
            }

            for (i in 0 until header.bigramRecordCount) {
                val id = buffer.getShort(header.bigramRecordOffset + i * RECORD_SIZE).toInt() and 0xffff
                if (id >= header.vocabCount) return false
            }
            for (i in 0 until header.trigramRecordCount) {
                val id = buffer.getShort(header.trigramRecordOffset + i * RECORD_SIZE).toInt() and 0xffff
                if (id >= header.vocabCount) return false
            }
            return true
        }

        private fun hashBytes(bytes: ByteArray): Long {
            var h = FNV_OFFSET
            for (b in bytes) {
                h = h xor (b.toLong() and 0xff)
                h *= FNV_PRIME
            }
            return h
        }

        private data class WordHash(val hash: Long, val id: Int)
    }
}





