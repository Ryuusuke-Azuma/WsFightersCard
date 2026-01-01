/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

import org.gradle.api.JavaVersion

object Config {
    const val compileSdk = 35
    const val targetSdk = 35
    const val minSdk = 24
    
    val javaVersion = JavaVersion.VERSION_11
    const val jvmTarget = "11"
}
