package de.goork.mapflip.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ClipboardUtilTest {

    @Test
    fun `getClipboardTextSafely returns text when valid clip exists`() {
        val context = mockk<Context>()
        val clipboardManager = mockk<ClipboardManager>()
        val clipData = mockk<ClipData>()
        val item = mockk<ClipData.Item>()

        every { context.getSystemService(Context.CLIPBOARD_SERVICE) } returns clipboardManager
        every { clipboardManager.hasPrimaryClip() } returns true
        every { clipboardManager.primaryClip } returns clipData
        every { clipData.itemCount } returns 1
        every { clipData.getItemAt(0) } returns item
        every { item.text } returns "https://maps.apple.com/?q=Berlin"

        val result = ClipboardUtil.getClipboardTextSafely(context)
        assertEquals("https://maps.apple.com/?q=Berlin", result)
    }

    @Test
    fun `getClipboardTextSafely returns null when no primary clip`() {
        val context = mockk<Context>()
        val clipboardManager = mockk<ClipboardManager>()

        every { context.getSystemService(Context.CLIPBOARD_SERVICE) } returns clipboardManager
        every { clipboardManager.hasPrimaryClip() } returns false

        val result = ClipboardUtil.getClipboardTextSafely(context)
        assertNull(result)
    }

    @Test
    fun `getClipboardTextSafely returns null when primary clip has zero items`() {
        val context = mockk<Context>()
        val clipboardManager = mockk<ClipboardManager>()
        val clipData = mockk<ClipData>()

        every { context.getSystemService(Context.CLIPBOARD_SERVICE) } returns clipboardManager
        every { clipboardManager.hasPrimaryClip() } returns true
        every { clipboardManager.primaryClip } returns clipData
        every { clipData.itemCount } returns 0

        val result = ClipboardUtil.getClipboardTextSafely(context)
        assertNull(result)
    }

    @Test
    fun `getClipboardTextSafely returns null when item text is null`() {
        val context = mockk<Context>()
        val clipboardManager = mockk<ClipboardManager>()
        val clipData = mockk<ClipData>()
        val item = mockk<ClipData.Item>()

        every { context.getSystemService(Context.CLIPBOARD_SERVICE) } returns clipboardManager
        every { clipboardManager.hasPrimaryClip() } returns true
        every { clipboardManager.primaryClip } returns clipData
        every { clipData.itemCount } returns 1
        every { clipData.getItemAt(0) } returns item
        every { item.text } returns null

        val result = ClipboardUtil.getClipboardTextSafely(context)
        assertNull(result)
    }

    @Test
    fun `getClipboardTextSafely catches SecurityException safely and returns null`() {
        val context = mockk<Context>()
        every { context.getSystemService(Context.CLIPBOARD_SERVICE) } throws SecurityException("Clipboard access denied")

        val result = ClipboardUtil.getClipboardTextSafely(context)
        assertNull(result)
    }

    @Test
    fun `getClipboardTextSafely returns null when service is not ClipboardManager`() {
        val context = mockk<Context>()
        every { context.getSystemService(Context.CLIPBOARD_SERVICE) } returns "NotAClipboardManager"

        val result = ClipboardUtil.getClipboardTextSafely(context)
        assertNull(result)
    }
}
