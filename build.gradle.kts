/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.13.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
        // Compose Multiplatform plugin
        classpath("org.jetbrains.compose:compose-gradle-plugin:1.6.0")
        // SQLDelight plugin
        classpath("app.cash.sqldelight:gradle-plugin:2.0.1")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
