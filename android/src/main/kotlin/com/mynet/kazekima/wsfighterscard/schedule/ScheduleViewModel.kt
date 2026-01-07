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
import com.mynet.kazekima.wsfighterscard.db.enums.GameStyle
import com.mynet.kazekima.wsfighterscard.db.enums.TeamWinLose
import com.mynet.kazekima.wsfighterscard.db.enums.WinLose
import com.mynet.kazekima.wsfighterscard.schedule.models.GameDisplayItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FightersRepository(DatabaseDriverFactory(application))
    
    private val _games = MutableLiveData<List<GameDisplayItem>>()
    val games: LiveData<List<GameDisplayItem>> = _games

    private val _selectedDate = MutableLiveData(LocalDate.now())
    val selectedDate: LiveData<LocalDate> = _selectedDate

    private val _markedDates = MutableLiveData<List<LocalDate>>()
    val markedDates: LiveData<List<LocalDate>> = _markedDates

    private val _selectedGame = MutableLiveData<GameDisplayItem?>()
    val selectedGame: LiveData<GameDisplayItem?> = _selectedGame

    private val _scores = MutableLiveData<List<Score>>()
    val scores: LiveData<List<Score>> = _scores

    fun loadData() {
        val date = _selectedDate.value ?: LocalDate.now()
        val millis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val dailyGames = repository.getGamesByDate(millis)
                val displayItems = dailyGames.map { game ->
                    val scores = repository.getScoresForGame(game.id)
                    GameDisplayItem(
                        game = game,
                        winCount = scores.count { it.win_lose == WinLose.WIN },
                        lossCount = scores.count { it.win_lose == WinLose.LOSE }
                    )
                }

                val allDates = repository.getGameDates().map {
                    Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                }

                withContext(Dispatchers.Main) {
                    _games.value = displayItems
                    _markedDates.value = allDates
                    
                    val currentId = _selectedGame.value?.game?.id
                    if (currentId != null) {
                        val updatedGame = displayItems.find { it.game.id == currentId }
                        if (updatedGame != null) {
                            _selectedGame.value = updatedGame
                            loadScores(currentId)
                        } else {
                            clearSelectedGame()
                        }
                    }
                }
            }
        }
    }

    fun selectGame(item: GameDisplayItem) {
        _selectedGame.value = item
        loadScores(item.game.id)
    }

    private fun loadScores(gameId: Long) {
        viewModelScope.launch {
            val s = withContext(Dispatchers.IO) {
                repository.getScoresForGame(gameId)
            }
            _scores.value = s
        }
    }

    fun deleteGame(id: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { repository.deleteGame(id) }
            loadData()
        }
    }

    fun deleteScore(scoreId: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { repository.deleteScore(scoreId) }
            loadData()
        }
    }

    fun updateGame(id: Long, name: String, date: LocalDate, style: GameStyle, memo: String) {
        val millis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.updateGame(id, name, millis, style, memo)
            }
            loadData()
        }
    }

    fun updateScore(id: Long, battleDeck: String, matchingDeck: String, winLose: WinLose, teamWinLose: TeamWinLose?, memo: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.updateScore(id, battleDeck, matchingDeck, winLose, teamWinLose, memo)
            }
            loadData()
        }
    }

    fun clearSelectedGame() {
        _selectedGame.value = null
        _scores.value = emptyList()
    }

    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
        loadData()
    }
}
