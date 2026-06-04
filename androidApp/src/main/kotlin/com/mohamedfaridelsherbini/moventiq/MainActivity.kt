package com.mohamedfaridelsherbini.moventiq

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.mohamedfaridelsherbini.moventiq.ui.permissions.PermissionAppSession
import com.mohamedfaridelsherbini.moventiq.ui.splash.SplashBranding
import com.mohamedfaridelsherbini.moventiq.ui.splash.SplashPhase
import com.mohamedfaridelsherbini.moventiq.ui.splash.SplashUiState
import com.mohamedfaridelsherbini.moventiq.ui.splash.SplashViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.atomic.AtomicBoolean

class MainActivity : ComponentActivity() {
    private val splashViewModel: SplashViewModel by viewModel()
    private val keepSystemSplashOn = AtomicBoolean(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            shouldKeepSystemSplashOn(keepSystemSplashOn.get(), splashViewModel.state.value)
        }

        splashScreen.setOnExitAnimationListener { splashScreenViewProvider ->
            val splashView = splashScreenViewProvider.view
            ObjectAnimator.ofFloat(splashView, View.ALPHA, 1f, 0f).apply {
                duration = SplashBranding.SYSTEM_EXIT_DURATION_MS
                interpolator = DecelerateInterpolator()
                doOnEnd { splashScreenViewProvider.remove() }
                start()
            }
        }

        setContent {
            MoventiqApp(
                splashViewModel = splashViewModel,
                onSplashDrawn = { keepSystemSplashOn.set(false) },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }

    override fun onStart() {
        super.onStart()
        PermissionAppSession.onActivityStarted()
    }

    override fun onStop() {
        PermissionAppSession.onActivityStopped()
        super.onStop()
    }
}

/** Keeps the Android 12+ system splash up only while Compose splash is still in [SplashPhase.Visible]. */
internal fun shouldKeepSystemSplashOn(
    keepSystemSplashOn: Boolean,
    state: SplashUiState,
): Boolean = keepSystemSplashOn && !state.isComplete && state.phase == SplashPhase.Visible
