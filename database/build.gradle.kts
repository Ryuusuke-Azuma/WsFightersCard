/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("app.cash.sqldelight")
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.fromTarget(Config.JVM_TARGET))
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "database"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":shared"))
                // SQLDelight common runtime
                implementation("app.cash.sqldelight:runtime:2.0.1")
            }
        }
        val androidMain by getting {
            dependencies {
                // SQLDelight Android driver
                implementation("app.cash.sqldelight:android-driver:2.0.1")
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            
            dependencies {
                // SQLDelight Native driver (iOS)
                implementation("app.cash.sqldelight:native-driver:2.0.1")
            }
        }
    }
}

android {
    namespace = "com.mynet.kazekima.wsfighterscard.database"
    compileSdk = Config.COMPILE_SDK
    defaultConfig {
        minSdk = Config.MIN_SDK
    }
    compileOptions {
        sourceCompatibility = Config.JAVA_VERSION
        targetCompatibility = Config.JAVA_VERSION
    }
}

sqldelight {
    databases {
        create("FightersDatabase") {
            packageName.set("com.mynet.kazekima.wsfighterscard.db")
        }
    }
}
