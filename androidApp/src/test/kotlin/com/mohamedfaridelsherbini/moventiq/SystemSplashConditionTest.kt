package com.mohamedfaridelsherbini.moventiq

import com.mohamedfaridelsherbini.moventiq.ui.splash.SplashPhase
import com.mohamedfaridelsherbini.moventiq.ui.splash.SplashUiState
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SystemSplashConditionTest {

    @Test
    fun shouldKeepSystemSplashOn_defaultVisibleState_keepsSplashUp() {
        assertTrue(
            shouldKeepSystemSplashOn(
                keepSystemSplashOn = true,
                state = SplashUiState(),
            ),
        )
    }

    @Test
    fun shouldKeepSystemSplashOn_contentDrawn_dismissesSplash() {
        assertFalse(
            shouldKeepSystemSplashOn(
                keepSystemSplashOn = false,
                state = SplashUiState(),
            ),
        )
    }

    @Test
    fun shouldKeepSystemSplashOn_afterComplete_dismissesSplashOnRecreate() {
        assertFalse(
            shouldKeepSystemSplashOn(
                keepSystemSplashOn = true,
                state = SplashUiState(phase = SplashPhase.Exiting, isComplete = true),
            ),
        )
    }

    @Test
    fun shouldKeepSystemSplashOn_whenExiting_dismissesSystemSplash() {
        assertFalse(
            shouldKeepSystemSplashOn(
                keepSystemSplashOn = true,
                state = SplashUiState(phase = SplashPhase.Exiting),
            ),
        )
    }
}
