plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("kapt") // ✅ Necesario para Room
}

android {
    namespace = "com.example.nuevasdivisas"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.nuevasdivisas"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17 // 🔥 Mejor usar Java 17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17" // 🔥 Asegurar compatibilidad con Room y Kotlin
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    // ✅ AndroidX Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // ✅ Jetpack Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // ✅ Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // ✅ Room Database (SQLite) - 🔥 Versión actualizada
    implementation("androidx.room:room-runtime:2.6.1") // 🔥 Usa la última versión estable
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // ✅ WorkManager (Para tareas en segundo plano) - 🔥 Versión estable
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // ✅ Retrofit (Para consumir API REST)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // ✅ Gson (Para convertir JSON a Kotlin)
    implementation("com.google.code.gson:gson:2.10.1")

    // ✅ Corrutinas (Necesarias para Room y Retrofit)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}
