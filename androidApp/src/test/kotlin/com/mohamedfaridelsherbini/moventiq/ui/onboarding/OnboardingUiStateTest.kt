package com.mohamedfaridelsherbini.moventiq.ui.onboarding

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OnboardingUiStateTest {
    @Test
    fun shouldShowOnboarding_whenNotCompletedAndNotFinished() {
        val state = OnboardingUiState(hasCompletedOnboarding = false, isFinished = false)

        assertTrue(state.shouldShowOnboarding)
    }

    @Test
    fun shouldShowOnboarding_isFalse_whenAlreadyCompleted() {
        val state = OnboardingUiState(hasCompletedOnboarding = true, isFinished = false)

        assertFalse(state.shouldShowOnboarding)
    }

    @Test
    fun shouldShowOnboarding_isFalse_whenFinished() {
        val state = OnboardingUiState(hasCompletedOnboarding = false, isFinished = true)

        assertFalse(state.shouldShowOnboarding)
    }

    @Test
    fun currentPageData_clampsOutOfRangeIndex() {
        val state = OnboardingUiState(currentPage = 99)

        assertEquals(OnboardingPage.AutoSurface, state.currentPageData)
    }

    @Test
    fun preview_defaultsToFirstPageAndIncomplete() {
        val state = OnboardingUiState.preview()

        assertEquals(0, state.currentPage)
        assertTrue(state.shouldShowOnboarding)
    }
}
