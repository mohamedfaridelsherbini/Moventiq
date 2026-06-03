package com.mohamedfaridelsherbini.moventiq.ui.onboarding

internal class FreshOnboardingStatusStore : OnboardingStatusStore {
    private var completed = false

    override fun hasCompletedOnboarding(): Boolean = completed

    override fun setOnboardingCompleted() {
        completed = true
    }
}
