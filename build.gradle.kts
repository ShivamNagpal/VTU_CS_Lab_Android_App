// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("com.google.devtools.ksp").version("1.9.21-1.0.15")
}

buildscript {
    dependencies {
        val versions = object {
            val firebaseCrashlyticsGradle = "2.9.4"
            val googleServices = "4.3.15"
            val gradle = "8.2.2"
            val kotlinGradlePlugin = "1.9.21"
            val navigation = "2.7.7"
        }
        rootProject.extra["navigation"] = versions.navigation

        classpath("com.android.tools.build:gradle:${versions.gradle}")
        classpath("com.google.gms:google-services:${versions.googleServices}")
        classpath("com.google.firebase:firebase-crashlytics-gradle:${versions.firebaseCrashlyticsGradle}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${versions.kotlinGradlePlugin}")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:${versions.navigation}")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

tasks.register<Delete>("clean", Delete::class.java) {
    delete(rootProject.buildDir)
}
