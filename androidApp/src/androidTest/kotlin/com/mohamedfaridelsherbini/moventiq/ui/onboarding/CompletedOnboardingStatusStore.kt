package com.mohamedfaridelsherbini.moventiq.ui.onboarding

internal object CompletedOnboardingStatusStore : OnboardingStatusStore {
    override fun hasCompletedOnboarding(): Boolean = true

    override fun setOnboardingCompleted() = Unit
}
