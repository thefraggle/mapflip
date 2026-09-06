package de.goork.mapflip.parser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PlusCodeParserTest {

    @Test
    fun `decodes Eiffel Tower plus code correctly`() {
        // Eiffel Tower: 8FW4V75V+8G
        val code = "8FW4V75V+8G"
        assertTrue(PlusCodeParser.canParse(code))

        val decoded = PlusCodeParser.decode(code)
        assertNotNull(decoded)
        assertEquals(48.8583, decoded!!.first, 0.001)
        assertEquals(2.2944, decoded.second, 0.001)

        val parsed = PlusCodeParser.parse(code)
        assertTrue(parsed is ParsedLocation.Coordinates)
        val coords = parsed as ParsedLocation.Coordinates
        assertEquals(48.8583, coords.latitude, 0.001)
        assertEquals(2.2944, coords.longitude, 0.001)
    }

    @Test
    fun `decodes Berlin Brandenburger Tor area plus code`() {
        // Berlin Brandenburger Tor: 9F4MG98H+G3
        val code = "9F4MG98H+G3"
        val decoded = PlusCodeParser.decode(code)
        assertNotNull(decoded)
        assertEquals(52.5163, decoded!!.first, 0.001)
        assertEquals(13.3777, decoded.second, 0.001)
    }

    @Test
    fun `extracts plus code from web URL`() {
        val text = "Treffen wir uns hier: https://plus.codes/8FW4V75V+8G bis gleich!"
        val extracted = PlusCodeParser.extractUrl(text)
        assertNotNull(extracted)
        assertEquals("https://plus.codes/8FW4V75V+8G", extracted)

        val parsed = PlusCodeParser.parse(extracted!!)
        assertTrue(parsed is ParsedLocation.Coordinates)
        val coords = parsed as ParsedLocation.Coordinates
        assertEquals(48.8583, coords.latitude, 0.001)
        assertEquals(2.2944, coords.longitude, 0.001)
    }

    @Test
    fun `extracts standalone plus code from chat message`() {
        val message = "Hier ist mein Code 8FW4V75V+8G danke"
        val extracted = PlusCodeParser.extractUrl(message)
        assertNotNull(extracted)
        assertEquals("https://plus.codes/8FW4V75V+8G", extracted)
    }

    @Test
    fun `rejects invalid or non-plus-code strings`() {
        assertNull(PlusCodeParser.extractUrl(null))
        assertNull(PlusCodeParser.extractUrl(""))
        assertNull(PlusCodeParser.extractUrl("Hallo +12345678"))
        assertNull(PlusCodeParser.extractUrl("C++ ist toll"))
        assertNull(PlusCodeParser.decode("INVALID+CODE"))
        assertNull(PlusCodeParser.decode("12345+67")) // 1 is not in OLC alphabet
    }
}
