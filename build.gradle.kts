// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.12.0" apply false
    id("org.jetbrains.kotlin.android") version "2.2.0" apply false
    id("com.google.devtools.ksp") version ("2.2.0-2.0.2") apply false
    id("com.diffplug.spotless") version "7.2.1"
}

buildscript {
    dependencies {
        val firebaseCrashlyticsGradleVersion = "3.0.5"
        val googleServicesVersion = "4.4.3"
        val gradleVersion = "8.12.0"
        val navigationVersion = "2.9.3"
        rootProject.extra["navigation"] = navigationVersion

        classpath("com.android.tools.build:gradle:$gradleVersion")
        classpath("com.google.gms:google-services:$googleServicesVersion")
        classpath("com.google.firebase:firebase-crashlytics-gradle:$firebaseCrashlyticsGradleVersion")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navigationVersion")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

spotless {
    kotlin {
        target("**/*.kt")
        targetExclude("${layout.buildDirectory}/**/*.kt")
        ktlint()
    }

    kotlinGradle {
        target("**/*.gradle.kts")
        ktlint()
    }
}

tasks.register<Delete>("cleanBuildDirectory") {
    delete(rootProject.layout.buildDirectory)
}
