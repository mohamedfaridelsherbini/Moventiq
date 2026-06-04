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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mohamedfaridelsherbini.moventiq.navigation.MoventiqNavHost
import com.mohamedfaridelsherbini.moventiq.ui.onboarding.OnboardingScreen
import com.mohamedfaridelsherbini.moventiq.ui.onboarding.OnboardingViewModel
import com.mohamedfaridelsherbini.moventiq.ui.permissions.PermissionEvent
import com.mohamedfaridelsherbini.moventiq.ui.permissions.PermissionFlowHost
import com.mohamedfaridelsherbini.moventiq.ui.permissions.PermissionFlowViewModel
import com.mohamedfaridelsherbini.moventiq.ui.splash.SplashBranding
import com.mohamedfaridelsherbini.moventiq.ui.splash.SplashScreen
import com.mohamedfaridelsherbini.moventiq.ui.splash.SplashViewModel
import com.mohamedfaridelsherbini.moventiq.ui.theme.MoventiqTheme
import org.koin.compose.viewmodel.koinViewModel

private enum class AppPhase {
    Splash,
    Onboarding,
    Permissions,
    Main,
}

@Composable
fun MoventiqApp(
    splashViewModel: SplashViewModel,
    onSplashDrawn: () -> Unit,
    modifier: Modifier = Modifier,
    onboardingViewModel: OnboardingViewModel = koinViewModel(),
    permissionViewModel: PermissionFlowViewModel = koinViewModel(),
) {
    val splashState by splashViewModel.state.collectAsStateWithLifecycle()
    val onboardingState by onboardingViewModel.state.collectAsStateWithLifecycle()
    val permissionState by permissionViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(onboardingState.shouldShowOnboarding, onboardingState.isFinished) {
        if (!onboardingState.shouldShowOnboarding) {
            permissionViewModel.onEvent(PermissionEvent.Refresh)
        }
    }

    val phase = when {
        !splashState.isComplete -> AppPhase.Splash
        onboardingState.shouldShowOnboarding -> AppPhase.Onboarding
        !permissionState.isFlowComplete -> AppPhase.Permissions
        else -> AppPhase.Main
    }

    MoventiqTheme {
        AnimatedContent(
            targetState = phase,
            modifier = modifier.fillMaxSize(),
            transitionSpec = {
                when {
                    initialState == AppPhase.Splash -> {
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
                    }
                    else -> {
                        fadeIn(
                            tween(durationMillis = SplashBranding.APP_CROSSFADE_DURATION_MS),
                        ) togetherWith fadeOut(
                            tween(durationMillis = SplashBranding.APP_CROSSFADE_DURATION_MS),
                        )
                    }
                }
            },
            label = "appPhase",
        ) { currentPhase ->
            when (currentPhase) {
                AppPhase.Splash -> SplashScreen(
                    viewModel = splashViewModel,
                    onContentDrawn = onSplashDrawn,
                )
                AppPhase.Onboarding -> OnboardingScreen(viewModel = onboardingViewModel)
                AppPhase.Permissions -> PermissionFlowHost(viewModel = permissionViewModel)
                AppPhase.Main -> MoventiqNavHost()
            }
        }
    }
}
