package com.mohamedfaridelsherbini.moventiq.ui.permissions

import android.content.Context
import androidx.core.content.edit
import com.mohamedfaridelsherbini.moventiq.feature.permissions.presentation.PermissionStatusStore

private const val PREFS_NAME = "moventiq_app_prefs"
private const val KEY_LOCATION_PROMPT_COMPLETED = "location_permission_prompt_completed"
private const val KEY_NOTIFICATION_PROMPT_COMPLETED = "notification_permission_prompt_completed"
private const val KEY_LIMITED_FEATURES = "limited_features_acknowledged"
private const val KEY_SHOW_LOCATION_DENIED = "show_location_denied_screen"
private const val KEY_LOCATION_ALLOW_ATTEMPTED = "location_allow_attempted"

class PermissionPreferences(
    context: Context,
) : PermissionStatusStore {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Clears prompt-completion flags persisted by older app versions; the keys are
    // no longer read, only wiped on launch so stale values cannot linger.
    override fun clearLegacyDeferFlags() {
        prefs.edit(commit = true) {
            putBoolean(KEY_LOCATION_PROMPT_COMPLETED, false)
            putBoolean(KEY_NOTIFICATION_PROMPT_COMPLETED, false)
        }
    }

    override fun wasLocationAllowAttempted(): Boolean =
        prefs.getBoolean(KEY_LOCATION_ALLOW_ATTEMPTED, false)

    override fun setLocationAllowAttempted() {
        prefs.edit(commit = true) { putBoolean(KEY_LOCATION_ALLOW_ATTEMPTED, true) }
    }

    override fun isLimitedFeaturesAcknowledged(): Boolean =
        prefs.getBoolean(KEY_LIMITED_FEATURES, false)

    override fun setLimitedFeaturesAcknowledged() {
        prefs.edit(commit = true) { putBoolean(KEY_LIMITED_FEATURES, true) }
    }

    override fun shouldShowLocationDeniedScreen(): Boolean =
        prefs.getBoolean(KEY_SHOW_LOCATION_DENIED, false)

    override fun setShowLocationDeniedScreen(show: Boolean) {
        prefs.edit(commit = true) { putBoolean(KEY_SHOW_LOCATION_DENIED, show) }
    }
}
