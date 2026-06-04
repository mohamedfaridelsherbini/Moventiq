package com.mohamedfaridelsherbini.moventiq.ui.permissions

import com.mohamedfaridelsherbini.moventiq.test.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PermissionFlowViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        PermissionFlowViewModel.resetSessionForTests()
    }

    @Test
    fun locationResults_granted_thenRefresh_movesToNotification() {
        val checker = FakePermissionStatusChecker()
        val viewModel = PermissionFlowViewModel(
            statusStore = FreshPermissionStatusStore(),
            statusChecker = checker,
        )

        viewModel.onEvent(
            PermissionEvent.LocationResults(
                fineGranted = true,
                backgroundGranted = true,
            ),
        )
        checker.adequateLocation = true
        viewModel.onEvent(PermissionEvent.Refresh)

        assertEquals(PermissionFlowStep.Notification, viewModel.state.value.step)
    }

    @Test
    fun notificationResult_granted_completesFlow() {
        val checker = FakePermissionStatusChecker(adequateLocation = true)
        val viewModel = PermissionFlowViewModel(
            statusStore = FreshPermissionStatusStore(),
            statusChecker = checker,
        )

        checker.notificationGranted = true
        viewModel.onEvent(PermissionEvent.NotificationResult(granted = true))

        assertEquals(PermissionFlowStep.None, viewModel.state.value.step)
    }

    @Test
    fun notificationSkip_completesFlow() {
        val viewModel = PermissionFlowViewModel(
            statusStore = FreshPermissionStatusStore(),
            statusChecker = FakePermissionStatusChecker(adequateLocation = true),
        )

        viewModel.onEvent(PermissionEvent.NotificationSkip)

        assertEquals(PermissionFlowStep.None, viewModel.state.value.step)
    }

    @Test
    fun appForeground_afterLimitedFeatures_staysComplete() {
        val store = FreshPermissionStatusStore()
        store.setLimitedFeaturesAcknowledged()
        val viewModel = PermissionFlowViewModel(
            statusStore = store,
            statusChecker = FakePermissionStatusChecker(),
        )

        assertEquals(PermissionFlowStep.None, viewModel.state.value.step)

        viewModel.onEvent(PermissionEvent.AppReturnedFromBackground)

        assertEquals(PermissionFlowStep.None, viewModel.state.value.step)
    }

    @Test
    fun refreshFlow_startsAtLocation_whenFreshStoreAndNoAccess() {
        val viewModel = PermissionFlowViewModel(
            statusStore = FreshPermissionStatusStore(),
            statusChecker = FakePermissionStatusChecker(),
        )

        assertEquals(PermissionFlowStep.Location, viewModel.state.value.step)
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
    fun locationLater_doesNotShowDeniedScreen() {
        val store = FreshPermissionStatusStore()
        val viewModel = PermissionFlowViewModel(
            statusStore = store,
            statusChecker = FakePermissionStatusChecker(),
        )

        viewModel.onEvent(PermissionEvent.LocationLater)

        assertEquals(PermissionFlowStep.Notification, viewModel.state.value.step)
        assertEquals(false, store.shouldShowLocationDeniedScreen())
    }

    @Test
    fun locationGranted_thenNotificationSkip_thenAppForeground_showsNotificationAgain() {
        val viewModel = PermissionFlowViewModel(
            statusStore = FreshPermissionStatusStore(),
            statusChecker = FakePermissionStatusChecker(adequateLocation = true),
        )

        viewModel.onEvent(PermissionEvent.NotificationSkip)
        assertEquals(PermissionFlowStep.None, viewModel.state.value.step)

        viewModel.onEvent(PermissionEvent.AppReturnedFromBackground)

        assertEquals(PermissionFlowStep.Notification, viewModel.state.value.step)
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
    fun appForeground_afterMaybeLaterAndNotNow_reShowsLocation() {
        val viewModel = PermissionFlowViewModel(
            statusStore = FreshPermissionStatusStore(),
            statusChecker = FakePermissionStatusChecker(),
        )

        viewModel.onEvent(PermissionEvent.LocationLater)
        viewModel.onEvent(PermissionEvent.NotificationSkip)
        assertEquals(PermissionFlowStep.None, viewModel.state.value.step)

        viewModel.onEvent(PermissionEvent.AppReturnedFromBackground)

        assertEquals(PermissionFlowStep.Location, viewModel.state.value.step)
    }

    @Test
    fun refresh_afterMaybeLater_keepsNotificationInSameSession() {
        val viewModel = PermissionFlowViewModel(
            statusStore = FreshPermissionStatusStore(),
            statusChecker = FakePermissionStatusChecker(),
        )

        viewModel.onEvent(PermissionEvent.LocationLater)
        viewModel.onEvent(PermissionEvent.Refresh)

        assertEquals(PermissionFlowStep.Notification, viewModel.state.value.step)
    }

    @Test
    fun coldStart_osDenied_showsDeniedScreen() {
        val viewModel = PermissionFlowViewModel(
            statusStore = FreshPermissionStatusStore(),
            statusChecker = FakePermissionStatusChecker(locationPermissionDenied = true),
        )

        assertEquals(PermissionFlowStep.Denied, viewModel.state.value.step)
    }
}
