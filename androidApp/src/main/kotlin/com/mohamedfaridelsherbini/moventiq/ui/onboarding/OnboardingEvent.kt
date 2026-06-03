package com.mohamedfaridelsherbini.moventiq.ui.onboarding

sealed interface OnboardingEvent {
    data class PageChanged(val page: Int) : OnboardingEvent
    data object Continue : OnboardingEvent
    data object Skip : OnboardingEvent
}
