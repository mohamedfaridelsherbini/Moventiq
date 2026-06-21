package com.mohamedfaridelsherbini.moventiq.ui.permissions

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
import com.mohamedfaridelsherbini.moventiq.ui.onboarding.FreshOnboardingStatusStore
import com.mohamedfaridelsherbini.moventiq.ui.onboarding.OnboardingTestTags
import com.mohamedfaridelsherbini.moventiq.ui.onboarding.OnboardingViewModel
import com.mohamedfaridelsherbini.moventiq.ui.splash.SplashViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PermissionFlowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    @Test
    fun app_showsLocationPermission_afterOnboardingSkipped() {
        val splashViewModel = SplashViewModel(enterWindowMs = 0L, exitDurationMs = 0L)
        val onboardingViewModel = OnboardingViewModel(FreshOnboardingStatusStore())
        val permissionViewModel = PermissionFlowViewModel(
            statusStore = FreshPermissionStatusStore(),
            statusReader = FakePermissionStatusChecker(),
        )

        composeTestRule.setContent {
            MoventiqApp(
                splashViewModel = splashViewModel,
                onboardingViewModel = onboardingViewModel,
                permissionViewModel = permissionViewModel,
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
                .onAllNodesWithTag(PermissionTestTags.LOCATION_SCREEN)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        composeTestRule
            .onNodeWithText(context.getString(R.string.permission_location_headline))
            .assertIsDisplayed()
    }

    @Test
    fun app_showsNotification_afterLocationLater() {
        val splashViewModel = SplashViewModel(enterWindowMs = 0L, exitDurationMs = 0L)
        val onboardingViewModel = OnboardingViewModel(FreshOnboardingStatusStore())
        val permissionViewModel = PermissionFlowViewModel(
            statusStore = FreshPermissionStatusStore(),
            statusReader = FakePermissionStatusChecker(),
        )

        composeTestRule.setContent {
            MoventiqApp(
                splashViewModel = splashViewModel,
                onboardingViewModel = onboardingViewModel,
                permissionViewModel = permissionViewModel,
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
                .onAllNodesWithTag(PermissionTestTags.LOCATION_SCREEN)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        composeTestRule.onNodeWithTag(PermissionTestTags.LOCATION_LATER).performClick()

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule
                .onAllNodesWithTag(PermissionTestTags.NOTIFICATION_SCREEN)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        composeTestRule
            .onNodeWithText(context.getString(R.string.permission_notification_headline))
            .assertIsDisplayed()
    }

    @Test
    fun app_reachesHome_afterDeniedLimitedFeatures() {
        val splashViewModel = SplashViewModel(enterWindowMs = 0L, exitDurationMs = 0L)
        val onboardingViewModel = OnboardingViewModel(FreshOnboardingStatusStore())
        val permissionViewModel = PermissionFlowViewModel(
            statusStore = FreshPermissionStatusStore(),
            statusReader = FakePermissionStatusChecker(locationPermissionDenied = true),
        )

        composeTestRule.setContent {
            MoventiqApp(
                splashViewModel = splashViewModel,
                onboardingViewModel = onboardingViewModel,
                permissionViewModel = permissionViewModel,
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
                .onAllNodesWithTag(PermissionTestTags.DENIED_SCREEN)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        composeTestRule
            .onNodeWithText(context.getString(R.string.permission_denied_headline))
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag(PermissionTestTags.DENIED_LIMITED).performClick()

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule
                .onAllNodesWithTag(HomeTestTags.SCREEN)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        composeTestRule.onNodeWithTag(HomeTestTags.SCREEN).assertIsDisplayed()
    }

    @Test
    fun app_reachesHome_afterPermissionSkips() {
        val splashViewModel = SplashViewModel(enterWindowMs = 0L, exitDurationMs = 0L)
        val onboardingViewModel = OnboardingViewModel(FreshOnboardingStatusStore())
        val permissionViewModel = PermissionFlowViewModel(
            statusStore = FreshPermissionStatusStore(),
            statusReader = FakePermissionStatusChecker(),
        )

        composeTestRule.setContent {
            MoventiqApp(
                splashViewModel = splashViewModel,
                onboardingViewModel = onboardingViewModel,
                permissionViewModel = permissionViewModel,
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
                .onAllNodesWithTag(PermissionTestTags.LOCATION_SCREEN)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        composeTestRule.onNodeWithTag(PermissionTestTags.LOCATION_LATER).performClick()

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule
                .onAllNodesWithTag(PermissionTestTags.NOTIFICATION_SCREEN)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        composeTestRule.onNodeWithTag(PermissionTestTags.NOTIFICATION_SCREEN).assertIsDisplayed()
        composeTestRule.onNodeWithTag(PermissionTestTags.NOTIFICATION_SKIP).performClick()

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule
                .onAllNodesWithTag(HomeTestTags.SCREEN)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        composeTestRule.onNodeWithTag(HomeTestTags.SCREEN).assertIsDisplayed()
    }
}
