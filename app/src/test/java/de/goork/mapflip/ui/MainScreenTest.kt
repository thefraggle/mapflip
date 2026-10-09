package de.goork.mapflip.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import de.goork.mapflip.data.PreferencesRepository
import de.goork.mapflip.ui.theme.MapFlipTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MainScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var repository: PreferencesRepository

    @Before
    fun setUp() {
        val context = RuntimeEnvironment.getApplication()
        repository = PreferencesRepository.getInstance(context)
        repository.unpause()
    }

    @Test
    fun testMainScreenRendersHeadline() {
        val s = Strings.getStrings("de")
        repository.setLanguage("de")

        composeTestRule.setContent {
            MapFlipTheme {
                MainScreen(repository = repository)
            }
        }

        composeTestRule.onNodeWithText(s.headline).assertIsDisplayed()
    }

    @Test
    fun testMainScreenRendersTestLinkSection() {
        val s = Strings.getStrings("de")
        repository.setLanguage("de")

        composeTestRule.setContent {
            MapFlipTheme {
                MainScreen(repository = repository)
            }
        }

        composeTestRule.onNodeWithText(s.testLinkTitle).assertExists()
    }

    @Test
    fun testMainScreenRendersQuickGuideSection() {
        val s = Strings.getStrings("de")
        repository.setLanguage("de")

        composeTestRule.setContent {
            MapFlipTheme {
                MainScreen(repository = repository)
            }
        }

        composeTestRule.onNodeWithText(s.quickGuideTitle).assertExists()
    }
}
