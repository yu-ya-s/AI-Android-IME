package com.zeroconvert.ime

import org.junit.Assert.assertEquals
import org.junit.Test

class UserDictionaryFileTest {
    @Test fun encodedEntriesDecodeBackUnchanged() {
        val entries = listOf(UserDictEntry("ぜろこん", "ZeroCon"), UserDictEntry("やまだ", "山田"))
        assertEquals(entries, UserDictionaryFile.decode(UserDictionaryFile.encode(entries)))
    }

    @Test fun tabsAndNewlinesInsideAnEntryCannotBreakTheFormat() {
        val encoded = UserDictionaryFile.encode(listOf(UserDictEntry("よ\tみ", "単\n語")))
        assertEquals(listOf(UserDictEntry("よ み", "単 語")), UserDictionaryFile.decode(encoded))
    }

    @Test fun readsMozcAndGboardExportsByTheirFirstTwoColumns() {
        val mozc = "とうきょう\t東京\t地名\tコメント\r\n"
        val gboard = "# Gboard Dictionary version:1\nおおさか\t大阪\tja-JP\n"
        assertEquals(listOf(UserDictEntry("とうきょう", "東京")), UserDictionaryFile.decode(mozc))
        assertEquals(listOf(UserDictEntry("おおさか", "大阪")), UserDictionaryFile.decode(gboard))
    }

    @Test fun skipsBlankCommentAndIncompleteLinesAndAByteOrderMark() {
        val text = 0xFEFF.toChar() + "# header\n\nよみだけ\n\t単語だけ\nかな\t仮名\n"
        assertEquals(listOf(UserDictEntry("かな", "仮名")), UserDictionaryFile.decode(text))
    }
}
