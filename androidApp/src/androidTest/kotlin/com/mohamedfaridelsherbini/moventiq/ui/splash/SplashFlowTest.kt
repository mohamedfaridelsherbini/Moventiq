package com.mohamedfaridelsherbini.moventiq.ui.splash

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mohamedfaridelsherbini.moventiq.MoventiqApp
import com.mohamedfaridelsherbini.moventiq.ui.home.HomeTestTags
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Behavior UI tests for splash → home navigation using a test-double [SplashViewModel].
 * See https://developer.android.com/training/testing/ui-tests
 */
@RunWith(AndroidJUnit4::class)
class SplashFlowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun app_navigatesToHome_afterSplashCompletes() {
        val splashViewModel = SplashViewModel(
            enterWindowMs = 0L,
            exitDurationMs = 0L,
        )

        composeTestRule.setContent {
            MoventiqApp(
                splashViewModel = splashViewModel,
                onSplashDrawn = {},
            )
        }

        composeTestRule.waitForIdle()
        composeTestRule.mainClock.advanceTimeBy(1_000)
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule
                .onAllNodesWithTag(HomeTestTags.SCREEN)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        composeTestRule.onNodeWithTag(HomeTestTags.SCREEN).assertIsDisplayed()
    }

    @Test
    fun app_showsSplash_beforeNavigationCompletes() {
        val splashViewModel = SplashViewModel(
            enterWindowMs = 60_000L,
            exitDurationMs = 60_000L,
        )

        composeTestRule.setContent {
            MoventiqApp(
                splashViewModel = splashViewModel,
                onSplashDrawn = {},
            )
        }

        composeTestRule.onNodeWithTag(SplashTestTags.SCREEN).assertIsDisplayed()
    }
}
