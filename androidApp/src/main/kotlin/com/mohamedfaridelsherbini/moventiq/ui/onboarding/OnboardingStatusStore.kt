package com.mohamedfaridelsherbini.moventiq.ui.onboarding

interface OnboardingStatusStore {
    fun hasCompletedOnboarding(): Boolean
    fun setOnboardingCompleted()
}
