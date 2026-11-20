package com.zeroconvert.ime

import org.junit.Assert.assertEquals
import org.junit.Test

class SwipeDeleteSpanTest {
    @Test fun zeroWordsSelectsNothing() {
        assertEquals(0, SwipeDeleteSpan.lengthForWords("hello world", 0))
    }

    @Test fun englishWordsTakeTheirTrailingSpaces() {
        assertEquals("world".length, SwipeDeleteSpan.lengthForWords("hello world", 1))
        assertEquals("world  ".length, SwipeDeleteSpan.lengthForWords("hello world  ", 1))
        assertEquals("hello world".length, SwipeDeleteSpan.lengthForWords("hello world", 2))
    }

    @Test fun japaneseSplitsAtScriptChanges() {
        val text = "今日は天気です"
        assertEquals("です".length, SwipeDeleteSpan.lengthForWords(text, 1))
        assertEquals("天気です".length, SwipeDeleteSpan.lengthForWords(text, 2))
        assertEquals("は天気です".length, SwipeDeleteSpan.lengthForWords(text, 3))
    }

    @Test fun katakanaKeepsTheLongVowelMark() {
        assertEquals("コーヒー".length, SwipeDeleteSpan.lengthForWords("熱いコーヒー", 1))
    }

    @Test fun punctuationCountsPerCharacter() {
        assertEquals(1, SwipeDeleteSpan.lengthForWords("ありがとう。", 1))
        assertEquals("ありがとう。".length, SwipeDeleteSpan.lengthForWords("ありがとう。", 2))
    }

    @Test fun surrogatePairsAreNotSplit() {
        assertEquals("😀".length, SwipeDeleteSpan.lengthForWords("abc😀", 1))
    }

    @Test fun requestingMoreWordsThanExistSelectsEverything() {
        assertEquals(5, SwipeDeleteSpan.lengthForWords("ab cd", 10))
        assertEquals(0, SwipeDeleteSpan.lengthForWords("", 3))
    }
}
