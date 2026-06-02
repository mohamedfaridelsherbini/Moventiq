package com.mohamedfaridelsherbini.moventiq.ui.splash

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import com.mohamedfaridelsherbini.moventiq.R
import com.mohamedfaridelsherbini.moventiq.ui.theme.MoventiqPrimaryContainer
import com.mohamedfaridelsherbini.moventiq.ui.theme.MoventiqSurfaceDarkElevated

/**
 * Shared splash branding used by:
 * - System splash theme (`Theme.Moventiq.Splash` → ic_splash_system_icon_*)
 * - Compose [SplashScreen] → ic_moventiq_splash_mark_*
 */
object SplashBranding {
    const val MARK_SIZE_DP = 112
    const val GLOW_SIZE_DP = 440
    const val WORDMARK_TOP_SPACING_DP = 28
    const val TAGLINE_TOP_SPACING_DP = 10
    const val TAGLINE_ALPHA = 0.6f
    const val GLOW_ALPHA_LIGHT = 0.14f
    const val GLOW_ALPHA_DARK = 0.45f

    const val MARK_ENTER_DURATION_MS = 650
    const val BACKGROUND_FADE_DURATION_MS = 680
    const val GLOW_ENTER_DURATION_MS = 900
    const val WORDMARK_ENTER_DURATION_MS = 550
    const val TAGLINE_ENTER_DURATION_MS = 550
    const val GLOW_ENTER_DELAY_MS = 100L
    const val WORDMARK_ENTER_DELAY_MS = 260L
    const val TAGLINE_ENTER_DELAY_MS = 420L
    const val SPLASH_HOLD_MS = 900L
    const val SPLASH_ENTER_WINDOW_MS: Long =
        GLOW_ENTER_DELAY_MS + GLOW_ENTER_DURATION_MS + SPLASH_HOLD_MS
    const val SPLASH_EXIT_DURATION_MS = 500
    const val SYSTEM_EXIT_DURATION_MS = 450L
    const val APP_CROSSFADE_DURATION_MS = 550

    const val MARK_INITIAL_SCALE = 0.86f
    const val MARK_EXIT_SCALE = 0.94f
    const val TEXT_SLIDE_DP = 18
    const val GLOW_INITIAL_SCALE = 0.75f

    fun fadeBackground(
        darkTheme: Boolean,
        targetBackground: Color,
        progress: Float,
    ): Color {
        val start = if (darkTheme) MoventiqSurfaceDarkElevated else MoventiqPrimaryContainer
        return lerp(start, targetBackground, progress)
    }

    fun markDrawable(darkTheme: Boolean): Int =
        if (darkTheme) R.drawable.ic_moventiq_splash_mark_dark else R.drawable.ic_moventiq_splash_mark_light

    fun glowAlpha(darkTheme: Boolean): Float =
        if (darkTheme) GLOW_ALPHA_DARK else GLOW_ALPHA_LIGHT
}
