package com.mohamedfaridelsherbini.moventiq.ui.onboarding

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OnboardingViewModelTest {
    @Test
    fun continue_advancesPages_thenFinishesOnLastPage() {
        val store = FakeOnboardingStatusStore()
        val viewModel = OnboardingViewModel(store)

        assertEquals(0, viewModel.state.value.currentPage)

        viewModel.onEvent(OnboardingEvent.Continue)
        assertEquals(1, viewModel.state.value.currentPage)

        viewModel.onEvent(OnboardingEvent.Continue)
        assertEquals(2, viewModel.state.value.currentPage)

        viewModel.onEvent(OnboardingEvent.Continue)
        assertTrue(viewModel.state.value.isFinished)
        assertTrue(store.completed)
    }

    @Test
    fun skip_marksOnboardingComplete() {
        val store = FakeOnboardingStatusStore()
        val viewModel = OnboardingViewModel(store)

        viewModel.onEvent(OnboardingEvent.Skip)

        assertTrue(viewModel.state.value.isFinished)
        assertTrue(store.completed)
    }

    @Test
    fun loadsCompletedFlagFromPreferences() {
        val store = FakeOnboardingStatusStore(initialCompleted = true)
        val viewModel = OnboardingViewModel(store)

        assertFalse(viewModel.state.value.shouldShowOnboarding)
    }

    @Test
    fun pageChanged_updatesCurrentPage() {
        val viewModel = OnboardingViewModel(FakeOnboardingStatusStore())

        viewModel.onEvent(OnboardingEvent.PageChanged(2))

        assertEquals(2, viewModel.state.value.currentPage)
    }

    @Test
    fun pageChanged_ignoresOutOfRangeIndex() {
        val viewModel = OnboardingViewModel(FakeOnboardingStatusStore())

        viewModel.onEvent(OnboardingEvent.PageChanged(5))

        assertEquals(0, viewModel.state.value.currentPage)
    }

    private class FakeOnboardingStatusStore(
        initialCompleted: Boolean = false,
    ) : OnboardingStatusStore {
        var completed: Boolean = initialCompleted
            private set

        override fun hasCompletedOnboarding(): Boolean = completed

        override fun setOnboardingCompleted() {
            completed = true
        }
    }
}
