plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.firebase.crashlytics")
    id("androidx.navigation.safeargs")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.nagpal.shivam.vtucslab"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.nagpal.shivam.vtucslab"
        minSdk = 23
        targetSdk = 34
        versionCode = 10
        versionName = "7.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
        vectorDrawables {
            useSupportLibrary = true
        }
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    kotlin {
        jvmToolchain(11)
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}


dependencies {
    val versions = object {
        val appCompat = "1.6.1"
        val constraintLayout = "2.1.4"
        val coreKTX = "1.12.0"
        val espressoCore = "3.5.1"
        val firebaseBom = "31.2.3"
        val junit = "4.13.2"
        val material = "1.11.0"
        val moshi = "1.15.0"
        val multidex = "2.0.1"
        val navigation = rootProject.extra["navigation"] as String
        val retrofit = "2.9.0"
        val room = "2.6.1"
        val swipeRefreshLayout = "1.1.0"
        val testRunner = "1.5.2"
    }

    implementation(platform("com.google.firebase:firebase-bom:${versions.firebaseBom}"))
    implementation(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))

    implementation("androidx.core:core-ktx:${versions.coreKTX}")
    implementation("androidx.appcompat:appcompat:${versions.appCompat}")
    implementation("androidx.constraintlayout:constraintlayout:${versions.constraintLayout}")
    implementation("com.google.android.material:material:${versions.material}")
    implementation("androidx.multidex:multidex:${versions.multidex}")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:${versions.navigation}")
    implementation("androidx.navigation:navigation-ui-ktx:${versions.navigation}")

    implementation("androidx.room:room-runtime:${versions.room}")
    ksp("androidx.room:room-compiler:${versions.room}")

    ksp("com.squareup.moshi:moshi-kotlin-codegen:${versions.moshi}")
    implementation("com.squareup.moshi:moshi:${versions.moshi}")

    // https://mvnrepository.com/artifact/androidx.swiperefreshlayout/swiperefreshlayout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:${versions.swipeRefreshLayout}")

    // Firebase SDK
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:${versions.retrofit}")
    implementation("com.squareup.retrofit2:converter-moshi:${versions.retrofit}") {
        exclude(group = "com.squareup.moshi", module = "moshi")
    }
    implementation("com.squareup.retrofit2:converter-scalars:${versions.retrofit}")

    testImplementation("junit:junit:${versions.junit}")
    androidTestImplementation("androidx.test:runner:${versions.testRunner}")
    androidTestImplementation("androidx.test.espresso:espresso-core:${versions.espressoCore}")
}

apply {
    plugin("com.google.gms.google-services")
    plugin("org.jetbrains.kotlin.android")
}
