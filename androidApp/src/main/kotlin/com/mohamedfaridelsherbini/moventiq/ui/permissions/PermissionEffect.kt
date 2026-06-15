package com.mohamedfaridelsherbini.moventiq.ui.permissions

/**
 * One-shot effects emitted by [PermissionFlowViewModel] for the UI to perform once
 * (navigation, launching system screens). Unlike [PermissionFlowUiState] these must
 * not be re-applied on recomposition, so they travel over a channel, not state.
 */
sealed interface PermissionEffect {
    data object OpenAppSettings : PermissionEffect
}
