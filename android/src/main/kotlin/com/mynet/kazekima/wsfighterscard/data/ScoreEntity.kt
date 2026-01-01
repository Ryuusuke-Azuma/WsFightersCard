/*
 * Copyright (c) 2025 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.data

data class ScoreEntity(
    var scoreId: Int = 0,
    var gameId: String? = null,
    var matchingDeck: String? = null,
    var winOrLose: String? = null,
    var memo: String? = null
)
