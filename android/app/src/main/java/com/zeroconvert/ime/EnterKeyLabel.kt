package com.zeroconvert.ime

import android.view.inputmethod.EditorInfo
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardReturn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * The editor's Enter action as a key label. Phone number, postcode and search fields ask for
 * "next" or "search" instead of a line break, and the key should say what pressing it will do.
 */
object EnterKeyLabel {
    /**
     * Mirrors InputMethodService.sendDefaultEditorAction: the editor action runs only when the
     * field has one and does not opt out of Enter actions (multi-line fields do). Returns null
     * when Enter inserts a line break or sends a plain Enter key.
     */
    fun forEditor(imeOptions: Int): String? {
        if (imeOptions and EditorInfo.IME_FLAG_NO_ENTER_ACTION != 0) return null
        return when (imeOptions and EditorInfo.IME_MASK_ACTION) {
            EditorInfo.IME_ACTION_NEXT -> "次へ"
            EditorInfo.IME_ACTION_PREVIOUS -> "前へ"
            EditorInfo.IME_ACTION_DONE -> "完了"
            EditorInfo.IME_ACTION_SEARCH -> "検索"
            EditorInfo.IME_ACTION_SEND -> "送信"
            EditorInfo.IME_ACTION_GO -> "移動"
            else -> null
        }
    }
}

/** The label for the Enter key right now, or null for the return arrow. */
val LocalEnterKeyLabel = compositionLocalOf<String?> { null }

@Composable
fun EnterKeyContent(tint: Color, iconSize: Dp = 24.dp, description: String? = null) {
    val label = LocalEnterKeyLabel.current
    if (label != null) {
        Text(text = label, color = tint, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
    } else {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardReturn,
            contentDescription = description,
            tint = tint,
            modifier = Modifier.size(iconSize)
        )
    }
}
