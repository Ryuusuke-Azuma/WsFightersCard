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
import com.mynet.kazekima.wsfighterscard.db.SelectGamesWithStatsByDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FightersRepository(DatabaseDriverFactory(application))
    
    private val _games = MutableLiveData<List<SelectGamesWithStatsByDate>>()
    val games: LiveData<List<SelectGamesWithStatsByDate>> = _games

    private val _selectedDate = MutableLiveData(LocalDate.now())
    val selectedDate: LiveData<LocalDate> = _selectedDate

    private val _markedDates = MutableLiveData<List<LocalDate>>()
    val markedDates: LiveData<List<LocalDate>> = _markedDates

    fun loadData() {
        val date = _selectedDate.value ?: LocalDate.now()
        val millis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val dailyGames = repository.getGamesWithStatsByDate(millis)
                val allDates = repository.getGameDates().map {
                    Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                }

                withContext(Dispatchers.Main) {
                    _games.value = dailyGames
                    _markedDates.value = allDates
                }
            }
        }
    }

    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
        loadData()
    }
}
