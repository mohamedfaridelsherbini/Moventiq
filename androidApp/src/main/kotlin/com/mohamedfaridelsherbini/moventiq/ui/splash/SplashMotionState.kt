package com.mohamedfaridelsherbini.moventiq.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal data class SplashMotionState(
    val backgroundProgress: Animatable<Float, *>,
    val markAlpha: Animatable<Float, *>,
    val markScale: Animatable<Float, *>,
    val glowAlpha: Animatable<Float, *>,
    val glowScale: Animatable<Float, *>,
    val wordmarkAlpha: Animatable<Float, *>,
    val wordmarkOffset: Animatable<Float, *>,
    val taglineAlpha: Animatable<Float, *>,
    val taglineOffset: Animatable<Float, *>,
    val screenAlpha: Animatable<Float, *>,
    val screenScale: Animatable<Float, *>,
)

@Composable
internal fun rememberSplashMotionState(isExiting: Boolean): SplashMotionState {
    val markAlpha = remember { Animatable(0f) }
    val markScale = remember { Animatable(SplashBranding.MARK_INITIAL_SCALE) }
    val glowAlpha = remember { Animatable(0f) }
    val glowScale = remember { Animatable(SplashBranding.GLOW_INITIAL_SCALE) }
    val wordmarkAlpha = remember { Animatable(0f) }
    val wordmarkOffset = remember { Animatable(SplashBranding.TEXT_SLIDE_DP.toFloat()) }
    val taglineAlpha = remember { Animatable(0f) }
    val taglineOffset = remember { Animatable(SplashBranding.TEXT_SLIDE_DP.toFloat()) }
    val screenAlpha = remember { Animatable(1f) }
    val screenScale = remember { Animatable(1f) }
    val backgroundProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            backgroundProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = SplashBranding.BACKGROUND_FADE_DURATION_MS,
                    easing = FastOutSlowInEasing,
                ),
            )
        }
        launch {
            markAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = SplashBranding.MARK_ENTER_DURATION_MS,
                    easing = FastOutSlowInEasing,
                ),
            )
        }
        launch {
            markScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = SplashBranding.MARK_ENTER_DURATION_MS + 120,
                    easing = FastOutSlowInEasing,
                ),
            )
        }
        launch {
            delay(SplashBranding.GLOW_ENTER_DELAY_MS)
            glowAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = SplashBranding.GLOW_ENTER_DURATION_MS,
                    easing = LinearOutSlowInEasing,
                ),
            )
        }
        launch {
            delay(SplashBranding.GLOW_ENTER_DELAY_MS)
            glowScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = SplashBranding.GLOW_ENTER_DURATION_MS,
                    easing = LinearOutSlowInEasing,
                ),
            )
        }
        launch {
            delay(SplashBranding.WORDMARK_ENTER_DELAY_MS)
            launch {
                wordmarkAlpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = SplashBranding.WORDMARK_ENTER_DURATION_MS,
                        easing = FastOutSlowInEasing,
                    ),
                )
            }
            launch {
                wordmarkOffset.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = SplashBranding.WORDMARK_ENTER_DURATION_MS,
                        easing = FastOutSlowInEasing,
                    ),
                )
            }
        }
        launch {
            delay(SplashBranding.TAGLINE_ENTER_DELAY_MS)
            launch {
                taglineAlpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = SplashBranding.TAGLINE_ENTER_DURATION_MS,
                        easing = FastOutSlowInEasing,
                    ),
                )
            }
            launch {
                taglineOffset.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = SplashBranding.TAGLINE_ENTER_DURATION_MS,
                        easing = FastOutSlowInEasing,
                    ),
                )
            }
        }
    }

    LaunchedEffect(isExiting) {
        if (!isExiting) return@LaunchedEffect

        launch {
            screenAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = SplashBranding.SPLASH_EXIT_DURATION_MS,
                    easing = FastOutSlowInEasing,
                ),
            )
        }
        launch {
            screenScale.animateTo(
                targetValue = SplashBranding.MARK_EXIT_SCALE,
                animationSpec = tween(
                    durationMillis = SplashBranding.SPLASH_EXIT_DURATION_MS,
                    easing = FastOutSlowInEasing,
                ),
            )
        }
    }

    return SplashMotionState(
        backgroundProgress = backgroundProgress,
        markAlpha = markAlpha,
        markScale = markScale,
        glowAlpha = glowAlpha,
        glowScale = glowScale,
        wordmarkAlpha = wordmarkAlpha,
        wordmarkOffset = wordmarkOffset,
        taglineAlpha = taglineAlpha,
        taglineOffset = taglineOffset,
        screenAlpha = screenAlpha,
        screenScale = screenScale,
    )
}
