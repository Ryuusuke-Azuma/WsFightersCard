plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.mynet.kazekima.wsfighterscard"
    compileSdk = Config.COMPILE_SDK

    defaultConfig {
        applicationId = "com.mynet.kazekima.wsfighterscard"
        minSdk = Config.MIN_SDK
        targetSdk = Config.TARGET_SDK
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = Config.JAVA_VERSION
        targetCompatibility = Config.JAVA_VERSION
    }

    kotlinOptions {
        jvmTarget = Config.JVM_TARGET
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(project(":database"))

    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.13.0-rc01")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    
    // Preference Library
    implementation("androidx.preference:preference-ktx:1.2.1")
    
    // Lifecycle components
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    
    // KTX Extensions
    implementation("androidx.activity:activity-ktx:1.9.3")
    implementation("androidx.fragment:fragment-ktx:1.8.5")

    // Unit Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.13.13")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test:2.0.21")

    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
