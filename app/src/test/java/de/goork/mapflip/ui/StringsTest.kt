package de.goork.mapflip.ui

import de.goork.mapflip.navigation.TargetNavigationApp
import org.junit.Assert.assertEquals
import org.junit.Test

class StringsTest {

    @Test
    fun testFormatSubtitleReturnsOriginalForGoogleMaps() {
        val languages = listOf("en", "de", "ja", "ko", "zh-cn", "zh-tw", "ar", "ru")
        for (lang in languages) {
            val strings = Strings.getStrings(lang)
            assertEquals(
                "Language $lang should return original subtitle for Google Maps",
                strings.subtitle,
                strings.formatSubtitle(TargetNavigationApp.GOOGLE_MAPS)
            )
        }
    }

    @Test
    fun testFormatSubtitleReplacesTargetForAlternativeApp() {
        // LTR standard
        val en = Strings.getStrings("en")
        assertEquals("Apple Maps → Waze", en.formatSubtitle(TargetNavigationApp.WAZE))

        val de = Strings.getStrings("de")
        assertEquals("Apple Maps → Waze", de.formatSubtitle(TargetNavigationApp.WAZE))

        // CJK languages
        val ja = Strings.getStrings("ja")
        assertEquals("Apple Maps → Waze", ja.formatSubtitle(TargetNavigationApp.WAZE))

        val ko = Strings.getStrings("ko")
        assertEquals("Apple Maps → Waze", ko.formatSubtitle(TargetNavigationApp.WAZE))

        val zhCn = Strings.getStrings("zh-cn")
        assertEquals("Apple Maps → Waze", zhCn.formatSubtitle(TargetNavigationApp.WAZE))

        val zhTw = Strings.getStrings("zh-tw")
        assertEquals("Apple Maps → Waze", zhTw.formatSubtitle(TargetNavigationApp.WAZE))

        // Cyrillic (source is localized as 'Apple Карты')
        val ru = Strings.getStrings("ru")
        assertEquals("Apple Карты → Waze", ru.formatSubtitle(TargetNavigationApp.WAZE))

        // Arabic RTL (source is localized as 'خرائط Apple', arrow is '←')
        val ar = Strings.getStrings("ar")
        assertEquals("خرائط Apple ← Waze", ar.formatSubtitle(TargetNavigationApp.WAZE))
    }

    @Test
    fun testFormatSubtitleReplacesTargetForSystemPicker() {
        val en = Strings.getStrings("en")
        assertEquals("Apple Maps → Navigation App", en.formatSubtitle(TargetNavigationApp.SYSTEM_PICKER))

        val de = Strings.getStrings("de")
        assertEquals("Apple Maps → Ziel-Navigations-App", de.formatSubtitle(TargetNavigationApp.SYSTEM_PICKER))

        val ja = Strings.getStrings("ja")
        assertEquals("Apple Maps → ナビゲーションアプリ", ja.formatSubtitle(TargetNavigationApp.SYSTEM_PICKER))

        val ko = Strings.getStrings("ko")
        assertEquals("Apple Maps → 내비게이션 앱", ko.formatSubtitle(TargetNavigationApp.SYSTEM_PICKER))

        val zhCn = Strings.getStrings("zh-cn")
        assertEquals("Apple Maps → 导航应用", zhCn.formatSubtitle(TargetNavigationApp.SYSTEM_PICKER))

        val zhTw = Strings.getStrings("zh-tw")
        assertEquals("Apple Maps → 導航應用程式", zhTw.formatSubtitle(TargetNavigationApp.SYSTEM_PICKER))

        val ru = Strings.getStrings("ru")
        assertEquals("Apple Карты → Навигатор", ru.formatSubtitle(TargetNavigationApp.SYSTEM_PICKER))

        val ar = Strings.getStrings("ar")
        assertEquals("خرائط Apple ← تطبيق الملاحة", ar.formatSubtitle(TargetNavigationApp.SYSTEM_PICKER))
    }

    @Test
    fun testResolveLanguageMapsLegacyIndonesianAndHant() {
        assertEquals("id", Strings.resolveLanguage("in"))
        assertEquals("id", Strings.resolveLanguage("id"))
        assertEquals("no", Strings.resolveLanguage("nb"))
        assertEquals("no", Strings.resolveLanguage("nn"))
    }
}
