plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
    // Dagger Hilt
    id("dagger.hilt.android.plugin")
    // KSP
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.talkies"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.talkies"
        minSdk = 26
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.database.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation ("com.google.android.gms:play-services-auth:20.6.0")

    // Add the dependency for the Firebase Authentication library
    implementation("com.google.firebase:firebase-auth-ktx")

    //dagger-hilt
    val hiltVersion = "2.48"
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    implementation("com.google.dagger:hilt-android-gradle-plugin:$hiltVersion")
    ksp("com.google.dagger:hilt-compiler:$hiltVersion")
    ksp("androidx.hilt:hilt-compiler:1.1.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // coroutines
    val coroutineVersion = "1.3.9"
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${coroutineVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${coroutineVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:${coroutineVersion}")

    //coroutine lifecycle scopes
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")

    //retrofit & json converter
    val retrofitVersion = "2.9.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")

    //OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // okHttp Dependency
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    //coil
    implementation("io.coil-kt:coil-compose:2.5.0")

    //ZWing
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")

    implementation ("androidx.media3:media3-exoplayer:1.2.0")
    implementation ("androidx.media3:media3-ui:1.2.0")

    implementation ("com.google.accompanist:accompanist-swiperefresh:0.31.3-beta")

    implementation ("io.github.vanpra.compose-material-dialogs:datetime:0.9.0")

    implementation ("androidx.core:core-splashscreen:1.0.1")
    implementation ("androidx.compose.material:material-icons-extended:1.5.4")
    implementation ("androidx.datastore:datastore-preferences:1.0.0")


    implementation ("androidx.navigation:navigation-compose:2.7.7") // Use the latest version
    implementation ("androidx.compose.animation:animation:1.6.3")
    implementation("com.google.firebase:firebase-appcheck-playintegrity")
    implementation ("com.google.firebase:firebase-auth:22.3.0")
    implementation ("com.google.android.gms:play-services-safetynet:18.0.1")
}