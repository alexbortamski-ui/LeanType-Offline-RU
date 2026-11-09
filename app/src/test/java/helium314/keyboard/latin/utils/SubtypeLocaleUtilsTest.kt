package helium314.keyboard.latin.utils

import android.view.inputmethod.InputMethodSubtype
import androidx.test.core.app.ApplicationProvider
import helium314.keyboard.latin.utils.SubtypeLocaleUtils.displayName
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
class SubtypeLocaleUtilsTest {

    @Before
    fun setup() {
        SubtypeLocaleUtils.init(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun testGetSubtypeLocaleDisplayName() {
        // Standard locale
        assertEquals("English (US)", SubtypeLocaleUtils.getSubtypeLocaleDisplayName(Locale("en", "US")))

        // NO_LANGUAGE should fallback to system default layout or "No language" handling
        // Based on NO_LANGUAGE = "zz", test system logic
        val noLanguage = SubtypeLocaleUtils.getSubtypeLocaleDisplayName(Locale(SubtypeLocaleUtils.NO_LANGUAGE))
        assert(noLanguage.isNotEmpty())
    }

    @Test
    fun testGetSubtypeLanguageDisplayName() {
        assertEquals("English", SubtypeLocaleUtils.getSubtypeLanguageDisplayName(Locale("en", "US")))
        assertEquals("Deutsch", SubtypeLocaleUtils.getSubtypeLanguageDisplayName(Locale("de", "DE")))

        // NO_LANGUAGE
        val noLanguage = SubtypeLocaleUtils.getSubtypeLanguageDisplayName(Locale(SubtypeLocaleUtils.NO_LANGUAGE))
        assert(noLanguage.isNotEmpty())
    }

    @Test
    fun testGetSubtypeNameForLogging() {
        assertEquals("<null subtype>", SubtypeLocaleUtils.getSubtypeNameForLogging(null))

        val subtype = InputMethodSubtype.InputMethodSubtypeBuilder()
            .setLanguageTag("fr")
            .build()

        assertEquals("fr/qwerty", SubtypeLocaleUtils.getSubtypeNameForLogging(subtype)) // Fallbacks to qwerty because mainLayoutName missing
    }

    @Test
    fun testSubtypeDisplayNameExtension() {
        val subtype = InputMethodSubtype.InputMethodSubtypeBuilder()
            .setLanguageTag("en-US")
            .setSubtypeExtraValue("KeyboardLayoutSet=MAIN:qwerty")
            .build()

        val displayName = SubtypeLocaleUtils.run { subtype.displayName() }
        assert(displayName.isNotEmpty())
    }

    @Test
    fun testTamilV4LayoutAndSubtypeName() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        SubtypeSettings.init(context)

        // Resource subtypes should only have the base subtype (no duplicate language rows)
        val taInResourceSubtypes = SubtypeSettings.getResourceSubtypesForLocale(Locale("ta", "IN"))
        assertEquals(1, taInResourceSubtypes.size)
        assertEquals("tamil", taInResourceSubtypes.first().mainLayoutName())

        val taSgResourceSubtypes = SubtypeSettings.getResourceSubtypesForLocale(Locale("ta", "SG"))
        assertEquals(1, taSgResourceSubtypes.size)
        assertEquals("tamil", taSgResourceSubtypes.first().mainLayoutName())

        // Available layouts for Tamil locales must include tamil and tamil_v4
        val taInLayouts = LayoutUtils.getAvailableLayouts(LayoutType.MAIN, context, Locale("ta", "IN"))
        assert(taInLayouts.contains("tamil"))
        assert(taInLayouts.contains("tamil_v4"))

        val taSgLayouts = LayoutUtils.getAvailableLayouts(LayoutType.MAIN, context, Locale("ta", "SG"))
        assert(taSgLayouts.contains("tamil"))
        assert(taSgLayouts.contains("tamil_v4"))

        // Display name for layout
        assertEquals("Tamil (V4)", SubtypeLocaleUtils.getMainLayoutDisplayName("tamil_v4"))
        assertEquals("Tamil (V4)", SubtypeLocaleUtils.getLayoutDisplayNameInSystemLocale("tamil_v4", Locale("ta", "IN")))

        // Additional subtype displayName
        val v4Subtype = SubtypeUtilsAdditional.createEmojiCapableAdditionalSubtype(Locale.forLanguageTag("ta-IN"), "tamil_v4", false)
        val v4DisplayName = SubtypeLocaleUtils.run { v4Subtype.displayName(Locale.ENGLISH) }
        assertEquals("Tamil (India) (V4)", v4DisplayName)
    }
}
