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
import com.mynet.kazekima.wsfighterscard.db.Game
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * スケジュール画面のデータ表示管理を担当する ViewModel
 */
class ScheduleViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FightersRepository(DatabaseDriverFactory(application))
    
    private val _games = MutableLiveData<List<Game>>()
    val games: LiveData<List<Game>> = _games

    fun loadData() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repository.getAllGames()
            }
            _games.value = result
        }
    }
}
