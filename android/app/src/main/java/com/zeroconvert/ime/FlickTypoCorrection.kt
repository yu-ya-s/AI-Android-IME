package com.zeroconvert.ime

import androidx.compose.runtime.staticCompositionLocalOf
import com.zeroconvert.ime.assist.AiAssistResult
import com.zeroconvert.ime.assist.AiAssistType
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min

/**
 * One 12-key gesture as the finger made it. Drag is in units of the flick threshold and the
 * touch-down point is relative to the key (0..1), so the numbers do not depend on screen size.
 */
data class FlickTouchSample(
    val keyCenter: String,
    val direction: FlickDirection,
    val dragX: Float,
    val dragY: Float,
    val downX: Float,
    val downY: Float
) {
    val output: String get() = FlickTypoCorrector.outputOf(keyCenter, direction)
}

/** Receives every 12-key gesture before its kana is inserted. */
val LocalFlickTouchRecorder = staticCompositionLocalOf<((FlickTouchSample) -> Unit)?> { null }

/** Gesture evidence for kana typed with the 12-key layout, by reading index. */
class FlickTouchLog {
    private data class Entry(val index: Int, val kana: String, val sample: FlickTouchSample)

    private val entries = ArrayList<Entry>(64)

    fun clear() = entries.clear()

    fun add(index: Int, kana: String, sample: FlickTouchSample) {
        entries.removeAll { it.index >= index }
        entries.add(Entry(index, kana, sample))
        if (entries.size > MAX_ENTRIES) entries.removeAt(0)
    }

    /**
     * Evidence still matching [reading]. Edits that shifted or rewrote a kana (deletion in the
     * middle, dakuten, auto-commit of a prefix) make the stored kana disagree, and those entries
     * are dropped rather than applied to the wrong character.
     */
    fun aligned(reading: String): Map<Int, FlickTouchSample> = entries
        .filter { reading.regionMatches(it.index, it.kana, 0, it.kana.length) }
        .associate { it.index to it.sample }

    private companion object { const val MAX_ENTRIES = 128 }
}

object FlickTypoCorrector {
    /** A kana the finger may have meant instead of the one it produced, with 0..1 confidence. */
    data class Alternative(val kana: String, val weight: Float)

    private val grid = listOf(
        listOf(FlickKeyDefinitions.keyA, FlickKeyDefinitions.keyKa, FlickKeyDefinitions.keySa),
        listOf(FlickKeyDefinitions.keyTa, FlickKeyDefinitions.keyNa, FlickKeyDefinitions.keyHa),
        listOf(FlickKeyDefinitions.keyMa, FlickKeyDefinitions.keyYa, FlickKeyDefinitions.keyRa),
        listOf(null, FlickKeyDefinitions.keyWa, null)
    )
    private val positions: Map<String, Pair<Int, Int>> = buildMap {
        grid.forEachIndexed { row, keys -> keys.forEachIndexed { col, key -> if (key != null) put(key.center, row to col) } }
    }

    // Drag shorter than this fraction of the threshold is an ordinary tap wobble.
    private const val TAP_DRAG_MIN = 0.45f
    // A flick barely past the threshold may have been meant as a tap.
    private const val SHORT_FLICK_MAX = 1.35f
    // Near 45 degrees the other axis is about as likely.
    private const val DIAGONAL_RATIO_MIN = 0.72f
    // Touch-down this close to a key edge may have been meant for the neighbouring key.
    private const val EDGE = 0.18f

    // Dictionary score gain a correction needs: higher when the gesture evidence is weak.
    internal const val MARGIN_STRONG = 3.0f
    internal const val MARGIN_WEAK = 8.0f
    private const val MAX_VARIANTS = 12

    fun outputOf(keyCenter: String, direction: FlickDirection): String {
        val key = positions[keyCenter]?.let { (row, col) -> grid[row][col] } ?: return ""
        return when (direction) {
            FlickDirection.CENTER -> key.center
            FlickDirection.LEFT -> key.left
            FlickDirection.UP -> key.up
            FlickDirection.RIGHT -> key.right
            FlickDirection.DOWN -> key.down
        }
    }

