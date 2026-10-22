package de.goork.mapflip.util

import android.content.ClipboardManager
import android.content.Context
import android.util.Log

object ClipboardUtil {
    fun getClipboardTextSafely(context: Context): String? {
        return try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            val clip = clipboard?.takeIf { it.hasPrimaryClip() }?.primaryClip
            if (clip != null && clip.itemCount > 0) clip.getItemAt(0)?.text?.toString() else null
        } catch (e: Throwable) {
            Log.d("MapFlip", "Clipboard read unavailable", e)
            null
        }
    }
}
