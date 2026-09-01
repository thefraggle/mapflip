package de.goork.mapflip.util

import android.content.Context
import android.content.pm.verify.domain.DomainVerificationManager
import android.content.pm.verify.domain.DomainVerificationUserState
import android.os.Build

object DomainVerificationHelper {
    /**
     * Checks if the user has enabled supported web links for MapFlip in Android system settings.
     * Returns true if links are verified/selected, false if not, or null on pre-Android 12 devices.
     */
    fun checkLinksEnabled(context: Context): Boolean? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return try {
                val manager = context.getSystemService(DomainVerificationManager::class.java)
                val userState = manager?.getDomainVerificationUserState(context.packageName) ?: return null
                if (!userState.isLinkHandlingAllowed) return false

                val hostMap = userState.hostToStateMap
                hostMap.values.any { state ->
                    state == DomainVerificationUserState.DOMAIN_STATE_SELECTED ||
                    state == DomainVerificationUserState.DOMAIN_STATE_VERIFIED
                }
            } catch (_: Exception) {
                null
            }
        }
        return null
    }
}
