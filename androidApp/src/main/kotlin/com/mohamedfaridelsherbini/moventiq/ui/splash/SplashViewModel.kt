package com.mohamedfaridelsherbini.moventiq.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SplashViewModel(
    private val enterWindowMs: Long = SplashBranding.SPLASH_ENTER_WINDOW_MS,
    private val exitDurationMs: Long = SplashBranding.SPLASH_EXIT_DURATION_MS.toLong(),
) : ViewModel() {
    private val _state = MutableStateFlow(SplashUiState())
    val state: StateFlow<SplashUiState> = _state.asStateFlow()
    private var isStarted = false

    fun onEvent(event: SplashEvent) {
        when (event) {
            SplashEvent.ContentDrawn -> startSplashSequenceIfNeeded()
        }
    }

    private fun startSplashSequenceIfNeeded() {
        if (isStarted) return
        isStarted = true
        viewModelScope.launch {
            delay(enterWindowMs)
            _state.update { it.copy(phase = SplashPhase.Exiting) }
            delay(exitDurationMs)
            _state.update { it.copy(isComplete = true) }
        }
    }
}
