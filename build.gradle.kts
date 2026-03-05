/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:9.1.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.2.10")
        classpath("org.jetbrains.kotlin:kotlin-serialization:2.2.10")
        classpath("org.jetbrains.compose:compose-gradle-plugin:1.10.1")
        classpath("app.cash.sqldelight:gradle-plugin:2.2.1")
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
