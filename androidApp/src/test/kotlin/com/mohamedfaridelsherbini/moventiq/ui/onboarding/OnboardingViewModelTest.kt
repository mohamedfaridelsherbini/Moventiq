package com.mohamedfaridelsherbini.moventiq.ui.onboarding

import app.cash.turbine.test
import com.mohamedfaridelsherbini.moventiq.test.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun continue_advancesPages_thenFinishesOnLastPage() = runTest {
        val store = FakeOnboardingStatusStore()
        val viewModel = OnboardingViewModel(store)

        viewModel.state.test {
            assertEquals(
                OnboardingUiState(hasCompletedOnboarding = false),
                awaitItem(),
            )

            viewModel.onEvent(OnboardingEvent.Continue)
            assertEquals(
                OnboardingUiState(hasCompletedOnboarding = false, currentPage = 1),
                awaitItem(),
            )

            viewModel.onEvent(OnboardingEvent.Continue)
            assertEquals(
                OnboardingUiState(hasCompletedOnboarding = false, currentPage = 2),
                awaitItem(),
            )

            viewModel.onEvent(OnboardingEvent.Continue)
            assertEquals(
                OnboardingUiState(
                    hasCompletedOnboarding = true,
                    currentPage = 2,
                    isFinished = true,
                ),
                awaitItem(),
            )
            assertTrue(store.completed)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun skip_marksOnboardingComplete() = runTest {
        val store = FakeOnboardingStatusStore()
        val viewModel = OnboardingViewModel(store)

        viewModel.state.test {
            skipItems(1)
            viewModel.onEvent(OnboardingEvent.Skip)
            assertEquals(
                OnboardingUiState(hasCompletedOnboarding = true, isFinished = true),
                awaitItem(),
            )
            assertTrue(store.completed)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun shouldShowOnboarding_isFalse_whenStoreReportsCompleted() = runTest {
        val store = FakeOnboardingStatusStore(initialCompleted = true)
        val viewModel = OnboardingViewModel(store)

        viewModel.state.test {
            assertFalse(awaitItem().shouldShowOnboarding)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun pageChanged_updatesCurrentPage() = runTest {
        val viewModel = OnboardingViewModel(FakeOnboardingStatusStore())

        viewModel.state.test {
            skipItems(1)
            viewModel.onEvent(OnboardingEvent.PageChanged(2))
            assertEquals(
                OnboardingUiState(hasCompletedOnboarding = false, currentPage = 2),
                awaitItem(),
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun pageChanged_isNoOpWhenPageUnchanged() = runTest {
        val viewModel = OnboardingViewModel(FakeOnboardingStatusStore())

        viewModel.state.test {
            skipItems(1)
            viewModel.onEvent(OnboardingEvent.PageChanged(1))
            assertEquals(
                OnboardingUiState(hasCompletedOnboarding = false, currentPage = 1),
                awaitItem(),
            )
            viewModel.onEvent(OnboardingEvent.PageChanged(1))
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun pageChanged_ignoresOutOfRangeIndex() = runTest {
        val viewModel = OnboardingViewModel(FakeOnboardingStatusStore())

        viewModel.state.test {
            skipItems(1)
            viewModel.onEvent(OnboardingEvent.PageChanged(5))
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
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
