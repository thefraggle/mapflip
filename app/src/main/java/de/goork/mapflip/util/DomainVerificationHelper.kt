package de.goork.mapflip.util

import android.content.Context
import android.content.pm.verify.domain.DomainVerificationManager
import android.content.pm.verify.domain.DomainVerificationUserState
import android.os.Build

data class DomainStatusInfo(
    val totalHosts: Int,
    val enabledHosts: Int,
    val unverifiedHosts: List<String>,
    val isFullyEnabled: Boolean,
    val isPartiallyEnabled: Boolean
)

object DomainVerificationHelper {
    /**
     * Detailed status of supported web links on Android 12+ (API 31+).
     * Returns null on pre-Android 12 devices.
     */
    fun getDomainStatus(context: Context): DomainStatusInfo? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return try {
                val manager = context.getSystemService(DomainVerificationManager::class.java) ?: return null
                val userState = manager.getDomainVerificationUserState(context.packageName) ?: return null
                val hostMap = userState.hostToStateMap
                val total = hostMap.size
                if (total == 0) return null

                if (!userState.isLinkHandlingAllowed) {
                    return DomainStatusInfo(
                        totalHosts = total,
                        enabledHosts = 0,
                        unverifiedHosts = hostMap.keys.toList(),
                        isFullyEnabled = false,
                        isPartiallyEnabled = false
                    )
                }

                val unverified = hostMap.filter { (_, state) ->
                    state != DomainVerificationUserState.DOMAIN_STATE_SELECTED &&
                    state != DomainVerificationUserState.DOMAIN_STATE_VERIFIED
                }.keys.toList()

                val enabled = total - unverified.size
                val isFully = unverified.isEmpty()
                val isPartially = enabled > 0 && unverified.isNotEmpty()

                DomainStatusInfo(
                    totalHosts = total,
                    enabledHosts = enabled,
                    unverifiedHosts = unverified,
                    isFullyEnabled = isFully,
                    isPartiallyEnabled = isPartially
                )
            } catch (_: Throwable) {
                null
            }
        }
        return null
    }

    /**
     * Checks if the user has enabled supported web links for MapFlip in Android system settings.
     * Returns true if links are verified/selected, false if not, or null on pre-Android 12 devices.
     */
    fun checkLinksEnabled(context: Context): Boolean? {
        val status = getDomainStatus(context) ?: return null
        return status.enabledHosts > 0
    }
}

