package com.mohamedfaridelsherbini.moventiq.ui.onboarding

import com.mohamedfaridelsherbini.moventiq.R

enum class OnboardingPage(
    val headlineRes: Int,
    val bodyRes: Int,
    val ctaRes: Int,
) {
    LinkTasks(
        headlineRes = R.string.onboarding_1_headline,
        bodyRes = R.string.onboarding_1_body,
        ctaRes = R.string.onboarding_continue,
    ),
    DetectArrival(
        headlineRes = R.string.onboarding_2_headline,
        bodyRes = R.string.onboarding_2_body,
        ctaRes = R.string.onboarding_continue,
    ),
    AutoSurface(
        headlineRes = R.string.onboarding_3_headline,
        bodyRes = R.string.onboarding_3_body,
        ctaRes = R.string.onboarding_get_started,
    ),
    ;

    companion object {
        const val COUNT: Int = 3
    }
}
