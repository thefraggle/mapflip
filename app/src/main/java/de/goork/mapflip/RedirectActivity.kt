package de.goork.mapflip

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle

/**
 * Transparent activity that silently redirects Apple Maps links to Google Maps.
 * Uses no-animation transitions to remain completely invisible to the user.
 */
class RedirectActivity : Activity() {
    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appleUrl = intent?.data?.toString()
        if (appleUrl != null) {
            val googleUri = AppleMapsParser.convert(appleUrl)
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(googleUri)).apply {
                    setPackage("com.google.android.apps.maps")
                })
            } catch (_: Exception) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(googleUri)))
            }
        }
        finish()
        // Suppress transition animation so the activity is completely invisible
        overridePendingTransition(0, 0)
    }
}
