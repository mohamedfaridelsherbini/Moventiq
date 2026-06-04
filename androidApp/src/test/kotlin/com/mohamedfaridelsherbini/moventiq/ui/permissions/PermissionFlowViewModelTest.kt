package com.mohamedfaridelsherbini.moventiq.ui.permissions

import org.junit.Assert.assertEquals
import org.junit.Test

class PermissionFlowViewModelTest {

    @Test
    fun refreshFlow_startsAtLocation_whenFreshStoreAndNoAccess() {
        val viewModel = PermissionFlowViewModel(
            statusStore = FreshPermissionStatusStore(),
            statusChecker = FakePermissionStatusChecker(),
        )

        assertEquals(PermissionFlowStep.Location, viewModel.state.value.step)
    }

    @Test
    fun refreshFlow_skipsToNotification_afterLocationPromptCompleted() {
        val store = FreshPermissionStatusStore()
        store.setLocationPromptCompleted()
        val viewModel = PermissionFlowViewModel(
            statusStore = store,
            statusChecker = FakePermissionStatusChecker(),
        )

        assertEquals(PermissionFlowStep.Notification, viewModel.state.value.step)
    }

    @Test
    fun refreshFlow_completes_whenAllPromptsCompleted() {
        val viewModel = PermissionFlowViewModel(
            statusStore = CompletedPermissionStatusStore(),
            statusChecker = FakePermissionStatusChecker(),
        )

        assertEquals(PermissionFlowStep.None, viewModel.state.value.step)
        assertEquals(true, viewModel.state.value.isFlowComplete)
    }

    @Test
    fun locationLater_movesToNotification() {
        val viewModel = PermissionFlowViewModel(
            statusStore = FreshPermissionStatusStore(),
            statusChecker = FakePermissionStatusChecker(),
        )

        viewModel.onEvent(PermissionEvent.LocationLater)

        assertEquals(PermissionFlowStep.Notification, viewModel.state.value.step)
    }

    @Test
    fun deniedLimitedFeatures_completesFlow() {
        val store = FreshPermissionStatusStore()
        store.setShowLocationDeniedScreen(true)
        val viewModel = PermissionFlowViewModel(
            statusStore = store,
            statusChecker = FakePermissionStatusChecker(),
        )

        viewModel.onEvent(PermissionEvent.DeniedLimitedFeatures)

        assertEquals(PermissionFlowStep.None, viewModel.state.value.step)
    }

    @Test
    fun locationResults_denied_showsDeniedScreen() {
        val viewModel = PermissionFlowViewModel(
            statusStore = FreshPermissionStatusStore(),
            statusChecker = FakePermissionStatusChecker(),
        )

        viewModel.onEvent(
            PermissionEvent.LocationResults(
                fineGranted = false,
                backgroundGranted = false,
            ),
        )

        assertEquals(PermissionFlowStep.Denied, viewModel.state.value.step)
    }
}
