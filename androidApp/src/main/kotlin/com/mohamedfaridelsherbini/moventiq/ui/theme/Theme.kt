package com.mohamedfaridelsherbini.moventiq.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = MoventiqPrimary,
    background = MoventiqLight,
    onBackground = MoventiqDark,
)

private val DarkColorScheme = darkColorScheme(
    primary = MoventiqPrimary,
    background = MoventiqDark,
    onBackground = MoventiqLight,
)

@Composable
fun MoventiqTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MoventiqTypography,
        content = content,
    )
}
