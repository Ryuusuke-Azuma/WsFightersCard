/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard

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

class MainViewModel(application: Application) : AndroidViewModel(application) {

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

    fun addGame(name: String, date: String, deck: String, memo: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.addGame(name, date, deck, memo)
            }
            loadData() // Refresh list after adding
        }
    }
}
