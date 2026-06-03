package com.mohamedfaridelsherbini.moventiq.ui.onboarding

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

@RunWith(AndroidJUnit4::class)
class OnboardingContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    @Composable
    private fun TestHost(
        state: OnboardingUiState = OnboardingUiState.preview(),
        darkTheme: Boolean = false,
    ) {
        MoventiqTheme(darkTheme = darkTheme) {
            OnboardingContent(
                state = state,
                onEvent = {},
            )
        }
    }

    @Test
    fun onboarding_showsScreenRoot() {
        composeTestRule.setContent { TestHost() }
        composeTestRule.onNodeWithTag(OnboardingTestTags.SCREEN).assertIsDisplayed()
    }

    @Test
    fun onboarding_showsSkip_onPageOne() {
        composeTestRule.setContent { TestHost() }
        composeTestRule.onNodeWithTag(OnboardingTestTags.SKIP).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.onboarding_skip)).assertIsDisplayed()
    }

    @Test
    fun onboarding_showsContinue_onPageOne() {
        composeTestRule.setContent { TestHost() }
        composeTestRule.onNodeWithTag(OnboardingTestTags.CONTINUE).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.onboarding_continue)).assertIsDisplayed()
    }

    @Test
    fun onboarding_showsPageOneHeadline() {
        composeTestRule.setContent { TestHost(state = OnboardingUiState.preview(currentPage = 0)) }
        composeTestRule.onNodeWithText(context.getString(R.string.onboarding_1_headline)).assertIsDisplayed()
    }

    @Test
    fun onboarding_showsPageTwoHeadline() {
        composeTestRule.setContent { TestHost(state = OnboardingUiState.preview(currentPage = 1)) }
        composeTestRule.onNodeWithText(context.getString(R.string.onboarding_2_headline)).assertIsDisplayed()
    }

    @Test
    fun onboarding_showsGetStarted_onPageThree() {
        composeTestRule.setContent { TestHost(state = OnboardingUiState.preview(currentPage = 2)) }
        composeTestRule.onNodeWithText(context.getString(R.string.onboarding_get_started)).assertIsDisplayed()
    }

    @Test
    fun onboarding_showsProgressDots() {
        composeTestRule.setContent { TestHost() }
        composeTestRule.onNodeWithTag(OnboardingTestTags.PROGRESS).assertIsDisplayed()
    }

    @Test
    fun onboarding_showsPageOneHeadline_inDarkTheme() {
        composeTestRule.setContent {
            TestHost(
                state = OnboardingUiState.preview(currentPage = 0),
                darkTheme = true,
            )
        }
        composeTestRule.onNodeWithText(context.getString(R.string.onboarding_1_headline)).assertIsDisplayed()
    }
}
