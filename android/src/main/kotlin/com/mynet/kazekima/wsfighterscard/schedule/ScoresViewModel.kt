/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.schedule

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mynet.kazekima.wsfighterscard.db.DatabaseDriverFactory
import com.mynet.kazekima.wsfighterscard.db.FightersRepository
import com.mynet.kazekima.wsfighterscard.db.Score
import com.mynet.kazekima.wsfighterscard.db.enums.TeamWinLose
import com.mynet.kazekima.wsfighterscard.db.enums.WinLose
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScoresViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FightersRepository(DatabaseDriverFactory(application))

    private val _scores = MutableLiveData<List<Score>>()
    val scores: LiveData<List<Score>> = _scores

    private var currentGameId: Long? = null

    fun loadScores(gameId: Long) {
        currentGameId = gameId
        viewModelScope.launch {
            val gameScores = withContext(Dispatchers.IO) {
                repository.getScoresForGame(gameId)
            }
            _scores.value = gameScores
        }
    }

    fun addScore(gameId: Long, battleDeck: String, matchingDeck: String, winLose: WinLose, teamWinLose: TeamWinLose?, memo: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.addScore(gameId, battleDeck, matchingDeck, winLose, teamWinLose, memo)
            }
            loadScores(gameId)
        }
    }

    fun updateScore(id: Long, battleDeck: String, matchingDeck: String, winLose: WinLose, teamWinLose: TeamWinLose?, memo: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.updateScore(id, battleDeck, matchingDeck, winLose, teamWinLose, memo)
            }
            currentGameId?.let { loadScores(it) }
        }
    }

    fun deleteScore(id: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.deleteScore(id)
            }
            currentGameId?.let { loadScores(it) }
        }
    }
}
