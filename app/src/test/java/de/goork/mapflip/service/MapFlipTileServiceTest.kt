package de.goork.mapflip.service

import android.app.PendingIntent
import android.content.Intent
import android.service.quicksettings.Tile
import de.goork.mapflip.data.PreferencesRepository
import de.goork.mapflip.util.DomainVerificationHelper
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MapFlipTileServiceTest {

    private lateinit var repository: PreferencesRepository

    @Before
    fun setUp() {
        val context = RuntimeEnvironment.getApplication()
        repository = PreferencesRepository.getInstance(context)
        repository.unpause()
        mockkObject(DomainVerificationHelper)
        every { DomainVerificationHelper.checkLinksEnabled(any()) } returns true
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun testOnStartListeningUpdatesTileActive() {
        val controller = Robolectric.buildService(MapFlipTileService::class.java)
        val rawService = controller.create().get()
        val service = spyk(rawService)

        val tile = service.qsTile
        if (tile != null) {
            service.onStartListening()
            assertEquals(Tile.STATE_ACTIVE, tile.state)
        }
    }

    @Test
    fun testOnStartListeningUpdatesTilePaused() {
        repository.pauseIndefinitely()
        val controller = Robolectric.buildService(MapFlipTileService::class.java)
        val rawService = controller.create().get()
        val service = spyk(rawService)

        val tile = service.qsTile
        if (tile != null) {
            service.onStartListening()
            assertEquals(Tile.STATE_INACTIVE, tile.state)
        }
    }

    @Test
    fun testOnClickWhenPausedAndLinksEnabledUnpauses() {
        every { DomainVerificationHelper.checkLinksEnabled(any()) } returns true
        repository.pauseIndefinitely()
        assertTrue(repository.isCurrentlyPaused())

        val controller = Robolectric.buildService(MapFlipTileService::class.java)
        val rawService = controller.create().get()
        val service = spyk(rawService) {
            every { startActivityAndCollapse(any<PendingIntent>()) } returns Unit
            every { @Suppress("DEPRECATION") startActivityAndCollapse(any<Intent>()) } returns Unit
        }

        service.onClick()
        assertFalse(repository.isCurrentlyPaused())
    }

    @Test
    fun testOnClickWhenLinksDisabledOpensSettingsAndKeepsPauseState() {
        every { DomainVerificationHelper.checkLinksEnabled(any()) } returns false
        repository.pauseIndefinitely()
        assertTrue(repository.isCurrentlyPaused())

        val controller = Robolectric.buildService(MapFlipTileService::class.java)
        val rawService = controller.create().get()
        val service = spyk(rawService) {
            every { startActivityAndCollapse(any<PendingIntent>()) } returns Unit
            every { @Suppress("DEPRECATION") startActivityAndCollapse(any<Intent>()) } returns Unit
        }

        service.onClick()
        assertTrue(repository.isCurrentlyPaused())
        verify(exactly = 1) { service.startActivityAndCollapse(any<PendingIntent>()) }
    }

    @Test
    fun testOnClickWhenActiveLaunchesPauseDialog() {
        every { DomainVerificationHelper.checkLinksEnabled(any()) } returns true
        repository.unpause()

        val controller = Robolectric.buildService(MapFlipTileService::class.java)
        val rawService = controller.create().get()
        val service = spyk(rawService) {
            every { startActivityAndCollapse(any<PendingIntent>()) } returns Unit
            every { @Suppress("DEPRECATION") startActivityAndCollapse(any<Intent>()) } returns Unit
        }

        service.onClick()
        verify(exactly = 1) { service.startActivityAndCollapse(any<PendingIntent>()) }
    }
}
