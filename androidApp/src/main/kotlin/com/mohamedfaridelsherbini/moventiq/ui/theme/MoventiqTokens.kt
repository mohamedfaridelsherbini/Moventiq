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
)

data class MoventiqSpacing(
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 24.dp,
    val xl: Dp = 32.dp,
    val xxl: Dp = 48.dp,
)

val LocalMoventiqColors = staticCompositionLocalOf { MoventiqLightColors }
val LocalMoventiqSpacing = staticCompositionLocalOf { MoventiqSpacing() }

@Composable
fun moventiqColors(): MoventiqColors = LocalMoventiqColors.current

@Composable
fun moventiqSpacing(): MoventiqSpacing = LocalMoventiqSpacing.current
