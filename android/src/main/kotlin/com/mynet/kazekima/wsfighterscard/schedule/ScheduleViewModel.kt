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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * スケジュール画面のデータ表示管理を担当する ViewModel
 */
class ScheduleViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FightersRepository(DatabaseDriverFactory(application))
    
    private val _games = MutableLiveData<List<SelectGamesWithStatsByDate>>()
    val games: LiveData<List<SelectGamesWithStatsByDate>> = _games

    private val _selectedDate = MutableLiveData<LocalDate>(LocalDate.now())
    val selectedDate: LiveData<LocalDate> = _selectedDate

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")

    fun loadData() {
        val date = _selectedDate.value ?: LocalDate.now()
        val dateString = date.format(dateFormatter)

        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repository.getGamesWithStatsByDate(dateString)
            }
            _games.value = result
        }
    }

    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
        loadData()
    }
}
