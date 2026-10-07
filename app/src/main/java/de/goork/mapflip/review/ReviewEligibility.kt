package de.goork.mapflip.review

/**
 * Pure business logic for coordinating Google Play In-App Review eligibility politely.
 * Independent of Google Play SDK to allow clean cross-flavor unit testing.
 */
object ReviewEligibility {

    const val MIN_FLIPS_FOR_REVIEW = 5
    const val MIN_DAYS_AFTER_INSTALL_MS = 3 * 24 * 60 * 60 * 1000L // 3 days
    const val PROMPT_COOLDOWN_MS = 21 * 24 * 60 * 60 * 1000L // 21 days

    /**
     * Checks whether the user satisfies all prerequisites for an in-app review prompt:
     * 1. App has been installed for at least 3 days
     * 2. At least 21 days have passed since the last review prompt
     * 3. At least 5 links have been successfully converted
     */
    fun isEligibleForReview(
        now: Long,
        flips: Int,
        installTime: Long,
        lastPrompt: Long
    ): Boolean {
        val isInstalledLongEnough = (now - installTime) >= MIN_DAYS_AFTER_INSTALL_MS
        val isCooldownPassed = (now - lastPrompt) >= PROMPT_COOLDOWN_MS
        val hasEnoughFlips = flips >= MIN_FLIPS_FOR_REVIEW
        return hasEnoughFlips && isInstalledLongEnough && isCooldownPassed
    }
}
