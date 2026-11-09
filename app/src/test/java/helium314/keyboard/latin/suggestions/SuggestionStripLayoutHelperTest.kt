// SPDX-License-Identifier: GPL-3.0-only
package helium314.keyboard.latin.suggestions

import helium314.keyboard.latin.SuggestedWords
import helium314.keyboard.latin.suggestions.SuggestionStripLayoutHelper.Companion.getPositionInSuggestionStrip
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class SuggestionStripLayoutHelperTest {
    // suggestion list while typing with a pending auto-correction, as created in Suggest:
    // 0: typed word (omitted from strip), 1: auto-correction, 2: typed word again, 3+: other suggestions
    private val typedWord = SuggestedWords.INDEX_OF_TYPED_WORD
    private val autoCorrection = SuggestedWords.INDEX_OF_AUTO_CORRECTION
    private val reAddedTypedWord = autoCorrection + 1

    private fun position(index: Int, willAutoCorrect: Boolean, centerTypedWord: Boolean, center: Int = 1, left: Int = 0) =
        getPositionInSuggestionStrip(index, willAutoCorrect, true, center, left, centerTypedWord)

    @Test fun autoCorrectionIsInCenterByDefault() {
        assertEquals(-1, position(typedWord, willAutoCorrect = true, centerTypedWord = false))
        assertEquals(1, position(autoCorrection, willAutoCorrect = true, centerTypedWord = false))
        assertEquals(0, position(reAddedTypedWord, willAutoCorrect = true, centerTypedWord = false))
        assertEquals(2, position(3, willAutoCorrect = true, centerTypedWord = false))
    }

    @Test fun typedWordIsInCenterWhenEnabled() {
        assertEquals(-1, position(typedWord, willAutoCorrect = true, centerTypedWord = true))
        assertEquals(0, position(autoCorrection, willAutoCorrect = true, centerTypedWord = true))
        assertEquals(1, position(reAddedTypedWord, willAutoCorrect = true, centerTypedWord = true))
        assertEquals(2, position(3, willAutoCorrect = true, centerTypedWord = true))
    }

    @Test fun centerTypedWordDoesNotChangeOrderWithoutAutoCorrection() {
        for (index in 0..3) {
            assertEquals(
                position(index, willAutoCorrect = false, centerTypedWord = false),
                position(index, willAutoCorrect = false, centerTypedWord = true)
            )
        }
    }

    @Test fun typedWordIsInCenterWithFiveSuggestions() {
        val positions = (1..5).map { position(it, willAutoCorrect = true, centerTypedWord = true, center = 2, left = 1) }
        assertEquals(listOf(1, 2, 3, 0, 4), positions)
    }
}
