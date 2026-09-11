package de.goork.mapflip.util

import de.goork.mapflip.ui.Strings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DomainVerificationHelperTest {

    @Test
    fun `domain status info correctly reflects full enablement`() {
        val info = DomainStatusInfo(
            totalHosts = 18,
            enabledHosts = 18,
            unverifiedHosts = emptyList(),
            isFullyEnabled = true,
            isPartiallyEnabled = false
        )

        assertTrue(info.isFullyEnabled)
        assertFalse(info.isPartiallyEnabled)
        assertEquals(18, info.totalHosts)
        assertEquals(18, info.enabledHosts)
        assertEquals(0, info.unverifiedHosts.size)
    }

    @Test
    fun `domain status info correctly reflects partial enablement`() {
        val info = DomainStatusInfo(
            totalHosts = 18,
            enabledHosts = 5,
            unverifiedHosts = listOf("wego.here.com", "yandex.ru"),
            isFullyEnabled = false,
            isPartiallyEnabled = true
        )

        assertFalse(info.isFullyEnabled)
        assertTrue(info.isPartiallyEnabled)
        assertEquals(18, info.totalHosts)
        assertEquals(5, info.enabledHosts)
        assertEquals(2, info.unverifiedHosts.size)
    }

    @Test
    fun `domain status info correctly reflects zero enablement`() {
        val info = DomainStatusInfo(
            totalHosts = 18,
            enabledHosts = 0,
            unverifiedHosts = listOf("maps.apple.com", "osm.org"),
            isFullyEnabled = false,
            isPartiallyEnabled = false
        )

        assertFalse(info.isFullyEnabled)
        assertFalse(info.isPartiallyEnabled)
        assertEquals(0, info.enabledHosts)
    }

    @Test
    fun `app strings status formatting returns localized values`() {
        val de = Strings.DE
        val en = Strings.EN

        assertEquals("18 von 18 Links aktiv", de.formatStatusAllActive(18))
        assertEquals("5 von 18 Links aktiv – weitere hinzufügen", de.formatStatusPartial(5, 18))

        assertEquals("18 of 18 links active", en.formatStatusAllActive(18))
        assertEquals("5 of 18 links active – tap to add more", en.formatStatusPartial(5, 18))
    }

    @Test
    fun `all 20 languages have non-blank setup guide strings`() {
        for (item in Strings.SUPPORTED_LANGUAGES) {
            val s = Strings.getStrings(item.code)
            assertTrue("setupSheetTitle missing in ${item.code}", s.setupSheetTitle.isNotBlank())
            assertTrue("setupSheetSubtitle missing in ${item.code}", s.setupSheetSubtitle.isNotBlank())
            assertTrue("setupStep1Title missing in ${item.code}", s.setupStep1Title.isNotBlank())
            assertTrue("setupStep1Desc missing in ${item.code}", s.setupStep1Desc.isNotBlank())
            assertTrue("setupStep2Title missing in ${item.code}", s.setupStep2Title.isNotBlank())
            assertTrue("setupStep2Desc missing in ${item.code}", s.setupStep2Desc.isNotBlank())
            assertTrue("setupStep3Title missing in ${item.code}", s.setupStep3Title.isNotBlank())
            assertTrue("setupStep3Desc missing in ${item.code}", s.setupStep3Desc.isNotBlank())
            assertTrue("setupAltTitle missing in ${item.code}", s.setupAltTitle.isNotBlank())
            assertTrue("setupAltShare missing in ${item.code}", s.setupAltShare.isNotBlank())
            assertTrue("setupAltClipboard missing in ${item.code}", s.setupAltClipboard.isNotBlank())
            assertTrue("setupBtnOpenSettings missing in ${item.code}", s.setupBtnOpenSettings.isNotBlank())
            assertTrue("setupBtnDismiss missing in ${item.code}", s.setupBtnDismiss.isNotBlank())
            assertTrue("setupStatusNone missing in ${item.code}", s.setupStatusNone.isNotBlank())
            assertTrue("menuSetupGuide missing in ${item.code}", s.menuSetupGuide.isNotBlank())
        }
    }
}
