package de.goork.mapflip

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle

class RedirectActivity : Activity() {
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
    }
}
