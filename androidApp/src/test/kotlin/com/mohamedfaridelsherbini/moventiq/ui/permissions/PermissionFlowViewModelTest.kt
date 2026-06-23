package com.mohamedfaridelsherbini.moventiq.ui.permissions

import app.cash.turbine.test
import com.mohamedfaridelsherbini.moventiq.feature.permissions.presentation.PermissionEffect
import com.mohamedfaridelsherbini.moventiq.feature.permissions.presentation.PermissionEvent
import com.mohamedfaridelsherbini.moventiq.feature.permissions.presentation.PermissionFlowStep
import com.mohamedfaridelsherbini.moventiq.test.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Adapter-level tests: the reducer itself is covered once in
 * `:sharedLogic` `PermissionFlowStoreTest`. These only verify that the Android
 * ViewModel forwards events to the store and surfaces its state/effects.
 */
class PermissionFlowViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        PermissionFlowViewModel.resetSessionForTests()
    }

    @Test
    fun forwardsEvent_updatesStateFromStore() {
        val viewModel = PermissionFlowViewModel(
            statusStore = FreshPermissionStatusStore(),
            statusReader = FakePermissionStatusChecker(),
        )

        viewModel.onEvent(PermissionEvent.LocationLater)

        assertEquals(PermissionFlowStep.Notification, viewModel.state.value.step)
    }

    @Test
    fun deniedOpenSettings_surfacesOpenAppSettingsEffect() = runTest {
        val viewModel = PermissionFlowViewModel(
            statusStore = FreshPermissionStatusStore(),
            statusReader = FakePermissionStatusChecker(),
        )

        viewModel.effects.test {
            viewModel.onEvent(PermissionEvent.DeniedOpenSettings)
            assertEquals(PermissionEffect.OpenAppSettings, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
