/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.13.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
        classpath("org.jetbrains.compose:compose-gradle-plugin:1.6.0")
        classpath("app.cash.sqldelight:gradle-plugin:2.0.1")
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
