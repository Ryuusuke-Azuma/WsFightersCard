/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.schedule.record

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mynet.kazekima.wsfighterscard.db.DatabaseDriverFactory
import com.mynet.kazekima.wsfighterscard.db.FightersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * スケジュールおよび対戦結果のデータ操作を担当する ViewModel
 */
class RecordViewModel(
    application: Application,
    private val repository: FightersRepository = FightersRepository(DatabaseDriverFactory(application))
) : AndroidViewModel(application) {

    /**
     * スケジュール（イベント）を追加する
     */
    fun addGame(name: String, date: String, memo: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.addGame(name, date, memo)
            }
            onComplete()
        }
    }

    /**
     * 対戦結果（スコア）を追加する
     */
    fun addScore(gameId: Long, battleDeck: String, matchingDeck: String, winOrLose: Long, memo: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.addScore(gameId, battleDeck, matchingDeck, winOrLose, memo)
            }
            onComplete()
        }
    }
}
