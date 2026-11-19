package de.goork.mapflip.review

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.google.android.play.core.review.ReviewManagerFactory

/**
 * Play Store adapter for [ReviewPromptPort].
 */
object ReviewHelper : ReviewPromptPort {

    override fun launchReviewFlow(
        activity: Activity,
        onComplete: () -> Unit
    ) {
        try {
            val manager = ReviewManagerFactory.create(activity)
            val request = manager.requestReviewFlow()
            request.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val reviewInfo = task.result
                    val flow = manager.launchReviewFlow(activity, reviewInfo)
                    flow.addOnCompleteListener {
                        onComplete()
                    }
                } else {
                    onComplete()
                }
            }
        } catch (e: Throwable) {
            Log.w("MapFlip", "Failed to launch in-app review flow", e)
            onComplete()
        }
    }

    override fun openStoreListing(context: Context) {
        val packageName = context.packageName
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.w("MapFlip", "Play Store app intent unavailable, falling back to web", e)
            try {
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(webIntent)
            } catch (e: Exception) {
                Log.w("MapFlip", "Failed to open Play Store web listing", e)
            }
        }
    }
}
