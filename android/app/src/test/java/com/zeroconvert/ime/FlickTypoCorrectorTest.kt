package com.zeroconvert.ime

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FlickTypoCorrectorTest {
    private fun tap(key: String, dragX: Float = 0f, dragY: Float = 0f, downX: Float = 0.5f, downY: Float = 0.5f) =
        FlickTouchSample(key, FlickDirection.CENTER, dragX, dragY, downX, downY)

    private fun flick(key: String, direction: FlickDirection, dragX: Float, dragY: Float, downX: Float = 0.5f, downY: Float = 0.5f) =
        FlickTouchSample(key, direction, dragX, dragY, downX, downY)

    // Words score high; any other reading scores as unknown text.
    private val dictionary = mapOf("てんき" to "天気", "あした" to "明日", "はな" to "花", "なか" to "中")
    private fun score(reading: String): Pair<String, Float> =
        dictionary[reading]?.let { it to -10f } ?: (reading to -10f - 13f * reading.length)

    @Test
    fun aShortLeftDragOnTapSuggestsTheLeftFlick() {
        // Meant てんき, but the left flick on か fell short and registered as か.
        val touches = mapOf(2 to tap("か", dragX = -0.8f))
        val result = FlickTypoCorrector.suggest("てんか", "天下", touches, ::score)
        assertEquals("天気", result?.text)
        assertEquals("てんか", result?.sourceReading)
    }

    @Test
    fun aTouchAtTheKeyEdgeSuggestsTheNeighbourKey() {
        // Meant はな, but the first tap landed on the right edge of な, next to は.
        val touches = mapOf(0 to tap("な", downX = 0.95f))
        val alternatives = FlickTypoCorrector.alternatives(touches.getValue(0)).map { it.kana }
        assertTrue("は" in alternatives)
        val result = FlickTypoCorrector.suggest("なな", "七", touches) { if (it == "はな") "花" to -10f else score(it) }
        assertEquals("花", result?.text)
    }

    @Test
    fun aCleanTapProducesNoAlternatives() {
        assertTrue(FlickTypoCorrector.alternatives(tap("か", dragX = 0.1f)).isEmpty())
        assertNull(FlickTypoCorrector.suggest("てんか", "天下", mapOf(2 to tap("か")), ::score))
    }

    @Test
    fun aCorrectlyTypedWordIsNotRewritten() {
        // A wobbly tap on き in てんき: the original is already the best reading.
        val touches = mapOf(2 to tap("か", dragX = -0.8f))
        assertNull(FlickTypoCorrector.suggest("てんき", "天気", mapOf(2 to flick("か", FlickDirection.LEFT, -1.1f, 0f)), ::score))
        // The variant must beat the original by the margin, not just be a word.
        assertNull(FlickTypoCorrector.suggest("てんか", "天下", touches) { "x" to -20f })
    }

    @Test
    fun aDiagonalFlickOffersTheOtherAxis() {
        val alternatives = FlickTypoCorrector.alternatives(flick("か", FlickDirection.LEFT, -1.6f, -1.5f)).map { it.kana }
        assertEquals(listOf("く"), alternatives)
    }

    @Test
    fun aBarelyRegisteredFlickOffersTheTap() {
        val alternatives = FlickTypoCorrector.alternatives(flick("か", FlickDirection.LEFT, -1.05f, 0f)).map { it.kana }
        assertEquals(listOf("か"), alternatives)
    }

    @Test
    fun nonKanaAlternativesAreDropped() {
        // や's left flick is "（", which is not a reading.
        assertTrue(FlickTypoCorrector.alternatives(tap("や", dragX = -0.9f)).isEmpty())
    }

    @Test
    fun theLogDropsEvidenceForRewrittenKana() {
        val log = FlickTouchLog()
        log.add(0, "か", tap("か", dragX = -0.8f))
        log.add(1, "な", tap("な"))
        assertEquals(setOf(0, 1), log.aligned("かな").keys)
        // Dakuten turned か into が: that evidence no longer describes the reading.
        assertEquals(setOf(1), log.aligned("がな").keys)
        // Inserting earlier replaces the evidence after the insertion point.
        log.add(1, "の", tap("な", dragY = 0.9f))
        assertEquals(setOf(0, 1), log.aligned("かの").keys)
    }
}
