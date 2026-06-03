package com.mohamedfaridelsherbini.moventiq.ui.onboarding

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.mohamedfaridelsherbini.moventiq.ui.theme.MoventiqTheme

@Preview(name = "Onboarding — Page 1 Light", showBackground = true)
@Composable
private fun OnboardingPage1LightPreview() {
    MoventiqTheme(darkTheme = false) {
        OnboardingContent(
            state = OnboardingUiState.preview(currentPage = 0),
            onEvent = {},
        )
    }
}

@Preview(
    name = "Onboarding — Page 1 Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
)
@Composable
private fun OnboardingPage1DarkPreview() {
    MoventiqTheme(darkTheme = true) {
        OnboardingContent(
            state = OnboardingUiState.preview(currentPage = 0),
            onEvent = {},
        )
    }
}

@Preview(name = "Onboarding — Page 2 Light", showBackground = true)
@Composable
private fun OnboardingPage2LightPreview() {
    MoventiqTheme(darkTheme = false) {
        OnboardingContent(
            state = OnboardingUiState.preview(currentPage = 1),
            onEvent = {},
        )
    }
}

@Preview(
    name = "Onboarding — Page 2 Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
)
@Composable
private fun OnboardingPage2DarkPreview() {
    MoventiqTheme(darkTheme = true) {
        OnboardingContent(
            state = OnboardingUiState.preview(currentPage = 1),
            onEvent = {},
        )
    }
}

@Preview(name = "Onboarding — Page 3 Light", showBackground = true)
@Composable
private fun OnboardingPage3LightPreview() {
    MoventiqTheme(darkTheme = false) {
        OnboardingContent(
            state = OnboardingUiState.preview(currentPage = 2),
            onEvent = {},
        )
    }
}

@Preview(
    name = "Onboarding — Page 3 Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
)
@Composable
private fun OnboardingPage3DarkPreview() {
    MoventiqTheme(darkTheme = true) {
        OnboardingContent(
            state = OnboardingUiState.preview(currentPage = 2),
            onEvent = {},
        )
    }
}
