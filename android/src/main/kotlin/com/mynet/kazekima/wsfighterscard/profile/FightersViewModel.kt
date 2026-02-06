/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mynet.kazekima.wsfighterscard.db.DatabaseDriverFactory
import com.mynet.kazekima.wsfighterscard.db.Fighter
import com.mynet.kazekima.wsfighterscard.db.FightersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FightersViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FightersRepository(DatabaseDriverFactory(application))

    private val _fighters = MutableLiveData<List<Fighter>>()
    val fighters: LiveData<List<Fighter>> = _fighters

    private val _selectedFighter = MutableLiveData<Fighter?>()
    val selectedFighter: LiveData<Fighter?> = _selectedFighter

    private suspend fun loadFighters() {
        val allFighters = withContext(Dispatchers.IO) {
            repository.getAllFighters()
        }
        _fighters.value = allFighters
    }

    fun loadInitialFighters() {
        viewModelScope.launch {
            loadFighters()
            selectFighter(_fighters.value?.firstOrNull())
        }
    }

    fun addFighter(name: String, memo: String) {
        viewModelScope.launch {
            val newFighterId = withContext(Dispatchers.IO) {
                repository.addFighter(name, 0L, memo)
            }
            loadFighters()
            val newItem = _fighters.value?.find { it.id == newFighterId }
            selectFighter(newItem)
        }
    }

    fun updateFighter(id: Long, name: String, memo: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.updateFighter(id, name, 0L, memo)
            }
            loadFighters()
            val updatedItem = _fighters.value?.find { it.id == id }
            selectFighter(updatedItem)
        }
    }

    fun deleteFighter(id: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.deleteFighter(id)
            }
            loadFighters()
            selectFighter(_fighters.value?.firstOrNull())
        }
    }

    fun selectFighter(fighter: Fighter?) {
        _selectedFighter.value = fighter
    }
}
