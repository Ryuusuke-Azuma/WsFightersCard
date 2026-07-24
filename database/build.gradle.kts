/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

plugins {
    kotlin("multiplatform")
    id("com.android.kotlin.multiplatform.library")
    id("app.cash.sqldelight")
}

kotlin {
    jvmToolchain(21)

    android {
        namespace = "com.mynet.kazekima.wsfighterscard.database"
        compileSdk = Config.COMPILE_SDK
        minSdk = Config.MIN_SDK
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = "database"
            isStatic = true
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":shared"))
                // SQLDelight common runtime
                implementation("app.cash.sqldelight:runtime:2.3.2")
            }
        }
        
        androidMain {
            dependencies {
                // SQLDelight Android driver
                implementation("app.cash.sqldelight:android-driver:2.3.2")
            }
        }
        
        iosMain {
            dependencies {
                // SQLDelight Native driver (iOS)
                implementation("app.cash.sqldelight:native-driver:2.3.2")
            }
        }
    }
}

sqldelight {
    databases {
        create("FightersDatabase") {
            packageName.set("com.mynet.kazekima.wsfighterscard.db")
        }
    }
}
