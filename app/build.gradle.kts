import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

val newsApiKey: String = providers.gradleProperty("NEWS_API_KEY").orNull
    ?: gradleLocalProperties(rootDir, providers).getProperty("NEWS_API_KEY")
    ?: System.getenv("NEWS_API_KEY")
    ?: error("NEWS_API_KEY no definido (gradle.properties / local.properties / env)")

val geminiApiKey: String = providers.gradleProperty("GEMINI").orNull
    ?: gradleLocalProperties(rootDir, providers).getProperty("GEMINI")
    ?: System.getenv("GEMINI")
    ?: error("GEMINI no definido (gradle.properties / local.properties / env)")


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
        buildConfigField("String", "BACKEND_BASE_URL", "\"http://lagvis.es//lagvis-endpoints/\"")
        buildConfigField("String", "CONVENIOS_BASE_URL", "\"http://lagvis.es//lagvis-convenios/\"")
        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKey\"")
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
    // --- Base AndroidX / Material ---
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.material3)

    // --- Firebase / Auth / Credentials ---
    implementation(libs.firebase.auth)
    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.play.services.auth)

    // --- Networking: Volley (si aún lo usas) ---
    implementation(libs.volley)

    // --- OkHttp + Retrofit (coherentes) ---
    implementation(libs.okhttp)
    debugImplementation(libs.okhttp.logging.interceptor)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.gson)
    implementation(libs.gson)

    // --- Room (KSP) ---
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // --- Glide / UI clásicos ---
    implementation(libs.glide)
    implementation(libs.recyclerview)
    implementation(libs.cardview)

    // --- Jetpack Compose (BOM) ---
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.material.icons.extended)
    implementation(libs.activity.compose)
    androidTestImplementation(platform(libs.compose.bom))
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
    androidTestImplementation(libs.ui.test.junit4)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.compose.runtime.livedata)

    // --- Navigation (Compose/Runtime) ---
    implementation(libs.navigation.compose)
    implementation(libs.navigation.runtime.ktx)

    // --- Material3 extra ---
    implementation(libs.material3.window.size.class1)

    // --- Composables externos / Accompanist ---
    implementation(libs.composables.core)
    implementation(libs.accompanist.drawablepainter)

    // --- Calendario ---
    implementation(libs.kizitonwose.calendar.view)
    implementation(libs.kizitonwose.calendar.compose)

    // --- Google AI (Gemini client) ---
    implementation(libs.generativeai)

    // --- Tests ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
