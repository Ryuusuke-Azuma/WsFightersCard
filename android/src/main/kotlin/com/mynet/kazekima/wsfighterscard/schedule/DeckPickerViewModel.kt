/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.schedule

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.mynet.kazekima.wsfighterscard.db.DatabaseDriverFactory
import com.mynet.kazekima.wsfighterscard.db.Deck
import com.mynet.kazekima.wsfighterscard.db.Fighter
import com.mynet.kazekima.wsfighterscard.db.FightersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeckPickerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FightersRepository(DatabaseDriverFactory(application))

    suspend fun getFighters(isMyDeck: Boolean): List<Fighter> = withContext(Dispatchers.IO) {
        val allFighters = repository.getAllFighters()
        if (isMyDeck) {
            val selfId = repository.getSelfFighterId()
            selfId?.let { id -> allFighters.filter { it.id == id } } ?: emptyList()
        } else {
            val selfId = repository.getSelfFighterId()
            if (selfId == null) {
                allFighters
            } else {
                allFighters.filter { it.id != selfId }
            }
        }
    }

    suspend fun getDecksForFighter(fighterId: Long): List<Deck> = withContext(Dispatchers.IO) {
        repository.getDecksByFighterId(fighterId)
    }
}
