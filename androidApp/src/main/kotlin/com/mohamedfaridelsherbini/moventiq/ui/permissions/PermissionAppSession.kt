package com.mohamedfaridelsherbini.moventiq.ui.permissions

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Detects when the app returns from background so permission defers can reset per page.
 */
object PermissionAppSession {
    private var visibleActivityCount = 0
    private var wasInBackground = false

    private val _returnedFromBackground = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val returnedFromBackground: SharedFlow<Unit> = _returnedFromBackground.asSharedFlow()

    fun onActivityStarted() {
        if (visibleActivityCount == 0 && wasInBackground) {
            _returnedFromBackground.tryEmit(Unit)
            wasInBackground = false
        }
        visibleActivityCount++
    }

    fun onActivityStopped() {
        visibleActivityCount = (visibleActivityCount - 1).coerceAtLeast(0)
        if (visibleActivityCount == 0) {
            wasInBackground = true
        }
    }

    internal fun resetForTests() {
        visibleActivityCount = 0
        wasInBackground = false
    }
}
