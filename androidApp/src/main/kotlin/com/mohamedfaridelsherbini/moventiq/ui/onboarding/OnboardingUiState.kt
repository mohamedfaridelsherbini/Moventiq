package com.mohamedfaridelsherbini.moventiq.ui.onboarding

data class OnboardingUiState(
    val currentPage: Int = 0,
    val hasCompletedOnboarding: Boolean? = null,
    val isFinished: Boolean = false,
) {
    val shouldShowOnboarding: Boolean
        get() = hasCompletedOnboarding == false && !isFinished

    val currentPageData: OnboardingPage
        get() = OnboardingPage.entries[currentPage.coerceIn(0, OnboardingPage.COUNT - 1)]

    companion object {
        fun preview(
            currentPage: Int = 0,
            hasCompletedOnboarding: Boolean? = false,
            isFinished: Boolean = false,
        ) = OnboardingUiState(
            currentPage = currentPage,
            hasCompletedOnboarding = hasCompletedOnboarding,
            isFinished = isFinished,
        )
    }
}
