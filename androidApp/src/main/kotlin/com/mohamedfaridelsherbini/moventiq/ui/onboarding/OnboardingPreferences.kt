package com.mohamedfaridelsherbini.moventiq.ui.onboarding

import android.content.Context
import androidx.core.content.edit

private const val PREFS_NAME = "moventiq_app_prefs"
private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"

class OnboardingPreferences(
    context: Context,
) : OnboardingStatusStore {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun hasCompletedOnboarding(): Boolean =
        prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)

    override fun setOnboardingCompleted() {
        prefs.edit { putBoolean(KEY_ONBOARDING_COMPLETED, true) }
    }
}
