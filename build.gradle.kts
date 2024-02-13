// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("com.google.devtools.ksp").version("1.9.21-1.0.15")
}

buildscript {
    dependencies {
        val firebaseCrashlyticsGradleVersion = "2.9.9"
        val googleServicesVersion = "4.4.1"
        val gradleVersion = "8.2.2"
        val kotlinGradlePluginVersion = "1.9.21"
        val navigationVersion = "2.7.7"
        rootProject.extra["navigation"] = navigationVersion

        classpath("com.android.tools.build:gradle:$gradleVersion")
        classpath("com.google.gms:google-services:$googleServicesVersion")
        classpath("com.google.firebase:firebase-crashlytics-gradle:$firebaseCrashlyticsGradleVersion")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinGradlePluginVersion")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navigationVersion")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
