package com.mohamedfaridelsherbini.moventiq.ui.splash

import app.cash.turbine.test
import com.mohamedfaridelsherbini.moventiq.test.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun initialState_isVisibleAndNotComplete() = runTest {
        val viewModel = SplashViewModel(
            enterWindowMs = 60_000L,
            exitDurationMs = 60_000L,
        )

        assertEquals(SplashUiState(), viewModel.state.value)
    }

    @Test
    fun enterWindow_elapsed_transitionsToExitingThenComplete() = runTest {
        val viewModel = SplashViewModel(
            enterWindowMs = 100L,
            exitDurationMs = 200L,
        )

        viewModel.onEvent(SplashEvent.ContentDrawn)

        viewModel.state.test {
            assertEquals(SplashUiState(), awaitItem())
            advanceTimeBy(100)
            assertEquals(SplashUiState(phase = SplashPhase.Exiting), awaitItem())
            advanceTimeBy(200)
            assertEquals(
                SplashUiState(phase = SplashPhase.Exiting, isComplete = true),
                awaitItem(),
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun zeroDelay_completesImmediately() = runTest {
        val viewModel = SplashViewModel(
            enterWindowMs = 0L,
            exitDurationMs = 0L,
        )

        viewModel.onEvent(SplashEvent.ContentDrawn)
        advanceTimeBy(1)
        assertTrue(viewModel.state.value.isComplete)
        assertEquals(SplashPhase.Exiting, viewModel.state.value.phase)
    }

    @Test
    fun defaultEnterWindow_transitionsToExitingThenComplete() = runTest {
        val viewModel = SplashViewModel()

        viewModel.onEvent(SplashEvent.ContentDrawn)

        viewModel.state.test {
            assertEquals(SplashUiState(), awaitItem())
            advanceTimeBy(SplashBranding.SPLASH_ENTER_WINDOW_MS)
            assertEquals(SplashUiState(phase = SplashPhase.Exiting), awaitItem())
            advanceTimeBy(SplashBranding.SPLASH_EXIT_DURATION_MS.toLong())
            assertEquals(
                SplashUiState(phase = SplashPhase.Exiting, isComplete = true),
                awaitItem(),
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onEvent_contentDrawn_doesNotChangeState() = runTest {
        val viewModel = SplashViewModel(
            enterWindowMs = 60_000L,
            exitDurationMs = 60_000L,
        )

        viewModel.onEvent(SplashEvent.ContentDrawn)

        assertEquals(SplashUiState(), viewModel.state.value)
    }
}
