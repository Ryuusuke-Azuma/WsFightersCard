/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

import org.gradle.api.JavaVersion

object Config {
    const val COMPILE_SDK = 37
    const val TARGET_SDK = 37
    const val MIN_SDK = 26
    
    val JAVA_VERSION = JavaVersion.VERSION_21
    const val JVM_TARGET = "21"
}
