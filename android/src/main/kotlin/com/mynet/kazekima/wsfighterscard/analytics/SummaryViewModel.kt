/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.analytics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mynet.kazekima.wsfighterscard.analytics.models.DetailedWinLose
import com.mynet.kazekima.wsfighterscard.db.DatabaseDriverFactory
import com.mynet.kazekima.wsfighterscard.db.FightersRepository
import com.mynet.kazekima.wsfighterscard.db.Game
import com.mynet.kazekima.wsfighterscard.db.Score
import com.mynet.kazekima.wsfighterscard.db.enums.GameStyle
import com.mynet.kazekima.wsfighterscard.db.enums.WinLose
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId

class SummaryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FightersRepository(DatabaseDriverFactory(application))

    private val _totalGameCount = MutableLiveData<Int>()
    val totalGameCount: LiveData<Int> = _totalGameCount

    private val _individualWinLose = MutableLiveData<Pair<Int, Int>>()
    val individualWinLose: LiveData<Pair<Int, Int>> = _individualWinLose

    private val _detailedWinLose = MutableLiveData<DetailedWinLose>()
    val detailedWinLose: LiveData<DetailedWinLose> = _detailedWinLose

    private val _teamsWinLose = MutableLiveData<Pair<Int, Int>>()
    val teamsWinLose: LiveData<Pair<Int, Int>> = _teamsWinLose

    fun loadSummaryStats(startDate: LocalDate, endDate: LocalDate) {
        viewModelScope.launch(Dispatchers.IO) {
            val startMillis = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val endMillis = endDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

            val allGames = repository.getAllGames()
            val allScores = repository.getAllScores()

            val filteredGames = allGames.filter { it.game_date in startMillis..endMillis }
            val filteredGameIds = filteredGames.map { it.id }.toSet()
            val filteredScores = allScores.filter { filteredGameIds.contains(it.game_id) }

            val individualStats = calculateIndividualStats(filteredScores, filteredGames)
            val teamStats = calculateTeamStats(filteredScores)

            withContext(Dispatchers.Main) {
                _totalGameCount.value = filteredGames.size
                _individualWinLose.value = individualStats.first
                _detailedWinLose.value = individualStats.second
                _teamsWinLose.value = teamStats
            }
        }
    }

    private fun calculateIndividualStats(scores: List<Score>, games: List<Game>): Pair<Pair<Int, Int>, DetailedWinLose> {
        var sWins = 0
        var sLosses = 0
        var tpWins = 0
        var tpLosses = 0

        scores.forEach { score ->
            val game = games.find { it.id == score.game_id }
            if (game?.game_style == GameStyle.SINGLES) {
                if (score.win_lose == WinLose.WIN) sWins++ else sLosses++
            } else if (game?.game_style == GameStyle.TEAMS) {
                if (score.win_lose == WinLose.WIN) tpWins++ else tpLosses++
            }
        }

        val totalWins = sWins + tpWins
        val totalLosses = sLosses + tpLosses
        return Pair(Pair(totalWins, totalLosses), DetailedWinLose(sWins, tpWins, sLosses, tpLosses))
    }

    private fun calculateTeamStats(scores: List<Score>): Pair<Int, Int> {
        val wins = scores.count { it.team_win_lose?.winLose == WinLose.WIN }
        val losses = scores.count { it.team_win_lose?.winLose == WinLose.LOSE }
        return Pair(wins, losses)
    }
}
