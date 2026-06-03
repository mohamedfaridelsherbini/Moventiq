plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.ktlint) apply false
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

private val staticAnalysisModules = setOf("androidApp", "sharedLogic")

subprojects {
    if (name !in staticAnalysisModules) {
        return@subprojects
    }

    apply(plugin = rootProject.libs.plugins.detekt.get().pluginId)
    apply(plugin = rootProject.libs.plugins.ktlint.get().pluginId)

    extensions.configure<dev.detekt.gradle.extensions.DetektExtension>("detekt") {
        buildUponDefaultConfig = true
        allRules = false
        config.setFrom(rootProject.files("config/detekt/detekt.yml"))
        parallel = true
    }

    extensions.configure<org.jlleitschuh.gradle.ktlint.KtlintExtension>("ktlint") {
        version.set(rootProject.libs.versions.ktlint.engine.get())
        android.set(true)
        filter {
            exclude("**/build/**")
            exclude("**/generated/**")
        }
    }
}

tasks.register("staticAnalysis") {
    group = "verification"
    description = "Run detekt, ktlint, and Android Lint (debug)."
    dependsOn(
        staticAnalysisModules.map { ":$it:detekt" },
        staticAnalysisModules.map { ":$it:ktlintCheck" },
        ":androidApp:lintDebug",
    )
}