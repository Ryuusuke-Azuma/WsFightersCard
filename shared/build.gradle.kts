/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

plugins {
    id("com.android.kotlin.multiplatform.library")
    kotlin("multiplatform")
}

kotlin {
    jvmToolchain(21)

    android {
        namespace = "com.mynet.kazekima.wsfighterscard.shared"
        compileSdk = Config.COMPILE_SDK
        minSdk = Config.MIN_SDK
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                // Put your common dependencies here
            }
        }
        androidMain { }
        iosMain { }
    }
}
