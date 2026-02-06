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
import com.mynet.kazekima.wsfighterscard.db.Deck
import com.mynet.kazekima.wsfighterscard.db.FightersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DecksViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FightersRepository(DatabaseDriverFactory(application))

    private val _decks = MutableLiveData<List<Deck>>()
    val decks: LiveData<List<Deck>> = _decks

    private var currentFighterId: Long? = null

    private suspend fun loadDecks(fighterId: Long) {
        currentFighterId = fighterId
        val fighterDecks = withContext(Dispatchers.IO) {
            repository.getDecksByFighterId(fighterId)
        }
        _decks.value = fighterDecks
    }

    fun loadInitialDecksForFighter(fighterId: Long) {
        viewModelScope.launch {
            loadDecks(fighterId)
        }
    }

    fun addDeck(fighterId: Long, name: String, memo: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.addDeck(fighterId, name, memo)
            }
            loadDecks(fighterId)
        }
    }

    fun updateDeck(id: Long, name: String, memo: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.updateDeck(id, name, memo)
            }
            currentFighterId?.let { loadDecks(it) }
        }
    }

    fun deleteDeck(id: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.deleteDeck(id)
            }
            currentFighterId?.let { loadDecks(it) }
        }
    }
}
