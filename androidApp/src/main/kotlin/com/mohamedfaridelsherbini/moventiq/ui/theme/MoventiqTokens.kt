package com.mohamedfaridelsherbini.moventiq.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class MoventiqColors(
    val primary: Color,
    val primaryContainer: Color,
    val accent: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val textOnPrimary: Color,
    val border: Color,
    val progressInactive: Color,
    val skeleton: Color,
    val skeletonMuted: Color,
    val error: Color,
    val errorContainer: Color,
)

val MoventiqLightColors = MoventiqColors(
    primary = MoventiqPrimary,
    primaryContainer = MoventiqPrimaryContainer,
    accent = MoventiqAccent,
    surface = MoventiqSurfaceLight,
    surfaceElevated = MoventiqLight,
    textPrimary = MoventiqTextPrimary,
    textSecondary = MoventiqTextSecondary,
    textMuted = MoventiqTextMuted,
    textOnPrimary = MoventiqLight,
    border = MoventiqBorderLight,
    progressInactive = MoventiqProgressInactive,
    skeleton = MoventiqBorderLight,
    skeletonMuted = MoventiqPrimaryContainer,
    error = MoventiqError,
    errorContainer = MoventiqErrorContainer,
)

val MoventiqDarkColors = MoventiqColors(
    primary = MoventiqPrimaryDark,
    primaryContainer = MoventiqPrimaryContainerDark,
    accent = MoventiqAccent,
    surface = MoventiqSurfaceDark,
    surfaceElevated = MoventiqSurfaceDarkElevated,
    textPrimary = MoventiqSurfaceLight,
    textSecondary = MoventiqTextMutedDark,
    textMuted = MoventiqTextMuted,
    textOnPrimary = MoventiqLight,
    border = MoventiqBorderDark,
    progressInactive = MoventiqProgressInactive,
    skeleton = MoventiqBorderDark,
    skeletonMuted = MoventiqPrimaryContainerDark,
    error = MoventiqError,
    errorContainer = MoventiqErrorContainer,
)

data class MoventiqSpacing(
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 24.dp,
    val xl: Dp = 32.dp,
    val xxl: Dp = 48.dp,
) {
    /** sm + 6 — button vertical padding per DESIGN.md */
    val smPlus: Dp get() = sm + xs + 2.dp

    val iconHero: Dp get() = xxl - xs

    val iconInline: Dp get() = md

    val iconButton: Dp get() = smPlus + xs

    val iconContainerTrust: Dp get() = smPlus + md + xs

    val iconStepBadge: Dp get() = lg + xs

    val cardPaddingInset: Dp get() = smPlus + xs
}

data class MoventiqRounded(
    val xs: Dp = 6.dp,
    val sm: Dp = 10.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 24.dp,
    val xl: Dp = 32.dp,
    val full: Dp = 999.dp,
)

/** Stroke widths — DESIGN.md card borders use 1px. */
data class MoventiqStroke(
    val hairline: Dp = 1.dp,
)

val LocalMoventiqColors = staticCompositionLocalOf { MoventiqLightColors }
val LocalMoventiqSpacing = staticCompositionLocalOf { MoventiqSpacing() }
val LocalMoventiqRounded = staticCompositionLocalOf { MoventiqRounded() }
val LocalMoventiqStroke = staticCompositionLocalOf { MoventiqStroke() }

@Composable
fun moventiqColors(): MoventiqColors = LocalMoventiqColors.current

@Composable
fun moventiqSpacing(): MoventiqSpacing = LocalMoventiqSpacing.current

@Composable
fun moventiqRounded(): MoventiqRounded = LocalMoventiqRounded.current

@Composable
fun moventiqStroke(): MoventiqStroke = LocalMoventiqStroke.current
