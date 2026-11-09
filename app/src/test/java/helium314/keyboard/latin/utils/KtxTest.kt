package helium314.keyboard.latin.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class KtxTest {

    @Test
    fun testSumOf() {
        val emptyList = emptyList<Float>()
        assertEquals(0f, emptyList.sumOf { it }, 0.0001f)

        val positiveList = listOf(1.5f, 2.5f, 3.0f)
        assertEquals(7.0f, positiveList.sumOf { it }, 0.0001f)

        val mixedList = listOf(-1.0f, 2.0f, -3.5f)
        assertEquals(-2.5f, mixedList.sumOf { it }, 0.0001f)
    }

    @Test
    fun testSplitAt() {
        val emptyList = emptyList<Int>()
        val (emptyFirst, emptySecond) = emptyList.splitAt { it == 1 }
        assertEquals(emptyList<Int>(), emptyFirst)
        assertEquals(emptyList<Int>(), emptySecond)

        val list1 = listOf(1, 2, 3, 4, 5)

        // Condition never met
        val (first1, second1) = list1.splitAt { it == 10 }
        assertEquals(listOf(1, 2, 3, 4, 5), first1)
        assertEquals(emptyList<Int>(), second1)

        // Condition met in middle
        val (first2, second2) = list1.splitAt { it == 3 }
        assertEquals(listOf(1, 2), first2)
        assertEquals(listOf(4, 5), second2) // 3 is discarded

        // Condition met at first
        val (first3, second3) = list1.splitAt { it == 1 }
        assertEquals(emptyList<Int>(), first3)
        assertEquals(listOf(2, 3, 4, 5), second3)

        // Condition met at last
        val (first4, second4) = list1.splitAt { it == 5 }
        assertEquals(listOf(1, 2, 3, 4), first4)
        assertEquals(emptyList<Int>(), second4)
    }

    @Test
    fun testAddCollections() {
        val list1 = listOf(1, 2)
        val list2 = listOf(3, 4)

        assertNull(addCollections<Int>(null, null))
        assertEquals(list2, addCollections(null, list2))
        assertEquals(list1, addCollections(list1, null))

        assertEquals(list2, addCollections(emptyList(), list2))
        assertEquals(list1, addCollections(list1, emptyList()))

        assertEquals(listOf(1, 2, 3, 4), addCollections(list1, list2))
    }

    @Test
    fun testRemoveFirst() {
        val list = mutableListOf(1, 2, 3, 2, 4)

        // Condition never met
        list.removeFirst { it == 10 }
        assertEquals(listOf(1, 2, 3, 2, 4), list)

        // Condition met in middle (removes only first match)
        list.removeFirst { it == 2 }
        assertEquals(listOf(1, 3, 2, 4), list)

        // Condition met at first
        list.removeFirst { it == 1 }
        assertEquals(listOf(3, 2, 4), list)

        // Condition met at last
        list.removeFirst { it == 4 }
        assertEquals(listOf(3, 2), list)
    }

    @Test
    fun testReplaceFirst() {
        val list = mutableListOf(1, 2, 3, 2, 4)

        // Condition never met
        list.replaceFirst({ it == 10 }, { 99 })
        assertEquals(listOf(1, 2, 3, 2, 4), list)

        // Condition met in middle (replaces only first match)
        list.replaceFirst({ it == 2 }, { 99 })
        assertEquals(listOf(1, 99, 3, 2, 4), list)

        // Condition met at first
        list.replaceFirst({ it == 1 }, { 88 })
        assertEquals(listOf(88, 99, 3, 2, 4), list)

        // Condition met at last
        list.replaceFirst({ it == 4 }, { 77 })
        assertEquals(listOf(88, 99, 3, 2, 77), list)
    }
}
