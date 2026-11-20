package com.zeroconvert.ime

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerId
import kotlin.math.abs
import kotlin.math.sign

/** Space key slide: moves the cursor by the given number of characters (negative = left). */
internal val LocalSpaceCursorDrag = staticCompositionLocalOf<((Int) -> Unit)?> { null }

/** Backspace slide to the left: selects whole words, deleting them when the finger lifts. */
interface SwipeDeleteHandler {
    /** Returns false when the editor state does not allow it; the key then acts as a normal press. */
    fun begin(): Boolean
    fun update(wordCount: Int)
    fun finish()
}

internal val LocalSwipeDelete = staticCompositionLocalOf<SwipeDeleteHandler?> { null }

/**
 * Waits for the pointer to lift. A horizontal movement beyond [slopPx] turns the press into a
 * drag: [onDragStart] is called once (returning false keeps it a plain press), then [onDragSteps]
 * reports each further [stepPx] of movement as a signed step delta.
 *
 * Returns true for a tap released inside the key, false for a drag or a release outside.
 */
internal suspend fun AwaitPointerEventScope.awaitTapOrHorizontalDrag(
    pointerId: PointerId,
    downPosition: Offset,
    widthPx: Float,
    heightPx: Float,
    forgivenessPx: Float,
    slopPx: Float,
    stepPx: Float,
    onDragStart: () -> Boolean,
    onDragSteps: (Int) -> Unit
): Boolean {
    var dragging = false
    var dragRejected = false
    var anchorX = downPosition.x
    while (true) {
        val event = awaitPointerEvent()
        val change = event.changes.firstOrNull { it.id == pointerId } ?: return false
        if (!change.pressed) {
            change.consume()
            if (dragging) return false
            val pos = change.position
            return pos.x >= -forgivenessPx && pos.x <= widthPx + forgivenessPx &&
                pos.y >= -forgivenessPx && pos.y <= heightPx + forgivenessPx
        }
        change.consume()
        val dx = change.position.x - anchorX
        if (!dragging && !dragRejected && abs(dx) > slopPx) {
            if (onDragStart()) {
                dragging = true
                anchorX += sign(dx) * slopPx
            } else {
                dragRejected = true
            }
        }
        if (dragging) {
            val steps = ((change.position.x - anchorX) / stepPx).toInt()
            if (steps != 0) {
                onDragSteps(steps)
                anchorX += steps * stepPx
            }
        }
    }
}

internal object SwipeDeleteSpan {
    private enum class CharClass { SPACE, HIRAGANA, KATAKANA, KANJI, WORD, OTHER }

    private fun classOf(codePoint: Int): CharClass = when {
        Character.isWhitespace(codePoint) -> CharClass.SPACE
        Character.UnicodeScript.of(codePoint) == Character.UnicodeScript.HIRAGANA -> CharClass.HIRAGANA
        Character.UnicodeScript.of(codePoint) == Character.UnicodeScript.KATAKANA ||
            codePoint == 'ー'.code -> CharClass.KATAKANA
        Character.UnicodeScript.of(codePoint) == Character.UnicodeScript.HAN ||
            codePoint == '々'.code -> CharClass.KANJI
        Character.isLetterOrDigit(codePoint) || codePoint == '_'.code -> CharClass.WORD
        else -> CharClass.OTHER
    }

    /**
     * Number of UTF-16 chars at the end of [textBeforeCursor] covering the last [wordCount]
     * words. A word is a run of one script (kanji, hiragana, katakana, latin/digits); punctuation
     * counts per character, and whitespace before the cursor goes with the word before it.
     */
    fun lengthForWords(textBeforeCursor: CharSequence, wordCount: Int): Int {
        var index = textBeforeCursor.length
        repeat(wordCount) {
            while (index > 0) {
                val cp = Character.codePointBefore(textBeforeCursor, index)
                if (classOf(cp) != CharClass.SPACE) break
                index -= Character.charCount(cp)
            }
            if (index == 0) return textBeforeCursor.length
            val first = Character.codePointBefore(textBeforeCursor, index)
            val cls = classOf(first)
            index -= Character.charCount(first)
            if (cls != CharClass.OTHER) {
                while (index > 0) {
                    val cp = Character.codePointBefore(textBeforeCursor, index)
                    if (classOf(cp) != cls) break
                    index -= Character.charCount(cp)
                }
            }
        }
        return textBeforeCursor.length - index
    }
}
