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

    fun loadGamesForDate(date: LocalDate) {
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

    fun selectGame(item: GameDisplayItem?) {
        _selectedGame.value = item
    }
}
