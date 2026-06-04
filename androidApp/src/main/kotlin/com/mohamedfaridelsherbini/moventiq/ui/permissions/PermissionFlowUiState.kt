package com.mohamedfaridelsherbini.moventiq.ui.permissions

data class PermissionFlowUiState(
    val step: PermissionFlowStep = PermissionFlowStep.None,
) {
    val isFlowComplete: Boolean
        get() = step == PermissionFlowStep.None

    companion object {
        fun preview(step: PermissionFlowStep = PermissionFlowStep.Location) =
            PermissionFlowUiState(step = step)
    }
}
