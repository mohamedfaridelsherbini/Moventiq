package com.mohamedfaridelsherbini.moventiq.ui.onboarding

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class OnboardingViewModel(
    private val statusStore: OnboardingStatusStore,
) : ViewModel() {
    private val _state = MutableStateFlow(
        OnboardingUiState(hasCompletedOnboarding = statusStore.hasCompletedOnboarding()),
    )
    val state: StateFlow<OnboardingUiState> = _state.asStateFlow()

    fun onEvent(event: OnboardingEvent) {
        when (event) {
            is OnboardingEvent.PageChanged -> {
                if (event.page !in 0 until OnboardingPage.COUNT) return
                if (event.page == _state.value.currentPage) return
                _state.update { it.copy(currentPage = event.page) }
            }
            OnboardingEvent.Continue -> {
                val page = _state.value.currentPage
                if (page < OnboardingPage.COUNT - 1) {
                    _state.update { it.copy(currentPage = page + 1) }
                } else {
                    finishOnboarding()
                }
            }
            OnboardingEvent.Skip -> finishOnboarding()
        }
    }

    private fun finishOnboarding() {
        statusStore.setOnboardingCompleted()
        _state.update { it.copy(hasCompletedOnboarding = true, isFinished = true) }
    }
}
