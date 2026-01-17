/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.analytics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mynet.kazekima.wsfighterscard.analytics.models.DeckStat
import com.mynet.kazekima.wsfighterscard.db.DatabaseDriverFactory
import com.mynet.kazekima.wsfighterscard.db.FightersRepository
import com.mynet.kazekima.wsfighterscard.db.Score
import com.mynet.kazekima.wsfighterscard.db.enums.WinLose
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId

class DecksViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FightersRepository(DatabaseDriverFactory(application))

    private val _deckStats = MutableLiveData<List<DeckStat>>()
    val deckStats: LiveData<List<DeckStat>> = _deckStats

    fun loadDeckStats(startDate: LocalDate, endDate: LocalDate) {
        viewModelScope.launch(Dispatchers.IO) {
            val startMillis = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val endMillis = endDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

            val allGames = repository.getAllGames().filter { it.game_date in startMillis..endMillis }
            val gameIds = allGames.map { it.id }.toSet()
            val scores = repository.getAllScores().filter { gameIds.contains(it.game_id) }

            val stats = calculateDeckStats(scores)

            withContext(Dispatchers.Main) {
                _deckStats.value = stats
            }
        }
    }

    private fun calculateDeckStats(scores: List<Score>): List<DeckStat> {
        return scores.groupBy { it.battle_deck }.map { (name, scoreList) ->
            val total = scoreList.size
            val wins = scoreList.count { it.win_lose == WinLose.WIN }
            val winRate = if (total > 0) (wins.toFloat() / total * 100) else 0f
            DeckStat(name, total, wins, total - wins, winRate)
        }.sortedByDescending { it.winRate }
    }
}
