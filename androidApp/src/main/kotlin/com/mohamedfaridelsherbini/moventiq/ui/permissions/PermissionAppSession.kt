package com.mohamedfaridelsherbini.moventiq.ui.permissions

/**
 * Detects when the app returns from background so permission defers can reset per page.
 */
object PermissionAppSession {
    private var visibleActivityCount = 0
    private var wasInBackground = false

    var onReturnedFromBackground: (() -> Unit)? = null

    fun onActivityStarted() {
        if (visibleActivityCount == 0 && wasInBackground) {
            onReturnedFromBackground?.invoke()
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
        onReturnedFromBackground = null
    }
}
