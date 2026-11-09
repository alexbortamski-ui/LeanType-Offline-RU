package helium314.keyboard.latin.utils

import helium314.keyboard.latin.NgramContext
import helium314.keyboard.latin.makedict.DictionaryHeader
import helium314.keyboard.latin.makedict.ProbabilityInfo
import helium314.keyboard.latin.makedict.WeightedString
import helium314.keyboard.latin.makedict.WordProperty
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.ArrayList

@RunWith(RobolectricTestRunner::class)
class CombinedFormatUtilsTest {

    @Test
    fun testFormatAttributeMap() {
        val attributeMap = linkedMapOf(
            DictionaryHeader.DICTIONARY_ID_KEY to "test_dict",
            DictionaryHeader.DICTIONARY_VERSION_KEY to "1",
            DictionaryHeader.DICTIONARY_LOCALE_KEY to "en",
            DictionaryHeader.DICTIONARY_DESCRIPTION_KEY to "Test Description",
            DictionaryHeader.DICTIONARY_DATE_KEY to "1234567890"
        )
        val formattedString = CombinedFormatUtils.formatAttributeMap(attributeMap)

        val expectedString = "dictionary=test_dict,version=1,locale=en,description=Test Description,date=1234567890\n"
        assertEquals(expectedString, formattedString)
    }

    @Test
    fun testFormatAttributeMap_NoDictId() {
        val attributeMap = linkedMapOf(
            DictionaryHeader.DICTIONARY_VERSION_KEY to "1",
            DictionaryHeader.DICTIONARY_LOCALE_KEY to "en"
        )
        val formattedString = CombinedFormatUtils.formatAttributeMap(attributeMap)

        val expectedString = "dictionary=,version=1,locale=en\n"
        assertEquals(expectedString, formattedString)
    }

    @Test
    fun testFormatWordProperty_Basic() {
        val probabilityInfo = ProbabilityInfo(128)
        val wordProperty = WordProperty("test", probabilityInfo, null, null, isNotAWord = false, isPossiblyOffensive = false)
        val formattedString = CombinedFormatUtils.formatWordProperty(wordProperty)

        val expectedString = " word=test,f=128\n"
        assertEquals(expectedString, formattedString)
    }

    @Test
    fun testFormatWordProperty_AllFlags() {
        val probabilityInfo = ProbabilityInfo(128, 1000, 1, 5)
        val shortcuts = ArrayList<WeightedString>()
        shortcuts.add(WeightedString("shortcut1", ProbabilityInfo(100)))

        val bigrams = ArrayList<WeightedString>()
        bigrams.add(WeightedString("bigram1", ProbabilityInfo(50)))

        val wordProperty = WordProperty(
            "test_word",
            probabilityInfo,
            shortcuts,
            bigrams,
            isNotAWord = true,
            isPossiblyOffensive = true
        )

        val formattedString = CombinedFormatUtils.formatWordProperty(wordProperty)

        val expectedString = " word=test_word,f=128,historicalInfo=1000:1:5,not_a_word=true,possibly_offensive=true\n" +
                "  shortcut=shortcut1,f=100\n" +
                " ngram=bigram1,f=50\n" +
                "  prev_word[0]=test_word\n"

        assertEquals(expectedString, formattedString)
    }

    @Test
    fun testFormatProbabilityInfo() {
        val probabilityInfo = ProbabilityInfo(100)
        val formattedString = CombinedFormatUtils.formatProbabilityInfo(probabilityInfo)
        assertEquals("f=100", formattedString)
    }

    @Test
    fun testFormatProbabilityInfo_Historical() {
        val probabilityInfo = ProbabilityInfo(100, 1234567, 2, 10)
        val formattedString = CombinedFormatUtils.formatProbabilityInfo(probabilityInfo)
        assertEquals("f=100,historicalInfo=1234567:2:10", formattedString)
    }

    @Test
    fun testIsLiteralTrue() {
        assertTrue(CombinedFormatUtils.isLiteralTrue("true"))
        assertTrue(CombinedFormatUtils.isLiteralTrue("TRUE"))
        assertTrue(CombinedFormatUtils.isLiteralTrue("True"))
        assertFalse(CombinedFormatUtils.isLiteralTrue("false"))
        assertFalse(CombinedFormatUtils.isLiteralTrue("1"))
        assertFalse(CombinedFormatUtils.isLiteralTrue(null))
        assertFalse(CombinedFormatUtils.isLiteralTrue(""))
    }
}
