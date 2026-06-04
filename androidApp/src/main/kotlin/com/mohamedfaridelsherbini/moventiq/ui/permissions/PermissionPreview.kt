package com.mohamedfaridelsherbini.moventiq.ui.permissions

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.mohamedfaridelsherbini.moventiq.ui.theme.MoventiqTheme

@Preview(name = "Location Permission — Light", showBackground = true)
@Composable
private fun LocationPermissionLightPreview() {
    MoventiqTheme(darkTheme = false) {
        LocationPermissionContent(onEvent = {})
    }
}

@Preview(
    name = "Location Permission — Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
)
@Composable
private fun LocationPermissionDarkPreview() {
    MoventiqTheme(darkTheme = true) {
        LocationPermissionContent(onEvent = {})
    }
}

@Preview(name = "Notification Permission — Light", showBackground = true)
@Composable
private fun NotificationPermissionLightPreview() {
    MoventiqTheme(darkTheme = false) {
        NotificationPermissionContent(onEvent = {})
    }
}

@Preview(
    name = "Notification Permission — Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
)
@Composable
private fun NotificationPermissionDarkPreview() {
    MoventiqTheme(darkTheme = true) {
        NotificationPermissionContent(onEvent = {})
    }
}

@Preview(name = "Permission Denied — Light", showBackground = true)
@Composable
private fun PermissionDeniedLightPreview() {
    MoventiqTheme(darkTheme = false) {
        PermissionDeniedContent(onEvent = {})
    }
}

@Preview(
    name = "Permission Denied — Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
)
@Composable
private fun PermissionDeniedDarkPreview() {
    MoventiqTheme(darkTheme = true) {
        PermissionDeniedContent(onEvent = {})
    }
}
