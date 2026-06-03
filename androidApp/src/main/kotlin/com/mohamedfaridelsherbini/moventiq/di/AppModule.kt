package com.mohamedfaridelsherbini.moventiq.di

import com.mohamedfaridelsherbini.moventiq.ui.onboarding.OnboardingPreferences
import com.mohamedfaridelsherbini.moventiq.ui.onboarding.OnboardingStatusStore
import com.mohamedfaridelsherbini.moventiq.ui.onboarding.OnboardingViewModel
import com.mohamedfaridelsherbini.moventiq.ui.splash.SplashViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<OnboardingStatusStore> { OnboardingPreferences(androidContext()) }
    viewModel { SplashViewModel() }
    viewModel { OnboardingViewModel(get()) }
}
