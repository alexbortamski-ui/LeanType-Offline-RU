package helium314.keyboard.latin.utils

import android.content.Context
import android.content.res.Configuration
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
class LocaleUtilsTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun parseLocale_simpleCode_returnsLocale() {
        val locale = LocaleUtils.parseLocale("en")
        assertEquals("en", locale.language)
        assertEquals("", locale.country)
    }

    @Test
    fun parseLocale_combinedCode_returnsLocaleWithCountry() {
        val locale = LocaleUtils.parseLocale("pt-BR")
        assertEquals("pt", locale.language)
        assertEquals("BR", locale.country)
    }

    @Test
    fun wrapContextWithLocale_systemTag_returnsContextWithSystemLocale() {
        val wrappedContext = LocaleUtils.wrapContextWithLocale(context, "system")
        val config = wrappedContext.resources.configuration
        // Should not crash and should produce a valid context
        assertTrue(config != null)
    }

    @Test
    fun wrapContextWithLocale_validTag_returnsContextWithSpecifiedLocale() {
        val wrappedContext = LocaleUtils.wrapContextWithLocale(context, "pt-BR")
        val config = wrappedContext.resources.configuration

        val locales = config.locales
        assertEquals("pt", locales.get(0).language)
        assertEquals("BR", locales.get(0).country)
    }

    @Test
    fun applyAppLanguageToResources_validTag_updatesConfiguration() {
        LocaleUtils.applyAppLanguageToResources(context, "fr")
        val config = context.resources.configuration
        assertEquals("fr", config.locales.get(0).language)
    }

    @Test
    fun getAppLanguageItems_containsSystemAndSpecificLocales() {
        val items = LocaleUtils.getAppLanguageItems(context)

        // At least system + elements in localeCodes
        assertTrue(items.size > LocaleUtils.localeCodes.size)

        // System item should be first and have empty tag
        assertEquals("", items[0].second)

        // Check a known locale
        val enItem = items.find { it.second == "en" }
        assertTrue(enItem != null)
        assertTrue(enItem!!.first.isNotEmpty())
    }
}
