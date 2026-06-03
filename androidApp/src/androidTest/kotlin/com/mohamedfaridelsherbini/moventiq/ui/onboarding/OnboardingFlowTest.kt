package com.mohamedfaridelsherbini.moventiq.ui.onboarding

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mohamedfaridelsherbini.moventiq.MoventiqApp
import com.mohamedfaridelsherbini.moventiq.R
import com.mohamedfaridelsherbini.moventiq.ui.home.HomeTestTags
import com.mohamedfaridelsherbini.moventiq.ui.splash.SplashTestTags
import com.mohamedfaridelsherbini.moventiq.ui.splash.SplashViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OnboardingFlowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    @Test
    fun app_showsOnboarding_afterSplashCompletes() {
        val splashViewModel = SplashViewModel(enterWindowMs = 0L, exitDurationMs = 0L)
        val onboardingViewModel = OnboardingViewModel(FreshOnboardingStatusStore())

        composeTestRule.setContent {
            MoventiqApp(
                splashViewModel = splashViewModel,
                onboardingViewModel = onboardingViewModel,
                onSplashDrawn = {},
            )
        }

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule
                .onAllNodesWithTag(OnboardingTestTags.SCREEN)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        composeTestRule.onNodeWithTag(OnboardingTestTags.SCREEN).assertIsDisplayed()
    }

    @Test
    fun app_navigatesToHome_whenOnboardingSkipped() {
        val splashViewModel = SplashViewModel(enterWindowMs = 0L, exitDurationMs = 0L)
        val onboardingViewModel = OnboardingViewModel(FreshOnboardingStatusStore())

        composeTestRule.setContent {
            MoventiqApp(
                splashViewModel = splashViewModel,
                onboardingViewModel = onboardingViewModel,
                onSplashDrawn = {},
            )
        }

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule
                .onAllNodesWithTag(OnboardingTestTags.SCREEN)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        composeTestRule.onNodeWithTag(OnboardingTestTags.SKIP).performClick()

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule
                .onAllNodesWithTag(HomeTestTags.SCREEN)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        composeTestRule.onNodeWithTag(HomeTestTags.SCREEN).assertIsDisplayed()
    }

    @Test
    fun app_advancesOnboarding_whenContinueTapped() {
        val splashViewModel = SplashViewModel(enterWindowMs = 0L, exitDurationMs = 0L)
        val onboardingViewModel = OnboardingViewModel(FreshOnboardingStatusStore())

        composeTestRule.setContent {
            MoventiqApp(
                splashViewModel = splashViewModel,
                onboardingViewModel = onboardingViewModel,
                onSplashDrawn = {},
            )
        }

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule
                .onAllNodesWithTag(OnboardingTestTags.SCREEN)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        composeTestRule.onNodeWithText(context.getString(R.string.onboarding_1_headline)).assertIsDisplayed()
        composeTestRule.onNodeWithTag(OnboardingTestTags.CONTINUE).performClick()
        composeTestRule.onNodeWithText(context.getString(R.string.onboarding_2_headline)).assertIsDisplayed()
    }

    @Test
    fun app_navigatesToHome_afterOnboardingCompletes() {
        val splashViewModel = SplashViewModel(enterWindowMs = 0L, exitDurationMs = 0L)
        val onboardingViewModel = OnboardingViewModel(FreshOnboardingStatusStore())

        composeTestRule.setContent {
            MoventiqApp(
                splashViewModel = splashViewModel,
                onboardingViewModel = onboardingViewModel,
                onSplashDrawn = {},
            )
        }

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule
                .onAllNodesWithTag(OnboardingTestTags.SCREEN)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        repeat(3) {
            composeTestRule.onNodeWithTag(OnboardingTestTags.CONTINUE).performClick()
        }

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule
                .onAllNodesWithTag(HomeTestTags.SCREEN)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        composeTestRule.onNodeWithTag(HomeTestTags.SCREEN).assertIsDisplayed()
    }

    @Test
    fun app_showsSplash_beforeOnboardingWhenSplashDelayed() {
        val splashViewModel = SplashViewModel(enterWindowMs = 60_000L, exitDurationMs = 60_000L)
        val onboardingViewModel = OnboardingViewModel(FreshOnboardingStatusStore())

        composeTestRule.setContent {
            MoventiqApp(
                splashViewModel = splashViewModel,
                onboardingViewModel = onboardingViewModel,
                onSplashDrawn = {},
            )
        }

        composeTestRule.onNodeWithTag(SplashTestTags.SCREEN).assertIsDisplayed()
    }
}
