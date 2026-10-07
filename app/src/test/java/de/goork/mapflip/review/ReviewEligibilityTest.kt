package de.goork.mapflip.review

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReviewEligibilityTest {

    private val oneDay = 24 * 60 * 60 * 1000L
    private val now = 100 * oneDay

    @Test
    fun testNotEligibleWhenLessThanMinimumFlips() {
        val flips = ReviewEligibility.MIN_FLIPS_FOR_REVIEW - 1
        val installTime = now - (ReviewEligibility.MIN_DAYS_AFTER_INSTALL_MS + 1000L)
        val lastPrompt = 0L

        assertFalse(ReviewEligibility.isEligibleForReview(now, flips, installTime, lastPrompt))
    }

    @Test
    fun testNotEligibleWhenNotInstalledLongEnough() {
        val flips = ReviewEligibility.MIN_FLIPS_FOR_REVIEW
        val installTime = now - (ReviewEligibility.MIN_DAYS_AFTER_INSTALL_MS - 1000L)
        val lastPrompt = 0L

        assertFalse(ReviewEligibility.isEligibleForReview(now, flips, installTime, lastPrompt))
    }

    @Test
    fun testNotEligibleWhenCooldownNotPassed() {
        val flips = ReviewEligibility.MIN_FLIPS_FOR_REVIEW
        val installTime = now - (10 * oneDay)
        val lastPrompt = now - (ReviewEligibility.PROMPT_COOLDOWN_MS - 1000L)

        assertFalse(ReviewEligibility.isEligibleForReview(now, flips, installTime, lastPrompt))
    }

    @Test
    fun testEligibleWhenAllCriteriaMetFirstTime() {
        val flips = ReviewEligibility.MIN_FLIPS_FOR_REVIEW
        val installTime = now - (ReviewEligibility.MIN_DAYS_AFTER_INSTALL_MS + 1000L)
        val lastPrompt = 0L

        assertTrue(ReviewEligibility.isEligibleForReview(now, flips, installTime, lastPrompt))
    }

    @Test
    fun testEligibleWhenCooldownPassed() {
        val flips = ReviewEligibility.MIN_FLIPS_FOR_REVIEW + 5
        val installTime = now - (30 * oneDay)
        val lastPrompt = now - (ReviewEligibility.PROMPT_COOLDOWN_MS + 1000L)

        assertTrue(ReviewEligibility.isEligibleForReview(now, flips, installTime, lastPrompt))
    }
}
