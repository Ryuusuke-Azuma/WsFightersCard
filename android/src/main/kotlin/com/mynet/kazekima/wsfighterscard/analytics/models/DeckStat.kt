/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.analytics.models

data class DeckStat(
    val deckName: String,
    val totalGames: Int,
    val winCount: Int,
    val lossCount: Int,
    val winRate: Float
)
