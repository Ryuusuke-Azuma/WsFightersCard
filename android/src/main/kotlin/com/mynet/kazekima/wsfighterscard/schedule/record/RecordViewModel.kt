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
 * スケジュール登録ダイアログのデータ操作を担当する ViewModel
 */
class RecordViewModel(
    application: Application,
    // テスト時に差し替え可能にするための最小限の変更
    private val repository: FightersRepository = FightersRepository(DatabaseDriverFactory(application))
) : AndroidViewModel(application) {

    fun addGame(name: String, date: String, deck: String, memo: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.addGame(name, date, deck, memo)
            }
            onComplete()
        }
    }
}
