import SharedLogic

/// Resolves shared Kotlin dependencies from the Koin graph started in [MoventiqApp].
enum AppDependencies {
    static func bootstrap() {
        guard !KoinInitKt.isKoinInitialized() else { return }
        KoinInitIosKt.doInitKoinIos()
    }
}
