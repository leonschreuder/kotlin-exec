package io.github.leonschreuder.kotlinexec

import kotlin.test.assertEquals
import org.junit.Test

class WordSplitterTest {

    @Test
    fun should_split_words() {
        assertEquals(listOf("one", "two"), "one two".splitWords())
        assertEquals(listOf("one", "two three"), "one 'two three'".splitWords())
        assertEquals(listOf("one", "two three"), "one \"two three\"".splitWords())
        assertEquals(listOf("one", "two \"three\""), "one \"two \\\"three\\\"\"".splitWords())
    }

    @Test
    fun split_words2() {
        assertEquals(listOf("one", "two"), "one two".splitWords())
        assertEquals(listOf("one", "two three", "four"), "one 'two three' four".splitWords())
        assertEquals(listOf("one", "two three"), "one \"two three\"".splitWords())

        assertEquals(listOf("one", "two three's", "four"), "one 'two three\\'s' four".splitWords())
        assertEquals(
            listOf("one", "two \"and\" three", "four"),
            "one \"two \\\"and\\\" three\" four".splitWords()
        )

        assertEquals(
            listOf("one", "two \"three's", "four"),
            "one 'two \"three\\'s' four".splitWords()
        )
    }
}
