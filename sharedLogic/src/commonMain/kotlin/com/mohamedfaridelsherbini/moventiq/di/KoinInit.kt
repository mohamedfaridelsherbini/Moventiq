package com.mohamedfaridelsherbini.moventiq.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.KoinAppDeclaration

private var koinApplication: KoinApplication? = null

/**
 * Shared Koin bootstrap for Android, iOS, and tests.
 *
 * @see <a href="https://insert-koin.io/docs/reference/koin-core/kmp-setup/">Koin KMP setup</a>
 */
fun initKoin(config: KoinAppDeclaration? = null): KoinApplication {
    koinApplication?.let { return it }
    val app =
        startKoin {
            modules(sharedLogicModule, platformModule)
            config?.invoke(this)
        }
    koinApplication = app
    return app
}

fun isKoinInitialized(): Boolean = koinApplication != null

/** Test-only teardown. */
internal fun resetKoinForTests() {
    if (koinApplication == null) return
    stopKoin()
    koinApplication = null
}
