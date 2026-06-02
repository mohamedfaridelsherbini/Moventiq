package com.mohamedfaridelsherbini.moventiq.ui.splash

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SplashUiStateTest {

    @Test
    fun defaultState_isVisibleAndNotComplete() {
        val state = SplashUiState()

        assertEquals(SplashPhase.Visible, state.phase)
        assertFalse(state.isComplete)
        assertFalse(state.isExiting)
    }

    @Test
    fun isExiting_returnsTrue_whenPhaseIsExiting() {
        assertTrue(SplashUiState(phase = SplashPhase.Exiting).isExiting)
    }

    @Test
    fun preview_returnsStateWithGivenPhase() {
        assertEquals(
            SplashUiState(phase = SplashPhase.Exiting),
            SplashUiState.preview(phase = SplashPhase.Exiting),
        )
    }
}
