package com.mohamedfaridelsherbini.moventiq.ui.splash

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.mohamedfaridelsherbini.moventiq.ui.theme.MoventiqTheme

@Preview(name = "Splash — Light", showBackground = false)
@Composable
private fun SplashContentLightPreview() {
    MoventiqTheme(darkTheme = false) {
        SplashContent(
            state = SplashUiState.preview(),
            onEvent = {},
        )
    }
}

@Preview(
    name = "Splash — Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
)
@Composable
private fun SplashContentDarkPreview() {
    MoventiqTheme(darkTheme = true) {
        SplashContent(
            state = SplashUiState.preview(),
            onEvent = {},
        )
    }
}

@Preview(name = "Splash — Exiting Light", showBackground = true)
@Composable
private fun SplashContentExitingLightPreview() {
    MoventiqTheme(darkTheme = false) {
        SplashContent(
            state = SplashUiState.preview(phase = SplashPhase.Exiting),
            onEvent = {},
        )
    }
}

@Preview(
    name = "Splash — Exiting Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
)
@Composable
private fun SplashContentExitingDarkPreview() {
    MoventiqTheme(darkTheme = true) {
        SplashContent(
            state = SplashUiState.preview(phase = SplashPhase.Exiting),
            onEvent = {},
        )
    }
}
