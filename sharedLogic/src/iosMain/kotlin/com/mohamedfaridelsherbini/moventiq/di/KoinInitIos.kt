package com.mohamedfaridelsherbini.moventiq.di

/**
 * iOS entry point — Swift calls `KoinInitIosKt.doInitKoinIos()`.
 *
 * @see <a href="https://insert-koin.io/docs/reference/koin-core/kmp-setup/">Koin KMP setup</a>
 */
@Suppress("unused") // Exported to Swift; not referenced from Kotlin.
fun initKoinIos() {
    initKoin()
}
