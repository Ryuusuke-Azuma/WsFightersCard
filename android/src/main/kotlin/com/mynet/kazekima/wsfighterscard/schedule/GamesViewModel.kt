/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.schedule

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mynet.kazekima.wsfighterscard.R
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

    private suspend fun loadGamesForDate(date: LocalDate) {
        currentDate = date
        val millis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val displayItems = withContext(Dispatchers.IO) {
            val dailyGames = repository.getGamesByDate(millis)
            dailyGames.map { game ->
                val scores = repository.getScoresForGame(game.id)
                val wins: Int
                val losses: Int

                if (game.game_style == GameStyle.TEAMS) {
                    wins = scores.count { it.team_win_lose?.winLose == WinLose.WIN }
                    losses = scores.count { it.team_win_lose?.winLose == WinLose.LOSE }
                } else {
                    wins = scores.count { it.win_lose == WinLose.WIN }
                    losses = scores.count { it.win_lose == WinLose.LOSE }
                }

                GameDisplayItem(
                    game = game,
                    winCount = wins,
                    lossCount = losses
                )
            }
        }
        _games.value = displayItems
    }

    fun loadInitialGamesForDate(date: LocalDate) {
        viewModelScope.launch {
            loadGamesForDate(date)
            selectGame(_games.value?.firstOrNull())
        }
    }

    fun addGame(name: String, date: Long, style: GameStyle, memo: String) {
        viewModelScope.launch {
            val newGameId = withContext(Dispatchers.IO) {
                repository.addGame(name, date, style, memo)
            }
            currentDate?.let { loadGamesForDate(it) }
            val newItem = _games.value?.find { it.game.id == newGameId }
            selectGame(newItem)
        }
    }

    fun updateGame(id: Long, name: String, date: Long, style: GameStyle, memo: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.updateGame(id, name, date, style, memo)
            }
            currentDate?.let { loadGamesForDate(it) }
            val updatedItem = _games.value?.find { it.game.id == id }
            selectGame(updatedItem)
        }
    }

    fun deleteGame(id: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.deleteGame(id)
            }
            currentDate?.let { loadGamesForDate(it) }
            selectGame(_games.value?.firstOrNull())
        }
    }

    fun selectGame(item: GameDisplayItem?) {
        _selectedGame.value = item
    }

    suspend fun getShareText(context: Context, item: GameDisplayItem): String = withContext(Dispatchers.IO) {
        val scores = repository.getScoresForGame(item.game.id)
        
        val sb = StringBuilder()
        sb.append(item.game.game_name).append("\n")
        
        val usedDecks = scores.map { it.battle_deck }.distinct()
        if (usedDecks.isNotEmpty()) {
            sb.append(context.getString(R.string.schedule_hint_battle_deck)).append(": ")
            sb.append(usedDecks.joinToString(", ")).append("\n")
        }
        sb.append("\n")

        scores.forEach { score ->
            val resultMark = if (score.win_lose == WinLose.WIN) "○" else "×"
            sb.append(score.matching_deck).append(" ").append(resultMark).append("\n")
        }

        sb.toString()
    }
}
