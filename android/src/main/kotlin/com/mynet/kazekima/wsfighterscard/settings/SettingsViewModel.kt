/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mynet.kazekima.wsfighterscard.db.DatabaseDriverFactory
import com.mynet.kazekima.wsfighterscard.db.FightersRepository
import com.mynet.kazekima.wsfighterscard.db.enums.GameStyle
import com.mynet.kazekima.wsfighterscard.db.enums.TeamWinLose
import com.mynet.kazekima.wsfighterscard.db.enums.WinLose
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FightersRepository(DatabaseDriverFactory(application))
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")

    fun importFromStream(inputStream: InputStream, onComplete: (count: Int) -> Unit) {
        viewModelScope.launch {
            val count = withContext(Dispatchers.IO) {
                var importCount = 0
                var lastGameId: Long? = null
                inputStream.bufferedReader().use { reader ->
                    reader.lineSequence().forEach { row ->
                        if (row.isBlank()) return@forEach
                        val newId = processImportRow(row, lastGameId)
                        if (newId != lastGameId && row.trim().uppercase().startsWith("GAME")) {
                            importCount++
                        }
                        lastGameId = newId
                    }
                }
                importCount
            }
            onComplete(count)
        }
    }

    fun exportToStream(outputStream: OutputStream, onComplete: () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val allGames = repository.getAllGames()
                val allScores = repository.getAllScores()

                outputStream.bufferedWriter().use { writer ->
                    allGames.forEach { game ->
                        val dateStr = Instant.ofEpochMilli(game.game_date).atZone(ZoneId.systemDefault()).toLocalDate().format(dateFormatter)
                        writer.write("GAME, ${game.game_name}, $dateStr, ${game.game_style.name}, ${game.memo}\n")

                        allScores.filter { it.game_id == game.id }.forEach { score ->
                            val teamResultStr = score.team_win_lose?.name ?: ""
                            writer.write("SCORE, ${score.battle_deck}, ${score.matching_deck}, ${score.win_lose.name}, $teamResultStr, ${score.memo}\n")
                        }
                    }
                }
            }
            onComplete()
        }
    }

    private fun processImportRow(row: String, lastGameId: Long?): Long? {
        val columns = row.split(",")
        val type = columns[0].trim().uppercase()

        return when (type) {
            "GAME" -> processGameRow(columns)
            "SCORE" -> {
                processScoreRow(columns, lastGameId)
                lastGameId
            }
            else -> lastGameId
        }
    }

    private fun processGameRow(columns: List<String>): Long? {
        if (columns.size < 4) return null
        
        val name = columns[1].trim()
        val dateString = columns[2].trim()
        val styleString = columns[3].trim().uppercase()
        val memo = if (columns.size >= 5) columns[4].trim() else ""

        return runCatching {
            val date = LocalDate.parse(dateString, dateFormatter)
            val millis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val style = if (styleString == "TEAMS") GameStyle.TEAMS else GameStyle.SINGLES
            
            repository.addGame(name, millis, style, memo)
            repository.lastInsertId()
        }.getOrNull()
    }

    private fun processScoreRow(columns: List<String>, gameId: Long?) {
        if (gameId == null || columns.size < 4) return

        val battleDeck = columns[1].trim()
        val matchingDeck = columns[2].trim()
        val winLoseString = columns[3].trim().uppercase()
        val teamWinLoseString = if (columns.size >= 5) columns[4].trim().uppercase() else ""
        val memo = if (columns.size >= 6) columns[5].trim() else ""

        runCatching {
            val winLose = if (winLoseString == "WIN") WinLose.WIN else WinLose.LOSE
            val teamWinLose = parseTeamWinLose(teamWinLoseString)
            repository.addScore(gameId, battleDeck, matchingDeck, winLose, teamWinLose, memo)
        }
    }

    private fun parseTeamWinLose(value: String): TeamWinLose? {
        if (value.isBlank()) return null
        return runCatching { TeamWinLose.valueOf(value) }.getOrNull()
            ?: runCatching { TeamWinLose.valueOf("WIN_${value.replace("-", "_")}") }.getOrNull()
            ?: runCatching { TeamWinLose.valueOf("LOSE_${value.replace("-", "_")}") }.getOrNull()
    }
}
