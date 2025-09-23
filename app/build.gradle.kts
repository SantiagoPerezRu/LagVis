import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

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
        buildConfigField("String", "BACKEND_BASE_URL", "\"https://tu-backend.prod/\"")
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

    buildFeatures {
        viewBinding = true
        buildConfig = true
        compose = true
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)                      // Material Components (una sola vez)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    implementation(libs.firebase.auth)
    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)

    implementation(libs.volley)

    // ⚠️ En Android no suele usarse el conector MySQL de servidor:
    // implementation("mysql:mysql-connector-java:5.1.49")

    implementation("com.google.code.gson:gson:2.13.1")
    implementation("com.squareup.okhttp3:okhttp:5.1.0")

    // Retrofit + Gson
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")
    // (Si quieres lo último: 2.11.0 para ambos)

    implementation("com.github.bumptech.glide:glide:4.16.0")

    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("androidx.cardview:cardview:1.0.0")

    // Calendario con decoradores
    implementation("com.kizitonwose.calendar:view:2.8.0")
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
}
