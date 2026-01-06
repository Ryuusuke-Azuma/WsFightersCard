/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.schedule.models

import com.mynet.kazekima.wsfighterscard.db.Game

data class GameDisplayItem(
    val game: Game,
    val winCount: Int,
    val lossCount: Int
)
