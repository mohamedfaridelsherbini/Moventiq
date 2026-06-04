package com.mohamedfaridelsherbini.moventiq.ui.permissions

import android.content.Context
import androidx.core.content.edit

private const val PREFS_NAME = "moventiq_app_prefs"
private const val KEY_LOCATION_PROMPT_COMPLETED = "location_permission_prompt_completed"
private const val KEY_NOTIFICATION_PROMPT_COMPLETED = "notification_permission_prompt_completed"
private const val KEY_LIMITED_FEATURES = "limited_features_acknowledged"
private const val KEY_SHOW_LOCATION_DENIED = "show_location_denied_screen"

class PermissionPreferences(
    context: Context,
) : PermissionStatusStore {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun isLocationPromptCompleted(): Boolean =
        prefs.getBoolean(KEY_LOCATION_PROMPT_COMPLETED, false)

    override fun setLocationPromptCompleted() {
        prefs.edit { putBoolean(KEY_LOCATION_PROMPT_COMPLETED, true) }
    }

    override fun isNotificationPromptCompleted(): Boolean =
        prefs.getBoolean(KEY_NOTIFICATION_PROMPT_COMPLETED, false)

    override fun setNotificationPromptCompleted() {
        prefs.edit { putBoolean(KEY_NOTIFICATION_PROMPT_COMPLETED, true) }
    }

    override fun isLimitedFeaturesAcknowledged(): Boolean =
        prefs.getBoolean(KEY_LIMITED_FEATURES, false)

    override fun setLimitedFeaturesAcknowledged() {
        prefs.edit { putBoolean(KEY_LIMITED_FEATURES, true) }
    }

    override fun shouldShowLocationDeniedScreen(): Boolean =
        prefs.getBoolean(KEY_SHOW_LOCATION_DENIED, false)

    override fun setShowLocationDeniedScreen(show: Boolean) {
        prefs.edit { putBoolean(KEY_SHOW_LOCATION_DENIED, show) }
    }
}
