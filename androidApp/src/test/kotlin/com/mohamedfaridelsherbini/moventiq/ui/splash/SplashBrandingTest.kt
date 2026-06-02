package com.mohamedfaridelsherbini.moventiq.ui.splash

import org.junit.Assert.assertEquals
import org.junit.Test

class SplashBrandingTest {

    @Test
    fun splashEnterWindow_equalsSumOfBrandingDurations() {
        assertEquals(
            SplashBranding.GLOW_ENTER_DELAY_MS +
                SplashBranding.GLOW_ENTER_DURATION_MS +
                SplashBranding.SPLASH_HOLD_MS,
            SplashBranding.SPLASH_ENTER_WINDOW_MS,
        )
    }
}
