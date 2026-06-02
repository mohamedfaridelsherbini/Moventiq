package com.mohamedfaridelsherbini.moventiq.ui.splash

enum class SplashPhase {
    Visible,
    Exiting,
}

data class SplashUiState(
    val phase: SplashPhase = SplashPhase.Visible,
    val isComplete: Boolean = false,
) {
    val isExiting: Boolean get() = phase == SplashPhase.Exiting

    companion object {
        fun preview(
            phase: SplashPhase = SplashPhase.Visible,
        ): SplashUiState = SplashUiState(phase = phase)
    }
}
