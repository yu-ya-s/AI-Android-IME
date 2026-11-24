package com.zeroconvert.ime

import android.view.inputmethod.EditorInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class EnterKeyLabelTest {
    @Test
    fun phoneAndPostcodeFieldsAskForNext() {
        assertEquals("次へ", EnterKeyLabel.forEditor(EditorInfo.IME_ACTION_NEXT))
    }

    @Test
    fun eachEditorActionHasItsOwnLabel() {
        assertEquals("完了", EnterKeyLabel.forEditor(EditorInfo.IME_ACTION_DONE))
        assertEquals("検索", EnterKeyLabel.forEditor(EditorInfo.IME_ACTION_SEARCH))
        assertEquals("送信", EnterKeyLabel.forEditor(EditorInfo.IME_ACTION_SEND))
        assertEquals("移動", EnterKeyLabel.forEditor(EditorInfo.IME_ACTION_GO))
        assertEquals("前へ", EnterKeyLabel.forEditor(EditorInfo.IME_ACTION_PREVIOUS))
    }

    @Test
    fun multiLineFieldsKeepTheReturnArrow() {
        // Multi-line editors set NO_ENTER_ACTION so Enter inserts a line break.
        assertNull(EnterKeyLabel.forEditor(EditorInfo.IME_ACTION_SEND or EditorInfo.IME_FLAG_NO_ENTER_ACTION))
        assertNull(EnterKeyLabel.forEditor(EditorInfo.IME_ACTION_UNSPECIFIED))
        assertNull(EnterKeyLabel.forEditor(EditorInfo.IME_ACTION_NONE))
    }
}
