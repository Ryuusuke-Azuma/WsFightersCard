/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.shared

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
