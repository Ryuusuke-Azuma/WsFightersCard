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
import com.mynet.kazekima.wsfighterscard.db.enums.TeamResult
import com.mynet.kazekima.wsfighterscard.db.enums.TeamWinLose
import com.mynet.kazekima.wsfighterscard.db.enums.WinLose
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FightersRepository(DatabaseDriverFactory(application))
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")

    fun importFromStream(inputStream: InputStream, onComplete: (count: Int) -> Unit) {
        viewModelScope.launch {
            var importCount = 0
            withContext(Dispatchers.IO) {
                var lastGameId: Long? = null
                inputStream.bufferedReader().use { reader ->
                    reader.lineSequence().forEach { row ->
                        if (row.isNotBlank()) {
                            lastGameId = processImportRow(row, lastGameId)?.also {
                                if (row.trim().uppercase().startsWith("GAME")) importCount++
                            } ?: lastGameId
                        }
                    }
                }
            }
            onComplete(importCount)
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
        val teamResultString = if (columns.size >= 5) columns[4].trim() else ""
        val teamWinLoseString = if (columns.size >= 6) columns[5].trim().uppercase() else ""
        val memo = if (columns.size >= 7) columns[6].trim() else ""

        runCatching {
            val winLose = if (winLoseString == "WIN") WinLose.WIN else WinLose.LOSE
            
            val teamResult = if (teamResultString.isNotBlank()) {
                runCatching { TeamResult.valueOf("WIN_${teamResultString.replace("-", "_")}") }
                    .getOrNull() ?: runCatching { TeamResult.valueOf("LOSE_${teamResultString.replace("-", "_")}") }
                    .getOrNull()
            } else null

            val teamWinLose = if (teamWinLoseString == "WIN") TeamWinLose.WIN 
                             else if (teamWinLoseString == "LOSE") TeamWinLose.LOSE 
                             else null
            
            repository.addScore(gameId, battleDeck, matchingDeck, winLose, teamResult, teamWinLose, memo)
        }
    }
}
