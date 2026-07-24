plugins {
    id("com.android.application")
    kotlin("plugin.serialization")
}

extensions.configure<com.android.build.api.dsl.ApplicationExtension> {
    namespace = "com.mynet.kazekima.wsfighterscard"
    compileSdk = Config.COMPILE_SDK

    defaultConfig {
        applicationId = "com.mynet.kazekima.wsfighterscard"
        minSdk = Config.MIN_SDK
        targetSdk = Config.TARGET_SDK
        versionCode = 1_9
        versionName = "1.9"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = Config.JAVA_VERSION
        targetCompatibility = Config.JAVA_VERSION
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.fromTarget(Config.JVM_TARGET))
    }
}

dependencies {
    implementation(project(":database"))

    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.14.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    
    // MPAndroidChart
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // Preference Library
    implementation("androidx.preference:preference-ktx:1.2.1")

    // Calendar Library
    implementation("com.github.mhiew:material-calendarview:2.0.3")
    
    // Lifecycle components
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.11.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.11.0")
    
    // KTX Extensions
    implementation("androidx.activity:activity-ktx:1.13.0")
    implementation("androidx.fragment:fragment-ktx:1.8.9")

    // Kotlinx Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")

    // Unit Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.14.11")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.11.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test:2.4.10")

    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
}
