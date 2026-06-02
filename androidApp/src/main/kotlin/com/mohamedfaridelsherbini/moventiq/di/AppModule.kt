package com.mohamedfaridelsherbini.moventiq.di

import com.mohamedfaridelsherbini.moventiq.ui.splash.SplashViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::SplashViewModel)
}
