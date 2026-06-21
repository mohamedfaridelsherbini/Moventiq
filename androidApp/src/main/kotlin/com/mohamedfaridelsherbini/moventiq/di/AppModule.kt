package com.mohamedfaridelsherbini.moventiq.di

import com.mohamedfaridelsherbini.moventiq.ui.onboarding.OnboardingPreferences
import com.mohamedfaridelsherbini.moventiq.ui.onboarding.OnboardingStatusStore
import com.mohamedfaridelsherbini.moventiq.ui.onboarding.OnboardingViewModel
import com.mohamedfaridelsherbini.moventiq.feature.permissions.presentation.PermissionStatusReader
import com.mohamedfaridelsherbini.moventiq.feature.permissions.presentation.PermissionStatusStore
import com.mohamedfaridelsherbini.moventiq.ui.permissions.AndroidPermissionStatusChecker
import com.mohamedfaridelsherbini.moventiq.ui.permissions.PermissionFlowViewModel
import com.mohamedfaridelsherbini.moventiq.ui.permissions.PermissionPreferences
import com.mohamedfaridelsherbini.moventiq.ui.splash.SplashViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<OnboardingStatusStore> { OnboardingPreferences(androidContext()) }
    single<PermissionStatusStore> { PermissionPreferences(androidContext()) }
    single<PermissionStatusReader> { AndroidPermissionStatusChecker(androidContext()) }
    viewModel { SplashViewModel() }
    viewModel { OnboardingViewModel(get()) }
    viewModel { PermissionFlowViewModel(get(), get()) }
}
