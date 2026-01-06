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
import com.mynet.kazekima.wsfighterscard.analytics.models.DetailedWinLose
import com.mynet.kazekima.wsfighterscard.analytics.models.OpponentLossStat
import com.mynet.kazekima.wsfighterscard.db.DatabaseDriverFactory
import com.mynet.kazekima.wsfighterscard.db.FightersRepository
import com.mynet.kazekima.wsfighterscard.db.enums.GameStyle
import com.mynet.kazekima.wsfighterscard.db.enums.WinLose
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId

class AnalyticsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FightersRepository(DatabaseDriverFactory(application))

    private val _startDate = MutableLiveData(LocalDate.now().withDayOfYear(1))
    val startDate: LiveData<LocalDate> = _startDate

    private val _endDate = MutableLiveData(LocalDate.now().withMonth(12).withDayOfMonth(31))
    val endDate: LiveData<LocalDate> = _endDate

    private val _totalGameCount = MutableLiveData<Int>()
    val totalGameCount: LiveData<Int> = _totalGameCount

    private val _individualWinLose = MutableLiveData<Pair<Int, Int>>()
    val individualWinLose: LiveData<Pair<Int, Int>> = _individualWinLose

    private val _detailedWinLose = MutableLiveData<DetailedWinLose>()
    val detailedWinLose: LiveData<DetailedWinLose> = _detailedWinLose

    private val _teamsWinLose = MutableLiveData<Pair<Int, Int>>()
    val teamsWinLose: LiveData<Pair<Int, Int>> = _teamsWinLose

    private val _deckStats = MutableLiveData<List<DeckStat>>()
    val deckStats: LiveData<List<DeckStat>> = _deckStats

    private val _opponentLossStats = MutableLiveData<List<OpponentLossStat>>()
    val opponentLossStats: LiveData<List<OpponentLossStat>> = _opponentLossStats

    fun setDateRange(start: LocalDate, end: LocalDate) {
        _startDate.value = start
        _endDate.value = end
        loadStats()
    }

    fun loadStats() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val startMillis = _startDate.value?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli() ?: 0L
                val endMillis = _endDate.value?.atTime(23, 59, 59)?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli() ?: Long.MAX_VALUE

                val allGames = repository.getAllGames()
                val allScores = repository.getAllScores()
                
                val filteredGames = allGames.filter { it.game_date in startMillis..endMillis }
                val filteredGameIds = filteredGames.map { it.id }.toSet()
                val filteredScores = allScores.filter { filteredGameIds.contains(it.game_id) }

                var sWins = 0; var sLosses = 0
                var tpWins = 0; var tpLosses = 0

                filteredScores.forEach { score ->
                    val game = filteredGames.find { it.id == score.game_id }
                    if (game?.game_style == GameStyle.SINGLES) {
                        if (score.win_lose == WinLose.WIN) sWins++ else sLosses++
                    } else if (game?.game_style == GameStyle.TEAMS) {
                        if (score.win_lose == WinLose.WIN) tpWins++ else tpLosses++
                    }
                }

                val teamsWins = filteredScores.count { it.team_win_lose?.winLose == WinLose.WIN }
                val teamsLosses = filteredScores.count { it.team_win_lose?.winLose == WinLose.LOSE }

                val decks = filteredScores.groupBy { it.battle_deck }.map { (name, scores) ->
                    val total = scores.size
                    val wins = scores.count { it.win_lose == WinLose.WIN }
                    DeckStat(name, total, wins, total - wins, if (total > 0) (wins.toFloat() / total * 100) else 0f)
                }.sortedByDescending { it.winRate }

                val losses = filteredScores.filter { it.win_lose == WinLose.LOSE }.groupBy { it.matching_deck }
                    .map { (name, s) -> OpponentLossStat(name, s.size) }.sortedByDescending { it.lossCount }.take(10)

                withContext(Dispatchers.Main) {
                    _totalGameCount.value = filteredGames.size
                    _individualWinLose.value = Pair(sWins + tpWins, sLosses + tpLosses)
                    _detailedWinLose.value = DetailedWinLose(sWins, tpWins, sLosses, tpLosses)
                    _teamsWinLose.value = Pair(teamsWins, teamsLosses)
                    _deckStats.value = decks
                    _opponentLossStats.value = losses
                }
            }
        }
    }
}
