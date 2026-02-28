/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.settings.models

import kotlinx.serialization.Serializable

@Serializable
data class GameExportDto(
    val name: String,
    val date: String,
    val style: String,
    val memo: String,
    val scores: List<ScoreExportDto>
)

@Serializable
data class ScoreExportDto(
    val battleDeck: String,
    val matchingDeck: String,
    val firstSecond: String,
    val winLose: String,
    val teamWinLose: String?,
    val memo: String
)

@Serializable
data class FighterExportDto(
    val name: String,
    val isSelf: Boolean,
    val memo: String,
    val decks: List<DeckExportDto>
)

@Serializable
data class DeckExportDto(
    val name: String,
    val memo: String
)

@Serializable
data class AppDataExportDto(
    val games: List<GameExportDto>? = null,
    val fighters: List<FighterExportDto>? = null
)
