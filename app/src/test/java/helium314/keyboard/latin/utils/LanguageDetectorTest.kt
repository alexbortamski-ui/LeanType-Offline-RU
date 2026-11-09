package helium314.keyboard.latin.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LanguageDetectorTest {

    @Test
    fun detectByScript_arabic() {
        val text = "مرحبا العالم"
        assertEquals("ar", LanguageDetector.detect(null, text))
    }

    @Test
    fun detectByScript_japanese() {
        val text = "こんにちは世界" // Hiragana + Kanji
        assertEquals("ja", LanguageDetector.detect(null, text))
    }

    @Test
    fun detectByScript_chinese() {
        val text = "你好世界" // Only Han
        assertEquals("zh", LanguageDetector.detect(null, text))
    }

    @Test
    fun detectByScript_korean() {
        val text = "안녕하세요 세계" // Hangul
        assertEquals("ko", LanguageDetector.detect(null, text))
    }

    @Test
    fun detectByScript_russian() {
        val text = "Привет, мир" // Cyrillic
        assertEquals("ru", LanguageDetector.detect(null, text))
    }

    @Test
    fun detectByScript_greek() {
        val text = "Γειά σου Κόσμε" // Greek
        assertEquals("el", LanguageDetector.detect(null, text))
    }

    @Test
    fun detectByScript_hindi() {
        val text = "नमस्ते दुनिया" // Devanagari
        assertEquals("hi", LanguageDetector.detect(null, text))
    }

    @Test
    fun detectByScript_hebrew() {
        val text = "שלום עולם" // Hebrew
        assertEquals("he", LanguageDetector.detect(null, text))
    }

    @Test
    fun detectByHeuristics_spanish() {
        val text = "Hola amigo, gracias por todo."
        assertEquals("es", LanguageDetector.detect(null, text))
    }

    @Test
    fun detectByHeuristics_spanish_diacritics() {
        val text = "¿Qué estás haciendo?"
        assertEquals("es", LanguageDetector.detect(null, text))
    }

    @Test
    fun detectByHeuristics_french() {
        val text = "Bonjour, merci beaucoup."
        assertEquals("fr", LanguageDetector.detect(null, text))
    }

    @Test
    fun detectByHeuristics_french_diacritics() {
        val text = "Le cœur est çà"
        assertEquals("fr", LanguageDetector.detect(null, text))
    }

    @Test
    fun detectByHeuristics_german() {
        val text = "Guten Morgen, danke schön."
        assertEquals("de", LanguageDetector.detect(null, text))
    }

    @Test
    fun detectByHeuristics_german_diacritics() {
        val text = "Ich heiße Müller und das ist süß"
        assertEquals("de", LanguageDetector.detect(null, text))
    }

    @Test
    fun detectByHeuristics_italian() {
        val text = "Ciao, buongiorno a tutti."
        assertEquals("it", LanguageDetector.detect(null, text))
    }

    @Test
    fun detectByHeuristics_portuguese() {
        val text = "Olá, muito obrigado por tudo."
        assertEquals("pt", LanguageDetector.detect(null, text))
    }

    @Test
    fun detectByHeuristics_portuguese_diacritics() {
        val text = "A maçã é tão doce"
        assertEquals("pt", LanguageDetector.detect(null, text))
    }

    @Test
    fun detectByHeuristics_english() {
        val text = "Hello world, thanks for the help."
        assertEquals("en", LanguageDetector.detect(null, text))
    }

    @Test
    fun detectByHeuristics_targetLanguageAvoidance() {
        // If it detects "es" strongly, but target is "es", and no other language is strong,
        // it might fall back to "auto" or keyboard language.
        // We'll just test that detecting "es" with target "es" returns "es" if no alternative strong candidate exists.
        val text = "Hola amigo."
        assertEquals("es", LanguageDetector.detect(null, text, "es"))
    }

    @Test
    fun detectByHeuristics_targetLanguageAvoidance_withCandidate() {
        // If it detects "es" strongly and "en" strongly, and target is "es", it should return "en".
        // "gracias" is es (2 pts), "thanks" is en (2 pts). Both >= 2.
        // If target is "es", it should return "en".
        val text = "gracias thanks"
        assertEquals("en", LanguageDetector.detect(null, text, "es"))
    }

    @Test
    fun detectEmptyString() {
        assertEquals("auto", LanguageDetector.detect(null, "   "))
    }
}
