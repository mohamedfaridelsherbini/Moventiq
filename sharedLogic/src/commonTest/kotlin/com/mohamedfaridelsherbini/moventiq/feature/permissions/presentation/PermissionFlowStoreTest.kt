package com.mohamedfaridelsherbini.moventiq.feature.permissions.presentation

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class PermissionFlowStoreTest {
    private fun newStore(
        statusStore: PermissionStatusStore = FakePermissionStatusStore(),
        reader: PermissionStatusReader = FakePermissionStatusReader()
    ) = PermissionFlowStore(statusStore, reader)

    @Test
    fun startsAtLocation_whenFreshStoreAndNoAccess() {
        assertEquals(PermissionFlowStep.Location, newStore().state.value.step)
    }

    @Test
    fun locationResults_denied_showsDeniedScreen() {
        val s = newStore()
        s.onEvent(PermissionEvent.LocationResults(fineGranted = false, backgroundGranted = false))
        assertEquals(PermissionFlowStep.Denied, s.state.value.step)
    }

    @Test
    fun locationResults_whenInUseOnly_showsDeniedScreen() {
        val s = newStore()
        s.onEvent(PermissionEvent.LocationResults(fineGranted = true, backgroundGranted = false))
        assertEquals(PermissionFlowStep.Denied, s.state.value.step)
    }

    @Test
    fun locationResults_granted_thenRefresh_movesToNotification() {
        val reader = FakePermissionStatusReader()
        val s = newStore(reader = reader)

        s.onEvent(PermissionEvent.LocationResults(fineGranted = true, backgroundGranted = true))
        reader.adequateLocation = true
        s.onEvent(PermissionEvent.Refresh)

        assertEquals(PermissionFlowStep.Notification, s.state.value.step)
    }

    @Test
    fun notificationResult_granted_completesFlow() {
        val reader = FakePermissionStatusReader(adequateLocation = true)
        val s = newStore(reader = reader)

        reader.notificationGranted = true
        s.onEvent(PermissionEvent.NotificationResult(granted = true))

        assertEquals(PermissionFlowStep.None, s.state.value.step)
    }

    @Test
    fun notificationSkip_completesFlow() {
        val s = newStore(reader = FakePermissionStatusReader(adequateLocation = true))
        s.onEvent(PermissionEvent.NotificationSkip)
        assertEquals(PermissionFlowStep.None, s.state.value.step)
    }

    @Test
    fun locationLater_movesToNotification_andDoesNotShowDenied() {
        val statusStore = FakePermissionStatusStore()
        val s = newStore(statusStore = statusStore)

        s.onEvent(PermissionEvent.LocationLater)

        assertEquals(PermissionFlowStep.Notification, s.state.value.step)
        assertFalse(statusStore.shouldShowLocationDeniedScreen())
    }

    @Test
    fun deniedLimitedFeatures_completesFlow() {
        val statusStore = FakePermissionStatusStore().apply { setShowLocationDeniedScreen(true) }
        val s = newStore(statusStore = statusStore)

        s.onEvent(PermissionEvent.DeniedLimitedFeatures)

        assertEquals(PermissionFlowStep.None, s.state.value.step)
    }

    @Test
    fun appReturnedFromBackground_afterLimitedFeatures_staysComplete() {
        val statusStore = FakePermissionStatusStore().apply { setLimitedFeaturesAcknowledged() }
        val s = newStore(statusStore = statusStore)
        assertEquals(PermissionFlowStep.None, s.state.value.step)

        s.onEvent(PermissionEvent.AppReturnedFromBackground)

        assertEquals(PermissionFlowStep.None, s.state.value.step)
    }

    @Test
    fun locationGranted_thenNotificationSkip_thenForeground_showsNotificationAgain() {
        val s = newStore(reader = FakePermissionStatusReader(adequateLocation = true))

        s.onEvent(PermissionEvent.NotificationSkip)
        assertEquals(PermissionFlowStep.None, s.state.value.step)

        s.onEvent(PermissionEvent.AppReturnedFromBackground)

        assertEquals(PermissionFlowStep.Notification, s.state.value.step)
    }

    @Test
    fun foreground_afterMaybeLaterAndNotNow_reShowsLocation() {
        val s = newStore()

        s.onEvent(PermissionEvent.LocationLater)
        s.onEvent(PermissionEvent.NotificationSkip)
        assertEquals(PermissionFlowStep.None, s.state.value.step)

        s.onEvent(PermissionEvent.AppReturnedFromBackground)

        assertEquals(PermissionFlowStep.Location, s.state.value.step)
    }

    @Test
    fun coldStart_osDenied_showsDeniedScreen() {
        val s = newStore(reader = FakePermissionStatusReader(locationPermissionDenied = true))
        assertEquals(PermissionFlowStep.Denied, s.state.value.step)
    }

    @Test
    fun deniedOpenSettings_emitsOpenAppSettingsEffect() =
        runTest {
            val s = newStore()
            s.onEvent(PermissionEvent.DeniedOpenSettings)
            assertEquals(PermissionEffect.OpenAppSettings, s.effects.first())
        }
}

private class FakePermissionStatusStore : PermissionStatusStore {
    private var limitedFeatures = false
    private var showDenied = false
    private var allowAttempted = false

    override fun clearLegacyDeferFlags() = Unit

    override fun wasLocationAllowAttempted() = allowAttempted

    override fun setLocationAllowAttempted() {
        allowAttempted = true
    }

    override fun isLimitedFeaturesAcknowledged() = limitedFeatures

    override fun setLimitedFeaturesAcknowledged() {
        limitedFeatures = true
    }

    override fun shouldShowLocationDeniedScreen() = showDenied

    override fun setShowLocationDeniedScreen(show: Boolean) {
        showDenied = show
    }
}

private class FakePermissionStatusReader(
    var adequateLocation: Boolean = false,
    var locationPermissionDenied: Boolean = false,
    var notificationPromptRequired: Boolean = true,
    var notificationGranted: Boolean = false,
    override val requiresBackgroundLocation: Boolean = true
) : PermissionStatusReader {
    override fun hasAdequateLocationAccess() = adequateLocation

    override fun isLocationPermissionDenied() = locationPermissionDenied

    override fun isNotificationPromptRequired() = notificationPromptRequired

    override fun isNotificationGranted() = notificationGranted
}
