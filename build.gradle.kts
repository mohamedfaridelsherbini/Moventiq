plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
}

val moventiqCompileSdk = providers.gradleProperty("android.compileSdk").get().toInt()
val moventiqMinSdk = providers.gradleProperty("android.minSdk").get().toInt()
val moventiqTargetSdk = providers.gradleProperty("android.targetSdk").get().toInt()
check(moventiqTargetSdk <= moventiqCompileSdk) {
    "targetSdk ($moventiqTargetSdk) must not exceed compileSdk ($moventiqCompileSdk)"
}
check(moventiqMinSdk <= moventiqTargetSdk) {
    "minSdk ($moventiqMinSdk) must not exceed targetSdk ($moventiqTargetSdk)"
}