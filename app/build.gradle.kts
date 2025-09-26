import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp") // KSP para Room
}

val room_version = "2.8.0"        // <-- define la versión que estás usando
val lifecycle_version = "2.8.6"   // estable actual
val okhttp_version = "4.12.0"     // alinea con logging-interceptor
val retrofit_version = "2.11.0"   // última estable

val newsApiKey: String = providers.gradleProperty("NEWS_API_KEY").orNull
    ?: gradleLocalProperties(rootDir, providers).getProperty("NEWS_API_KEY")
    ?: System.getenv("NEWS_API_KEY")
    ?: error("NEWS_API_KEY no definido (gradle.properties / local.properties / env)")

android {
    namespace = "com.example.lagvis_v1"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.lagvis_v1"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "API_KEY_NEWS", "\"$newsApiKey\"")
        buildConfigField("String", "NEWS_BASE_URL", "\"https://newsdata.io/api/1/\"")
        buildConfigField("String", "HOLIDAYS_BASE_URL", "\"https://calendario-laboral-api.onrender.com/\"")
        // Asegúrate de terminar en "/" para Retrofit
        buildConfigField("String", "BACKEND_BASE_URL", "\"http://83.33.97.100/lagvis-endpoints/\"")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions { jvmTarget = "11" }

    buildFeatures {
        viewBinding = true
        buildConfig = true
        compose = true
    }
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Firebase
    implementation(libs.firebase.auth)
    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)

    // Volley (si aún lo usas)
    implementation(libs.volley)

    // --- ROOM (KSP) ---
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    ksp("androidx.room:room-compiler:$room_version")

    implementation("androidx.credentials:credentials:1.3.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.3.0")

    // Google Identity (esto es lo que te falta para GoogleIdCredential)
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    // (Opcional pero útil) Play Services Auth si lo usas en otros flujos
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    implementation("androidx.credentials:credentials:1.3.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1") // <- define GoogleIdCredential
    implementation("com.google.firebase:firebase-auth:23.0.0") // si usas Firebase

    // --- Retrofit + OkHttp coherentes ---
    implementation("com.squareup.okhttp3:okhttp:$okhttp_version")
    debugImplementation("com.squareup.okhttp3:logging-interceptor:$okhttp_version")

    implementation("com.squareup.retrofit2:retrofit:$retrofit_version")
    implementation("com.squareup.retrofit2:converter-gson:$retrofit_version")

    // Gson (si lo usas explícitamente)
    implementation("com.google.code.gson:gson:2.10.1")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Recycler / CardView
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("androidx.cardview:cardview:1.0.0")

    // Lifecycle Compose (usa versiones válidas)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycle_version")
    implementation("androidx.compose.runtime:runtime-livedata")

    // Compose
    implementation(platform("androidx.compose:compose-bom:2024.09.01"))

    // --- Compose core (sin versión, la pone el BOM) ---
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.activity:activity-compose:1.9.2") // setContent

    // Calendario
    implementation("com.kizitonwose.calendar:view:2.8.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
}
