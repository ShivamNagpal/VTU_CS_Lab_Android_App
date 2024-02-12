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
    val appCompatVersion = "1.6.1"
    val constraintLayoutVersion = "2.1.4"
    val coreKTXVersion = "1.12.0"
    val espressoCoreVersion = "3.5.1"
    val firebaseBomVersion = "31.2.3"
    val junitVersion = "4.13.2"
    val materialVersion = "1.11.0"
    val moshiVersion = "1.15.0"
    val multidexVersion = "2.0.1"
    val navigationVersion = rootProject.extra["navigation"] as String
    val retrofitVersion = "2.9.0"
    val roomVersion = "2.6.1"
    val swipeRefreshLayoutVersion = "1.1.0"
    val testRunnerVersion = "1.5.2"

    implementation(platform("com.google.firebase:firebase-bom:$firebaseBomVersion"))
    implementation(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))

    implementation("androidx.core:core-ktx:$coreKTXVersion")
    implementation("androidx.appcompat:appcompat:$appCompatVersion")
    implementation("androidx.constraintlayout:constraintlayout:$constraintLayoutVersion")
    implementation("com.google.android.material:material:$materialVersion")
    implementation("androidx.multidex:multidex:$multidexVersion")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:$navigationVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navigationVersion")

    implementation("androidx.room:room-runtime:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    ksp("com.squareup.moshi:moshi-kotlin-codegen:$moshiVersion")
    implementation("com.squareup.moshi:moshi:$moshiVersion")

    // https://mvnrepository.com/artifact/androidx.swiperefreshlayout/swiperefreshlayout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:$swipeRefreshLayoutVersion")

    // Firebase SDK
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofitVersion") {
        exclude(group = "com.squareup.moshi", module = "moshi")
    }
    implementation("com.squareup.retrofit2:converter-scalars:$retrofitVersion")

    testImplementation("junit:junit:$junitVersion")
    androidTestImplementation("androidx.test:runner:$testRunnerVersion")
    androidTestImplementation("androidx.test.espresso:espresso-core:$espressoCoreVersion")
}

apply {
    plugin("com.google.gms.google-services")
    plugin("org.jetbrains.kotlin.android")
}
