package com.mohamedfaridelsherbini.moventiq.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val LightColorScheme = lightColorScheme(
    primary = MoventiqPrimary,
    background = MoventiqLight,
    onBackground = MoventiqTextPrimary,
)

private val DarkColorScheme = darkColorScheme(
    primary = MoventiqPrimaryDark,
    background = MoventiqDark,
    onBackground = MoventiqSurfaceLight,
)

@Composable
fun MoventiqTheme(
    darkTheme: Boolean = androidx.compose.foundation.isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val moventiqColors = if (darkTheme) MoventiqDarkColors else MoventiqLightColors

    CompositionLocalProvider(
        LocalMoventiqColors provides moventiqColors,
        LocalMoventiqSpacing provides MoventiqSpacing(),
        LocalMoventiqRounded provides MoventiqRounded(),
        LocalMoventiqStroke provides MoventiqStroke(),
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = MoventiqTypography,
            content = content,
        )
    }
}
