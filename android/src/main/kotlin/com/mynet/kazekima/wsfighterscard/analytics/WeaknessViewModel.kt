/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.analytics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mynet.kazekima.wsfighterscard.analytics.models.OpponentLossStat
import com.mynet.kazekima.wsfighterscard.db.DatabaseDriverFactory
import com.mynet.kazekima.wsfighterscard.db.FightersRepository
import com.mynet.kazekima.wsfighterscard.db.Score
import com.mynet.kazekima.wsfighterscard.db.enums.WinLose
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId

class WeaknessViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FightersRepository(DatabaseDriverFactory(application))

    private val _opponentLossStats = MutableLiveData<List<OpponentLossStat>>()
    val opponentLossStats: LiveData<List<OpponentLossStat>> = _opponentLossStats

    fun loadOpponentStats(startDate: LocalDate, endDate: LocalDate) {
        viewModelScope.launch(Dispatchers.IO) {
            val startMillis = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val endMillis = endDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

            val allGames = repository.getAllGames().filter { it.game_date in startMillis..endMillis }
            val gameIds = allGames.map { it.id }.toSet()
            val scores = repository.getAllScores().filter { gameIds.contains(it.game_id) }

            val stats = calculateOpponentStats(scores)

            withContext(Dispatchers.Main) {
                _opponentLossStats.value = stats
            }
        }
    }

    private fun calculateOpponentStats(scores: List<Score>): List<OpponentLossStat> {
        return scores.filter { it.win_lose == WinLose.LOSE }
            .groupBy { it.matching_deck }
            .map { (name, scoreList) -> OpponentLossStat(name, scoreList.size) }
            .sortedByDescending { it.lossCount }
            .take(10)
    }
}
