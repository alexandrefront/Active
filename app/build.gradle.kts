plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.radio.radioactive"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.radio.radioactive"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("androidx.media:media:1.6.0")
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("androidx.core:core:1.12.0")
    implementation ("androidx.core:core-ktx:1.12.0")
    implementation ("com.google.android.material:material:1.10.0")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
}
