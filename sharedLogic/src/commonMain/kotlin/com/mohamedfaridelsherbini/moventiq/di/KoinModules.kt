package com.mohamedfaridelsherbini.moventiq.di

import com.mohamedfaridelsherbini.moventiq.Greeting
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Shared Koin graph — repositories and use cases (M1+).
 * Android ViewModels stay in `:androidApp` (`appModule`).
 *
 * @see <a href="https://insert-koin.io/docs/reference/koin-core/kmp-setup/">Koin KMP setup</a>
 */
val sharedLogicModule = module {
    single { Greeting() }
}

/** Platform-specific bindings (database driver, geofencing, etc.). */
expect val platformModule: Module
