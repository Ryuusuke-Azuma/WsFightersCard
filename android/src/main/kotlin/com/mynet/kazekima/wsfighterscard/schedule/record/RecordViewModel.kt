/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.schedule.record

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mynet.kazekima.wsfighterscard.db.DatabaseDriverFactory
import com.mynet.kazekima.wsfighterscard.db.FightersRepository
import com.mynet.kazekima.wsfighterscard.db.Score
import com.mynet.kazekima.wsfighterscard.db.enums.GameStyle
import com.mynet.kazekima.wsfighterscard.db.enums.TeamWinLose
import com.mynet.kazekima.wsfighterscard.db.enums.WinLose
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId

class RecordViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FightersRepository(DatabaseDriverFactory(application))

    fun addGame(name: String, date: LocalDate, style: GameStyle, memo: String, onComplete: () -> Unit) {
        val millis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.addGame(name, millis, style, memo)
            }
            onComplete()
        }
    }

    fun getScoresForGame(gameId: Long, onComplete: (List<Score>) -> Unit) {
        viewModelScope.launch {
            val scores = withContext(Dispatchers.IO) {
                repository.getScoresForGame(gameId)
            }
            onComplete(scores)
        }
    }

    fun addScore(
        gameId: Long, 
        battleDeck: String, 
        matchingDeck: String, 
        winLose: WinLose, 
        teamWinLose: TeamWinLose?, 
        memo: String, 
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.addScore(gameId, battleDeck, matchingDeck, winLose, teamWinLose, memo)
            }
            onComplete()
        }
    }

    fun deleteGame(id: Long, onComplete: () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.deleteGame(id)
            }
            onComplete()
        }
    }
}
