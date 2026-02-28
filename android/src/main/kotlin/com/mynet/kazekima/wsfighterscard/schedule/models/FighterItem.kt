/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.schedule.models

import kotlinx.serialization.Serializable

@Serializable
data class FighterItem(
    val id: Long,
    val name: String
)
