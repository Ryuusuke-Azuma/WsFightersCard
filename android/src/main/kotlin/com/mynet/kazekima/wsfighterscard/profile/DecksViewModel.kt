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

    fun loadDecks(fighterId: Long) {
        viewModelScope.launch {
            val fighterDecks = withContext(Dispatchers.IO) {
                repository.getDecksByFighterId(fighterId)
            }
            _decks.value = fighterDecks
        }
    }

    fun addDeck(fighterId: Long, name: String, memo: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.addDeck(fighterId, name, memo)
            }
        }
    }
}
