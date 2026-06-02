package com.mohamedfaridelsherbini.moventiq

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mohamedfaridelsherbini.moventiq.navigation.MoventiqNavHost
import com.mohamedfaridelsherbini.moventiq.ui.splash.SplashBranding
import com.mohamedfaridelsherbini.moventiq.ui.splash.SplashScreen
import com.mohamedfaridelsherbini.moventiq.ui.splash.SplashViewModel
import com.mohamedfaridelsherbini.moventiq.ui.theme.MoventiqTheme

@Composable
fun MoventiqApp(
    splashViewModel: SplashViewModel,
    onSplashDrawn: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val splashState by splashViewModel.state.collectAsStateWithLifecycle()

    MoventiqTheme {
        AnimatedContent(
            targetState = !splashState.isComplete,
            modifier = modifier.fillMaxSize(),
            transitionSpec = {
                fadeIn(
                    animationSpec = tween(
                        durationMillis = SplashBranding.APP_CROSSFADE_DURATION_MS,
                        easing = FastOutSlowInEasing,
                    ),
                ) + scaleIn(
                    initialScale = 0.98f,
                    animationSpec = tween(
                        durationMillis = SplashBranding.APP_CROSSFADE_DURATION_MS,
                        easing = FastOutSlowInEasing,
                    ),
                ) + slideInVertically(
                    animationSpec = tween(
                        durationMillis = SplashBranding.APP_CROSSFADE_DURATION_MS,
                        easing = FastOutSlowInEasing,
                    ),
                    initialOffsetY = { it / 12 },
                ) togetherWith fadeOut(
                    animationSpec = tween(
                        durationMillis = SplashBranding.SPLASH_EXIT_DURATION_MS,
                        easing = FastOutSlowInEasing,
                    ),
                ) + scaleOut(
                    targetScale = SplashBranding.MARK_EXIT_SCALE,
                    animationSpec = tween(
                        durationMillis = SplashBranding.SPLASH_EXIT_DURATION_MS,
                        easing = FastOutSlowInEasing,
                    ),
                )
            },
            label = "splashToMain",
        ) { splashActive ->
            if (splashActive) {
                SplashScreen(
                    viewModel = splashViewModel,
                    onContentDrawn = onSplashDrawn,
                )
            } else {
                MoventiqNavHost()
            }
        }
    }
}
