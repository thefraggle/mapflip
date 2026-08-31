package de.goork.mapflip

import android.app.Application
import de.goork.mapflip.analytics.Analytics

class MapFlipApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Analytics.init(this)
    }
}
