package com.mohamedfaridelsherbini.moventiq.ui.permissions

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mohamedfaridelsherbini.moventiq.R
import com.mohamedfaridelsherbini.moventiq.ui.theme.MoventiqTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PermissionContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    @Test
    fun locationPermission_showsHeadline() {
        composeTestRule.setContent {
            MoventiqTheme {
                LocationPermissionContent(onEvent = {})
            }
        }

        composeTestRule
            .onNodeWithText(context.getString(R.string.permission_location_headline))
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag(PermissionTestTags.LOCATION_SCREEN).assertIsDisplayed()
    }

    @Test
    fun notificationPermission_showsHeadline() {
        composeTestRule.setContent {
            MoventiqTheme {
                NotificationPermissionContent(onEvent = {})
            }
        }

        composeTestRule
            .onNodeWithText(context.getString(R.string.permission_notification_headline))
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag(PermissionTestTags.NOTIFICATION_SCREEN).assertIsDisplayed()
    }

    @Test
    fun permissionDenied_showsHeadline() {
        composeTestRule.setContent {
            MoventiqTheme {
                PermissionDeniedContent(onEvent = {})
            }
        }

        composeTestRule
            .onNodeWithText(context.getString(R.string.permission_denied_headline))
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag(PermissionTestTags.DENIED_SCREEN).assertIsDisplayed()
        composeTestRule.onNodeWithTag(PermissionTestTags.DENIED_OPEN_SETTINGS).assertIsDisplayed()
    }

    @Test
    fun permissionDenied_limitedFeatures_emitsEvent() {
        var received: PermissionEvent? = null

        composeTestRule.setContent {
            MoventiqTheme {
                PermissionDeniedContent(onEvent = { received = it })
            }
        }

        composeTestRule.onNodeWithTag(PermissionTestTags.DENIED_LIMITED).performClick()

        assertEquals(PermissionEvent.DeniedLimitedFeatures, received)
    }
}
