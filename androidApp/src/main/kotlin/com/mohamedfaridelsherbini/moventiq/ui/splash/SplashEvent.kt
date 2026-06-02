package com.mohamedfaridelsherbini.moventiq.ui.splash

sealed interface SplashEvent {
    data object ContentDrawn : SplashEvent
}
