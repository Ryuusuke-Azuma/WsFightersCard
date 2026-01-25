/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.analytics.models

data class StrengthStat(
    val deckName: String,
    val totalGames: Int,
    val winCount: Int,
    val lossCount: Int,
    val winRate: Double
)
