package de.goork.mapflip

import org.junit.Assert.assertEquals
import org.junit.Test

class AppleMapsParserTest {
    @Test
    fun `converts search query`() {
        assertEquals("geo:0,0?q=Eiffelturm",
            AppleMapsParser.convert("https://maps.apple.com/?q=Eiffelturm"))
    }

    @Test
    fun `converts coordinates`() {
        assertEquals("geo:48.8584,2.2945",
            AppleMapsParser.convert("https://maps.apple.com/?ll=48.8584,2.2945"))
    }

    @Test
    fun `converts coordinates with query`() {
        assertEquals("geo:48.8584,2.2945?q=Eiffelturm",
            AppleMapsParser.convert("https://maps.apple.com/?ll=48.8584,2.2945&q=Eiffelturm"))
    }

    @Test
    fun `converts address`() {
        assertEquals("geo:0,0?q=Berlin",
            AppleMapsParser.convert("https://maps.apple.com/?address=Berlin"))
    }

    @Test
    fun `converts directions`() {
        assertEquals("google.navigation:q=Munich",
            AppleMapsParser.convert("https://maps.apple.com/?daddr=Munich"))
    }

    @Test
    fun `converts near parameter`() {
        assertEquals("geo:0,0?q=Hamburg",
            AppleMapsParser.convert("https://maps.apple.com/?near=Hamburg"))
    }

    @Test
    fun `fallback for empty URL`() {
        assertEquals("https://www.google.com/maps",
            AppleMapsParser.convert("https://maps.apple.com/"))
    }
}
