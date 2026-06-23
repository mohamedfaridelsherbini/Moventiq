package com.mohamedfaridelsherbini.moventiq.ui.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.mohamedfaridelsherbini.moventiq.feature.permissions.presentation.PermissionStatusReader

class AndroidPermissionStatusChecker(
    private val context: Context,
) : PermissionStatusReader {
    // Geofencing needs background location, which only exists as a separate grant on Q+.
    override val requiresBackgroundLocation: Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    override fun hasAdequateLocationAccess(): Boolean {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED

        if (!requiresBackgroundLocation) {
            return fineGranted
        }

        val backgroundGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED

        return fineGranted && backgroundGranted
    }

    override fun isLocationPermissionDenied(): Boolean {
        if (hasAdequateLocationAccess()) return false

        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED

        // Partial grant: foreground OK, background missing on Android Q+.
        return requiresBackgroundLocation && (fineGranted || coarseGranted)
    }

    override fun isNotificationPromptRequired(): Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    override fun isNotificationGranted(): Boolean {
        if (!isNotificationPromptRequired()) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }
}
