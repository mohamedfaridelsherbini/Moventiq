package com.mohamedfaridelsherbini.moventiq.di

import com.mohamedfaridelsherbini.moventiq.ui.splash.SplashViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // No-arg factory: constructor Long params have defaults and must not be resolved from Koin.
    viewModel { SplashViewModel() }
}
