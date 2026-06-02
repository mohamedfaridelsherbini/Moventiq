package com.mohamedfaridelsherbini.moventiq.ui.splash

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mohamedfaridelsherbini.moventiq.R
import com.mohamedfaridelsherbini.moventiq.ui.theme.MoventiqTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Behavior UI tests for [SplashContent] — fake state, no navigation.
 * See https://developer.android.com/training/testing/ui-tests/behavior
 */
@RunWith(AndroidJUnit4::class)
class SplashContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    @Composable
    private fun TestHost(
        state: SplashUiState = SplashUiState.preview(),
        darkTheme: Boolean = false,
    ) {
        MoventiqTheme(darkTheme = darkTheme) {
            SplashContent(
                state = state,
                onEvent = {},
            )
        }
    }

    @Test
    fun splash_showsScreenRoot() {
        composeTestRule.setContent { TestHost() }
        composeTestRule.onNodeWithTag(SplashTestTags.SCREEN).assertIsDisplayed()
    }

    @Test
    fun splash_showsBrandText_afterEnterAnimation() {
        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent { TestHost() }
        composeTestRule.mainClock.advanceTimeBy(1_500)
        composeTestRule.onNodeWithTag(SplashTestTags.WORDMARK).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.app_name)).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SplashTestTags.TAGLINE).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.splash_tagline)).assertIsDisplayed()
    }

    @Test
    fun splash_showsBrandText_inDarkTheme() {
        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent { TestHost(darkTheme = true) }
        composeTestRule.mainClock.advanceTimeBy(1_500)
        composeTestRule.onNodeWithTag(SplashTestTags.WORDMARK).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SplashTestTags.TAGLINE).assertIsDisplayed()
    }

    @Test
    fun splash_showsWordmark_whenExiting() {
        composeTestRule.setContent {
            TestHost(state = SplashUiState.preview(phase = SplashPhase.Exiting))
        }
        composeTestRule.onNodeWithTag(SplashTestTags.SCREEN).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SplashTestTags.WORDMARK).assertIsDisplayed()
    }
}
