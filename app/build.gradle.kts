plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("io.gitlab.arturbosch.detekt") version "1.23.4"
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.24"
}

android {
    namespace = "com.emenjivar.luminar"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.emenjivar.luminar"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val cameraxVersion = "1.3.3"
    val livedataVersion = "1.6.7"
    val hiltVersion = "2.51.1"
    val hiltViewModelVersion = "1.2.0"
    val composeUI = "1.6.7"
    val material3 = "1.2.1"
    val navigation = "2.8.0-alpha08"

    // Android
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // DI
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    kapt("com.google.dagger:hilt-compiler:$hiltVersion")
    implementation("androidx.hilt:hilt-navigation-compose:$hiltViewModelVersion")

    // Compose
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation(platform("androidx.compose:compose-bom:2024.05.00"))
    implementation("androidx.compose.ui:ui:$composeUI")
    implementation("androidx.compose.ui:ui-graphics:$composeUI")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeUI")
    implementation("androidx.compose.runtime:runtime-livedata:$livedataVersion")
    implementation("androidx.navigation:navigation-compose:$navigation")

    // Material
    implementation("androidx.compose.material3:material3:$material3")

    // CameraX
    implementation("androidx.camera:camera-core:${cameraxVersion}")
    implementation("androidx.camera:camera-camera2:${cameraxVersion}")
    implementation("androidx.camera:camera-lifecycle:${cameraxVersion}")
    implementation("androidx.camera:camera-view:${cameraxVersion}")
    implementation("androidx.camera:camera-extensions:${cameraxVersion}")

    // Graphics
    implementation("androidx.graphics:graphics-shapes:1.0.0-beta01")

    // Accompanist
    implementation("com.google.accompanist:accompanist-permissions:0.32.0")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.05.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // OpenCV module
    implementation(project(":opencv"))
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom("$projectDir/config/detekt.yml")
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}