/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.settings

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mynet.kazekima.wsfighterscard.db.DatabaseDriverFactory
import com.mynet.kazekima.wsfighterscard.db.FightersRepository
import com.mynet.kazekima.wsfighterscard.db.enums.FirstSecond
import com.mynet.kazekima.wsfighterscard.db.enums.GameStyle
import com.mynet.kazekima.wsfighterscard.db.enums.TeamWinLose
import com.mynet.kazekima.wsfighterscard.db.enums.WinLose
import com.mynet.kazekima.wsfighterscard.settings.models.AppDataExportDto
import com.mynet.kazekima.wsfighterscard.settings.models.DeckExportDto
import com.mynet.kazekima.wsfighterscard.settings.models.FighterExportDto
import com.mynet.kazekima.wsfighterscard.settings.models.GameExportDto
import com.mynet.kazekima.wsfighterscard.settings.models.ScoreExportDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FightersRepository(DatabaseDriverFactory(application))
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    // --- Import ---

    fun importAppDataFromJson(inputStream: InputStream, onComplete: (Int) -> Unit) {
        viewModelScope.launch {
            val count = withContext(Dispatchers.IO) {
                processImportJson(inputStream)
            }
            onComplete(count)
        }
    }

    fun importScheduleFromSample(context: Context, onComplete: (Int) -> Unit) {
        viewModelScope.launch {
            val count = withContext(Dispatchers.IO) {
                runCatching {
                    context.assets.open("sample_schedule.json").use { processImportJson(it) }
                }.getOrDefault(0)
            }
            onComplete(count)
        }
    }

    fun importProfileFromSample(context: Context, onComplete: (Int) -> Unit) {
        viewModelScope.launch {
            val count = withContext(Dispatchers.IO) {
                runCatching {
                    context.assets.open("sample_profile.json").use { processImportJson(it) }
                }.getOrDefault(0)
            }
            onComplete(count)
        }
    }

    private fun processImportJson(inputStream: InputStream): Int {
        return runCatching {
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val importData = json.decodeFromString<AppDataExportDto>(jsonString)
            var totalCount = 0

            val importingFighters = importData.fighters
            val hasSelfInImport = importingFighters?.any { it.isSelf } == true

            // Reset current 'self' ONLY IF the import data contains a new 'self'
            if (hasSelfInImport) {
                repository.setSelfFighter(-1L)
            }

            importingFighters?.forEach { fighterDto ->
                // Always add as new
                val fighterId = repository.addFighter(fighterDto.name, 0L, fighterDto.memo)

                // If isSelf is true, set it (this also resets others, including our -1L reset above)
                if (fighterDto.isSelf) {
                    repository.setSelfFighter(fighterId)
                }

                fighterDto.decks.forEach { deckDto ->
                    repository.addDeck(fighterId, deckDto.name, deckDto.memo)
                }
                totalCount++
            }

            importData.games?.forEach { gameDto ->
                val styleString = gameDto.style
                if (styleString.isBlank()) return@forEach

                val style = runCatching { GameStyle.valueOf(styleString) }.getOrNull() ?: return@forEach

                val date = LocalDate.parse(gameDto.date, dateFormatter)
                val millis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                val gameId = repository.addGame(gameDto.name, millis, style, gameDto.memo)

                gameDto.scores.forEach { scoreDto ->
                    val teamResult = scoreDto.teamWinLose?.let { runCatching { TeamWinLose.valueOf(it) }.getOrNull() }
                    
                    val isConsistent = when (style) {
                        GameStyle.TEAMS -> teamResult != null
                        GameStyle.SINGLES -> teamResult == null
                    }

                    if (isConsistent) {
                        repository.addScore(
                            gameId = gameId,
                            battleDeck = scoreDto.battleDeck,
                            matchingDeck = scoreDto.matchingDeck,
                            firstSecond = FirstSecond.valueOf(scoreDto.firstSecond),
                            winLose = WinLose.valueOf(scoreDto.winLose),
                            teamWinLose = teamResult,
                            memo = scoreDto.memo
                        )
                    }
                }
                totalCount++
            }
            totalCount
        }.getOrDefault(0)
    }

    // --- Export ---

    fun exportScheduleToJson(outputStream: OutputStream, onComplete: () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val games = repository.getAllGames().map { game ->
                    val scores = repository.getScoresForGame(game.id).map { score ->
                        ScoreExportDto(
                            battleDeck = score.battle_deck,
                            matchingDeck = score.matching_deck,
                            firstSecond = score.first_second.name,
                            winLose = score.win_lose.name,
                            teamWinLose = score.team_win_lose?.name,
                            memo = score.memo
                        )
                    }
                    val dateStr = Instant.ofEpochMilli(game.game_date).atZone(ZoneId.systemDefault()).toLocalDate().format(dateFormatter)
                    GameExportDto(game.game_name, dateStr, game.game_style.name, game.memo, scores)
                }

                val exportData = AppDataExportDto(games = games)
                val jsonString = json.encodeToString(exportData)
                outputStream.bufferedWriter().use { it.write(jsonString) }
            }
            onComplete()
        }
    }

    fun exportProfileToJson(outputStream: OutputStream, onComplete: () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val fighters = repository.getAllFighters().map { fighter ->
                    val decks = repository.getDecksByFighterId(fighter.id).map { deck ->
                        DeckExportDto(deck.deck_name, deck.memo)
                    }
                    FighterExportDto(fighter.name, fighter.is_self != 0L, fighter.memo, decks)
                }

                val exportData = AppDataExportDto(fighters = fighters)
                val jsonString = json.encodeToString(exportData)
                outputStream.bufferedWriter().use { it.write(jsonString) }
            }
            onComplete()
        }
    }

    // --- Maintenance ---

    fun clearAllData(onComplete: () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.clearAllData()
            }
            onComplete()
        }
    }
}