    fun alternatives(sample: FlickTouchSample): List<Alternative> {
        if (sample.keyCenter !in positions) return emptyList()
        val result = mutableListOf<Alternative>()
        val absX = abs(sample.dragX)
        val absY = abs(sample.dragY)
        val length = hypot(sample.dragX, sample.dragY)
        if (sample.direction == FlickDirection.CENTER) {
            // "The flick did not register": the finger moved, but not past the threshold.
            if (length >= TAP_DRAG_MIN) {
                val weight = (0.3f + (length - TAP_DRAG_MIN) / (1f - TAP_DRAG_MIN) * 0.7f).coerceIn(0.3f, 1f)
                result += Alternative(outputOf(sample.keyCenter, axisDirection(sample.dragX, sample.dragY)), weight)
            }
        } else {
            if (length < SHORT_FLICK_MAX) {
                val weight = (0.3f + (SHORT_FLICK_MAX - length) / (SHORT_FLICK_MAX - 1f) * 0.5f).coerceIn(0.3f, 0.8f)
                result += Alternative(outputOf(sample.keyCenter, FlickDirection.CENTER), weight)
            }
            val ratio = if (max(absX, absY) > 0f) min(absX, absY) / max(absX, absY) else 0f
            if (ratio >= DIAGONAL_RATIO_MIN) {
                val other = if (sample.direction == FlickDirection.LEFT || sample.direction == FlickDirection.RIGHT) {
                    if (sample.dragY < 0f) FlickDirection.UP else FlickDirection.DOWN
                } else {
                    if (sample.dragX < 0f) FlickDirection.LEFT else FlickDirection.RIGHT
                }
                val weight = (0.3f + (ratio - DIAGONAL_RATIO_MIN) / (1f - DIAGONAL_RATIO_MIN) * 0.7f).coerceIn(0.3f, 1f)
                result += Alternative(outputOf(sample.keyCenter, other), weight)
            }
        }
        // "One key off": the same gesture on the key next to the edge that was touched.
        val (row, col) = positions.getValue(sample.keyCenter)
        fun edgeWeight(distance: Float) = (0.3f + (EDGE - distance) / EDGE * 0.7f).coerceIn(0.3f, 1f)
        fun neighbour(dRow: Int, dCol: Int, weight: Float) {
            val key = grid.getOrNull(row + dRow)?.getOrNull(col + dCol) ?: return
            result += Alternative(outputOf(key.center, sample.direction), weight)
        }
        if (sample.downX < EDGE) neighbour(0, -1, edgeWeight(sample.downX))
        if (sample.downX > 1f - EDGE) neighbour(0, 1, edgeWeight(1f - sample.downX))
        if (sample.downY < EDGE) neighbour(-1, 0, edgeWeight(sample.downY))
        if (sample.downY > 1f - EDGE) neighbour(1, 0, edgeWeight(1f - sample.downY))
        val typed = sample.output
        return result
            .filter { it.kana != typed && it.kana.isCorrectableKana() }
            .groupBy { it.kana }.map { (_, same) -> same.maxBy { it.weight } }
            .sortedByDescending { it.weight }
    }

    /** One-kana substitutions of [reading] backed by gesture evidence, strongest first. */
    fun variants(reading: String, touches: Map<Int, FlickTouchSample>): List<Pair<String, Float>> =
        touches.flatMap { (index, sample) ->
            alternatives(sample).map { alternative ->
                reading.substring(0, index) + alternative.kana + reading.substring(index + 1) to alternative.weight
            }
        }.sortedByDescending { it.second }.distinctBy { it.first }.take(MAX_VARIANTS)

    /**
     * Picks the variant the dictionary finds clearly more natural than what was typed.
     * [score] returns the best surface and its dictionary score (higher is better) for a reading.
     */
    fun suggest(
        reading: String,
        baseline: String,
        touches: Map<Int, FlickTouchSample>,
        score: (String) -> Pair<String, Float>?
    ): AiAssistResult? {
        if (reading.length !in 2..64 || touches.isEmpty()) return null
        val candidates = variants(reading, touches)
        if (candidates.isEmpty()) return null
        val original = score(reading)?.second ?: return null
        var best: Pair<String, Float>? = null
        var bestGain = 0f
        for ((variant, weight) in candidates) {
            val (surface, value) = score(variant) ?: continue
            val gain = value - original
            val margin = MARGIN_WEAK - (MARGIN_WEAK - MARGIN_STRONG) * weight
            if (gain >= margin && gain > bestGain && surface != baseline) {
                best = surface to gain
                bestGain = gain
            }
        }
        val surface = best?.first ?: return null
        return AiAssistResult(
            type = AiAssistType.TYPO_CORRECTION,
            text = surface,
            label = "💡 $surface",
            sourceReading = reading,
            sourceSurface = baseline
        )
    }

    private fun axisDirection(dx: Float, dy: Float): FlickDirection =
        if (abs(dx) >= abs(dy)) {
            if (dx < 0f) FlickDirection.LEFT else FlickDirection.RIGHT
        } else {
            if (dy < 0f) FlickDirection.UP else FlickDirection.DOWN
        }

    private fun String.isCorrectableKana(): Boolean =
        length == 1 && (this[0] in 'ぁ'..'ゖ' || this[0] == 'ー')
}
