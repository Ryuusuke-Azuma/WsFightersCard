/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

plugins {
    id("com.android.application") version "9.2.1" apply false
    id("com.android.library") version "9.2.1" apply false
    id("com.android.kotlin.multiplatform.library") version "9.2.1" apply false
    kotlin("android") version "2.4.10" apply false
    kotlin("multiplatform") version "2.4.10" apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:9.2.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.4.10")
        classpath("org.jetbrains.kotlin:kotlin-serialization:2.4.10")
        classpath("org.jetbrains.compose:compose-gradle-plugin:1.11.1")
        classpath("app.cash.sqldelight:gradle-plugin:2.3.2")
    }
}

tasks.register<Delete>("clean") {
    description = "Cleans the build directory."
    delete(rootProject.layout.buildDirectory)
}
