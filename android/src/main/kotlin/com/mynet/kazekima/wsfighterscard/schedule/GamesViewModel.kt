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
import com.mynet.kazekima.wsfighterscard.db.enums.GameStyle
import com.mynet.kazekima.wsfighterscard.db.enums.WinLose
import com.mynet.kazekima.wsfighterscard.schedule.models.GameDisplayItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId

class GamesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FightersRepository(DatabaseDriverFactory(application))

    private val _games = MutableLiveData<List<GameDisplayItem>>()
    val games: LiveData<List<GameDisplayItem>> = _games

    private val _selectedGame = MutableLiveData<GameDisplayItem?>()
    val selectedGame: LiveData<GameDisplayItem?> = _selectedGame

    private var currentDate: LocalDate? = null

    fun loadGamesForDate(date: LocalDate) {
        currentDate = date
        viewModelScope.launch {
            val millis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

            val displayItems = withContext(Dispatchers.IO) {
                val dailyGames = repository.getGamesByDate(millis)
                dailyGames.map { game ->
                    val scores = repository.getScoresForGame(game.id)
                    GameDisplayItem(
                        game = game,
                        winCount = scores.count { it.win_lose == WinLose.WIN },
                        lossCount = scores.count { it.win_lose == WinLose.LOSE }
                    )
                }
            }
            _games.value = displayItems
        }
    }

    fun addGame(name: String, date: Long, style: GameStyle, memo: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.addGame(name, date, style, memo)
            }
            currentDate?.let { loadGamesForDate(it) }
        }
    }

    fun updateGame(id: Long, name: String, date: Long, style: GameStyle, memo: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.updateGame(id, name, date, style, memo)
            }
            currentDate?.let { loadGamesForDate(it) }
        }
    }

    fun deleteGame(id: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.deleteGame(id)
            }
            currentDate?.let { loadGamesForDate(it) }
        }
    }

    fun selectGame(item: GameDisplayItem?) {
        _selectedGame.value = item
    }
}
