package helium314.keyboard.latin.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class TextPlacementTest {

    @Test
    fun testTextPlacement() {
        val placement = TextPlacement("test", 5)

        assertEquals("test", placement.text)
        assertEquals(5, placement.selectionStart)
        assertEquals(9, placement.selectionEnd())

        // mutable text property behavior
        placement.text = "test2"
        assertEquals("test2", placement.text)
        assertEquals(5, placement.selectionStart)
        assertEquals(10, placement.selectionEnd())
    }

    @Test
    fun testTextPlacement_empty() {
        val placement = TextPlacement("", 0)

        assertEquals("", placement.text)
        assertEquals(0, placement.selectionStart)
        assertEquals(0, placement.selectionEnd())
    }
}
