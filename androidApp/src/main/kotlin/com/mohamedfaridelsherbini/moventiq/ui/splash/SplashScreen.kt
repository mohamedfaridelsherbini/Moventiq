package com.mohamedfaridelsherbini.moventiq.ui.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    modifier: Modifier = Modifier,
    onContentDrawn: () -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SplashContent(
        state = state,
        onEvent = { event ->
            viewModel.onEvent(event)
            if (event is SplashEvent.ContentDrawn) {
                onContentDrawn()
            }
        },
        modifier = modifier,
    )
}
