package de.goork.mapflip

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class VersionConsistencyTest {

    private fun findProjectRoot(): File {
        var dir: File? = File(".").canonicalFile
        while (dir != null && !File(dir, "settings.gradle.kts").exists()) {
            dir = dir.parentFile
        }
        return dir ?: File(".").canonicalFile
    }

    @Test
    fun testFdroidRecipeMatchesBuildConfig() {
        val root = findProjectRoot()
        val fdroidFile = File(root, "distribution/fdroid/de.goork.mapflip.yml")
        assertTrue("F-Droid recipe must exist at ${fdroidFile.path}", fdroidFile.exists())
        val content = fdroidFile.readText()

        val versionRegex = Regex("""CurrentVersion:\s*([^\s]+)""")
        val versionCodeRegex = Regex("""CurrentVersionCode:\s*([0-9]+)""")

        val fdroidVersion = versionRegex.find(content)?.groupValues?.get(1)
        val fdroidVersionCode = versionCodeRegex.find(content)?.groupValues?.get(1)?.toIntOrNull()

        assertEquals("F-Droid CurrentVersion must match BuildConfig.VERSION_NAME", BuildConfig.VERSION_NAME, fdroidVersion)
        assertEquals("F-Droid CurrentVersionCode must match BuildConfig.VERSION_CODE", BuildConfig.VERSION_CODE, fdroidVersionCode)
    }

    @Test
    fun testFastlaneChangelogsExistForCurrentVersionCode() {
        val root = findProjectRoot()
        val deChangelog = File(root, "fastlane/metadata/android/de-DE/changelogs/${BuildConfig.VERSION_CODE}.txt")
        val enChangelog = File(root, "fastlane/metadata/android/en-US/changelogs/${BuildConfig.VERSION_CODE}.txt")

        assertTrue("German changelog for ${BuildConfig.VERSION_CODE} must exist", deChangelog.exists() && deChangelog.readText().isNotBlank())
        assertTrue("English changelog for ${BuildConfig.VERSION_CODE} must exist", enChangelog.exists() && enChangelog.readText().isNotBlank())
    }

    @Test
    fun testMarkdownChangelogsContainCurrentVersion() {
        val root = findProjectRoot()
        val deChangelog = File(root, "docs/CHANGELOG.md")
        val enChangelog = File(root, "docs/CHANGELOG.en.md")

        assertTrue("docs/CHANGELOG.md must contain entry for ${BuildConfig.VERSION_NAME}", deChangelog.readText().contains("## ${BuildConfig.VERSION_NAME}"))
        assertTrue("docs/CHANGELOG.en.md must contain entry for ${BuildConfig.VERSION_NAME}", enChangelog.readText().contains("## ${BuildConfig.VERSION_NAME}"))
    }
}
