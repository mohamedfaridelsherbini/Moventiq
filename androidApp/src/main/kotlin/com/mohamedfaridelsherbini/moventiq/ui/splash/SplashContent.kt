package com.mohamedfaridelsherbini.moventiq.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.mohamedfaridelsherbini.moventiq.R
import com.mohamedfaridelsherbini.moventiq.ui.theme.MoventiqPrimary

@Composable
fun SplashContent(
    state: SplashUiState,
    onEvent: (SplashEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(Unit) {
        onEvent(SplashEvent.ContentDrawn)
    }

    val colorScheme = MaterialTheme.colorScheme
    val backgroundColor = colorScheme.background
    val foregroundColor = colorScheme.onBackground
    val darkTheme = backgroundColor.luminance() < 0.5f
    val motion = rememberSplashMotionState(isExiting = state.isExiting)
    val animatedBackground = SplashBranding.fadeBackground(
        darkTheme = darkTheme,
        targetBackground = backgroundColor,
        progress = motion.backgroundProgress.value,
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag(SplashTestTags.SCREEN),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(animatedBackground),
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(motion.screenAlpha.value)
                .scale(motion.screenScale.value),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(SplashBranding.GLOW_SIZE_DP.dp)
                    .scale(motion.glowScale.value)
                    .alpha(motion.glowAlpha.value)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                MoventiqPrimary.copy(alpha = SplashBranding.glowAlpha(darkTheme)),
                                Color.Transparent,
                            ),
                        ),
                    ),
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(SplashBranding.markDrawable(darkTheme)),
                    contentDescription = null,
                    modifier = Modifier
                        .size(SplashBranding.MARK_SIZE_DP.dp)
                        .alpha(motion.markAlpha.value)
                        .scale(motion.markScale.value),
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.height(SplashBranding.WORDMARK_TOP_SPACING_DP.dp))
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.headlineMedium,
                        color = foregroundColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .testTag(SplashTestTags.WORDMARK)
                            .alpha(motion.wordmarkAlpha.value)
                            .offset { IntOffset(0, motion.wordmarkOffset.value.dp.roundToPx()) },
                    )
                    Spacer(modifier = Modifier.height(SplashBranding.TAGLINE_TOP_SPACING_DP.dp))
                    Text(
                        text = stringResource(R.string.splash_tagline),
                        style = MaterialTheme.typography.labelLarge,
                        color = foregroundColor.copy(alpha = SplashBranding.TAGLINE_ALPHA),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .testTag(SplashTestTags.TAGLINE)
                            .alpha(motion.taglineAlpha.value)
                            .offset { IntOffset(0, motion.taglineOffset.value.dp.roundToPx()) },
                    )
                }
            }
        }
    }
}
